package org.traveldna.navigation.offroute

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.navigation.offroute.contracts.OffRouteDecision
import org.traveldna.navigation.offroute.contracts.OffRouteEvidence
import org.traveldna.navigation.offroute.contracts.OffRouteEvidenceReason
import org.traveldna.navigation.offroute.contracts.OffRouteObservation
import org.traveldna.navigation.offroute.contracts.OffRoutePolicy
import org.traveldna.navigation.offroute.contracts.OffRouteRejectionReason
import org.traveldna.navigation.offroute.contracts.OffRouteState
import org.traveldna.navigation.offroute.contracts.OffRouteTransition
import org.traveldna.routing.contracts.RouteId

class OffRouteTrackerTest {
    @Test
    fun falseAlarmRecoversBeforeConfirmation() {
        val tracker = OffRouteTracker(RouteIdValue, OffRoutePolicy(3, 2_000L))
        val started = assertIs<OffRouteDecision.Accepted>(
            tracker.accept(observation(0, 1_000L, suspicious())),
        )
        assertEquals(OffRouteTransition.SuspicionStarted, started.transition)

        val recovered = assertIs<OffRouteDecision.Accepted>(
            tracker.accept(observation(1, 1_500L, OffRouteEvidence.OnRoute)),
        )
        assertEquals(OffRouteTransition.Recovered, recovered.transition)
        assertSame(OffRouteState.OnRoute, tracker.state)
    }

    @Test
    fun confirmationRequiresCountAndDurationWhileIndeterminateHoldsState() {
        val tracker = OffRouteTracker(RouteIdValue, OffRoutePolicy(3, 2_000L))
        tracker.accept(observation(0, 1_000L, suspicious()))
        val countOnly = assertIs<OffRouteDecision.Accepted>(
            tracker.accept(observation(1, 1_500L, suspicious())),
        )
        assertEquals(OffRouteTransition.SuspicionContinued, countOnly.transition)

        val held = assertIs<OffRouteDecision.Accepted>(
            tracker.accept(observation(2, 2_000L, OffRouteEvidence.Indeterminate("fixture.no-signal"))),
        )
        assertEquals(OffRouteTransition.HeldIndeterminate, held.transition)
        assertEquals(2, assertIs<OffRouteState.Suspected>(tracker.state).suspiciousCount)

        val confirmed = assertIs<OffRouteDecision.Accepted>(
            tracker.accept(observation(3, 3_000L, suspicious())),
        )
        assertEquals(OffRouteTransition.ConfirmedNow, confirmed.transition)
        val state = assertIs<OffRouteState.Confirmed>(tracker.state)
        assertEquals(3, state.suspiciousCount)
        assertEquals(2_000L, state.suspiciousDurationMillis)

        val sticky = assertIs<OffRouteDecision.Accepted>(
            tracker.accept(observation(4, 4_000L, OffRouteEvidence.OnRoute)),
        )
        assertEquals(OffRouteTransition.StayedConfirmed, sticky.transition)
        assertEquals(state, tracker.state)
    }

    @Test
    fun rejectedObservationDoesNotMutateStateOrBaseline() {
        val tracker = OffRouteTracker(RouteIdValue)
        val accepted = observation(1, 1_000L, OffRouteEvidence.OnRoute)
        tracker.accept(accepted)
        val state = tracker.state

        val wrongRoute = assertIs<OffRouteDecision.Rejected>(
            tracker.accept(observation(2, 2_000L, suspicious(), RouteId("other-route-v0"))),
        )
        assertEquals(OffRouteRejectionReason.WrongRoute, wrongRoute.reason)

        val duplicate = assertIs<OffRouteDecision.Rejected>(
            tracker.accept(observation(1, 3_000L, suspicious())),
        )
        assertEquals(OffRouteRejectionReason.NonIncreasingSequence, duplicate.reason)

        val staleTime = assertIs<OffRouteDecision.Rejected>(
            tracker.accept(observation(2, 1_000L, suspicious())),
        )
        assertEquals(OffRouteRejectionReason.NonIncreasingMonotonicTime, staleTime.reason)
        assertSame(state, tracker.state)
        assertEquals(accepted, tracker.lastAcceptedObservation)
    }

    @Test
    fun inspectAndResetAreNonMutatingAndDeterministic() {
        val tracker = OffRouteTracker(RouteIdValue)
        val candidate = observation(0, 1_000L, suspicious())
        val inspected = assertIs<OffRouteDecision.Accepted>(tracker.inspect(candidate))
        assertEquals(OffRouteTransition.SuspicionStarted, inspected.transition)
        assertSame(OffRouteState.OnRoute, tracker.state)
        assertEquals(null, tracker.lastAcceptedObservation)

        tracker.accept(candidate)
        tracker.reset()
        assertSame(OffRouteState.OnRoute, tracker.state)
        assertEquals(null, tracker.lastAcceptedObservation)
        val restarted = assertIs<OffRouteDecision.Accepted>(tracker.accept(candidate))
        assertEquals(1L, assertIs<OffRouteState.Suspected>(restarted.state).episodeId.value)
    }
}

private fun suspicious(): OffRouteEvidence =
    OffRouteEvidence.Suspicious(OffRouteEvidenceReason.MissedExpectedManeuver)

private fun observation(
    sequence: Long,
    time: Long,
    evidence: OffRouteEvidence,
    routeId: RouteId = RouteIdValue,
): OffRouteObservation = OffRouteObservation(
    routeId = routeId,
    sample = LocationSample(
        sequence = LocationSequence(sequence),
        monotonicTime = MonotonicInstant(time),
        position = GeoPoint(0.0, sequence * 0.000_001),
        horizontalAccuracyMeters = 5.0,
        origin = LocationSampleOrigin.Replay,
    ),
    evidence = evidence,
)

private val RouteIdValue = RouteId("off-route-test-route-v0")
