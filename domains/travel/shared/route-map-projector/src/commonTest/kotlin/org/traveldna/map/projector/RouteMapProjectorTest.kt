package org.traveldna.map.projector

import kotlin.test.Test
import kotlin.test.assertEquals
import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.RouteOverlayRole
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.GeoPoint
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RouteProvenance

class RouteMapProjectorTest {
    @Test
    fun projectsCanonicalRouteWithoutProviderTypes() {
        val a = GeoPoint(0.0, 0.0)
        val b = GeoPoint(0.0, 0.01)
        val route = RoutePlan(
            id = RouteId("projector-route-v0"),
            geometry = listOf(a, b),
            legs = listOf(
                RouteLeg(0, 1, a, b, 1_200L, 80L, emptyList()),
            ),
            distanceMeters = 1_200L,
            durationSeconds = 80L,
            provenance = RouteProvenance(PluginId("org.traveldna.projector-test")),
        )

        val overlay = RouteMapProjector.project(
            route = route,
            overlayId = MapItemId("route.projected"),
            role = RouteOverlayRole.Primary,
        )

        assertEquals(route.id, overlay.routeId)
        assertEquals(route.geometry, overlay.geometry)
        assertEquals(RouteOverlayRole.Primary, overlay.role)
    }
}
