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
        require(instruction.length <= 512) { "maneuver instruction is too long" }
        require(roadName == null || roadName.isNotBlank()) { "road name must be null or non-blank" }
        require(exitNumber == null || exitNumber.isNotBlank()) {
            "exit number must be null or non-blank"
        }
    }
}

data class RouteLeg(
    val geometryStartIndex: Int,
    val geometryEndIndex: Int,
    val origin: GeoPoint,
    val destination: GeoPoint,
    val distanceMeters: Long,
    val durationSeconds: Long,
    val maneuvers: List<RouteManeuver>,
) {
    init {
        require(geometryStartIndex >= 0) { "leg start index must be non-negative" }
        require(geometryEndIndex > geometryStartIndex) {
            "leg end index must be greater than its start index"
        }
        require(distanceMeters > 0) { "leg distance must be positive" }
        require(durationSeconds > 0) { "leg duration must be positive" }
        require(maneuvers.zipWithNext().none { (first, second) ->
            first.geometryIndex > second.geometryIndex
        }) { "leg maneuvers must be ordered by geometry index" }
        require(maneuvers.all { it.geometryIndex in geometryStartIndex..geometryEndIndex }) {
            "leg maneuver lies outside the leg geometry range"
        }
    }
}

data class RouteProvenance(
    val providerId: PluginId,
    val providerRouteId: String? = null,
    val dataSources: Set<String> = emptySet(),
) {
    init {
        require(providerRouteId == null || providerRouteId.isNotBlank()) {
            "provider route id must be null or non-blank"
        }
        require(dataSources.none { it.isBlank() }) { "data sources must not contain blanks" }
    }
}

data class RoutePlan(
    val id: RouteId,
    val geometry: List<GeoPoint>,
    val legs: List<RouteLeg>,
    val distanceMeters: Long,
    val durationSeconds: Long,
    val provenance: RouteProvenance,
) {
    init {
        require(geometry.size >= 2) { "route geometry needs at least two points" }
        require(legs.isNotEmpty()) { "route needs at least one leg" }
        require(distanceMeters > 0) { "route distance must be positive" }
        require(durationSeconds > 0) { "route duration must be positive" }
        require(legs.first().geometryStartIndex == 0) {
            "first leg must start at geometry index zero"
        }
        require(legs.last().geometryEndIndex == geometry.lastIndex) {
            "last leg must end at the final geometry point"
        }
        require(legs.zipWithNext().all { (first, second) ->
            first.geometryEndIndex == second.geometryStartIndex
        }) { "route legs must form one contiguous geometry range" }

        legs.forEach { leg ->
            require(leg.geometryEndIndex < geometry.size) { "leg geometry range exceeds route geometry" }
            require(leg.origin == geometry[leg.geometryStartIndex]) {
                "leg origin must equal its first geometry point"
            }
            require(leg.destination == geometry[leg.geometryEndIndex]) {
                "leg destination must equal its last geometry point"
            }
            leg.maneuvers.forEach { maneuver ->
                require(maneuver.location == geometry[maneuver.geometryIndex]) {
                    "maneuver location must equal its referenced geometry point"
                }
            }
        }

        require(checkedSum(legs.map(RouteLeg::distanceMeters), "leg distance") == distanceMeters) {
            "route distance must equal the checked sum of leg distances"
        }
        require(checkedSum(legs.map(RouteLeg::durationSeconds), "leg duration") == durationSeconds) {
            "route duration must equal the checked sum of leg durations"
        }
    }

    val origin: GeoPoint get() = geometry.first()
    val destination: GeoPoint get() = geometry.last()
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
