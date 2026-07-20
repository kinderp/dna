package org.traveldna.navigation.progress

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.navigation.contracts.MatchConfidence
import org.traveldna.navigation.contracts.MatchedRoutePosition
import org.traveldna.navigation.contracts.RouteCoordinate
import org.traveldna.navigation.contracts.RouteProgressDecision
import org.traveldna.navigation.contracts.RouteProgressRejectionReason
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.ManeuverType
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RouteManeuver
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RouteProvenance

class RouteProgressTrackerTest {
    private val route = twoLegRoute()

    @Test
    fun acceptsStationaryUpdatesLegBoundaryAndArrival() {
        val tracker = RouteProgressTracker(route)

        val departure = accepted(tracker, position(0, 0, 0, 0.0))
        assertEquals(0, departure.activeLegIndex)
        assertEquals(ManeuverType.Depart, departure.upcomingManeuver?.maneuver?.type)
        assertEquals(false, departure.arrived)

        val stationary = accepted(tracker, position(1, 1_000, 0, 0.0))
        assertEquals(RouteCoordinate(0, 0.0), stationary.position.coordinate)

        val boundary = accepted(tracker, position(2, 2_000, 2, 0.0))
        assertEquals(1, boundary.activeLegIndex)
        assertEquals(1, boundary.upcomingManeuver?.legIndex)
        assertEquals(ManeuverType.Continue, boundary.upcomingManeuver?.maneuver?.type)

        val arrival = accepted(tracker, position(3, 3_000, 3, 0.0))
        assertEquals(1, arrival.activeLegIndex)
        assertEquals(ManeuverType.Arrive, arrival.upcomingManeuver?.maneuver?.type)
        assertEquals(true, arrival.arrived)
    }

    @Test
    fun arrivalPrefersArriveWhenAnotherFinalIndexManeuverComesFirst() {
        val tracker = RouteProgressTracker(route)
        val arrival = accepted(tracker, position(0, 0, 3, 0.0))

        assertEquals(true, arrival.arrived)
        assertEquals(ManeuverType.Arrive, arrival.upcomingManeuver?.maneuver?.type)
    }

    @Test
    fun rejectsRouteAndGeometryContractViolationsWithoutStateMutation() {
        val tracker = RouteProgressTracker(route)
        val initial = accepted(tracker, position(0, 0, 1, 0.5))

        assertRejected(
            tracker,
            position(1, 1_000, 1, 0.5, routeId = RouteId("other-route-v0")),
            RouteProgressRejectionReason.RouteMismatch,
        )
        assertRejected(
            tracker,
            position(1, 1_000, 99, 0.0),
            RouteProgressRejectionReason.GeometryIndexOutOfBounds,
        )
        assertRejected(
            tracker,
            position(1, 1_000, 3, 0.5),
            RouteProgressRejectionReason.FinalPointHasFraction,
        )
        assertEquals(initial, tracker.lastSnapshot)
    }

    @Test
    fun rejectsOrderingAndRegressionWithoutMovingAcceptedProgress() {
        val tracker = RouteProgressTracker(route)
        val initial = accepted(tracker, position(5, 5_000, 2, 0.5))

        assertRejected(
            tracker,
            position(5, 6_000, 2, 0.75),
            RouteProgressRejectionReason.NonIncreasingSequence,
        )
        assertRejected(
            tracker,
            position(6, 5_000, 2, 0.75),
            RouteProgressRejectionReason.NonIncreasingMonotonicTime,
        )
        assertRejected(
            tracker,
            position(6, 6_000, 1, 0.9),
            RouteProgressRejectionReason.RegressedAlongRoute,
        )
        assertRejected(
            tracker,
            position(6, 6_000, 2, 0.25),
            RouteProgressRejectionReason.RegressedAlongRoute,
        )
        assertEquals(initial, tracker.lastSnapshot)
    }

    @Test
    fun inspectDoesNotCommitAndResetAllowsAnEarlierNewSession() {
        val tracker = RouteProgressTracker(route)
        val inspected = tracker.inspect(position(10, 10_000, 2, 0.5))
        assertIs<RouteProgressDecision.Accepted>(inspected)
        assertNull(tracker.lastSnapshot)

        accepted(tracker, position(10, 10_000, 2, 0.5))
        assertRejected(
            tracker,
            position(11, 11_000, 1, 0.0),
            RouteProgressRejectionReason.RegressedAlongRoute,
        )

        tracker.reset()
        assertNull(tracker.lastSnapshot)
        val restarted = accepted(tracker, position(0, 0, 0, 0.0))
        assertEquals(RouteCoordinate(0, 0.0), restarted.position.coordinate)
    }

    private fun accepted(
        tracker: RouteProgressTracker,
        position: MatchedRoutePosition,
    ) = assertIs<RouteProgressDecision.Accepted>(tracker.accept(position)).snapshot

    private fun assertRejected(
        tracker: RouteProgressTracker,
        position: MatchedRoutePosition,
        reason: RouteProgressRejectionReason,
    ) {
        val decision = assertIs<RouteProgressDecision.Rejected>(tracker.accept(position))
        assertEquals(reason, decision.reason)
    }

    private fun position(
        sequence: Long,
        time: Long,
        index: Int,
        fraction: Double,
        routeId: RouteId = route.id,
    ): MatchedRoutePosition = MatchedRoutePosition(
        routeId = routeId,
        sampleSequence = LocationSequence(sequence),
        monotonicTime = MonotonicInstant(time),
        coordinate = RouteCoordinate(index, fraction),
        lateralDistanceMeters = 2.0,
        confidence = MatchConfidence.High,
    )
}

private fun twoLegRoute(): RoutePlan {
    val a = GeoPoint(0.0, 0.0)
    val b = GeoPoint(0.0, 0.01)
    val c = GeoPoint(0.01, 0.01)
    val d = GeoPoint(0.01, 0.02)
    val geometry = listOf(a, b, c, d)
    return RoutePlan(
        id = RouteId("progress-test-route-v0"),
        geometry = geometry,
        legs = listOf(
            RouteLeg(
                geometryStartIndex = 0,
                geometryEndIndex = 2,
                origin = a,
                destination = c,
                distanceMeters = 2_400L,
                durationSeconds = 160L,
                maneuvers = listOf(
                    RouteManeuver(0, ManeuverType.Depart, a, "Depart"),
                    RouteManeuver(1, ManeuverType.TurnRight, b, "Turn right"),
                    RouteManeuver(2, ManeuverType.KeepRight, c, "Finish first leg"),
                ),
            ),
            RouteLeg(
                geometryStartIndex = 2,
                geometryEndIndex = 3,
                origin = c,
                destination = d,
                distanceMeters = 1_200L,
                durationSeconds = 80L,
                maneuvers = listOf(
                    RouteManeuver(2, ManeuverType.Continue, c, "Continue second leg"),
                    RouteManeuver(3, ManeuverType.KeepRight, d, "Prepare to arrive"),
                    RouteManeuver(3, ManeuverType.Arrive, d, "Arrive"),
                ),
            ),
        ),
        distanceMeters = 3_600L,
        durationSeconds = 240L,
        provenance = RouteProvenance(PluginId("org.traveldna.progress-test")),
    )
}
