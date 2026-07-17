package org.traveldna.lab.matching

import java.util.Locale
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.navigation.contracts.MatchConfidence
import org.traveldna.navigation.contracts.MatchedRoutePosition
import org.traveldna.navigation.contracts.RouteCoordinate
import org.traveldna.navigation.matching.contracts.MapMatchProvenance
import org.traveldna.navigation.matching.contracts.MapMatchRequest
import org.traveldna.navigation.matching.contracts.MapMatchResult
import org.traveldna.navigation.matching.contracts.requireMatches
import org.traveldna.navigation.matching.fake.FakeMapMatchEntry
import org.traveldna.navigation.matching.fake.FakeMapMatchFixtures
import org.traveldna.navigation.matching.fake.FakeMapMatcher
import org.traveldna.navigation.matching.testkit.MapMatcherContractProbe
import org.traveldna.navigation.progress.RouteProgressTracker
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RouteProvenance

fun main(args: Array<String>) {
    when {
        args.isEmpty() -> runLab()
        args.size in 2..3 && args[0] == "--benchmark" -> {
            val samples = args[1].toInt()
            val iterations = args.getOrNull(2)?.toInt() ?: DefaultBenchmarkIterations
            runBenchmark(samples, iterations)
        }
        else -> {
            System.err.println("usage: map-matching-cli | --benchmark SAMPLE_COUNT [ODD_ITERATIONS]")
            kotlin.system.exitProcess(2)
        }
    }
}

private fun runLab() {
    val probe = runImmediate {
        MapMatcherContractProbe.verify(
            FakeMapMatchFixtures.matcher(),
            FakeMapMatchFixtures.MatchedRequest,
            FakeMapMatchFixtures.UnmatchedRequest,
        )
    }
    val matcher = FakeMapMatchFixtures.matcher()
    val tracker = RouteProgressTracker(FakeMapMatchFixtures.Route)
    var matched = 0
    var unmatched = 0
    var progressAccepted = 0
    var unmatchedReason = "none"

    runImmediate {
        FakeMapMatchFixtures.Requests.forEach { request ->
            when (val result = matcher.match(request)) {
                is MapMatchResult.Matched -> {
                    result.requireMatches(request, matcher.descriptor.id)
                    matched += 1
                    if (tracker.accept(result.position) is org.traveldna.navigation.contracts.RouteProgressDecision.Accepted) {
                        progressAccepted += 1
                    }
                }
                is MapMatchResult.Unmatched -> {
                    unmatched += 1
                    unmatchedReason = result.unmatched.reason.name
                }
                is MapMatchResult.Failure -> error("fixture matcher failed: ${result.error}")
            }
        }
    }

    val finalSnapshot = checkNotNull(tracker.lastSnapshot)
    check(probe.checks.size == ExpectedContractChecks)
    check(matched == ExpectedMatched)
    check(unmatched == ExpectedUnmatched)
    check(progressAccepted == ExpectedMatched)
    check(finalSnapshot.arrived)
    check(finalSnapshot.position.coordinate == RouteCoordinate(3, 0.0))
    check(matcher.totalRequestCount == FakeMapMatchFixtures.Requests.size.toLong())

    println(
        "{\"scenario\":\"reference-map-matching-v0\"," +
            "\"provider\":\"${matcher.descriptor.id.value}\"," +
            "\"contract_checks\":${probe.checks.size}," +
            "\"matched\":$matched," +
            "\"unmatched\":$unmatched," +
            "\"unmatched_reason\":\"$unmatchedReason\"," +
            "\"progress_accepted\":$progressAccepted," +
            "\"final_index\":${finalSnapshot.position.coordinate.completedGeometryIndex}," +
            "\"arrived\":${finalSnapshot.arrived}," +
            "\"calls\":${matcher.totalRequestCount}}",
    )
}

private fun runBenchmark(sampleCount: Int, iterations: Int) {
    require(sampleCount in 2..MaxBenchmarkSamples) {
        "benchmark sample count must be within [2, $MaxBenchmarkSamples]"
    }
    require(iterations in 1..MaxBenchmarkIterations && iterations % 2 == 1) {
        "benchmark iterations must be odd and within [1, $MaxBenchmarkIterations]"
    }
    val route = benchmarkRoute(sampleCount)
    val requests = List(sampleCount) { index ->
        MapMatchRequest(route, benchmarkSample(index, route.geometry[index]))
    }
    val entries = requests.mapIndexed { index, request ->
        FakeMapMatchEntry(
            request,
            MapMatchResult.Matched(
                position = MatchedRoutePosition(
                    routeId = route.id,
                    sampleSequence = request.sample.sequence,
                    monotonicTime = request.sample.monotonicTime,
                    coordinate = RouteCoordinate(index, 0.0),
                    lateralDistanceMeters = 1.0,
                    confidence = MatchConfidence.High,
                ),
                provenance = MapMatchProvenance(FakeMapMatcher.Id),
            ),
        )
    }
    val matcher = FakeMapMatcher(entries, maxRecordedRequests = 1)
    val tracker = RouteProgressTracker(route)

    repeat(BenchmarkWarmups) {
        matcher.resetRecordedRequests()
        tracker.reset()
        runPipeline(matcher, tracker, requests)
        verifyBenchmarkOutcome(tracker, sampleCount)
    }
    val elapsed = LongArray(iterations) {
        matcher.resetRecordedRequests()
        tracker.reset()
        val started = System.nanoTime()
        runPipeline(matcher, tracker, requests)
        val duration = System.nanoTime() - started
        verifyBenchmarkOutcome(tracker, sampleCount)
        duration
    }.sorted()
    val median = elapsed[elapsed.size / 2]
    println(
        "{\"benchmark\":\"map-matching-pipeline-v0\"," +
            "\"samples\":$sampleCount," +
            "\"catalog_entries\":${entries.size}," +
            "\"warmups\":$BenchmarkWarmups," +
            "\"iterations\":$iterations," +
            "\"min_elapsed_ns\":${elapsed.first()}," +
            "\"median_elapsed_ns\":$median," +
            "\"max_elapsed_ns\":${elapsed.last()}," +
            "\"median_ns_per_sample\":${"%.2f".format(Locale.ROOT, median.toDouble() / sampleCount)}}",
    )
}

private fun runPipeline(
    matcher: FakeMapMatcher,
    tracker: RouteProgressTracker,
    requests: List<MapMatchRequest>,
) {
    runImmediate {
        requests.forEach { request ->
            val result = matcher.match(request) as MapMatchResult.Matched
            tracker.accept(result.position)
        }
    }
}

private fun verifyBenchmarkOutcome(tracker: RouteProgressTracker, sampleCount: Int) {
    val snapshot = checkNotNull(tracker.lastSnapshot)
    check(snapshot.arrived)
    check(snapshot.position.sampleSequence == LocationSequence((sampleCount - 1).toLong()))
}

private fun benchmarkRoute(sampleCount: Int): RoutePlan {
    val points = List(sampleCount) { index -> GeoPoint(0.0, index * 0.000001) }
    val total = points.lastIndex.toLong()
    return RoutePlan(
        id = RouteId("benchmark-map-match-route-v0"),
        geometry = points,
        legs = listOf(RouteLeg(0, points.lastIndex, points.first(), points.last(), total, total, emptyList())),
        distanceMeters = total,
        durationSeconds = total,
        provenance = RouteProvenance(PluginId("org.traveldna.map-match-benchmark-route")),
    )
}

private fun benchmarkSample(index: Int, point: GeoPoint): LocationSample = LocationSample(
    sequence = LocationSequence(index.toLong()),
    monotonicTime = MonotonicInstant(index.toLong() * 100L),
    position = point,
    horizontalAccuracyMeters = 5.0,
    origin = LocationSampleOrigin.Replay,
)

private fun <T> runImmediate(block: suspend () -> T): T {
    var outcome: Result<T>? = null
    block.startCoroutine(object : Continuation<T> {
        override val context = EmptyCoroutineContext
        override fun resumeWith(result: Result<T>) {
            outcome = result
        }
    })
    return checkNotNull(outcome) { "deterministic fake unexpectedly suspended" }.getOrThrow()
}

private const val ExpectedContractChecks: Int = 4
private const val ExpectedMatched: Int = 4
private const val ExpectedUnmatched: Int = 1
private const val BenchmarkWarmups: Int = 3
private const val DefaultBenchmarkIterations: Int = 7
private const val MaxBenchmarkIterations: Int = 25
private const val MaxBenchmarkSamples: Int = 50_000
