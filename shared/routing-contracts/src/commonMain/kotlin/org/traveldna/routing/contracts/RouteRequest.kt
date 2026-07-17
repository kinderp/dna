package org.traveldna.routing.contracts

enum class RoutingProfile {
    Driving,
    Camper,
    Motorcycle,
    Cycling,
    Walking,
}

data class RouteRequest(
    val origin: GeoPoint,
    val destination: GeoPoint,
    val waypoints: List<GeoPoint> = emptyList(),
    val profile: RoutingProfile = RoutingProfile.Driving,
    val requestedAlternatives: Int = 1,
) {
    init {
        require(requestedAlternatives in 1..3) {
            "requested alternatives must be within [1, 3]"
        }
        val points = listOf(origin) + waypoints + destination
        require(points.zipWithNext().none { (first, second) -> first == second }) {
            "consecutive route points must differ"
        }
    }
}
