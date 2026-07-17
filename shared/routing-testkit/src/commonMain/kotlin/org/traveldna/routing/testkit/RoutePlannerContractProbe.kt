package org.traveldna.routing.testkit

import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RoutePlannerPort
import org.traveldna.routing.contracts.RoutePlanningErrorCode
import org.traveldna.routing.contracts.RoutePlanningResult
import org.traveldna.routing.contracts.RouteRequest
import org.traveldna.routing.contracts.RoutingCapabilities
import org.traveldna.routing.contracts.requireMatches

data class RoutePlannerContractReport(
    val providerId: String,
    val checks: List<String>,
)

/** Reusable conformance probe for deterministic route-planner test providers. */
object RoutePlannerContractProbe {
    suspend fun verify(
        planner: RoutePlannerPort,
        routableRequest: RouteRequest,
        unroutableRequest: RouteRequest,
    ): RoutePlannerContractReport {
        val checks = mutableListOf<String>()
        require(RoutingCapabilities.Plan in planner.descriptor.capabilities) {
            "route planner must declare routing.plan"
        }
        checks += "declares-routing-plan"

        val first = planner.plan(routableRequest).requireSuccess()
        require(first.routes.size <= routableRequest.requestedAlternatives) {
            "provider returned more alternatives than requested"
        }
        first.routes.forEach { route ->
            route.requireMatches(routableRequest)
            require(route.provenance.providerId == planner.descriptor.id) {
                "route provenance provider differs from plugin descriptor"
            }
        }
        checks += "returns-canonical-routes"

        if (RoutingCapabilities.Maneuvers in planner.descriptor.capabilities) {
            require(first.routes.all { route -> route.legs.all { it.maneuvers.isNotEmpty() } }) {
                "provider declares routing.maneuvers but returned an empty maneuver list"
            }
            checks += "returns-declared-maneuvers"
        }

        if (RoutingCapabilities.Deterministic in planner.descriptor.capabilities) {
            val second = planner.plan(routableRequest).requireSuccess()
            require(first == second) { "deterministic provider changed result for identical request" }
            checks += "deterministic-repeat"
        }

        val failure = planner.plan(unroutableRequest)
        require(failure is RoutePlanningResult.Failure) {
            "fixture-defined unroutable request unexpectedly succeeded"
        }
        require(failure.error.code == RoutePlanningErrorCode.NoRoute) {
            "unroutable request must produce NoRoute"
        }
        require(!failure.error.retryable) { "NoRoute must not be retryable" }
        checks += "canonical-no-route"

        return RoutePlannerContractReport(
            providerId = planner.descriptor.id.value,
            checks = checks.toList(),
        )
    }
}

private fun RoutePlanningResult.requireSuccess(): RoutePlanningResult.Success {
    require(this is RoutePlanningResult.Success) { "expected route-planning success but got $this" }
    require(routes.all(::hasCoherentManeuverRange)) { "route contains an invalid maneuver range" }
    return this
}

private fun hasCoherentManeuverRange(route: RoutePlan): Boolean =
    route.legs.flatMap { it.maneuvers }.all { it.geometryIndex in route.geometry.indices }
