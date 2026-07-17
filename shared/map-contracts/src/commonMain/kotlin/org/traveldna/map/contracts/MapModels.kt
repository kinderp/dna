package org.traveldna.map.contracts

import kotlin.jvm.JvmInline
import org.traveldna.routing.contracts.GeoPoint
import org.traveldna.routing.contracts.RouteId

private val STABLE_MAP_ID = Regex("[A-Za-z0-9][A-Za-z0-9._:-]{0,127}")

private fun requireStableMapId(value: String, field: String) {
    require(STABLE_MAP_ID.matches(value)) { "$field contains unsupported characters" }
}

@JvmInline
value class MapSceneId(val value: String) {
    init { requireStableMapId(value, "map scene id") }
    override fun toString(): String = value
}

@JvmInline
value class MapItemId(val value: String) {
    init { requireStableMapId(value, "map item id") }
    override fun toString(): String = value
}

data class MapCamera(
    val center: GeoPoint,
    val zoom: Double,
    val bearingDegrees: Double = 0.0,
    val pitchDegrees: Double = 0.0,
) {
    init {
        require(zoom.isFinite() && zoom in MinZoom..MaxZoom) {
            "map zoom must be finite and within [$MinZoom, $MaxZoom]"
        }
        require(bearingDegrees.isFinite() && bearingDegrees >= 0.0 && bearingDegrees < 360.0) {
            "map bearing must be finite and within [0, 360)"
        }
        require(pitchDegrees.isFinite() && pitchDegrees in 0.0..MaxPitch) {
            "map pitch must be finite and within [0, $MaxPitch]"
        }
    }

    companion object {
        const val MinZoom: Double = 0.0
        const val MaxZoom: Double = 24.0
        const val MaxPitch: Double = 85.0
    }
}

enum class MapMarkerKind {
    Place,
    Companion,
    DnaTrace,
}

enum class MapLocationSemantics {
    PublicPlace,
    ApproximateArea,
}

data class MapMarker(
    val id: MapItemId,
    val position: GeoPoint,
    val kind: MapMarkerKind,
    val locationSemantics: MapLocationSemantics,
    val label: String? = null,
) {
    init {
        require(label == null || (label.isNotBlank() && label.length <= MaxLabelLength)) {
            "map marker label must be null or non-blank and at most $MaxLabelLength characters"
        }
        if (kind == MapMarkerKind.Companion) {
            require(locationSemantics == MapLocationSemantics.ApproximateArea) {
                "companion markers must use approximate-area semantics"
            }
        }
    }

    companion object {
        const val MaxLabelLength: Int = 128
    }
}

enum class RouteOverlayRole {
    Primary,
    Alternative,
    Completed,
}

/** Immutable route geometry installed as part of a static map scene. */
class RouteOverlay(
    val id: MapItemId,
    val routeId: RouteId,
    geometry: List<GeoPoint>,
    val role: RouteOverlayRole,
) {
    val geometry: List<GeoPoint> = geometry.toList()

    init {
        require(this.geometry.size >= 2) { "route overlay geometry needs at least two points" }
    }

    override fun equals(other: Any?): Boolean =
        other is RouteOverlay &&
            id == other.id &&
            routeId == other.routeId &&
            geometry == other.geometry &&
            role == other.role

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + routeId.hashCode()
        result = 31 * result + geometry.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }

    override fun toString(): String =
        "RouteOverlay(id=$id, routeId=$routeId, geometryPoints=${geometry.size}, role=$role)"
}

/** Immutable static scene installed infrequently in a map renderer. */
class MapScene(
    val id: MapSceneId,
    val camera: MapCamera,
    routeOverlays: List<RouteOverlay> = emptyList(),
    markers: List<MapMarker> = emptyList(),
    val selectedItemId: MapItemId? = null,
) {
    val routeOverlays: List<RouteOverlay> = routeOverlays.toList()
    val markers: List<MapMarker> = markers.toList()

    init {
        require(this.routeOverlays.size <= MaxRouteOverlays) {
            "a map scene may contain at most $MaxRouteOverlays route overlays"
        }
        require(this.markers.size <= MaxMarkers) {
            "a map scene may contain at most $MaxMarkers markers"
        }
        val ids = this.routeOverlays.map(RouteOverlay::id) + this.markers.map(MapMarker::id)
        require(ids.toSet().size == ids.size) { "map scene item ids must be unique" }
        require(selectedItemId == null || selectedItemId in ids) {
            "selected map item must exist in the scene"
        }
    }

    fun containsItem(id: MapItemId): Boolean =
        routeOverlays.any { it.id == id } || markers.any { it.id == id }

    override fun equals(other: Any?): Boolean =
        other is MapScene &&
            id == other.id &&
            camera == other.camera &&
            routeOverlays == other.routeOverlays &&
            markers == other.markers &&
            selectedItemId == other.selectedItemId

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + camera.hashCode()
        result = 31 * result + routeOverlays.hashCode()
        result = 31 * result + markers.hashCode()
        result = 31 * result + (selectedItemId?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "MapScene(id=$id, camera=$camera, routes=${routeOverlays.size}, " +
            "markers=${markers.size}, selectedItemId=$selectedItemId)"

    companion object {
        const val MaxRouteOverlays: Int = 3
        const val MaxMarkers: Int = 500
    }
}
