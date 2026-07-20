package org.traveldna.map.contracts

import org.traveldna.plugin.sdk.CapabilityId
import org.traveldna.plugin.sdk.TravelDnaPlugin

object MapRendererCapabilities {
    val InstallScene = CapabilityId("map.install-scene")
    val ApplyDelta = CapabilityId("map.apply-delta")
    val RouteProgress = CapabilityId("map.route-progress")
    val Markers = CapabilityId("map.markers")
    val Selection = CapabilityId("map.selection")
    val Camera = CapabilityId("map.camera")
}

enum class MapRenderErrorCode {
    NoScene,
    StaleScene,
    UnknownItem,
    InvalidDelta,
    CapacityExceeded,
    UnsupportedOperation,
    Internal,
}

data class MapRenderError(
    val code: MapRenderErrorCode,
    val message: String,
    val retryable: Boolean,
) {
    init {
        require(message.isNotBlank() && message.length <= MaxMessageLength) {
            "map render error message must be non-blank and at most $MaxMessageLength characters"
        }
        if (
            code == MapRenderErrorCode.NoScene ||
            code == MapRenderErrorCode.StaleScene ||
            code == MapRenderErrorCode.UnknownItem ||
            code == MapRenderErrorCode.InvalidDelta ||
            code == MapRenderErrorCode.CapacityExceeded ||
            code == MapRenderErrorCode.UnsupportedOperation
        ) {
            require(!retryable) {
                "$code is not retryable without changing local state, input or provider"
            }
        }
    }

    companion object {
        const val MaxMessageLength: Int = 512
    }
}

sealed interface MapRenderResult {
    data object Success : MapRenderResult
    data class Failure(val error: MapRenderError) : MapRenderResult
}

/** Provider-neutral boundary implemented by MapLibre or another renderer adapter. */
interface MapRendererPort : TravelDnaPlugin {
    suspend fun install(scene: MapScene): MapRenderResult
    suspend fun apply(delta: MapSceneDelta): MapRenderResult
    suspend fun clear(sceneId: MapSceneId): MapRenderResult
}
