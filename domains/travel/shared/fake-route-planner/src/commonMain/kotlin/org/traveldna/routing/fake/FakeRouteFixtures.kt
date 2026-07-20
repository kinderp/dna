package org.traveldna.routing.fake

import org.traveldna.routing.contracts.GeoPoint
import org.traveldna.routing.contracts.ManeuverType
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RouteManeuver
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RouteProvenance
import org.traveldna.routing.contracts.RouteRequest

object FakeRouteFixtures {
    val A = GeoPoint(0.0, 0.0)
    val C = GeoPoint(0.01, 0.0)
    val D = GeoPoint(0.01, 0.01)
    val E = GeoPoint(0.01, 0.02)
    val Unreachable = GeoPoint(1.0, 1.0)

    val ReferenceRequest = RouteRequest(origin = A, destination = E)
    val UnroutableRequest = RouteRequest(origin = A, destination = Unreachable)

    val ReferenceRoute: RoutePlan = run {
        val geometry = listOf(A, C, D, E)
        val maneuvers = listOf(
            RouteManeuver(0, ManeuverType.Depart, A, "Depart toward C"),
            RouteManeuver(1, ManeuverType.TurnRight, C, "Turn right toward D"),
            RouteManeuver(2, ManeuverType.Continue, D, "Continue toward E"),
            RouteManeuver(3, ManeuverType.Arrive, E, "Arrive at destination"),
        )
        RoutePlan(
            id = RouteId("reference-route-v0"),
            geometry = geometry,
            legs = listOf(
                RouteLeg(
                    geometryStartIndex = 0,
                    geometryEndIndex = 3,
                    origin = A,
                    destination = E,
                    distanceMeters = 3_600L,
                    durationSeconds = 240L,
                    maneuvers = maneuvers,
                ),
            ),
            distanceMeters = 3_600L,
            durationSeconds = 240L,
            provenance = RouteProvenance(
                providerId = FakeRoutePlanner.Id,
                providerRouteId = "reference-network-v0",
                dataSources = setOf("synthetic.reference-network-v0"),
            ),
        )
    }

    fun planner(): FakeRoutePlanner = FakeRoutePlanner(
        catalog = mapOf(ReferenceRequest to listOf(ReferenceRoute)),
    )
}
