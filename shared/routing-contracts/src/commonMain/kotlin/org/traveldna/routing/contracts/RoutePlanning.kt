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
        require(
            providerDiagnosticCode == null ||
                (providerDiagnosticCode.isNotBlank() && providerDiagnosticCode.length <= 128),
        ) { "provider diagnostic code must be null or non-blank and at most 128 characters" }
        if (code == RoutePlanningErrorCode.InvalidRequest || code == RoutePlanningErrorCode.NoRoute) {
            require(!retryable) { "$code errors are not retryable without changing the request" }
        }
    }
}

sealed interface RoutePlanningResult {
    /** Immutable successful response containing one or more unique alternatives. */
    class Success(routes: List<RoutePlan>) : RoutePlanningResult {
        val routes: List<RoutePlan> = routes.toList()

        init {
            require(this.routes.isNotEmpty()) {
                "successful route planning needs at least one route"
            }
            require(this.routes.size <= RouteRequest.MaxAlternatives) {
                "successful route planning may contain at most ${RouteRequest.MaxAlternatives} routes"
            }
            require(this.routes.map(RoutePlan::id).toSet().size == this.routes.size) {
                "successful route planning must not contain duplicate route ids"
            }
        }

        override fun equals(other: Any?): Boolean =
            other is Success && routes == other.routes

        override fun hashCode(): Int = routes.hashCode()

        override fun toString(): String = "Success(routes=$routes)"
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
