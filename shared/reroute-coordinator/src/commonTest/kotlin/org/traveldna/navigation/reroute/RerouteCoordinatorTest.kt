package org.traveldna.navigation.reroute

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.navigation.offroute.contracts.OffRouteEpisodeId
import org.traveldna.navigation.offroute.contracts.OffRouteEvidence
import org.traveldna.navigation.offroute.contracts.OffRouteEvidenceReason
import org.traveldna.navigation.offroute.contracts.OffRouteObservation
import org.traveldna.navigation.offroute.contracts.OffRouteState
import org.traveldna.navigation.offroute.contracts.RerouteAttemptId
import org.traveldna.navigation.offroute.contracts.RerouteOutcome
import org.traveldna.plugin.sdk.KnownPlatforms
import org.traveldna.plugin.sdk.PluginDescriptor
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RoutePlannerPort
import org.traveldna.routing.contracts.RoutePlanningError
import org.traveldna.routing.contracts.RoutePlanningErrorCode
import org.traveldna.routing.contracts.RoutePlanningResult
import org.traveldna.routing.contracts.RouteProvenance
import org.traveldna.routing.contracts.RouteRequest
import org.traveldna.routing.contracts.RoutingCapabilities

class RerouteCoordinatorTest {
    @Test
    fun startsOneAttemptAndRetainsOldRouteWhileInFlight() {
        val old = oldRoute()
        val coordinator = RerouteCoordinator(old)
        val started = assertIs<RerouteBeginDecision.Started>(coordinator.begin(confirmation(old)))
        assertEquals(old, coordinator.activeRoute)
        assertEquals(old.id, started.command.sourceRouteId)
        assertEquals(old.destination, started.command.request.destination)
        assertEquals(confirmationPosition, started.command.request.origin)

        val duplicate = assertIs<RerouteBeginDecision.Ignored>(coordinator.begin(confirmation(old)))
        assertEquals(RerouteBeginIgnoreReason.AlreadyInFlight, duplicate.reason)
        assertEquals(started.command, coordinator.pendingCommand)
    }

    @Test
    fun staleAndFailureOutcomesNeverReplaceTheOldRoute() {
        val old = oldRoute()
        val coordinator = RerouteCoordinator(old)
        val command = assertIs<RerouteBeginDecision.Started>(coordinator.begin(confirmation(old))).command
        val error = failureError()

        val stale = coordinator.apply(
            RerouteOutcome.Failed(
                RerouteAttemptId(command.attemptId.value + 1L),
                command.sourceRouteId,
                error,
            ),
        )
        assertIs<RerouteApplyDecision.Stale>(stale)
        assertEquals(command, coordinator.pendingCommand)

        val failed = assertIs<RerouteApplyDecision.Failed>(
            coordinator.apply(RerouteOutcome.Failed(command.attemptId, command.sourceRouteId, error)),
        )
        assertEquals(old, failed.activeRoute)
        assertEquals(old, coordinator.activeRoute)
        assertEquals(error, assertIs<RerouteCoordinatorState.Ready>(coordinator.state).lastFailure)

        val retry = assertIs<RerouteBeginDecision.Started>(coordinator.begin(confirmation(old)))
        assertEquals(command.attemptId.value + 1L, retry.command.attemptId.value)
    }

    @Test
    fun validReplacementIsAppliedAtomicallyAndInvalidReplacementIsRejected() {
        val old = oldRoute()
        val coordinator = RerouteCoordinator(old)
        val command = assertIs<RerouteBeginDecision.Started>(coordinator.begin(confirmation(old))).command

        val invalid = coordinator.apply(RerouteOutcome.Planned(command.attemptId, old.id, old))
        assertIs<RerouteApplyDecision.InvalidReplacement>(invalid)
        assertEquals(old, coordinator.activeRoute)

        val retry = assertIs<RerouteBeginDecision.Started>(coordinator.begin(confirmation(old))).command
        val replacement = replacementRoute(retry.request, PlannerId)
        val replaced = assertIs<RerouteApplyDecision.Replaced>(
            coordinator.apply(RerouteOutcome.Planned(retry.attemptId, old.id, replacement)),
        )
        assertEquals(old, replaced.previousRoute)
        assertEquals(replacement, replaced.activeRoute)
        assertEquals(replacement, coordinator.activeRoute)
        assertNull(coordinator.pendingCommand)
    }

    @Test
    fun unexpectedExceptionAndInvalidProvenanceBecomeFailureWhileCancellationCleansState() {
        val old = oldRoute()
        val coordinator = RerouteCoordinator(old)
        coordinator.begin(confirmation(old))
        val failed = assertIs<RerouteApplyDecision.Failed>(
            runImmediate {
                coordinator.executePending(TestPlanner { throw IllegalStateException("boom") })
            },
        )
        assertEquals(RoutePlanningErrorCode.Internal, failed.error.code)
        assertEquals(old, coordinator.activeRoute)
        assertNull(coordinator.pendingCommand)

        val command = assertIs<RerouteBeginDecision.Started>(coordinator.begin(confirmation(old))).command
        val wrongProvenance = replacementRoute(command.request, PluginId("org.traveldna.other-provider"))
        val provenanceFailure = assertIs<RerouteApplyDecision.Failed>(
            runImmediate {
                coordinator.executePending(TestPlanner { RoutePlanningResult.Success(listOf(wrongProvenance)) })
            },
        )
        assertEquals(RoutePlanningErrorCode.Internal, provenanceFailure.error.code)
        assertEquals(old, coordinator.activeRoute)
        assertNull(coordinator.pendingCommand)

        coordinator.begin(confirmation(old))
        assertFailsWith<CancellationException> {
            runImmediate {
                coordinator.executePending(TestPlanner { throw CancellationException("cancel") })
            }
        }
        assertEquals(old, coordinator.activeRoute)
        assertNull(coordinator.pendingCommand)
    }

    @Test
    fun executorAndCoordinatorApplyAValidPlannerResult() {
        val old = oldRoute()
        val coordinator = RerouteCoordinator(old)
        val command = assertIs<RerouteBeginDecision.Started>(coordinator.begin(confirmation(old))).command
        val replacement = replacementRoute(command.request, PlannerId)
        val result = runImmediate {
            coordinator.executePending(TestPlanner { RoutePlanningResult.Success(listOf(replacement)) })
        }
        assertIs<RerouteApplyDecision.Replaced>(result)
        assertEquals(replacement, coordinator.activeRoute)
    }
}

private class TestPlanner(
    private val action: suspend (RouteRequest) -> RoutePlanningResult,
) : RoutePlannerPort {
    override val descriptor: PluginDescriptor = PluginDescriptor(
        id = PlannerId,
        implementationVersion = "0.1.0",
        contractVersion = 1,
        capabilities = setOf(RoutingCapabilities.Plan),
        supportedPlatforms = setOf(KnownPlatforms.Jvm),
    )

    override suspend fun plan(request: RouteRequest): RoutePlanningResult = action(request)
}

private fun oldRoute(): RoutePlan {
    val a = GeoPoint(0.0, 0.0)
    val b = GeoPoint(0.0, 0.01)
    val d = GeoPoint(0.0, 0.02)
    return RoutePlan(
        id = RouteId("old-reroute-test-v0"),
        geometry = listOf(a, b, d),
        legs = listOf(RouteLeg(0, 2, a, d, 2_000L, 120L, emptyList())),
        distanceMeters = 2_000L,
        durationSeconds = 120L,
        provenance = RouteProvenance(PluginId("org.traveldna.old-route-provider")),
    )
}

private fun replacementRoute(request: RouteRequest, providerId: PluginId): RoutePlan {
    val middle = GeoPoint(0.005, 0.016)
    return RoutePlan(
        id = RouteId("replacement-reroute-test-v0"),
        geometry = listOf(request.origin, middle, request.destination),
        legs = listOf(
            RouteLeg(0, 2, request.origin, request.destination, 1_500L, 90L, emptyList()),
        ),
        distanceMeters = 1_500L,
        durationSeconds = 90L,
        provenance = RouteProvenance(providerId),
    )
}

private fun confirmation(route: RoutePlan): OffRouteState.Confirmed {
    val first = observation(route, 10L, 1_000L)
    val confirmation = observation(route, 12L, 4_000L)
    return OffRouteState.Confirmed(
        episodeId = OffRouteEpisodeId(1L),
        firstSuspiciousObservation = first,
        confirmationObservation = confirmation,
        suspiciousCount = 3,
        suspiciousDurationMillis = 3_000L,
    )
}

private fun observation(route: RoutePlan, sequence: Long, time: Long): OffRouteObservation =
    OffRouteObservation(
        routeId = route.id,
        sample = LocationSample(
            sequence = LocationSequence(sequence),
            monotonicTime = MonotonicInstant(time),
            position = confirmationPosition,
            horizontalAccuracyMeters = 5.0,
            origin = LocationSampleOrigin.Replay,
        ),
        evidence = OffRouteEvidence.Suspicious(OffRouteEvidenceReason.MissedExpectedManeuver),
    )

private fun failureError(): RoutePlanningError = RoutePlanningError(
    code = RoutePlanningErrorCode.ProviderUnavailable,
    message = "fixture provider unavailable",
    retryable = true,
)

private fun <T> runImmediate(block: suspend () -> T): T {
    var outcome: Result<T>? = null
    block.startCoroutine(object : Continuation<T> {
        override val context = EmptyCoroutineContext
        override fun resumeWith(result: Result<T>) {
            outcome = result
        }
    })
    return checkNotNull(outcome) { "test planner unexpectedly suspended" }.getOrThrow()
}

private val confirmationPosition = GeoPoint(0.004, 0.013)
private val PlannerId = PluginId("org.traveldna.reroute-test-planner")
