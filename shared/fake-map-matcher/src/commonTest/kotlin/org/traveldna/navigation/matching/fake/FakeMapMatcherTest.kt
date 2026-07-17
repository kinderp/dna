package org.traveldna.navigation.matching.fake

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.navigation.contracts.MatchConfidence
import org.traveldna.navigation.contracts.MatchedRoutePosition
import org.traveldna.navigation.contracts.RouteCoordinate
import org.traveldna.navigation.matching.contracts.MapMatchProvenance
import org.traveldna.navigation.matching.contracts.MapMatchResult
import org.traveldna.navigation.matching.testkit.MapMatcherContractProbe

class FakeMapMatcherTest {
    @Test
    fun passesTheReusableContractProbe() {
        val matcher = FakeMapMatchFixtures.matcher()
        val report = runImmediate {
            MapMatcherContractProbe.verify(
                matcher,
                FakeMapMatchFixtures.MatchedRequest,
                FakeMapMatchFixtures.UnmatchedRequest,
            )
        }
        assertEquals(FakeMapMatcher.Id.value, report.providerId)
        assertEquals(4, report.checks.size)
    }

    @Test
    fun catalogMissIsAnExplicitUnmatchedOutcome() {
        val matcher = FakeMapMatcher(emptyList())
        val result = runImmediate { matcher.match(FakeMapMatchFixtures.MatchedRequest) }
        val unmatched = assertIs<MapMatchResult.Unmatched>(result)
        assertEquals("fake.catalog-miss", unmatched.unmatched.providerDiagnosticCode)
    }

    @Test
    fun requestRecordingIsBoundedAndResettable() {
        val matcher = FakeMapMatchFixtures.matcher(maxRecordedRequests = 2)
        runImmediate {
            FakeMapMatchFixtures.Requests.take(3).forEach { matcher.match(it) }
        }
        assertEquals(3L, matcher.totalRequestCount)
        assertEquals(FakeMapMatchFixtures.Requests.subList(1, 3), matcher.recordedRequests)
        matcher.resetRecordedRequests()
        assertEquals(0L, matcher.totalRequestCount)
        assertEquals(emptyList(), matcher.recordedRequests)
    }

    @Test
    fun invalidMatchedCatalogEntryIsRejectedAtConstruction() {
        val request = FakeMapMatchFixtures.MatchedRequest
        val invalid = MapMatchResult.Matched(
            position = MatchedRoutePosition(
                routeId = request.route.id,
                sampleSequence = LocationSequence(99),
                monotonicTime = request.sample.monotonicTime,
                coordinate = RouteCoordinate(0, 0.0),
                lateralDistanceMeters = 0.0,
                confidence = MatchConfidence.High,
            ),
            provenance = MapMatchProvenance(FakeMapMatcher.Id),
        )
        assertFailsWith<IllegalArgumentException> {
            FakeMapMatcher(listOf(FakeMapMatchEntry(request, invalid)))
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
    return checkNotNull(outcome) { "deterministic fake unexpectedly suspended" }.getOrThrow()
}
