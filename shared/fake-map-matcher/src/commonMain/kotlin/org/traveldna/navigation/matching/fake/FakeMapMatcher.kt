package org.traveldna.navigation.matching.fake

import org.traveldna.location.contracts.LocationSample
import org.traveldna.navigation.matching.contracts.MapMatchRequest
import org.traveldna.navigation.matching.contracts.MapMatchResult
import org.traveldna.navigation.matching.contracts.MapMatchUnmatched
import org.traveldna.navigation.matching.contracts.MapMatchUnmatchedReason
import org.traveldna.navigation.matching.contracts.MapMatcherPort
import org.traveldna.navigation.matching.contracts.MapMatchingCapabilities
import org.traveldna.navigation.matching.contracts.requireMatches
import org.traveldna.plugin.sdk.KnownPlatforms
import org.traveldna.plugin.sdk.PluginDescriptor
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.RouteId

data class FakeMapMatchEntry(
    val request: MapMatchRequest,
    val result: MapMatchResult,
)

/**
 * Deterministic exact-catalog map matcher for tests and teaching.
 *
 * It performs no geometric search or snapping. Lookup is indexed by route ID and
 * the immutable location sample, avoiding a hash of the full route per call.
 */
class FakeMapMatcher(
    entries: List<FakeMapMatchEntry>,
    override val descriptor: PluginDescriptor = defaultDescriptor,
    private val maxRecordedRequests: Int = DefaultMaxRecordedRequests,
) : MapMatcherPort {
    private data class Key(val routeId: RouteId, val sample: LocationSample)

    private val outcomesByKey: Map<Key, MapMatchResult>
    private val recentRequests = mutableListOf<MapMatchRequest>()
    private var requestCount: Long = 0L

    val recordedRequests: List<MapMatchRequest> get() = recentRequests.toList()
    val totalRequestCount: Long get() = requestCount

    init {
        require(MapMatchingCapabilities.MatchRoute in descriptor.capabilities) {
            "fake matcher descriptor must declare navigation.map-match"
        }
        require(maxRecordedRequests in 1..MaxRecordedRequests) {
            "max recorded requests must be within [1, $MaxRecordedRequests]"
        }
        require(entries.size <= MaxCatalogEntries) {
            "fake map-match catalog may contain at most $MaxCatalogEntries entries"
        }
        val indexed = linkedMapOf<Key, MapMatchResult>()
        entries.forEach { entry ->
            if (entry.result is MapMatchResult.Matched) {
                entry.result.requireMatches(entry.request, descriptor.id)
            }
            val previous = indexed.put(Key(entry.request.route.id, entry.request.sample), entry.result)
            require(previous == null) {
                "fake map-match catalog contains duplicate route/sample keys"
            }
        }
        outcomesByKey = indexed.toMap()
    }

    override suspend fun match(request: MapMatchRequest): MapMatchResult {
        record(request)
        return outcomesByKey[Key(request.route.id, request.sample)]
            ?: MapMatchResult.Unmatched(
                MapMatchUnmatched(
                    reason = MapMatchUnmatchedReason.NoCandidate,
                    providerDiagnosticCode = "fake.catalog-miss",
                ),
            )
    }

    fun resetRecordedRequests() {
        recentRequests.clear()
        requestCount = 0L
    }

    private fun record(request: MapMatchRequest) {
        check(requestCount < Long.MAX_VALUE) { "fake map-matcher request counter overflow" }
        requestCount += 1L
        if (recentRequests.size == maxRecordedRequests) {
            recentRequests.removeAt(0)
        }
        recentRequests += request
    }

    companion object {
        const val MaxCatalogEntries: Int = 100_000
        const val MaxRecordedRequests: Int = 10_000
        const val DefaultMaxRecordedRequests: Int = 1_024

        val Id = PluginId("org.traveldna.fake-map-matcher")

        val defaultDescriptor = PluginDescriptor(
            id = Id,
            implementationVersion = "0.1.0",
            contractVersion = 1,
            capabilities = setOf(
                MapMatchingCapabilities.MatchRoute,
                MapMatchingCapabilities.Deterministic,
                MapMatchingCapabilities.Offline,
            ),
            supportedPlatforms = setOf(KnownPlatforms.Jvm, KnownPlatforms.LinuxX64),
        )
    }
}
