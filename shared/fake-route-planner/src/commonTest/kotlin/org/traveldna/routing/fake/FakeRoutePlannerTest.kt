package org.traveldna.routing.fake

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import org.traveldna.routing.contracts.RoutePlanningErrorCode
import org.traveldna.routing.contracts.RoutePlanningResult
import org.traveldna.routing.testkit.RoutePlannerContractProbe

class FakeRoutePlannerTest {
    @Test
    fun returnsCanonicalRouteAndRecordsCalls() = runImmediate {
        val planner = FakeRouteFixtures.planner()
        val result = planner.plan(FakeRouteFixtures.ReferenceRequest)
        val success = assertIs<RoutePlanningResult.Success>(result)
        assertEquals(FakeRouteFixtures.ReferenceRoute, success.routes.single())
        assertEquals(listOf(FakeRouteFixtures.ReferenceRequest), planner.recordedRequests)
    }

    @Test
    fun catalogMissReturnsCanonicalNoRouteFailure() = runImmediate {
        val planner = FakeRouteFixtures.planner()
        val result = planner.plan(FakeRouteFixtures.UnroutableRequest)
        val failure = assertIs<RoutePlanningResult.Failure>(result)
        assertEquals(RoutePlanningErrorCode.NoRoute, failure.error.code)
        assertEquals(false, failure.error.retryable)
    }

    @Test
    fun passesReusablePlannerContractProbe() = runImmediate {
        val report = RoutePlannerContractProbe.verify(
            planner = FakeRouteFixtures.planner(),
            routableRequest = FakeRouteFixtures.ReferenceRequest,
            unroutableRequest = FakeRouteFixtures.UnroutableRequest,
        )
        assertEquals(FakeRoutePlanner.Id.value, report.providerId)
        assertEquals(
            listOf("declares-routing-plan", "returns-canonical-routes", "deterministic-repeat", "canonical-no-route"),
            report.checks,
        )
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
    return checkNotNull(outcome) {
        "test fake suspended; use a coroutine test runner for asynchronous providers"
    }.getOrThrow()
}
