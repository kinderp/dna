package org.traveldna.navigation.matching.fake

import org.traveldna.location.contracts.LocationSample
import org.traveldna.navigation.matching.contracts.MapMatchResult
import org.traveldna.navigation.matching.contracts.MapMatchSession
import org.traveldna.navigation.matching.contracts.MapMatchUnmatched
import org.traveldna.navigation.matching.contracts.MapMatchUnmatchedReason
import org.traveldna.navigation.matching.contracts.MapMatcherPort
import org.traveldna.navigation.matching.contracts.MapMatchingCapabilities
import org.traveldna.navigation.matching.contracts.requireMatches
import org.traveldna.plugin.sdk.KnownPlatforms
import org.traveldna.plugin.sdk.PluginDescriptor
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RoutePlan

data class FakeMapMatchEntry(
    val route: RoutePlan,
    val sample: LocationSample,
    val result: MapMatchResult,
)

data class FakeMapMatchCall(
    val routeId: RouteId,
    val sample: LocationSample,
)

/**
 * Single-threaded deterministic exact-catalog matcher; it performs no search or
 * snapping. Catalog lookup and bounded diagnostic recording are O(1) amortized.
 */
class FakeMapMatcher(
    entries: List<FakeMapMatchEntry>,
    override val descriptor: PluginDescriptor = defaultDescriptor,
    private val maxRecordedCalls: Int = DefaultMaxRecordedCalls,
) : MapMatcherPort {
    private data class Key(val routeId: RouteId, val sample: LocationSample)

    private val routesById: Map<RouteId, RoutePlan>
    private val outcomesByKey: Map<Key, MapMatchResult>
    private val recentCalls = ArrayDeque<FakeMapMatchCall>()
    private var matchCount: Long = 0L

    val recordedCalls: List<FakeMapMatchCall> get() = recentCalls.toList()
    val totalMatchCount: Long get() = matchCount

    init {
        require(MapMatchingCapabilities.MatchRoute in descriptor.capabilities) {
            "fake matcher descriptor must declare navigation.map-match"
        }
        require(maxRecordedCalls in 1..MaxRecordedCalls) {
            "max recorded calls must be within [1, $MaxRecordedCalls]"
        }
        require(entries.size <= MaxCatalogEntries) {
            "fake map-match catalog may contain at most $MaxCatalogEntries entries"
        }

        val routes = linkedMapOf<RouteId, RoutePlan>()
        val outcomes = linkedMapOf<Key, MapMatchResult>()
        entries.forEach { entry ->
            val existingRoute = routes[entry.route.id]
            if (existingRoute == null) {
                routes[entry.route.id] = entry.route
            } else {
                require(existingRoute == entry.route) {
                    "fake catalog reuses a route id for a different canonical route"
                }
            }
            if (entry.result is MapMatchResult.Matched) {
                entry.result.requireMatches(entry.route, entry.sample, descriptor.id)
            }
            require(outcomes.put(Key(entry.route.id, entry.sample), entry.result) == null) {
                "fake catalog contains duplicate route/sample keys"
            }
        }
        routesById = routes.toMap()
        outcomesByKey = outcomes.toMap()
    }

    override fun bind(route: RoutePlan): MapMatchSession {
        val catalogRoute = routesById[route.id]
        require(catalogRoute == null || catalogRoute == route) {
            "fake matcher cannot bind a different route snapshot with a reused route id"
        }
        return Session(route)
    }

    fun resetRecordedCalls() {
        recentCalls.clear()
        matchCount = 0L
    }

    private inner class Session(
        override val route: RoutePlan,
    ) : MapMatchSession {
        override suspend fun match(sample: LocationSample): MapMatchResult {
            record(route.id, sample)
            return outcomesByKey[Key(route.id, sample)]
                ?: MapMatchResult.Unmatched(
                    MapMatchUnmatched(
                        reason = MapMatchUnmatchedReason.NoCandidate,
                        providerDiagnosticCode = "fake.catalog-miss",
                    ),
                )
        }
    }

    private fun record(routeId: RouteId, sample: LocationSample) {
        check(matchCount < Long.MAX_VALUE) { "fake map-matcher call counter overflow" }
        matchCount += 1L
        if (recentCalls.size == maxRecordedCalls) {
            recentCalls.removeFirst()
        }
        recentCalls.addLast(FakeMapMatchCall(routeId, sample))
    }

    companion object {
        const val MaxCatalogEntries: Int = 100_000
        const val MaxRecordedCalls: Int = 10_000
        const val DefaultMaxRecordedCalls: Int = 1_024

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
