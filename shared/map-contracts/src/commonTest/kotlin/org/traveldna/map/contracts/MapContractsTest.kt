package org.traveldna.map.contracts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.traveldna.routing.contracts.GeoPoint
import org.traveldna.routing.contracts.RouteId

class MapContractsTest {
    private val a = GeoPoint(0.0, 0.0)
    private val b = GeoPoint(0.0, 0.01)

    @Test
    fun validatesCameraAndPrivacySemantics() {
        assertFailsWith<IllegalArgumentException> {
            MapCamera(center = a, zoom = 25.0)
        }
        assertFailsWith<IllegalArgumentException> {
            MapCamera(center = a, zoom = 10.0, bearingDegrees = 360.0)
        }
        assertFailsWith<IllegalArgumentException> {
            MapMarker(
                id = MapItemId("companion.invalid"),
                position = a,
                kind = MapMarkerKind.Companion,
                locationSemantics = MapLocationSemantics.PublicPlace,
            )
        }
    }

    @Test
    fun snapshotsSceneCollectionsAndRequiresUniqueItems() {
        val route = routeOverlay()
        val mutableGeometry = mutableListOf(a, b)
        val copiedRoute = RouteOverlay(
            id = MapItemId("route.copy"),
            routeId = RouteId("route-copy-v0"),
            geometry = mutableGeometry,
            role = RouteOverlayRole.Primary,
        )
        mutableGeometry.clear()
        assertEquals(2, copiedRoute.geometry.size)

        val marker = placeMarker("place.one")
        val mutableRoutes = mutableListOf(route)
        val mutableMarkers = mutableListOf(marker)
        val scene = MapScene(
            id = MapSceneId("scene.snapshot"),
            camera = MapCamera(a, 10.0),
            routeOverlays = mutableRoutes,
            markers = mutableMarkers,
            selectedItemId = marker.id,
        )
        mutableRoutes.clear()
        mutableMarkers.clear()
        assertEquals(1, scene.routeOverlays.size)
        assertEquals(1, scene.markers.size)

        assertFailsWith<IllegalArgumentException> {
            MapScene(
                id = MapSceneId("scene.duplicate"),
                camera = MapCamera(a, 10.0),
                routeOverlays = listOf(route),
                markers = listOf(marker.copy(id = route.id)),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            MapScene(
                id = MapSceneId("scene.missing-selection"),
                camera = MapCamera(a, 10.0),
                selectedItemId = MapItemId("missing.item"),
            )
        }
    }

    @Test
    fun validatesAndSnapshotsDeltas() {
        assertFailsWith<IllegalArgumentException> {
            RouteOverlayProgress(0, Double.NaN)
        }
        assertFailsWith<IllegalArgumentException> {
            RouteOverlayProgress(0, 1.0)
        }
        assertFailsWith<IllegalArgumentException> {
            MapSceneDelta.UpsertMarkers(
                sceneId = MapSceneId("scene.delta"),
                markers = emptyList(),
            )
        }

        val mutableMarkers = mutableListOf(placeMarker("place.delta"))
        val delta = MapSceneDelta.UpsertMarkers(MapSceneId("scene.delta"), mutableMarkers)
        mutableMarkers.clear()
        assertEquals(1, delta.markers.size)

        val mutableIds = mutableSetOf(MapItemId("place.delta"))
        val removal = MapSceneDelta.RemoveMarkers(MapSceneId("scene.delta"), mutableIds)
        mutableIds.clear()
        assertEquals(1, removal.markerIds.size)
    }

    @Test
    fun localStateAndCapabilityErrorsCannotBeRetryable() {
        assertFailsWith<IllegalArgumentException> {
            MapRenderError(
                code = MapRenderErrorCode.StaleScene,
                message = "Stale scene",
                retryable = true,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            MapRenderError(
                code = MapRenderErrorCode.UnsupportedOperation,
                message = "Unsupported operation",
                retryable = true,
            )
        }
    }

    private fun routeOverlay(): RouteOverlay = RouteOverlay(
        id = MapItemId("route.primary"),
        routeId = RouteId("route-primary-v0"),
        geometry = listOf(a, b),
        role = RouteOverlayRole.Primary,
    )

    private fun placeMarker(id: String): MapMarker = MapMarker(
        id = MapItemId(id),
        position = b,
        kind = MapMarkerKind.Place,
        locationSemantics = MapLocationSemantics.PublicPlace,
        label = "Place",
    )
}
