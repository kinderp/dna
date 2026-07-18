package org.traveldna.navigation.offroute.contracts

import kotlin.test.Test
import kotlin.test.assertFailsWith

class OffRouteContractsTest {
    @Test
    fun policyAndDiagnosticsAreBounded() {
        assertFailsWith<IllegalArgumentException> { OffRoutePolicy(1, 1_000L) }
        assertFailsWith<IllegalArgumentException> { OffRoutePolicy(21, 1_000L) }
        assertFailsWith<IllegalArgumentException> { OffRoutePolicy(3, 0L) }
        assertFailsWith<IllegalArgumentException> { OffRoutePolicy(3, 120_001L) }
        assertFailsWith<IllegalArgumentException> {
            OffRouteEvidence.Indeterminate("x".repeat(129))
        }
        assertFailsWith<IllegalArgumentException> { OffRouteEpisodeId(0L) }
        assertFailsWith<IllegalArgumentException> { RerouteAttemptId(0L) }
    }
}
