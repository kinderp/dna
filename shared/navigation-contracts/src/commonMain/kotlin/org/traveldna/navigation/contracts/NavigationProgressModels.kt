package org.traveldna.navigation.contracts

import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.routing.contracts.ManeuverType
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteManeuver

/** Canonical position along one installed route geometry. */
class RouteCoordinate(
    val completedGeometryIndex: Int,
    fractionToNext: Double,
) : Comparable<RouteCoordinate> {
    val fractionToNext: Double = canonicalZero(fractionToNext)

    init {
        require(completedGeometryIndex >= 0) {
            "completed geometry index must be non-negative"
        }
        require(this.fractionToNext.isFinite() && this.fractionToNext in 0.0..<1.0) {
            "route-coordinate fraction must be finite and within [0, 1)"
        }
    }

    override fun compareTo(other: RouteCoordinate): Int {
        val indexComparison = completedGeometryIndex.compareTo(other.completedGeometryIndex)
        return if (indexComparison != 0) indexComparison else fractionToNext.compareTo(other.fractionToNext)
    }

    override fun equals(other: Any?): Boolean =
        other is RouteCoordinate &&
            completedGeometryIndex == other.completedGeometryIndex &&
            fractionToNext == other.fractionToNext

    override fun hashCode(): Int = 31 * completedGeometryIndex + fractionToNext.hashCode()

    override fun toString(): String =
        "RouteCoordinate(index=$completedGeometryIndex, fraction=$fractionToNext)"
}

enum class MatchConfidence {
    Low,
    Medium,
    High,
}

/**
 * Provider-neutral result of a map-matching stage.
 *
 * This type does not perform map matching. It states which route segment an
 * upstream matcher selected for one ordered location observation.
 */
class MatchedRoutePosition(
    val routeId: RouteId,
    val sampleSequence: LocationSequence,
    val monotonicTime: MonotonicInstant,
    val coordinate: RouteCoordinate,
    lateralDistanceMeters: Double,
    val confidence: MatchConfidence,
) {
    val lateralDistanceMeters: Double = canonicalZero(lateralDistanceMeters)

    init {
        require(
            this.lateralDistanceMeters.isFinite() &&
                this.lateralDistanceMeters in 0.0..MaxLateralDistanceMeters,
        ) {
            "lateral distance must be finite and within [0, $MaxLateralDistanceMeters] metres"
        }
    }

    override fun equals(other: Any?): Boolean =
        other is MatchedRoutePosition &&
            routeId == other.routeId &&
            sampleSequence == other.sampleSequence &&
            monotonicTime == other.monotonicTime &&
            coordinate == other.coordinate &&
            lateralDistanceMeters == other.lateralDistanceMeters &&
            confidence == other.confidence

    override fun hashCode(): Int {
        var result = routeId.hashCode()
        result = 31 * result + sampleSequence.hashCode()
        result = 31 * result + monotonicTime.hashCode()
        result = 31 * result + coordinate.hashCode()
        result = 31 * result + lateralDistanceMeters.hashCode()
        result = 31 * result + confidence.hashCode()
        return result
    }

    override fun toString(): String =
        "MatchedRoutePosition(routeId=$routeId, sequence=$sampleSequence, time=$monotonicTime, " +
            "coordinate=$coordinate, lateralDistanceMeters=$lateralDistanceMeters, confidence=$confidence)"

    companion object {
        const val MaxLateralDistanceMeters: Double = 100_000.0
    }
}

data class RouteManeuverCursor(
    val legIndex: Int,
    val maneuverIndex: Int,
    val maneuver: RouteManeuver,
) {
    init {
        require(legIndex >= 0) { "maneuver cursor leg index must be non-negative" }
        require(maneuverIndex >= 0) { "maneuver cursor index must be non-negative" }
    }
}

data class RouteProgressSnapshot(
    val position: MatchedRoutePosition,
    val activeLegIndex: Int,
    val upcomingManeuver: RouteManeuverCursor?,
    val arrived: Boolean,
) {
    init {
        require(activeLegIndex >= 0) { "active leg index must be non-negative" }
        require(upcomingManeuver == null || upcomingManeuver.legIndex >= activeLegIndex) {
            "upcoming maneuver cannot belong to a completed leg"
        }
        require(!arrived || upcomingManeuver == null || upcomingManeuver.maneuver.type == ManeuverType.Arrive) {
            "an arrived snapshot may expose only an Arrive maneuver"
        }
    }
}

enum class RouteProgressRejectionReason {
    RouteMismatch,
    GeometryIndexOutOfBounds,
    FinalPointHasFraction,
    NonIncreasingSequence,
    NonIncreasingMonotonicTime,
    RegressedAlongRoute,
}

sealed interface RouteProgressDecision {
    val position: MatchedRoutePosition

    data class Accepted(
        val snapshot: RouteProgressSnapshot,
    ) : RouteProgressDecision {
        override val position: MatchedRoutePosition get() = snapshot.position
    }

    data class Rejected(
        override val position: MatchedRoutePosition,
        val reason: RouteProgressRejectionReason,
        val previousAccepted: MatchedRoutePosition?,
    ) : RouteProgressDecision
}

private fun canonicalZero(value: Double): Double = if (value == 0.0) 0.0 else value
