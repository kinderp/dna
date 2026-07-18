package org.traveldna.navigation.offroute.contracts

import kotlin.test.Test
import kotlin.test.assertFailsWith
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.routing.contracts.RouteId

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

    @Test
    fun publicSuspectedAndConfirmedStatesRejectIncoherentEvidence() {
        val first = observation(1L, 1_000L, suspicious())
        val second = observation(2L, 3_000L, suspicious())

        assertFailsWith<IllegalArgumentException> {
            OffRouteState.Suspected(
                episodeId = OffRouteEpisodeId(1L),
                firstObservation = first,
                lastObservation = observation(2L, 3_000L, OffRouteEvidence.OnRoute),
                suspiciousCount = 2,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            OffRouteState.Suspected(
                episodeId = OffRouteEpisodeId(1L),
                firstObservation = first,
                lastObservation = second,
                suspiciousCount = 1,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            OffRouteState.Confirmed(
                episodeId = OffRouteEpisodeId(1L),
                firstSuspiciousObservation = first,
                confirmationObservation = second,
                suspiciousCount = 2,
                suspiciousDurationMillis = 1_999L,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            OffRouteState.Confirmed(
                episodeId = OffRouteEpisodeId(1L),
                firstSuspiciousObservation = first,
                confirmationObservation = observation(2L, 3_000L, OffRouteEvidence.OnRoute),
                suspiciousCount = 2,
                suspiciousDurationMillis = 2_000L,
            )
        }
    }
}

private fun suspicious(): OffRouteEvidence =
    OffRouteEvidence.Suspicious(OffRouteEvidenceReason.LowConfidence)

private fun observation(
    sequence: Long,
    time: Long,
    evidence: OffRouteEvidence,
): OffRouteObservation = OffRouteObservation(
    routeId = RouteId("contract-off-route-v0"),
    sample = LocationSample(
        sequence = LocationSequence(sequence),
        monotonicTime = MonotonicInstant(time),
        position = GeoPoint(0.0, sequence * 0.000_001),
        horizontalAccuracyMeters = 5.0,
        origin = LocationSampleOrigin.Replay,
    ),
    evidence = evidence,
)
