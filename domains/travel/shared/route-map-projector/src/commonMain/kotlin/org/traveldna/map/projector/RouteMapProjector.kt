package org.traveldna.map.projector

import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.RouteOverlay
import org.traveldna.map.contracts.RouteOverlayRole
import org.traveldna.routing.contracts.RoutePlan

/** Converts canonical routing intent into a provider-neutral static map overlay. */
object RouteMapProjector {
    fun project(
        route: RoutePlan,
        overlayId: MapItemId,
        role: RouteOverlayRole,
    ): RouteOverlay = RouteOverlay(
        id = overlayId,
        routeId = route.id,
        geometry = route.geometry,
        role = role,
    )
}
