package org.traveldna.navigation.map

import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.MapSceneDelta
import org.traveldna.map.contracts.MapSceneId
import org.traveldna.map.contracts.RouteOverlay
import org.traveldna.map.contracts.RouteOverlayProgress
import org.traveldna.navigation.contracts.RouteProgressSnapshot
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RoutePlan

/**
 * Immutable O(1)-per-update binding created after a route overlay is installed.
 *
 * Full geometry equality is verified once by [bind]. The hot projection path
 * then carries only IDs, one point count and the compact progress coordinate.
 */
class RouteProgressMapBinding internal constructor(
    val sceneId: MapSceneId,
    val routeOverlayId: MapItemId,
    val routeId: RouteId,
    val geometryPointCount: Int,
)

/** Converts canonical navigation progress into one compact map-scene delta. */
object RouteProgressMapProjector {
    fun bind(
        sceneId: MapSceneId,
        overlay: RouteOverlay,
        route: RoutePlan,
    ): RouteProgressMapBinding {
        require(overlay.routeId == route.id) {
            "map overlay and canonical route refer to different route IDs"
        }
        require(overlay.geometry == route.geometry) {
            "map overlay geometry differs from the canonical route geometry"
        }
        return RouteProgressMapBinding(
            sceneId = sceneId,
            routeOverlayId = overlay.id,
            routeId = route.id,
            geometryPointCount = route.geometry.size,
        )
    }

    fun project(
        binding: RouteProgressMapBinding,
        snapshot: RouteProgressSnapshot,
    ): MapSceneDelta.UpdateRouteProgress {
        require(binding.routeId == snapshot.position.routeId) {
            "route-progress snapshot and map binding refer to different routes"
        }
        val coordinate = snapshot.position.coordinate
        require(coordinate.completedGeometryIndex in 0 until binding.geometryPointCount) {
            "route-progress index lies outside the bound overlay geometry"
        }
        require(
            coordinate.completedGeometryIndex != binding.geometryPointCount - 1 ||
                coordinate.fractionToNext == 0.0,
        ) {
            "final overlay geometry point cannot have next-segment progress"
        }
        return MapSceneDelta.UpdateRouteProgress(
            sceneId = binding.sceneId,
            routeOverlayId = binding.routeOverlayId,
            progress = RouteOverlayProgress(
                completedGeometryIndex = coordinate.completedGeometryIndex,
                fractionToNext = coordinate.fractionToNext,
            ),
        )
    }
}
