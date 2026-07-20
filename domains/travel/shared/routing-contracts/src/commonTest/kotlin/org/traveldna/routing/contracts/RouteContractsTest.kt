package org.traveldna.routing.contracts

import org.traveldna.plugin.sdk.PluginId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RouteContractsTest {
    private val providerId = PluginId("org.traveldna.contract-test")
    private val a = GeoPoint(0.0, 0.0)
    private val b = GeoPoint(0.0, 0.01)
    private val c = GeoPoint(0.01, 0.01)

    @Test
    fun rejectsInvalidCoordinatesAndRequests() {
        assertFailsWith<IllegalArgumentException> { GeoPoint(Double.NaN, 0.0) }
        assertFailsWith<IllegalArgumentException> { GeoPoint(0.0, 181.0) }
        assertFailsWith<IllegalArgumentException> {
            RouteRequest(origin = a, destination = a)
        }
        assertFailsWith<IllegalArgumentException> {
            RouteRequest(origin = a, destination = c, requestedAlternatives = 4)
        }
        assertFailsWith<IllegalArgumentException> {
            RouteRequest(
                origin = a,
                destination = c,
                waypoints = List(RouteRequest.MaxWaypoints + 1) { index ->
                    GeoPoint(0.1 + index * 0.001, 0.1)
                },
            )
        }
    }

    @Test
    fun validatesGeometryLegAndManeuverAlignment() {
        val route = validRoute()
        assertEquals(a, route.origin)
        assertEquals(c, route.destination)
        assertEquals(2_400L, route.distanceMeters)

        assertFailsWith<IllegalArgumentException> {
            RoutePlan(
                id = route.id,
                geometry = route.geometry,
                legs = listOf(
                    RouteLeg(
                        geometryStartIndex = 0,
                        geometryEndIndex = 2,
                        origin = a,
                        destination = b,
                        distanceMeters = 2_400L,
                        durationSeconds = 160L,
                        maneuvers = route.legs.single().maneuvers,
                    ),
                ),
                distanceMeters = 2_400L,
                durationSeconds = 160L,
                provenance = route.provenance,
            )
        }
    }

    @Test
    fun validatesCheckedRouteTotals() {
        val route = validRoute()
        assertFailsWith<IllegalArgumentException> {
            RoutePlan(
                id = route.id,
                geometry = route.geometry,
                legs = route.legs,
                distanceMeters = 2_401L,
                durationSeconds = route.durationSeconds,
                provenance = route.provenance,
            )
        }
    }

    @Test
    fun snapshotsMutableCollectionsAtContractBoundaries() {
        val mutableWaypoints = mutableListOf(b)
        val request = RouteRequest(origin = a, destination = c, waypoints = mutableWaypoints)
        mutableWaypoints.clear()
        assertEquals(listOf(b), request.waypoints)

        val mutableManeuvers = mutableListOf(
            RouteManeuver(0, ManeuverType.Depart, a, "Depart"),
            RouteManeuver(1, ManeuverType.TurnLeft, b, "Turn left"),
            RouteManeuver(2, ManeuverType.Arrive, c, "Arrive"),
        )
        val leg = RouteLeg(0, 2, a, c, 2_400L, 160L, mutableManeuvers)
        mutableManeuvers.clear()
        assertEquals(3, leg.maneuvers.size)

        val mutableGeometry = mutableListOf(a, b, c)
        val mutableLegs = mutableListOf(leg)
        val mutableSources = mutableSetOf("synthetic.test")
        val route = RoutePlan(
            id = RouteId("snapshot-route-v0"),
            geometry = mutableGeometry,
            legs = mutableLegs,
            distanceMeters = 2_400L,
            durationSeconds = 160L,
            provenance = RouteProvenance(providerId, dataSources = mutableSources),
        )
        mutableGeometry.clear()
        mutableLegs.clear()
        mutableSources.clear()
        assertEquals(3, route.geometry.size)
        assertEquals(1, route.legs.size)
        assertEquals(setOf("synthetic.test"), route.provenance.dataSources)

        val mutableRoutes = mutableListOf(route)
        val success = RoutePlanningResult.Success(mutableRoutes)
        mutableRoutes.clear()
        assertEquals(listOf(route), success.routes)
    }

    @Test
    fun boundedResponseAndProvenanceMetadataAreEnforced() {
        val route = validRoute()
        val alternatives = (0..RouteRequest.MaxAlternatives).map { index ->
            reidentified(route, "route-$index")
        }
        assertFailsWith<IllegalArgumentException> {
            RoutePlanningResult.Success(alternatives)
        }
        assertFailsWith<IllegalArgumentException> {
            RouteProvenance(
                providerId = providerId,
                dataSources = (0..RouteProvenance.MaxDataSources).map { "source.$it" }.toSet(),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            RoutePlanningError(
                code = RoutePlanningErrorCode.Internal,
                message = "Internal",
                retryable = false,
                providerDiagnosticCode = "x".repeat(129),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            RouteManeuver(
                geometryIndex = 0,
                type = ManeuverType.Continue,
                location = a,
                instruction = "Continue",
                roadName = "r".repeat(RouteManeuver.MaxRoadNameLength + 1),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            RouteManeuver(
                geometryIndex = 0,
                type = ManeuverType.ExitRight,
                location = a,
                instruction = "Take the exit",
                exitNumber = "e".repeat(RouteManeuver.MaxExitNumberLength + 1),
            )
        }
    }

    @Test
    fun successRequiresUniqueRouteIds() {
        val route = validRoute()
        assertFailsWith<IllegalArgumentException> {
            RoutePlanningResult.Success(listOf(route, route))
        }
    }

    @Test
    fun routeMustMatchEveryRequestedWaypointSegment() {
        val request = RouteRequest(origin = a, destination = c, waypoints = listOf(b))
        assertFailsWith<IllegalArgumentException> {
            validRoute().requireMatches(request)
        }

        val geometry = listOf(a, b, c)
        val first = RouteLeg(
            geometryStartIndex = 0,
            geometryEndIndex = 1,
            origin = a,
            destination = b,
            distanceMeters = 1_200L,
            durationSeconds = 80L,
            maneuvers = listOf(
                RouteManeuver(0, ManeuverType.Depart, a, "Depart"),
                RouteManeuver(1, ManeuverType.Continue, b, "Reach waypoint"),
            ),
        )
        val second = RouteLeg(
            geometryStartIndex = 1,
            geometryEndIndex = 2,
            origin = b,
            destination = c,
            distanceMeters = 1_200L,
            durationSeconds = 80L,
            maneuvers = listOf(
                RouteManeuver(1, ManeuverType.Continue, b, "Leave waypoint"),
                RouteManeuver(2, ManeuverType.Arrive, c, "Arrive"),
            ),
        )
        val route = RoutePlan(
            id = RouteId("waypoint-route-v0"),
            geometry = geometry,
            legs = listOf(first, second),
            distanceMeters = 2_400L,
            durationSeconds = 160L,
            provenance = RouteProvenance(providerId),
        )
        assertEquals(route, route.requireMatches(request))
    }

    @Test
    fun noRouteCannotBeMarkedRetryable() {
        assertFailsWith<IllegalArgumentException> {
            RoutePlanningError(
                code = RoutePlanningErrorCode.NoRoute,
                message = "No route",
                retryable = true,
            )
        }
    }

    private fun validRoute(): RoutePlan {
        val geometry = listOf(a, b, c)
        val maneuvers = listOf(
            RouteManeuver(0, ManeuverType.Depart, a, "Depart"),
            RouteManeuver(1, ManeuverType.TurnLeft, b, "Turn left"),
            RouteManeuver(2, ManeuverType.Arrive, c, "Arrive"),
        )
        val leg = RouteLeg(
            geometryStartIndex = 0,
            geometryEndIndex = 2,
            origin = a,
            destination = c,
            distanceMeters = 2_400L,
            durationSeconds = 160L,
            maneuvers = maneuvers,
        )
        return RoutePlan(
            id = RouteId("contract-route-v0"),
            geometry = geometry,
            legs = listOf(leg),
            distanceMeters = 2_400L,
            durationSeconds = 160L,
            provenance = RouteProvenance(providerId),
        )
    }

    private fun reidentified(route: RoutePlan, id: String): RoutePlan = RoutePlan(
        id = RouteId(id),
        geometry = route.geometry,
        legs = route.legs,
        distanceMeters = route.distanceMeters,
        durationSeconds = route.durationSeconds,
        provenance = route.provenance,
    )
}
