package org.traveldna.routing.fake

import org.traveldna.plugin.sdk.KnownPlatforms
import org.traveldna.plugin.sdk.PluginDescriptor
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RoutePlannerPort
import org.traveldna.routing.contracts.RoutePlanningError
import org.traveldna.routing.contracts.RoutePlanningErrorCode
import org.traveldna.routing.contracts.RoutePlanningResult
import org.traveldna.routing.contracts.RouteRequest
import org.traveldna.routing.contracts.RoutingCapabilities

/**
 * Deterministic, in-memory route planner for contract and application tests.
 *
 * This fake is intentionally single-threaded. It performs no I/O and matches
 * complete immutable requests exactly. Production concurrency behavior belongs
 * to provider adapters and integration tests, not to this fixture fake.
 */
class FakeRoutePlanner(
    catalog: Map<RouteRequest, List<RoutePlan>>,
    override val descriptor: PluginDescriptor = defaultDescriptor,
) : RoutePlannerPort {
    private val routesByRequest = catalog.mapValues { (_, routes) -> routes.toList() }
    private val calls = mutableListOf<RouteRequest>()

    val recordedRequests: List<RouteRequest> get() = calls.toList()

    init {
        require(RoutingCapabilities.Plan in descriptor.capabilities) {
            "fake route planner descriptor must declare routing.plan"
        }
        routesByRequest.forEach { (request, routes) ->
            require(routes.isNotEmpty()) { "fake catalog entry must contain at least one route" }
            routes.forEach { route ->
                require(route.origin == request.origin && route.destination == request.destination) {
                    "fake catalog route endpoints must match its request"
                }
                require(route.provenance.providerId == descriptor.id) {
                    "fake catalog route provenance must match the fake descriptor"
                }
            }
        }
    }

    override suspend fun plan(request: RouteRequest): RoutePlanningResult {
        calls += request
        val routes = routesByRequest[request]
            ?: return RoutePlanningResult.Failure(
                RoutePlanningError(
                    code = RoutePlanningErrorCode.NoRoute,
                    message = "The deterministic fake has no route for this exact request",
                    retryable = false,
                    providerDiagnosticCode = "fake.catalog-miss",
                ),
            )
        return RoutePlanningResult.Success(routes.take(request.requestedAlternatives))
    }

    companion object {
        val Id = PluginId("org.traveldna.fake-route-planner")

        val defaultDescriptor = PluginDescriptor(
            id = Id,
            implementationVersion = "0.1.0",
            contractVersion = 1,
            capabilities = setOf(
                RoutingCapabilities.Plan,
                RoutingCapabilities.Alternatives,
                RoutingCapabilities.Maneuvers,
                RoutingCapabilities.Offline,
                RoutingCapabilities.Deterministic,
            ),
            supportedPlatforms = setOf(
                KnownPlatforms.KotlinCommon,
                KnownPlatforms.Jvm,
                KnownPlatforms.LinuxX64,
            ),
        )
    }
}
