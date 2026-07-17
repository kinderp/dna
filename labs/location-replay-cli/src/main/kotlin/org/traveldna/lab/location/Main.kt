package org.traveldna.lab.location

import java.nio.file.Path
import java.util.Locale
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSampleRejectionReason
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.location.replay.DeterministicReplayRunner
import org.traveldna.location.replay.LocationReplayScenario
import org.traveldna.location.replay.PlaybackRate
import org.traveldna.location.replay.ReplayState
import org.traveldna.location.replay.ReplaySummary

fun main(args: Array<String>) {
    when {
        args.size == 1 -> runFixture(Path.of(args[0]))
        args.size in 2..3 && args[0] == "--benchmark" -> {
            val sampleCount = args[1].toInt()
            val iterations = args.getOrNull(2)?.toInt() ?: DefaultBenchmarkIterations
            runBenchmark(sampleCount, iterations)
        }
        else -> {
            System.err.println(
                "usage: location-replay-cli FIXTURE | --benchmark SAMPLE_COUNT [ITERATIONS]",
            )
            kotlin.system.exitProcess(2)
        }
    }
}

private fun runFixture(path: Path) {
    val fixture = ReplayFixtureParser.parse(path)
    val summary = DeterministicReplayRunner(fixture.scenario).runToEnd()
    fixture.expectations.requireMatches(summary)
    println(canonicalReplayReport(fixture.scenario, summary))
}

private fun runBenchmark(sampleCount: Int, iterations: Int) {
    require(sampleCount in 1..LocationReplayScenario.MaxSamples) {
        "benchmark sample count must be within [1, ${LocationReplayScenario.MaxSamples}]"
    }
    require(iterations in 1..MaxBenchmarkIterations) {
        "benchmark iterations must be within [1, $MaxBenchmarkIterations]"
    }
    val scenario = benchmarkScenario(sampleCount)

    repeat(BenchmarkWarmups) {
        DeterministicReplayRunner(scenario).runToEnd()
    }
    val elapsedRuns = LongArray(iterations) {
        val started = System.nanoTime()
        val summary = DeterministicReplayRunner(scenario).runToEnd()
        check(summary.acceptedSamples == sampleCount)
        System.nanoTime() - started
    }.sorted()

    val minimum = elapsedRuns.first()
    val median = elapsedRuns[elapsedRuns.size / 2]
    val maximum = elapsedRuns.last()
    val medianPerSample = median.toDouble() / sampleCount
    println(
        "{\"benchmark\":\"location-replay-v0\"," +
            "\"samples\":$sampleCount," +
            "\"warmups\":$BenchmarkWarmups," +
            "\"iterations\":$iterations," +
            "\"min_elapsed_ns\":$minimum," +
            "\"median_elapsed_ns\":$median," +
            "\"max_elapsed_ns\":$maximum," +
            "\"median_ns_per_sample\":${"%.2f".format(Locale.ROOT, medianPerSample)}}",
    )
}

private fun benchmarkScenario(sampleCount: Int): LocationReplayScenario {
    val samples = List(sampleCount) { index ->
        LocationSample(
            sequence = LocationSequence(index.toLong()),
            monotonicTime = MonotonicInstant(index.toLong() * 100L),
            position = GeoPoint(37.5 + index * 0.0000001, 15.1),
            horizontalAccuracyMeters = 5.0,
            speedMetersPerSecond = 20.0,
            bearingDegrees = 90.0,
            origin = LocationSampleOrigin.Replay,
        )
    }
    return LocationReplayScenario(
        id = "benchmark-location-replay-v0",
        samples = samples,
        playbackRate = PlaybackRate.RealTime,
    )
}

fun canonicalReplayReport(
    scenario: LocationReplayScenario,
    summary: ReplaySummary,
): String {
    require(summary.state == ReplayState.Completed) { "canonical report requires completed replay" }
    val finalClock = requireNotNull(summary.finalClock) { "completed replay must have final clock" }
    val lastSequence = requireNotNull(summary.lastAcceptedSequence) {
        "completed replay must have last accepted sequence"
    }
    val sequenceCount = summary.rejectionCounts[
        LocationSampleRejectionReason.NonIncreasingSequence,
    ] ?: 0
    val timeCount = summary.rejectionCounts[
        LocationSampleRejectionReason.NonIncreasingMonotonicTime,
    ] ?: 0
    return "{\"scenario\":\"${scenario.id}\"," +
        "\"rate\":\"${scenario.playbackRate}\"," +
        "\"state\":\"completed\"," +
        "\"processed\":${summary.processedSamples}," +
        "\"accepted\":${summary.acceptedSamples}," +
        "\"rejected\":${summary.rejectedSamples}," +
        "\"rejection_counts\":{" +
        "\"non_increasing_monotonic_time\":$timeCount," +
        "\"non_increasing_sequence\":$sequenceCount}," +
        "\"final_time_ms\":${finalClock.milliseconds}," +
        "\"playback_delay_ms\":${summary.totalPlaybackDelayMilliseconds}," +
        "\"last_sequence\":${lastSequence.value}}"
}

private const val BenchmarkWarmups: Int = 3
private const val DefaultBenchmarkIterations: Int = 7
private const val MaxBenchmarkIterations: Int = 25
