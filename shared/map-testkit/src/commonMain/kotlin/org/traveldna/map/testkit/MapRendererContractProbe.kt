package org.traveldna.map.testkit

import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.MapMarker
import org.traveldna.map.contracts.MapRenderErrorCode
import org.traveldna.map.contracts.MapRenderResult
import org.traveldna.map.contracts.MapRendererCapabilities
import org.traveldna.map.contracts.MapRendererPort
import org.traveldna.map.contracts.MapScene
import org.traveldna.map.contracts.MapSceneDelta
import org.traveldna.map.contracts.MapSceneId
import org.traveldna.map.contracts.RouteOverlayProgress

class MapRendererContractReport(
    val providerId: String,
    checks: List<String>,
) {
    val checks: List<String> = checks.toList()
}

/**
 * Reusable full-capability behavior probe for deterministic renderer test
 * implementations.
 *
 * A provider must declare every capability exercised here. Providers with a
 * smaller capability set should use narrower probes rather than pass a test
 * that invokes unsupported operations.
 */
object MapRendererContractProbe {
    suspend fun verify(
        renderer: MapRendererPort,
        scene: MapScene,
        routeOverlayId: MapItemId,
        markerToUpsert: MapMarker,
    ): MapRendererContractReport {
        val requiredCapabilities = setOf(
            MapRendererCapabilities.InstallScene,
            MapRendererCapabilities.ApplyDelta,
            MapRendererCapabilities.RouteProgress,
            MapRendererCapabilities.Markers,
            MapRendererCapabilities.Selection,
        )
        require(renderer.descriptor.capabilities.containsAll(requiredCapabilities)) {
            "full renderer probe requires install, delta, progress, markers and selection capabilities"
        }
        val checks = mutableListOf("declares-full-probe-capabilities")

        renderer.install(scene).requireSuccess()
        checks += "installs-scene"

        renderer.apply(
            MapSceneDelta.UpdateRouteProgress(
                sceneId = scene.id,
                routeOverlayId = routeOverlayId,
                progress = RouteOverlayProgress(0, 0.5),
            ),
        ).requireSuccess()
        checks += "applies-route-progress"

        renderer.apply(
            MapSceneDelta.UpsertMarkers(scene.id, listOf(markerToUpsert)),
        ).requireSuccess()
        renderer.apply(
            MapSceneDelta.SelectItem(scene.id, markerToUpsert.id),
        ).requireSuccess()
        renderer.apply(
            MapSceneDelta.RemoveMarkers(scene.id, setOf(markerToUpsert.id)),
        ).requireSuccess()
        checks += "applies-marker-and-selection-deltas"

        val stale = renderer.apply(
            MapSceneDelta.SelectItem(MapSceneId("scene.stale-probe"), null),
        )
        require(stale is MapRenderResult.Failure && stale.error.code == MapRenderErrorCode.StaleScene) {
            "renderer must reject deltas for a stale scene id"
        }
        checks += "rejects-stale-scene"

        renderer.clear(scene.id).requireSuccess()
        val afterClear = renderer.apply(MapSceneDelta.SelectItem(scene.id, null))
        require(afterClear is MapRenderResult.Failure && afterClear.error.code == MapRenderErrorCode.NoScene) {
            "renderer must report NoScene after clearing its active scene"
        }
        checks += "clears-scene"

        return MapRendererContractReport(renderer.descriptor.id.value, checks)
    }
}

private fun MapRenderResult.requireSuccess() {
    require(this is MapRenderResult.Success) { "expected map-render success but got $this" }
}
