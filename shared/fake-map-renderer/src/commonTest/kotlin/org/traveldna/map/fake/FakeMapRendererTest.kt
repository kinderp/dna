package org.traveldna.map.fake

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.traveldna.map.contracts.MapCamera
import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.MapLocationSemantics
import org.traveldna.map.contracts.MapMarker
import org.traveldna.map.contracts.MapMarkerKind
import org.traveldna.map.contracts.MapRenderErrorCode
import org.traveldna.map.contracts.MapRenderResult
import org.traveldna.map.contracts.MapRendererCapabilities
import org.traveldna.map.contracts.MapScene
import org.traveldna.map.contracts.MapSceneDelta
import org.traveldna.map.contracts.MapSceneId
import org.traveldna.map.contracts.RouteOverlay
import org.traveldna.map.contracts.RouteOverlayProgress
import org.traveldna.map.contracts.RouteOverlayRole
import org.traveldna.map.testkit.MapRendererContractProbe
import org.traveldna.plugin.sdk.KnownPlatforms
import org.traveldna.plugin.sdk.PluginDescriptor
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.GeoPoint
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RouteProvenance

class FakeMapRendererTest {
    private val a = GeoPoint(0.0, 0.0)
    private val b = GeoPoint(0.0, 0.01)
    private val c = GeoPoint(0.01, 0.01)
    private val routeId = MapItemId("route.primary")

    @Test
    fun appliesProgressWithoutReplacingInstalledGeometry() = runImmediate {
        val renderer = FakeMapRenderer()
        val scene = scene()
        assertIs<MapRenderResult.Success>(renderer.install(scene))
        val before = assertNotNull(renderer.snapshot)
        val geometryBefore = assertNotNull(before.routeGeometry[routeId])

        assertIs<MapRenderResult.Success>(
            renderer.apply(
                MapSceneDelta.UpdateRouteProgress(
                    scene.id,
                    routeId,
                    RouteOverlayProgress(1, 0.5),
                ),
            ),
        )
        val after = assertNotNull(renderer.snapshot)
        assertTrue(geometryBefore === after.routeGeometry[routeId])
        assertEquals(RouteOverlayProgress(1, 0.5), after.routeProgress[routeId])
    }

    @Test
    fun appliesMarkerDeltasAndRejectsStaleScenes() = runImmediate {
        val renderer = FakeMapRenderer()
        val scene = scene()
        renderer.install(scene)
        val marker = dnaMarker("dna.new")

        assertIs<MapRenderResult.Success>(
            renderer.apply(MapSceneDelta.UpsertMarkers(scene.id, listOf(marker))),
        )
        assertIs<MapRenderResult.Success>(
            renderer.apply(MapSceneDelta.SelectItem(scene.id, marker.id)),
        )
        assertEquals(marker.id, renderer.snapshot?.selectedItemId)

        val stale = renderer.apply(
            MapSceneDelta.SelectItem(MapSceneId("scene.stale"), null),
        )
        assertEquals(MapRenderErrorCode.StaleScene, assertIs<MapRenderResult.Failure>(stale).error.code)

        renderer.apply(MapSceneDelta.RemoveMarkers(scene.id, setOf(marker.id)))
        assertEquals(null, renderer.snapshot?.selectedItemId)
    }

    @Test
    fun validatesSceneAwareProgressAndCapacity() = runImmediate {
        val renderer = FakeMapRenderer()
        val fullScene = scene(
            markers = List(MapScene.MaxMarkers) { index -> dnaMarker("dna.$index") },
        )
        renderer.install(fullScene)

        val overflow = renderer.apply(
            MapSceneDelta.UpsertMarkers(fullScene.id, listOf(dnaMarker("dna.overflow"))),
        )
        assertEquals(
            MapRenderErrorCode.CapacityExceeded,
            assertIs<MapRenderResult.Failure>(overflow).error.code,
        )

        val invalidProgress = renderer.apply(
            MapSceneDelta.UpdateRouteProgress(
                fullScene.id,
                routeId,
                RouteOverlayProgress(2, 0.5),
            ),
        )
        assertEquals(
            MapRenderErrorCode.InvalidDelta,
            assertIs<MapRenderResult.Failure>(invalidProgress).error.code,
        )
    }

    @Test
    fun installRejectsStaticFeaturesMissingFromTheDescriptor() = runImmediate {
        val markerlessRenderer = FakeMapRenderer(
            descriptorWithout(MapRendererCapabilities.Markers),
        )
        val markerFailure = markerlessRenderer.install(
            scene(markers = listOf(dnaMarker("dna.static"))),
        )
        assertEquals(
            MapRenderErrorCode.UnsupportedOperation,
            assertIs<MapRenderResult.Failure>(markerFailure).error.code,
        )
        assertEquals(null, markerlessRenderer.snapshot)

        val selectionlessRenderer = FakeMapRenderer(
            descriptorWithout(MapRendererCapabilities.Selection),
        )
        val selectedScene = MapScene(
            id = MapSceneId("scene.static-selection"),
            camera = MapCamera(a, zoom = 12.0),
            routeOverlays = scene().routeOverlays,
            selectedItemId = routeId,
        )
        val selectionFailure = selectionlessRenderer.install(selectedScene)
        assertEquals(
            MapRenderErrorCode.UnsupportedOperation,
            assertIs<MapRenderResult.Failure>(selectionFailure).error.code,
        )
        assertEquals(null, selectionlessRenderer.snapshot)
    }

    @Test
    fun passesReusableRendererProbe() = runImmediate {
        val renderer = FakeMapRenderer()
        val scene = scene()
        val report = MapRendererContractProbe.verify(
            renderer = renderer,
            scene = scene,
            routeOverlayId = routeId,
            markerToUpsert = dnaMarker("dna.probe"),
        )
        assertEquals(FakeMapRenderer.Id.value, report.providerId)
        assertEquals(
            listOf(
                "declares-full-probe-capabilities",
                "installs-scene",
                "applies-route-progress",
                "applies-marker-and-selection-deltas",
                "rejects-stale-scene",
                "clears-scene",
            ),
            report.checks,
        )
    }

    @Test
    fun canonicalFixtureUsesTheCurrentMapContractGeneration() {
        val scene = FakeMapFixtures.scene(canonicalRoute())
        assertEquals(1, scene.routeOverlays.size)
        assertEquals(1, scene.markers.size)
        assertEquals(MapMarkerKind.Place, scene.markers.single().kind)
        assertEquals(MapLocationSemantics.PublicPlace, scene.markers.single().locationSemantics)
    }

    private fun descriptorWithout(capability: org.traveldna.plugin.sdk.CapabilityId): PluginDescriptor =
        PluginDescriptor(
            id = FakeMapRenderer.Id,
            implementationVersion = "0.1.0-test",
            contractVersion = 1,
            capabilities = FakeMapRenderer.defaultDescriptor.capabilities - capability,
            supportedPlatforms = setOf(KnownPlatforms.Jvm),
        )

    private fun scene(markers: List<MapMarker> = emptyList()): MapScene = MapScene(
        id = MapSceneId("scene.reference"),
        camera = MapCamera(a, zoom = 12.0),
        routeOverlays = listOf(
            RouteOverlay(
                id = routeId,
                routeId = RouteId("route-reference-v0"),
                geometry = listOf(a, b, c),
                role = RouteOverlayRole.Primary,
            ),
        ),
        markers = markers,
    )

    private fun canonicalRoute(): RoutePlan = RoutePlan(
        id = RouteId("route-fixture-v0"),
        geometry = listOf(a, b, c),
        legs = listOf(
            RouteLeg(
                geometryStartIndex = 0,
                geometryEndIndex = 2,
                origin = a,
                destination = c,
                distanceMeters = 2_400L,
                durationSeconds = 160L,
                maneuvers = emptyList(),
            ),
        ),
        distanceMeters = 2_400L,
        durationSeconds = 160L,
        provenance = RouteProvenance(PluginId("org.traveldna.fake-map-test")),
    )

    private fun dnaMarker(id: String): MapMarker = MapMarker(
        id = MapItemId(id),
        position = b,
        kind = MapMarkerKind.DnaTrace,
        locationSemantics = MapLocationSemantics.ApproximateArea,
        label = "DNA trace",
    )
}

private fun <T> runImmediate(block: suspend () -> T): T {
    var outcome: Result<T>? = null
    block.startCoroutine(object : Continuation<T> {
        override val context = EmptyCoroutineContext
        override fun resumeWith(result: Result<T>) {
            outcome = result
        }
    })
    return checkNotNull(outcome) {
        "fake renderer unexpectedly suspended"
    }.getOrThrow()
}
