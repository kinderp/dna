package org.traveldna.routing.contracts

import org.traveldna.plugin.sdk.PluginId

enum class ManeuverType {
    Depart,
    Continue,
    TurnLeft,
    TurnRight,
    KeepLeft,
    KeepRight,
    ExitLeft,
    ExitRight,
    Arrive,
}

data class RouteManeuver(
    val geometryIndex: Int,
    val type: ManeuverType,
    val location: GeoPoint,
    val instruction: String,
    val roadName: String? = null,
    val exitNumber: String? = null,
) {
    init {
        require(geometryIndex >= 0) { "maneuver geometry index must be non-negative" }
        require(instruction.isNotBlank()) { "maneuver instruction must not be blank" }
        require(instruction.length <= MaxInstructionLength) {
            "maneuver instruction is too long"
        }
        require(
            roadName == null ||
                (roadName.isNotBlank() && roadName.length <= MaxRoadNameLength),
        ) {
            "road name must be null or non-blank and at most $MaxRoadNameLength characters"
        }
        require(
            exitNumber == null ||
                (exitNumber.isNotBlank() && exitNumber.length <= MaxExitNumberLength),
        ) {
            "exit number must be null or non-blank and at most $MaxExitNumberLength characters"
        }
    }

    companion object {
        const val MaxInstructionLength: Int = 512
        const val MaxRoadNameLength: Int = 256
        const val MaxExitNumberLength: Int = 64
    }
}

/** Immutable snapshot of one contiguous portion between requested stops. */
class RouteLeg(
    val geometryStartIndex: Int,
    val geometryEndIndex: Int,
    val origin: GeoPoint,
    val destination: GeoPoint,
    val distanceMeters: Long,
    val durationSeconds: Long,
    maneuvers: List<RouteManeuver>,
) {
    val maneuvers: List<RouteManeuver> = maneuvers.toList()

    init {
        require(geometryStartIndex >= 0) { "leg start index must be non-negative" }
        require(geometryEndIndex > geometryStartIndex) {
            "leg end index must be greater than its start index"
        }
        require(distanceMeters > 0) { "leg distance must be positive" }
        require(durationSeconds > 0) { "leg duration must be positive" }
        require(this.maneuvers.zipWithNext().none { (first, second) ->
            first.geometryIndex > second.geometryIndex
        }) { "leg maneuvers must be ordered by geometry index" }
        require(this.maneuvers.all { it.geometryIndex in geometryStartIndex..geometryEndIndex }) {
            "leg maneuver lies outside the leg geometry range"
        }
    }

    override fun equals(other: Any?): Boolean =
        other is RouteLeg &&
            geometryStartIndex == other.geometryStartIndex &&
            geometryEndIndex == other.geometryEndIndex &&
            origin == other.origin &&
            destination == other.destination &&
            distanceMeters == other.distanceMeters &&
            durationSeconds == other.durationSeconds &&
            maneuvers == other.maneuvers

    override fun hashCode(): Int {
        var result = geometryStartIndex
        result = 31 * result + geometryEndIndex
        result = 31 * result + origin.hashCode()
        result = 31 * result + destination.hashCode()
        result = 31 * result + distanceMeters.hashCode()
        result = 31 * result + durationSeconds.hashCode()
        result = 31 * result + maneuvers.hashCode()
        return result
    }

    override fun toString(): String =
        "RouteLeg(geometryStartIndex=$geometryStartIndex, geometryEndIndex=$geometryEndIndex, " +
            "origin=$origin, destination=$destination, distanceMeters=$distanceMeters, " +
            "durationSeconds=$durationSeconds, maneuvers=$maneuvers)"
}

/** Immutable description of where a canonical route came from. */
class RouteProvenance(
    val providerId: PluginId,
    val providerRouteId: String? = null,
    dataSources: Set<String> = emptySet(),
) {
    val dataSources: Set<String> = dataSources.toSet()

    init {
        require(
            providerRouteId == null ||
                (providerRouteId.isNotBlank() && providerRouteId.length <= MaxProviderRouteIdLength),
        ) {
            "provider route id must be null or non-blank and at most $MaxProviderRouteIdLength characters"
        }
        require(this.dataSources.size <= MaxDataSources) {
            "route provenance may contain at most $MaxDataSources data sources"
        }
        require(this.dataSources.all { it.isNotBlank() && it.length <= MaxDataSourceLength }) {
            "data sources must be non-blank and at most $MaxDataSourceLength characters"
        }
    }

    override fun equals(other: Any?): Boolean =
        other is RouteProvenance &&
            providerId == other.providerId &&
            providerRouteId == other.providerRouteId &&
            dataSources == other.dataSources

    override fun hashCode(): Int {
        var result = providerId.hashCode()
        result = 31 * result + (providerRouteId?.hashCode() ?: 0)
        result = 31 * result + dataSources.hashCode()
        return result
    }

    override fun toString(): String =
        "RouteProvenance(providerId=$providerId, providerRouteId=$providerRouteId, " +
            "dataSources=$dataSources)"

    companion object {
        const val MaxProviderRouteIdLength: Int = 256
        const val MaxDataSources: Int = 32
        const val MaxDataSourceLength: Int = 128
    }
}

/** Immutable provider-neutral route snapshot consumed by application code. */
class RoutePlan(
    val id: RouteId,
    geometry: List<GeoPoint>,
    legs: List<RouteLeg>,
    val distanceMeters: Long,
    val durationSeconds: Long,
    val provenance: RouteProvenance,
) {
    val geometry: List<GeoPoint> = geometry.toList()
    val legs: List<RouteLeg> = legs.toList()

    init {
        require(this.geometry.size >= 2) { "route geometry needs at least two points" }
        require(this.legs.isNotEmpty()) { "route needs at least one leg" }
        require(distanceMeters > 0) { "route distance must be positive" }
        require(durationSeconds > 0) { "route duration must be positive" }
        require(this.legs.first().geometryStartIndex == 0) {
            "first leg must start at geometry index zero"
        }
        require(this.legs.last().geometryEndIndex == this.geometry.lastIndex) {
            "last leg must end at the final geometry point"
        }
        require(this.legs.zipWithNext().all { (first, second) ->
            first.geometryEndIndex == second.geometryStartIndex
        }) { "route legs must form one contiguous geometry range" }

        this.legs.forEach { leg ->
            require(leg.geometryEndIndex < this.geometry.size) {
                "leg geometry range exceeds route geometry"
            }
            require(leg.origin == this.geometry[leg.geometryStartIndex]) {
                "leg origin must equal its first geometry point"
            }
            require(leg.destination == this.geometry[leg.geometryEndIndex]) {
                "leg destination must equal its last geometry point"
            }
            leg.maneuvers.forEach { maneuver ->
                require(maneuver.location == this.geometry[maneuver.geometryIndex]) {
                    "maneuver location must equal its referenced geometry point"
                }
            }
        }

        require(checkedSum(this.legs.map(RouteLeg::distanceMeters), "leg distance") == distanceMeters) {
            "route distance must equal the checked sum of leg distances"
        }
        require(checkedSum(this.legs.map(RouteLeg::durationSeconds), "leg duration") == durationSeconds) {
            "route duration must equal the checked sum of leg durations"
        }
    }

    val origin: GeoPoint get() = geometry.first()
    val destination: GeoPoint get() = geometry.last()

    override fun equals(other: Any?): Boolean =
        other is RoutePlan &&
            id == other.id &&
            geometry == other.geometry &&
            legs == other.legs &&
            distanceMeters == other.distanceMeters &&
            durationSeconds == other.durationSeconds &&
            provenance == other.provenance

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + geometry.hashCode()
        result = 31 * result + legs.hashCode()
        result = 31 * result + distanceMeters.hashCode()
        result = 31 * result + durationSeconds.hashCode()
        result = 31 * result + provenance.hashCode()
        return result
    }

    override fun toString(): String =
        "RoutePlan(id=$id, geometry=$geometry, legs=$legs, distanceMeters=$distanceMeters, " +
            "durationSeconds=$durationSeconds, provenance=$provenance)"
}

/**
 * Verifies the postcondition relating one request to one canonical route.
 *
 * A canonical leg represents exactly one requested origin/waypoint/destination
 * segment. Providers with different internal leg semantics translate them in
 * their adapter before returning the route.
 */
fun RoutePlan.requireMatches(request: RouteRequest): RoutePlan {
    val stops = request.stops
    require(origin == request.origin) { "route origin differs from request origin" }
    require(destination == request.destination) { "route destination differs from request destination" }
    require(legs.size == stops.size - 1) {
        "route must contain exactly one canonical leg per requested stop segment"
    }
    legs.forEachIndexed { index, leg ->
        require(leg.origin == stops[index]) {
            "route leg $index origin differs from requested stop $index"
        }
        require(leg.destination == stops[index + 1]) {
            "route leg $index destination differs from requested stop ${index + 1}"
        }
    }
    return this
}

private fun checkedSum(values: Iterable<Long>, field: String): Long {
    var total = 0L
    for (value in values) {
        require(value >= 0) { "$field must not be negative" }
        require(total <= Long.MAX_VALUE - value) { "$field sum overflows Long" }
        total += value
    }
    return total
}
