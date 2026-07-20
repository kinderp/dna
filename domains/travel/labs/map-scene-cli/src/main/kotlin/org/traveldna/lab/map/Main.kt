package org.traveldna.lab.map

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import org.traveldna.map.contracts.MapCamera
import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.MapLocationSemantics
import org.traveldna.map.contracts.MapMarker
import org.traveldna.map.contracts.MapMarkerKind
import org.traveldna.map.contracts.MapRenderResult
import org.traveldna.map.contracts.MapScene
import org.traveldna.map.contracts.MapSceneDelta
import org.traveldna.map.contracts.MapSceneId
import org.traveldna.map.contracts.RouteOverlayProgress
import org.traveldna.map.contracts.RouteOverlayRole
import org.traveldna.map.fake.FakeMapRenderer
import org.traveldna.map.projector.RouteMapProjector
import org.traveldna.routing.fake.FakeRouteFixtures

fun main() {
    val route = FakeRouteFixtures.ReferenceRoute
    val routeOverlayId = MapItemId("route.primary")
    val routeOverlay = RouteMapProjector.project(route, routeOverlayId, RouteOverlayRole.Primary)
    val place = MapMarker(
        id = MapItemId("place.reference-stop"),
        position = FakeRouteFixtures.C,
        kind = MapMarkerKind.Place,
        locationSemantics = MapLocationSemantics.PublicPlace,
        label = "Reference stop",
    )
    val companion = MapMarker(
        id = MapItemId("companion.approximate"),
        position = FakeRouteFixtures.D,
        kind = MapMarkerKind.Companion,
        locationSemantics = MapLocationSemantics.ApproximateArea,
        label = "Companion ahead",
    )
    val scene = MapScene(
        id = MapSceneId("scene.reference-map-v0"),
        camera = MapCamera(FakeRouteFixtures.A, zoom = 11.0, pitchDegrees = 35.0),
        routeOverlays = listOf(routeOverlay),
        markers = listOf(place, companion),
    )
    val renderer = FakeMapRenderer()

    runImmediate { renderer.install(scene) }.requireSuccess()
    runImmediate {
        renderer.apply(
            MapSceneDelta.UpdateRouteProgress(
                scene.id,
                routeOverlayId,
                RouteOverlayProgress(2, 0.5),
            ),
        )
    }.requireSuccess()

    val dna = MapMarker(
        id = MapItemId("dna.scenic-stop"),
        position = FakeRouteFixtures.D,
        kind = MapMarkerKind.DnaTrace,
        locationSemantics = MapLocationSemantics.ApproximateArea,
        label = "Scenic stop DNA",
    )
    runImmediate { renderer.apply(MapSceneDelta.UpsertMarkers(scene.id, listOf(dna))) }.requireSuccess()
    runImmediate { renderer.apply(MapSceneDelta.SelectItem(scene.id, dna.id)) }.requireSuccess()

    val snapshot = checkNotNull(renderer.snapshot)
    val progress = checkNotNull(snapshot.routeProgress[routeOverlayId])
    val capabilities = renderer.descriptor.capabilities
        .map { it.value }
        .sorted()
        .joinToString(separator = "\",\"", prefix = "[\"", postfix = "\"]")

    println(
        "{\"provider\":\"${renderer.descriptor.id.value}\"," +
            "\"capabilities\":$capabilities," +
            "\"scene_id\":\"${snapshot.sceneId.value}\"," +
            "\"routes\":${snapshot.routeGeometry.size}," +
            "\"markers\":${snapshot.markers.size}," +
            "\"completed_index\":${progress.completedGeometryIndex}," +
            "\"progress_fraction\":${progress.fractionToNext}," +
            "\"selected\":\"${snapshot.selectedItemId?.value}\"," +
            "\"operations\":${snapshot.callCount}}",
    )
}

private fun MapRenderResult.requireSuccess() {
    check(this is MapRenderResult.Success) { "map Lab operation failed: $this" }
}

private fun <T> runImmediate(block: suspend () -> T): T {
    var outcome: Result<T>? = null
    block.startCoroutine(object : Continuation<T> {
        override val context = EmptyCoroutineContext
        override fun resumeWith(result: Result<T>) {
            outcome = result
        }
    })
    return checkNotNull(outcome) { "fake map renderer unexpectedly suspended" }.getOrThrow()
}
