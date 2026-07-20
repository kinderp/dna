package org.traveldna.lab.routing

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import org.traveldna.routing.contracts.RoutePlanningResult
import org.traveldna.routing.fake.FakeRouteFixtures

fun main() {
    val planner = FakeRouteFixtures.planner()
    val result = runImmediate { planner.plan(FakeRouteFixtures.ReferenceRequest) }
    when (result) {
        is RoutePlanningResult.Success -> {
            val route = result.routes.single()
            val capabilities = planner.descriptor.capabilities
                .map { it.value }
                .sorted()
                .joinToString(separator = "\",\"", prefix = "[\"", postfix = "\"]")
            println(
                "{\"provider\":\"${planner.descriptor.id.value}\"," +
                    "\"capabilities\":$capabilities," +
                    "\"result\":\"success\"," +
                    "\"route_id\":\"${route.id.value}\"," +
                    "\"geometry_points\":${route.geometry.size}," +
                    "\"maneuvers\":${route.legs.sumOf { it.maneuvers.size }}," +
                    "\"distance_m\":${route.distanceMeters}," +
                    "\"duration_s\":${route.durationSeconds}}",
            )
        }
        is RoutePlanningResult.Failure -> {
            println(
                "{\"provider\":\"${planner.descriptor.id.value}\"," +
                    "\"result\":\"failure\"," +
                    "\"error\":\"${result.error.code}\"}",
            )
        }
    }
}

private fun <T> runImmediate(block: suspend () -> T): T {
    var outcome: Result<T>? = null
    block.startCoroutine(object : Continuation<T> {
        override val context = EmptyCoroutineContext
        override fun resumeWith(result: Result<T>) {
            outcome = result
        }
    })
    return checkNotNull(outcome) { "fake provider unexpectedly suspended" }.getOrThrow()
}
