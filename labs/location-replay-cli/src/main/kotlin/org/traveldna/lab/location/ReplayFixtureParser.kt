package org.traveldna.lab.location

import java.nio.file.Files
import java.nio.file.Path
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSampleRejectionReason
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.location.replay.LocationReplayScenario
import org.traveldna.location.replay.PlaybackRate
import org.traveldna.location.replay.ReplaySummary

private val FIXTURE_ID = Regex("[A-Za-z0-9][A-Za-z0-9._:-]{0,127}")

class ReplayExpectations(
    val accepted: Int,
    val rejected: Int,
    val finalTimeMilliseconds: Long,
    val playbackDelayMilliseconds: Long,
    val lastSequence: Long,
    rejectionCounts: Map<LocationSampleRejectionReason, Int>,
) {
    val rejectionCounts: Map<LocationSampleRejectionReason, Int> = rejectionCounts.toMap()

    init {
        require(accepted > 0) { "expected accepted samples must be positive" }
        require(rejected >= 0) { "expected rejected samples must be non-negative" }
        require(finalTimeMilliseconds >= 0L) { "expected final time must be non-negative" }
        require(playbackDelayMilliseconds >= 0L) { "expected playback delay must be non-negative" }
        require(lastSequence >= 0L) { "expected last sequence must be non-negative" }
        require(this.rejectionCounts.values.all { it > 0 }) {
            "expected rejection counts must contain only positive values"
        }
        require(this.rejectionCounts.values.sum() == rejected) {
            "expected rejection reason counts must equal rejected samples"
        }
    }
}

class ParsedReplayFixture(
    val scenario: LocationReplayScenario,
    val expectations: ReplayExpectations,
)

object ReplayFixtureParser {
    const val MaxFixtureBytes: Long = 32L * 1024L * 1024L

    fun parse(path: Path): ParsedReplayFixture {
        val size = Files.size(path)
        require(size in 1L..MaxFixtureBytes) {
            "$path: fixture size must be within [1, $MaxFixtureBytes] bytes"
        }

        val lines = Files.readAllLines(path)
        var versionSeen = false
        var scenarioId: String? = null
        var playbackRate: PlaybackRate? = null
        var expectationsStarted = false
        val samples = mutableListOf<LocationSample>()
        val scalarExpectations = mutableMapOf<String, Long>()
        val rejectionCounts = mutableMapOf<LocationSampleRejectionReason, Int>()

        lines.forEachIndexed { index, raw ->
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("#")) return@forEachIndexed
            val parts = line.split(Regex("\\s+"))
            try {
                when (parts.first()) {
                    "TDNA_LOCATION_REPLAY_V0" -> {
                        requireArity(parts, 1)
                        require(!versionSeen) { "duplicate version header" }
                        require(scenarioId == null && playbackRate == null && samples.isEmpty()) {
                            "version header must be the first directive"
                        }
                        versionSeen = true
                    }
                    "scenario" -> {
                        requireArity(parts, 2)
                        require(versionSeen) { "version header must precede scenario data" }
                        require(!expectationsStarted && samples.isEmpty()) {
                            "scenario must precede samples and expectations"
                        }
                        require(scenarioId == null) { "duplicate scenario declaration" }
                        require(FIXTURE_ID.matches(parts[1])) { "invalid scenario id" }
                        scenarioId = parts[1]
                    }
                    "rate" -> {
                        requireArity(parts, 3)
                        require(versionSeen) { "version header must precede replay data" }
                        require(!expectationsStarted && samples.isEmpty()) {
                            "playback rate must precede samples and expectations"
                        }
                        require(playbackRate == null) { "duplicate playback rate" }
                        playbackRate = PlaybackRate.of(parts[1].toLong(), parts[2].toLong())
                    }
                    "sample" -> {
                        requireArity(parts, 9)
                        require(versionSeen) { "version header must precede replay data" }
                        require(scenarioId != null && playbackRate != null) {
                            "scenario and rate must precede samples"
                        }
                        require(!expectationsStarted) { "samples cannot follow expectations" }
                        require(samples.size < LocationReplayScenario.MaxSamples) {
                            "fixture exceeds ${LocationReplayScenario.MaxSamples} samples"
                        }
                        samples += parseSample(parts)
                    }
                    "expect" -> {
                        require(versionSeen && samples.isNotEmpty()) {
                            "expectations must follow at least one sample"
                        }
                        expectationsStarted = true
                        parseExpectation(parts, scalarExpectations, rejectionCounts)
                    }
                    else -> error("unknown directive: ${parts.first()}")
                }
            } catch (exception: RuntimeException) {
                throw IllegalArgumentException(
                    "$path:${index + 1}: ${exception.message}",
                    exception,
                )
            }
        }

        require(versionSeen) { "$path: missing TDNA_LOCATION_REPLAY_V0 header" }
        val id = requireNotNull(scenarioId) { "$path: missing scenario declaration" }
        val rate = requireNotNull(playbackRate) { "$path: missing playback rate" }
        val expectations = ReplayExpectations(
            accepted = requiredExpectation(scalarExpectations, "accepted").toIntChecked("accepted"),
            rejected = requiredExpectation(scalarExpectations, "rejected").toIntChecked("rejected"),
            finalTimeMilliseconds = requiredExpectation(scalarExpectations, "final_time_ms"),
            playbackDelayMilliseconds = requiredExpectation(scalarExpectations, "playback_delay_ms"),
            lastSequence = requiredExpectation(scalarExpectations, "last_sequence"),
            rejectionCounts = rejectionCounts,
        )
        require(expectations.accepted + expectations.rejected == samples.size) {
            "$path: accepted and rejected expectations must equal sample count"
        }
        return ParsedReplayFixture(
            scenario = LocationReplayScenario(id, samples, rate),
            expectations = expectations,
        )
    }

    private fun parseSample(parts: List<String>): LocationSample {
        require(parts[8] == "replay") { "location replay fixture samples must use replay origin" }
        return LocationSample(
            sequence = LocationSequence(parts[1].toLong()),
            monotonicTime = MonotonicInstant(parts[2].toLong()),
            position = GeoPoint(parts[3].toDouble(), parts[4].toDouble()),
            horizontalAccuracyMeters = parts[5].toDouble(),
            speedMetersPerSecond = parts[6].nullableDouble(),
            bearingDegrees = parts[7].nullableDouble(),
            origin = LocationSampleOrigin.Replay,
        )
    }

    private fun parseExpectation(
        parts: List<String>,
        scalarExpectations: MutableMap<String, Long>,
        rejectionCounts: MutableMap<LocationSampleRejectionReason, Int>,
    ) {
        if (parts.size == 3) {
            val key = parts[1]
            require(key in SCALAR_EXPECTATIONS) { "unknown scalar expectation: $key" }
            require(scalarExpectations.putIfAbsent(key, parts[2].toLong()) == null) {
                "duplicate expectation: $key"
            }
            return
        }
        requireArity(parts, 4)
        require(parts[1] == "reason") { "four-part expectation must use 'reason'" }
        val reason = when (parts[2]) {
            "non_increasing_sequence" -> LocationSampleRejectionReason.NonIncreasingSequence
            "non_increasing_monotonic_time" -> LocationSampleRejectionReason.NonIncreasingMonotonicTime
            else -> error("unknown rejection reason: ${parts[2]}")
        }
        val count = parts[3].toInt()
        require(count > 0) { "rejection reason expectation must be positive" }
        require(rejectionCounts.putIfAbsent(reason, count) == null) {
            "duplicate rejection reason expectation: $reason"
        }
    }

    private fun requiredExpectation(values: Map<String, Long>, key: String): Long =
        requireNotNull(values[key]) { "missing expectation: $key" }

    private fun requireArity(parts: List<String>, expected: Int) {
        require(parts.size == expected) {
            "directive ${parts.first()} expects ${expected - 1} argument(s)"
        }
    }

    private fun String.nullableDouble(): Double? = if (this == "-") null else toDouble()

    private fun Long.toIntChecked(field: String): Int {
        require(this in 0L..Int.MAX_VALUE.toLong()) { "$field expectation exceeds Int"
        }
        return toInt()
    }

    private val SCALAR_EXPECTATIONS = setOf(
        "accepted",
        "rejected",
        "final_time_ms",
        "playback_delay_ms",
        "last_sequence",
    )
}

fun ReplayExpectations.requireMatches(summary: ReplaySummary) {
    require(summary.acceptedSamples == accepted) { "accepted count differs from fixture expectation" }
    require(summary.rejectedSamples == rejected) { "rejected count differs from fixture expectation" }
    require(summary.finalClock?.milliseconds == finalTimeMilliseconds) {
        "final replay time differs from fixture expectation"
    }
    require(summary.totalPlaybackDelayMilliseconds == playbackDelayMilliseconds) {
        "playback delay differs from fixture expectation"
    }
    require(summary.lastAcceptedSequence?.value == lastSequence) {
        "last accepted sequence differs from fixture expectation"
    }
    require(summary.rejectionCounts == rejectionCounts) {
        "rejection counts differ from fixture expectation"
    }
}
