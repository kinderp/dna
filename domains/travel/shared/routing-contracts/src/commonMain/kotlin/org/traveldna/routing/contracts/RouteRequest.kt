package org.traveldna.routing.contracts

enum class RoutingProfile {
    Driving,
    Camper,
    Motorcycle,
    Cycling,
    Walking,
}

/** Immutable snapshot of one provider-neutral route request. */
class RouteRequest(
    val origin: GeoPoint,
    val destination: GeoPoint,
    waypoints: List<GeoPoint> = emptyList(),
    val profile: RoutingProfile = RoutingProfile.Driving,
    val requestedAlternatives: Int = 1,
) {
    val waypoints: List<GeoPoint> = waypoints.toList()

    init {
        require(requestedAlternatives in 1..MaxAlternatives) {
            "requested alternatives must be within [1, $MaxAlternatives]"
        }
        require(this.waypoints.size <= MaxWaypoints) {
            "a route request may contain at most $MaxWaypoints waypoints"
        }
        val points = stops
        require(points.zipWithNext().none { (first, second) -> first == second }) {
            "consecutive route points must differ"
        }
    }

    val stops: List<GeoPoint> get() = listOf(origin) + waypoints + destination

    override fun equals(other: Any?): Boolean =
        other is RouteRequest &&
            origin == other.origin &&
            destination == other.destination &&
            waypoints == other.waypoints &&
            profile == other.profile &&
            requestedAlternatives == other.requestedAlternatives

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + destination.hashCode()
        result = 31 * result + waypoints.hashCode()
        result = 31 * result + profile.hashCode()
        result = 31 * result + requestedAlternatives
        return result
    }

    override fun toString(): String =
        "RouteRequest(origin=$origin, destination=$destination, waypoints=$waypoints, " +
            "profile=$profile, requestedAlternatives=$requestedAlternatives)"

    companion object {
        const val MaxAlternatives: Int = 3
        const val MaxWaypoints: Int = 32
    }
}
