package org.traveldna.map.contracts

/**
 * Progress along an already installed route geometry.
 *
 * `completedGeometryIndex` identifies the last fully completed point. The
 * fraction is progress toward the following point and therefore uses `[0, 1)`.
 * Reaching the next point is represented canonically by incrementing the index
 * and resetting the fraction to zero.
 */
data class RouteOverlayProgress(
    val completedGeometryIndex: Int,
    val fractionToNext: Double,
) {
    init {
        require(completedGeometryIndex >= 0) { "completed geometry index must be non-negative" }
        require(fractionToNext.isFinite() && fractionToNext >= 0.0 && fractionToNext < 1.0) {
            "route progress fraction must be finite and within [0, 1)"
        }
    }
}

sealed interface MapSceneDelta {
    val sceneId: MapSceneId

    data class SetCamera(
        override val sceneId: MapSceneId,
        val camera: MapCamera,
    ) : MapSceneDelta

    data class UpdateRouteProgress(
        override val sceneId: MapSceneId,
        val routeOverlayId: MapItemId,
        val progress: RouteOverlayProgress,
    ) : MapSceneDelta

    class UpsertMarkers(
        override val sceneId: MapSceneId,
        markers: List<MapMarker>,
    ) : MapSceneDelta {
        val markers: List<MapMarker> = markers.toList()

        init {
            require(this.markers.isNotEmpty()) { "marker upsert delta must not be empty" }
            require(this.markers.size <= MaxMarkerOperations) {
                "one marker delta may contain at most $MaxMarkerOperations markers"
            }
            require(this.markers.map(MapMarker::id).toSet().size == this.markers.size) {
                "marker upsert delta contains duplicate ids"
            }
        }

        override fun equals(other: Any?): Boolean =
            other is UpsertMarkers && sceneId == other.sceneId && markers == other.markers

        override fun hashCode(): Int = 31 * sceneId.hashCode() + markers.hashCode()

        override fun toString(): String = "UpsertMarkers(sceneId=$sceneId, markers=$markers)"
    }

    class RemoveMarkers(
        override val sceneId: MapSceneId,
        markerIds: Set<MapItemId>,
    ) : MapSceneDelta {
        val markerIds: Set<MapItemId> = markerIds.toSet()

        init {
            require(this.markerIds.isNotEmpty()) { "marker removal delta must not be empty" }
            require(this.markerIds.size <= MaxMarkerOperations) {
                "one marker delta may remove at most $MaxMarkerOperations markers"
            }
        }

        override fun equals(other: Any?): Boolean =
            other is RemoveMarkers && sceneId == other.sceneId && markerIds == other.markerIds

        override fun hashCode(): Int = 31 * sceneId.hashCode() + markerIds.hashCode()

        override fun toString(): String = "RemoveMarkers(sceneId=$sceneId, markerIds=$markerIds)"
    }

    data class SelectItem(
        override val sceneId: MapSceneId,
        val itemId: MapItemId?,
    ) : MapSceneDelta

    companion object {
        const val MaxMarkerOperations: Int = 100
    }
}
