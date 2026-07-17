package org.traveldna.map.fake

import org.traveldna.map.contracts.MapCamera
import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.MapPointOverlay
import org.traveldna.map.contracts.MapPointRole
import org.traveldna.map.contracts.MapScene
import org.traveldna.map.contracts.MapSceneId
import org.traveldna.map.contracts.RouteOverlayProjector
import org.traveldna.routing.contracts.RoutePlan

object FakeMapFixtures {
    fun scene(route: RoutePlan): MapScene = MapScene(
        id = MapSceneId("reference-map-scene-v0"),
        camera = MapCamera(
            center = route.origin,
            zoomLevel = 12.0,
            bearingDegrees = 0.0,
            pitchDegrees = 35.0,
        ),
        routes = listOf(
            RouteOverlayProjector.project(
                route = route,
                itemId = MapItemId("route.primary"),
            ),
        ),
        points = listOf(
            MapPointOverlay(
                id = MapItemId("place.reference-stop"),
                point = route.destination,
                role = MapPointRole.Place,
                label = "Reference stop",
            ),
        ),
    )
}
