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
    }

    @Test
    fun validatesGeometryLegAndManeuverAlignment() {
        val route = validRoute()
        assertEquals(a, route.origin)
        assertEquals(c, route.destination)
        assertEquals(2_400L, route.distanceMeters)

        assertFailsWith<IllegalArgumentException> {
            route.copy(
                legs = listOf(route.legs.single().copy(destination = b)),
            )
        }
    }

    @Test
    fun validatesCheckedRouteTotals() {
        assertFailsWith<IllegalArgumentException> {
            validRoute().copy(distanceMeters = 2_401L)
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
}
