package org.traveldna.navigation.progress

import org.traveldna.navigation.contracts.MatchedRoutePosition
import org.traveldna.navigation.contracts.RouteManeuverCursor
import org.traveldna.navigation.contracts.RouteProgressDecision
import org.traveldna.navigation.contracts.RouteProgressRejectionReason
import org.traveldna.navigation.contracts.RouteProgressSnapshot
import org.traveldna.routing.contracts.ManeuverType
import org.traveldna.routing.contracts.RoutePlan

/**
 * Single-owner progress tracker bound to one immutable canonical route.
 *
 * It receives positions already matched by an upstream component. Equal route
 * coordinates are allowed for a stationary vehicle; backwards coordinates are
 * explicit rejections and never mutate accepted state.
 *
 * Leg and maneuver lookup use binary search, avoiding a full route scan for
 * every accepted sample. Arrival-maneuver selection is prepared once.
 */
class RouteProgressTracker(
    val route: RoutePlan,
) {
    private val maneuverCursors: List<RouteManeuverCursor> = route.legs.flatMapIndexed { legIndex, leg ->
        leg.maneuvers.mapIndexed { maneuverIndex, maneuver ->
            RouteManeuverCursor(legIndex, maneuverIndex, maneuver)
        }
    }
    private val arrivalManeuver: RouteManeuverCursor? = maneuverCursors.firstOrNull { cursor ->
        cursor.legIndex == route.legs.lastIndex &&
            cursor.maneuver.geometryIndex == route.geometry.lastIndex &&
            cursor.maneuver.type == ManeuverType.Arrive
    }

    var lastSnapshot: RouteProgressSnapshot? = null
        private set

    fun inspect(position: MatchedRoutePosition): RouteProgressDecision {
        val previous = lastSnapshot?.position
        rejection(position, previous)?.let { return it }
        return RouteProgressDecision.Accepted(buildSnapshot(position))
    }

    fun accept(position: MatchedRoutePosition): RouteProgressDecision {
        val decision = inspect(position)
        if (decision is RouteProgressDecision.Accepted) {
            lastSnapshot = decision.snapshot
        }
        return decision
    }

    fun reset() {
        lastSnapshot = null
    }

    private fun rejection(
        position: MatchedRoutePosition,
        previous: MatchedRoutePosition?,
    ): RouteProgressDecision.Rejected? {
        if (position.routeId != route.id) {
            return rejected(position, RouteProgressRejectionReason.RouteMismatch, previous)
        }
        val coordinate = position.coordinate
        if (coordinate.completedGeometryIndex !in route.geometry.indices) {
            return rejected(position, RouteProgressRejectionReason.GeometryIndexOutOfBounds, previous)
        }
        if (
            coordinate.completedGeometryIndex == route.geometry.lastIndex &&
            coordinate.fractionToNext != 0.0
        ) {
            return rejected(position, RouteProgressRejectionReason.FinalPointHasFraction, previous)
        }
        if (previous != null) {
            if (position.sampleSequence <= previous.sampleSequence) {
                return rejected(position, RouteProgressRejectionReason.NonIncreasingSequence, previous)
            }
            if (position.monotonicTime <= previous.monotonicTime) {
                return rejected(position, RouteProgressRejectionReason.NonIncreasingMonotonicTime, previous)
            }
            if (position.coordinate < previous.coordinate) {
                return rejected(position, RouteProgressRejectionReason.RegressedAlongRoute, previous)
            }
        }
        return null
    }

    private fun buildSnapshot(position: MatchedRoutePosition): RouteProgressSnapshot {
        val coordinate = position.coordinate
        val arrived = coordinate.completedGeometryIndex == route.geometry.lastIndex
        val activeLegIndex = findActiveLegIndex(coordinate.completedGeometryIndex, arrived)
        val upcomingManeuver = findUpcomingManeuver(
            activeLegIndex = activeLegIndex,
            completedGeometryIndex = coordinate.completedGeometryIndex,
            fractionToNext = coordinate.fractionToNext,
            arrived = arrived,
        )
        return RouteProgressSnapshot(
            position = position,
            activeLegIndex = activeLegIndex,
            upcomingManeuver = upcomingManeuver,
            arrived = arrived,
        )
    }

    private fun findActiveLegIndex(completedGeometryIndex: Int, arrived: Boolean): Int {
        if (arrived) return route.legs.lastIndex

        var low = 0
        var high = route.legs.lastIndex
        while (low < high) {
            val middle = low + (high - low) / 2
            if (completedGeometryIndex < route.legs[middle].geometryEndIndex) {
                high = middle
            } else {
                low = middle + 1
            }
        }
        check(completedGeometryIndex < route.legs[low].geometryEndIndex) {
            "route coordinate is not covered by any active leg"
        }
        return low
    }

    private fun findUpcomingManeuver(
        activeLegIndex: Int,
        completedGeometryIndex: Int,
        fractionToNext: Double,
        arrived: Boolean,
    ): RouteManeuverCursor? {
        if (arrived) return arrivalManeuver
        if (maneuverCursors.isEmpty()) return null
        val firstEligibleGeometryIndex = if (fractionToNext == 0.0) {
            completedGeometryIndex
        } else {
            completedGeometryIndex + 1
        }

        var low = 0
        var high = maneuverCursors.size
        while (low < high) {
            val middle = low + (high - low) / 2
            if (maneuverCursors[middle].maneuver.geometryIndex < firstEligibleGeometryIndex) {
                low = middle + 1
            } else {
                high = middle
            }
        }
        var candidateIndex = low
        while (
            candidateIndex < maneuverCursors.size &&
            maneuverCursors[candidateIndex].legIndex < activeLegIndex
        ) {
            candidateIndex += 1
        }
        return maneuverCursors.getOrNull(candidateIndex)
    }

    private fun rejected(
        position: MatchedRoutePosition,
        reason: RouteProgressRejectionReason,
        previous: MatchedRoutePosition?,
    ): RouteProgressDecision.Rejected = RouteProgressDecision.Rejected(
        position = position,
        reason = reason,
        previousAccepted = previous,
    )
}
