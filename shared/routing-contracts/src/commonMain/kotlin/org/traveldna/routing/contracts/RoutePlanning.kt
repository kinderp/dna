package org.traveldna.routing.contracts

import org.traveldna.plugin.sdk.CapabilityId
import org.traveldna.plugin.sdk.TravelDnaPlugin

object RoutingCapabilities {
    val Plan = CapabilityId("routing.plan")
    val Alternatives = CapabilityId("routing.alternatives")
    val Maneuvers = CapabilityId("routing.maneuvers")
    val Offline = CapabilityId("routing.offline")
    val Deterministic = CapabilityId("routing.deterministic")
}

enum class RoutePlanningErrorCode {
    InvalidRequest,
    NoRoute,
    UnsupportedProfile,
    ProviderUnavailable,
    Timeout,
    RateLimited,
    Internal,
}

data class RoutePlanningError(
    val code: RoutePlanningErrorCode,
    val message: String,
    val retryable: Boolean,
    val providerDiagnosticCode: String? = null,
) {
    init {
        require(message.isNotBlank()) { "routing error message must not be blank" }
        require(message.length <= 512) { "routing error message is too long" }
        require(providerDiagnosticCode == null || providerDiagnosticCode.isNotBlank()) {
            "provider diagnostic code must be null or non-blank"
        }
        if (code == RoutePlanningErrorCode.InvalidRequest || code == RoutePlanningErrorCode.NoRoute) {
            require(!retryable) { "$code errors are not retryable without changing the request" }
        }
    }
}

sealed interface RoutePlanningResult {
    data class Success(val routes: List<RoutePlan>) : RoutePlanningResult {
        init {
            require(routes.isNotEmpty()) { "successful route planning needs at least one route" }
            require(routes.map(RoutePlan::id).toSet().size == routes.size) {
                "successful route planning must not contain duplicate route ids"
            }
        }
    }

    data class Failure(val error: RoutePlanningError) : RoutePlanningResult
}

/**
 * Provider-neutral route planning boundary.
 *
 * Implementations translate provider data and failures into Travel DNA models.
 * Coroutine cancellation is not a provider failure and must propagate normally.
 */
interface RoutePlannerPort : TravelDnaPlugin {
    suspend fun plan(request: RouteRequest): RoutePlanningResult
}
