package org.traveldna.map.fake

import org.traveldna.map.contracts.MapCamera
import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.MapMarker
import org.traveldna.map.contracts.MapRenderError
import org.traveldna.map.contracts.MapRenderErrorCode
import org.traveldna.map.contracts.MapRenderResult
import org.traveldna.map.contracts.MapRendererCapabilities
import org.traveldna.map.contracts.MapRendererPort
import org.traveldna.map.contracts.MapScene
import org.traveldna.map.contracts.MapSceneDelta
import org.traveldna.map.contracts.MapSceneId
import org.traveldna.map.contracts.RouteOverlayProgress
import org.traveldna.plugin.sdk.KnownPlatforms
import org.traveldna.plugin.sdk.PluginDescriptor
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.GeoPoint

sealed interface FakeMapCall {
    data class Install(val sceneId: MapSceneId) : FakeMapCall
    data class Apply(val delta: MapSceneDelta) : FakeMapCall
    data class Clear(val sceneId: MapSceneId) : FakeMapCall
}

/** Immutable diagnostic snapshot exposed by the deterministic fake. */
class FakeMapSnapshot(
    val sceneId: MapSceneId,
    val camera: MapCamera,
    routeGeometry: Map<MapItemId, List<GeoPoint>>,
    routeProgress: Map<MapItemId, RouteOverlayProgress>,
    markers: Map<MapItemId, MapMarker>,
    val selectedItemId: MapItemId?,
    val callCount: Int,
) {
    val routeGeometry: Map<MapItemId, List<GeoPoint>> = routeGeometry.toMap()
    val routeProgress: Map<MapItemId, RouteOverlayProgress> = routeProgress.toMap()
    val markers: Map<MapItemId, MapMarker> = markers.toMap()
}

/**
 * Single-threaded, deterministic renderer fake.
 *
 * It stores semantic map state but never draws pixels. Tests can therefore
 * verify scene/delta contracts without MapLibre, Android or iOS.
 */
class FakeMapRenderer(
    override val descriptor: PluginDescriptor = defaultDescriptor,
) : MapRendererPort {
    private data class State(
        val sceneId: MapSceneId,
        var camera: MapCamera,
        val routeGeometry: Map<MapItemId, List<GeoPoint>>,
        val routeProgress: MutableMap<MapItemId, RouteOverlayProgress>,
        val markers: MutableMap<MapItemId, MapMarker>,
        var selectedItemId: MapItemId?,
    )

    private var state: State? = null
    private val calls = mutableListOf<FakeMapCall>()

    val recordedCalls: List<FakeMapCall> get() = calls.toList()

    val snapshot: FakeMapSnapshot?
        get() = state?.let { current ->
            FakeMapSnapshot(
                sceneId = current.sceneId,
                camera = current.camera,
                routeGeometry = current.routeGeometry,
                routeProgress = current.routeProgress,
                markers = current.markers,
                selectedItemId = current.selectedItemId,
                callCount = calls.size,
            )
        }

    init {
        val required = setOf(
            MapRendererCapabilities.InstallScene,
            MapRendererCapabilities.ApplyDelta,
        )
        require(descriptor.capabilities.containsAll(required)) {
            "fake renderer descriptor must declare scene installation and delta application"
        }
    }

    override suspend fun install(scene: MapScene): MapRenderResult {
        calls += FakeMapCall.Install(scene.id)
        state = State(
            sceneId = scene.id,
            camera = scene.camera,
            routeGeometry = scene.routeOverlays.associate { it.id to it.geometry },
            routeProgress = scene.routeOverlays.associate { overlay ->
                overlay.id to RouteOverlayProgress(0, 0.0)
            }.toMutableMap(),
            markers = scene.markers.associateBy(MapMarker::id).toMutableMap(),
            selectedItemId = scene.selectedItemId,
        )
        return MapRenderResult.Success
    }

    override suspend fun apply(delta: MapSceneDelta): MapRenderResult {
        calls += FakeMapCall.Apply(delta)
        val current = state ?: return failure(MapRenderErrorCode.NoScene, "No map scene is installed")
        if (delta.sceneId != current.sceneId) {
            return failure(MapRenderErrorCode.StaleScene, "Delta targets a stale map scene")
        }
        return when (delta) {
            is MapSceneDelta.SetCamera -> applyCamera(current, delta)
            is MapSceneDelta.UpdateRouteProgress -> applyProgress(current, delta)
            is MapSceneDelta.UpsertMarkers -> applyMarkerUpsert(current, delta)
            is MapSceneDelta.RemoveMarkers -> applyMarkerRemoval(current, delta)
            is MapSceneDelta.SelectItem -> applySelection(current, delta)
        }
    }

    override suspend fun clear(sceneId: MapSceneId): MapRenderResult {
        calls += FakeMapCall.Clear(sceneId)
        val current = state ?: return failure(MapRenderErrorCode.NoScene, "No map scene is installed")
        if (sceneId != current.sceneId) {
            return failure(MapRenderErrorCode.StaleScene, "Clear targets a stale map scene")
        }
        state = null
        return MapRenderResult.Success
    }

    private fun applyCamera(
        current: State,
        delta: MapSceneDelta.SetCamera,
    ): MapRenderResult {
        if (MapRendererCapabilities.Camera !in descriptor.capabilities) {
            return failure(MapRenderErrorCode.UnsupportedOperation, "Camera deltas are not supported")
        }
        current.camera = delta.camera
        return MapRenderResult.Success
    }

    private fun applyProgress(
        current: State,
        delta: MapSceneDelta.UpdateRouteProgress,
    ): MapRenderResult {
        if (MapRendererCapabilities.RouteProgress !in descriptor.capabilities) {
            return failure(MapRenderErrorCode.UnsupportedOperation, "Route progress is not supported")
        }
        val geometry = current.routeGeometry[delta.routeOverlayId]
            ?: return failure(MapRenderErrorCode.UnknownItem, "Route overlay is not installed")
        val progress = delta.progress
        if (progress.completedGeometryIndex !in geometry.indices) {
            return failure(MapRenderErrorCode.InvalidDelta, "Route progress index exceeds geometry")
        }
        if (progress.completedGeometryIndex == geometry.lastIndex && progress.fractionToNext != 0.0) {
            return failure(MapRenderErrorCode.InvalidDelta, "Final geometry point cannot have next-segment progress")
        }
        current.routeProgress[delta.routeOverlayId] = progress
        return MapRenderResult.Success
    }

    private fun applyMarkerUpsert(
        current: State,
        delta: MapSceneDelta.UpsertMarkers,
    ): MapRenderResult {
        if (MapRendererCapabilities.Markers !in descriptor.capabilities) {
            return failure(MapRenderErrorCode.UnsupportedOperation, "Marker deltas are not supported")
        }
        if (delta.markers.any { it.id in current.routeGeometry }) {
            return failure(MapRenderErrorCode.InvalidDelta, "Marker id collides with a route overlay")
        }
        val newIds = delta.markers.map(MapMarker::id).count { it !in current.markers }
        if (current.markers.size + newIds > MapScene.MaxMarkers) {
            return failure(MapRenderErrorCode.CapacityExceeded, "Marker capacity would be exceeded")
        }
        delta.markers.forEach { current.markers[it.id] = it }
        return MapRenderResult.Success
    }

    private fun applyMarkerRemoval(
        current: State,
        delta: MapSceneDelta.RemoveMarkers,
    ): MapRenderResult {
        if (MapRendererCapabilities.Markers !in descriptor.capabilities) {
            return failure(MapRenderErrorCode.UnsupportedOperation, "Marker deltas are not supported")
        }
        if (delta.markerIds.any { it in current.routeGeometry }) {
            return failure(MapRenderErrorCode.InvalidDelta, "Route overlays cannot be removed as markers")
        }
        delta.markerIds.forEach(current.markers::remove)
        if (current.selectedItemId?.let { it in delta.markerIds } == true) {
            current.selectedItemId = null
        }
        return MapRenderResult.Success
    }

    private fun applySelection(
        current: State,
        delta: MapSceneDelta.SelectItem,
    ): MapRenderResult {
        if (MapRendererCapabilities.Selection !in descriptor.capabilities) {
            return failure(MapRenderErrorCode.UnsupportedOperation, "Selection is not supported")
        }
        val itemId = delta.itemId
        if (itemId != null && itemId !in current.routeGeometry && itemId !in current.markers) {
            return failure(MapRenderErrorCode.UnknownItem, "Selected map item is not installed")
        }
        current.selectedItemId = itemId
        return MapRenderResult.Success
    }

    private fun failure(code: MapRenderErrorCode, message: String): MapRenderResult.Failure =
        MapRenderResult.Failure(MapRenderError(code, message, retryable = false))

    companion object {
        val Id = PluginId("org.traveldna.fake-map-renderer")

        val defaultDescriptor = PluginDescriptor(
            id = Id,
            implementationVersion = "0.1.0",
            contractVersion = 1,
            capabilities = setOf(
                MapRendererCapabilities.InstallScene,
                MapRendererCapabilities.ApplyDelta,
                MapRendererCapabilities.RouteProgress,
                MapRendererCapabilities.Markers,
                MapRendererCapabilities.Selection,
                MapRendererCapabilities.Camera,
            ),
            supportedPlatforms = setOf(KnownPlatforms.Jvm, KnownPlatforms.LinuxX64),
        )
    }
}
