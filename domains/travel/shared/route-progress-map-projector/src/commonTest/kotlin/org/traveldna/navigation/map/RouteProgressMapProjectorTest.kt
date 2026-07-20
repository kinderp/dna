package org.traveldna.navigation.map

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.MapSceneId
import org.traveldna.map.contracts.RouteOverlay
import org.traveldna.map.contracts.RouteOverlayRole
import org.traveldna.navigation.contracts.MatchConfidence
import org.traveldna.navigation.contracts.MatchedRoutePosition
import org.traveldna.navigation.contracts.RouteCoordinate
import org.traveldna.navigation.contracts.RouteProgressSnapshot
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RouteProvenance

class RouteProgressMapProjectorTest {
    @Test
    fun bindsCanonicalGeometryOnceAndEmitsCompactProgress() {
        val route = route(RouteId("projected-progress-route-v0"))
        val overlay = overlay(route.id, route.geometry)
        val binding = RouteProgressMapProjector.bind(
            sceneId = MapSceneId("scene.progress-v0"),
            overlay = overlay,
            route = route,
        )

        val delta = RouteProgressMapProjector.project(binding, snapshot(route.id))

        assertEquals(overlay.id, delta.routeOverlayId)
        assertEquals(1, delta.progress.completedGeometryIndex)
        assertEquals(0.25, delta.progress.fractionToNext)
    }

    @Test
    fun bindingRejectsAnotherRouteIdOrDifferentGeometryEvenWithTheSameLength() {
        val route = route(RouteId("binding-route-v0"))
        assertFailsWith<IllegalArgumentException> {
            RouteProgressMapProjector.bind(
                sceneId = MapSceneId("scene.progress-v0"),
                overlay = overlay(RouteId("other-route-v0"), route.geometry),
                route = route,
            )
        }
        val altered = listOf(route.geometry[0], GeoPoint(0.0, 0.02), route.geometry[2])
        assertFailsWith<IllegalArgumentException> {
            RouteProgressMapProjector.bind(
                sceneId = MapSceneId("scene.progress-v0"),
                overlay = overlay(route.id, altered),
                route = route,
            )
        }
    }

    @Test
    fun projectRejectsASnapshotForAnotherBoundRoute() {
        val route = route(RouteId("bound-route-v0"))
        val binding = RouteProgressMapProjector.bind(
            sceneId = MapSceneId("scene.progress-v0"),
            overlay = overlay(route.id, route.geometry),
            route = route,
        )
        assertFailsWith<IllegalArgumentException> {
            RouteProgressMapProjector.project(binding, snapshot(RouteId("snapshot-route-v0")))
        }
    }

    private fun route(routeId: RouteId): RoutePlan {
        val geometry = listOf(GeoPoint(0.0, 0.0), GeoPoint(0.0, 0.01), GeoPoint(0.01, 0.01))
        return RoutePlan(
            id = routeId,
            geometry = geometry,
            legs = listOf(
                RouteLeg(
                    geometryStartIndex = 0,
                    geometryEndIndex = 2,
                    origin = geometry.first(),
                    destination = geometry.last(),
                    distanceMeters = 2_000L,
                    durationSeconds = 120L,
                    maneuvers = emptyList(),
                ),
            ),
            distanceMeters = 2_000L,
            durationSeconds = 120L,
            provenance = RouteProvenance(PluginId("org.traveldna.progress-projector-test")),
        )
    }

    private fun overlay(routeId: RouteId, geometry: List<GeoPoint>): RouteOverlay = RouteOverlay(
        id = MapItemId("route.progress"),
        routeId = routeId,
        geometry = geometry,
        role = RouteOverlayRole.Primary,
    )

    private fun snapshot(routeId: RouteId): RouteProgressSnapshot = RouteProgressSnapshot(
        position = MatchedRoutePosition(
            routeId = routeId,
            sampleSequence = LocationSequence(1),
            monotonicTime = MonotonicInstant(1_000L),
            coordinate = RouteCoordinate(1, 0.25),
            lateralDistanceMeters = 1.0,
            confidence = MatchConfidence.High,
        ),
        activeLegIndex = 0,
        upcomingManeuver = null,
        arrived = false,
    )
}
