package org.traveldna.lab.reroute

import java.util.Locale
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.navigation.offroute.OffRouteTracker
import org.traveldna.navigation.offroute.contracts.OffRouteDecision
import org.traveldna.navigation.offroute.contracts.OffRouteEvidence
import org.traveldna.navigation.offroute.contracts.OffRouteEvidenceReason
import org.traveldna.navigation.offroute.contracts.OffRouteObservation
import org.traveldna.navigation.offroute.contracts.OffRoutePolicy
import org.traveldna.navigation.offroute.contracts.OffRouteState
import org.traveldna.navigation.offroute.contracts.OffRouteTransition
import org.traveldna.navigation.offroute.contracts.RerouteAttemptId
import org.traveldna.navigation.offroute.contracts.RerouteOutcome
import org.traveldna.navigation.progress.RouteProgressTracker
import org.traveldna.navigation.reroute.RerouteApplyDecision
import org.traveldna.navigation.reroute.RerouteBeginDecision
import org.traveldna.navigation.reroute.RerouteBeginIgnoreReason
import org.traveldna.navigation.reroute.RerouteCoordinator
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RoutePlanningError
import org.traveldna.routing.contracts.RoutePlanningErrorCode
import org.traveldna.routing.contracts.RouteProvenance
import org.traveldna.routing.contracts.RouteRequest
import org.traveldna.routing.fake.FakeRoutePlanner

fun main(args: Array<String>) {
    when {
        args.isEmpty() -> runLab()
        args.size in 2..3 && args[0] == "--benchmark" -> runBenchmark(
            args[1].toInt(),
            args.getOrNull(2)?.toInt() ?: DefaultBenchmarkIterations,
        )
        else -> {
            System.err.println("usage: missed-exit-cli | --benchmark SAMPLE_COUNT [ODD_ITERATIONS]")
            kotlin.system.exitProcess(2)
        }
    }
}

private fun runLab() {
    val oldRoute = oldRoute()
    val tracker = OffRouteTracker(oldRoute.id, OffRoutePolicy(3, 2_000L))
    var episodes = 0
    var recoveries = 0
    var indeterminate = 0
    var confirmations = 0
    var confirmation: OffRouteState.Confirmed? = null

    referenceObservations(oldRoute).forEach { observation ->
        val decision = tracker.accept(observation) as OffRouteDecision.Accepted
        when (decision.transition) {
            OffRouteTransition.SuspicionStarted -> episodes += 1
            OffRouteTransition.Recovered -> recoveries += 1
            OffRouteTransition.HeldIndeterminate -> indeterminate += 1
            OffRouteTransition.ConfirmedNow -> {
                confirmations += 1
                confirmation = decision.state as OffRouteState.Confirmed
            }
            else -> Unit
        }
    }

    val confirmed = checkNotNull(confirmation)
    val coordinator = RerouteCoordinator(oldRoute)
    val started = coordinator.begin(confirmed) as RerouteBeginDecision.Started
    val oldRouteHeld = coordinator.activeRoute == oldRoute
    val duplicate = coordinator.begin(confirmed) as RerouteBeginDecision.Ignored
    val stale = coordinator.apply(
        RerouteOutcome.Failed(
            attemptId = RerouteAttemptId(started.command.attemptId.value + 1L),
            sourceRouteId = oldRoute.id,
            error = RoutePlanningError(
                code = RoutePlanningErrorCode.ProviderUnavailable,
                message = "stale fixture outcome",
                retryable = true,
            ),
        ),
    ) is RerouteApplyDecision.Stale

    val replacement = replacementRoute(started.command.request)
    val planner = FakeRoutePlanner(
        catalog = mapOf(started.command.request to listOf(replacement)),
    )
    val apply = runImmediate { coordinator.executePending(planner) }
    val replaced = apply is RerouteApplyDecision.Replaced
    val newTracker = RouteProgressTracker(coordinator.activeRoute)

    check(episodes == 2)
    check(recoveries == 1)
    check(indeterminate == 1)
    check(confirmations == 1)
    check(duplicate.reason == RerouteBeginIgnoreReason.AlreadyInFlight)
    check(stale)
    check(oldRouteHeld)
    check(replaced)
    check(coordinator.activeRoute == replacement)
    check(newTracker.lastSnapshot == null)

    println(
        "{\"scenario\":\"reference-missed-exit-v0\"," +
            "\"episodes\":$episodes," +
            "\"recoveries\":$recoveries," +
            "\"indeterminate\":$indeterminate," +
            "\"confirmations\":$confirmations," +
            "\"attempts\":1," +
            "\"duplicate_begin\":\"${duplicate.reason}\"," +
            "\"stale_outcome_ignored\":$stale," +
            "\"old_route_held\":$oldRouteHeld," +
            "\"replaced\":$replaced," +
            "\"new_route_id\":\"${coordinator.activeRoute.id.value}\"," +
            "\"new_tracker_empty\":${newTracker.lastSnapshot == null}}",
    )
}

private fun runBenchmark(sampleCount: Int, iterations: Int) {
    require(sampleCount in 2..MaxBenchmarkSamples) {
        "benchmark sample count must be within [2, $MaxBenchmarkSamples]"
    }
    require(iterations in 1..MaxBenchmarkIterations && iterations % 2 == 1) {
        "benchmark iterations must be odd and within [1, $MaxBenchmarkIterations]"
    }
    val routeId = RouteId("benchmark-off-route-v0")
    val observations = List(sampleCount) { index ->
        val evidence = when {
            index == sampleCount - 1 -> OffRouteEvidence.OnRoute
            index % 8 == 1 || index % 8 == 2 ->
                OffRouteEvidence.Suspicious(OffRouteEvidenceReason.LowConfidence)
            else -> OffRouteEvidence.OnRoute
        }
        observation(routeId, index.toLong(), index.toLong() * 100L, evidence)
    }
    val tracker = OffRouteTracker(routeId, OffRoutePolicy(3, 2_000L))

    tracker.reset()
    verifyObservations(tracker, observations)

    repeat(BenchmarkWarmups) {
        tracker.reset()
        runObservations(tracker, observations)
        verifyBenchmarkOutcome(tracker, sampleCount)
    }
    val elapsed = LongArray(iterations) {
        tracker.reset()
        val started = System.nanoTime()
        runObservations(tracker, observations)
        val duration = System.nanoTime() - started
        verifyBenchmarkOutcome(tracker, sampleCount)
        duration
    }.sorted()
    val median = elapsed[elapsed.size / 2]
    println(
        "{\"benchmark\":\"off-route-state-v0\"," +
            "\"samples\":$sampleCount," +
            "\"warmups\":$BenchmarkWarmups," +
            "\"iterations\":$iterations," +
            "\"min_elapsed_ns\":${elapsed.first()}," +
            "\"median_elapsed_ns\":$median," +
            "\"max_elapsed_ns\":${elapsed.last()}," +
            "\"median_ns_per_sample\":${"%.2f".format(Locale.ROOT, median.toDouble() / sampleCount)}}",
    )
}

private fun verifyObservations(
    tracker: OffRouteTracker,
    observations: List<OffRouteObservation>,
) {
    observations.forEach { check(tracker.accept(it) is OffRouteDecision.Accepted) }
    verifyBenchmarkOutcome(tracker, observations.size)
}

private fun runObservations(
    tracker: OffRouteTracker,
    observations: List<OffRouteObservation>,
) {
    observations.forEach(tracker::accept)
}

private fun verifyBenchmarkOutcome(tracker: OffRouteTracker, sampleCount: Int) {
    check(tracker.state == OffRouteState.OnRoute)
    check(tracker.lastAcceptedObservation?.sample?.sequence == LocationSequence((sampleCount - 1).toLong()))
}

private fun referenceObservations(route: RoutePlan): List<OffRouteObservation> = listOf(
    observation(route.id, 0, 0, OffRouteEvidence.OnRoute),
    observation(route.id, 1, 1_000, suspicious()),
    observation(route.id, 2, 1_500, OffRouteEvidence.OnRoute),
    observation(route.id, 3, 2_000, suspicious()),
    observation(route.id, 4, 2_500, suspicious()),
    observation(route.id, 5, 3_000, OffRouteEvidence.Indeterminate("fixture.signal-gap")),
    observation(route.id, 6, 4_500, suspicious()),
)

private fun suspicious(): OffRouteEvidence =
    OffRouteEvidence.Suspicious(OffRouteEvidenceReason.MissedExpectedManeuver)

private fun observation(
    routeId: RouteId,
    sequence: Long,
    time: Long,
    evidence: OffRouteEvidence,
): OffRouteObservation = OffRouteObservation(
    routeId = routeId,
    sample = LocationSample(
        sequence = LocationSequence(sequence),
        monotonicTime = MonotonicInstant(time),
        position = GeoPoint(0.004, 0.013 + sequence * 0.000_001),
        horizontalAccuracyMeters = 5.0,
        origin = LocationSampleOrigin.Replay,
    ),
    evidence = evidence,
)

private fun oldRoute(): RoutePlan {
    val a = GeoPoint(0.0, 0.0)
    val b = GeoPoint(0.0, 0.01)
    val d = GeoPoint(0.0, 0.02)
    return RoutePlan(
        id = RouteId("reference-old-route-v0"),
        geometry = listOf(a, b, d),
        legs = listOf(RouteLeg(0, 2, a, d, 2_000L, 120L, emptyList())),
        distanceMeters = 2_000L,
        durationSeconds = 120L,
        provenance = RouteProvenance(PluginId("org.traveldna.fixture-old-route")),
    )
}

private fun replacementRoute(request: RouteRequest): RoutePlan {
    val middle = GeoPoint(0.005, 0.017)
    return RoutePlan(
        id = RouteId("reference-rerouted-route-v0"),
        geometry = listOf(request.origin, middle, request.destination),
        legs = listOf(RouteLeg(0, 2, request.origin, request.destination, 1_500L, 90L, emptyList())),
        distanceMeters = 1_500L,
        durationSeconds = 90L,
        provenance = RouteProvenance(FakeRoutePlanner.Id),
    )
}

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

private const val BenchmarkWarmups: Int = 3
private const val DefaultBenchmarkIterations: Int = 7
private const val MaxBenchmarkIterations: Int = 25
private const val MaxBenchmarkSamples: Int = 100_000
