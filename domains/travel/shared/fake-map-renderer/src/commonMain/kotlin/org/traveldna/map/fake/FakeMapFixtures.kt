package org.traveldna.map.fake

import org.traveldna.map.contracts.MapCamera
import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.MapLocationSemantics
import org.traveldna.map.contracts.MapMarker
import org.traveldna.map.contracts.MapMarkerKind
import org.traveldna.map.contracts.MapScene
import org.traveldna.map.contracts.MapSceneId
import org.traveldna.map.contracts.RouteOverlay
import org.traveldna.map.contracts.RouteOverlayRole
import org.traveldna.routing.contracts.RoutePlan

/** Canonical scene fixture used by examples that do not need the projector module. */
object FakeMapFixtures {
    fun scene(route: RoutePlan): MapScene = MapScene(
        id = MapSceneId("reference-map-scene-v0"),
        camera = MapCamera(
            center = route.origin,
            zoom = 12.0,
            bearingDegrees = 0.0,
            pitchDegrees = 35.0,
        ),
        routeOverlays = listOf(
            RouteOverlay(
                id = MapItemId("route.primary"),
                routeId = route.id,
                geometry = route.geometry,
                role = RouteOverlayRole.Primary,
            ),
        ),
        markers = listOf(
            MapMarker(
                id = MapItemId("place.reference-stop"),
                position = route.destination,
                kind = MapMarkerKind.Place,
                locationSemantics = MapLocationSemantics.PublicPlace,
                label = "Reference stop",
            ),
        ),
    )
}
