package org.traveldna.navigation.offroute.contracts

import kotlin.jvm.JvmInline
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RoutePlanningError
import org.traveldna.routing.contracts.RouteRequest

@JvmInline
value class OffRouteEpisodeId(val value: Long) {
    init {
        require(value > 0L) { "off-route episode id must be positive" }
    }
}

@JvmInline
value class RerouteAttemptId(val value: Long) {
    init {
        require(value > 0L) { "reroute attempt id must be positive" }
    }
}

enum class OffRouteEvidenceReason {
    Unmatched,
    LowConfidence,
    LateralDeviation,
    MissedExpectedManeuver,
}

sealed interface OffRouteEvidence {
    data object OnRoute : OffRouteEvidence

    data class Suspicious(
        val reason: OffRouteEvidenceReason,
    ) : OffRouteEvidence

    data class Indeterminate(
        val diagnosticCode: String? = null,
    ) : OffRouteEvidence {
        init {
            require(
                diagnosticCode == null ||
                    (diagnosticCode.isNotBlank() && diagnosticCode.length <= MaxDiagnosticCodeLength),
            ) {
                "indeterminate diagnostic code must be null or non-blank and at most " +
                    "$MaxDiagnosticCodeLength characters"
            }
        }

        companion object {
            const val MaxDiagnosticCodeLength: Int = 128
        }
    }
}

data class OffRouteObservation(
    val routeId: RouteId,
    val sample: LocationSample,
    val evidence: OffRouteEvidence,
)

data class OffRoutePolicy(
    val requiredConsecutiveSuspicious: Int = 3,
    val minimumSuspiciousDurationMillis: Long = 2_000L,
) {
    init {
        require(requiredConsecutiveSuspicious in 2..MaxConsecutiveSuspicious) {
            "required suspicious count must be within [2, $MaxConsecutiveSuspicious]"
        }
        require(minimumSuspiciousDurationMillis in 1L..MaxSuspiciousDurationMillis) {
            "minimum suspicious duration must be within [1, $MaxSuspiciousDurationMillis] ms"
        }
    }

    companion object {
        const val MaxConsecutiveSuspicious: Int = 20
        const val MaxSuspiciousDurationMillis: Long = 120_000L
    }
}

sealed interface OffRouteState {
    data object OnRoute : OffRouteState

    data class Suspected(
        val episodeId: OffRouteEpisodeId,
        val firstObservation: OffRouteObservation,
        val lastObservation: OffRouteObservation,
        val suspiciousCount: Int,
    ) : OffRouteState {
        init {
            require(firstObservation.routeId == lastObservation.routeId) {
                "suspected observations must target the same route"
            }
            require(lastObservation.sample.sequence >= firstObservation.sample.sequence) {
                "suspected observation sequence cannot move backwards"
            }
            require(lastObservation.sample.monotonicTime >= firstObservation.sample.monotonicTime) {
                "suspected observation time cannot move backwards"
            }
            require(suspiciousCount in 1..OffRoutePolicy.MaxConsecutiveSuspicious) {
                "suspicious count lies outside the bounded policy range"
            }
        }
    }

    data class Confirmed(
        val episodeId: OffRouteEpisodeId,
        val firstSuspiciousObservation: OffRouteObservation,
        val confirmationObservation: OffRouteObservation,
        val suspiciousCount: Int,
        val suspiciousDurationMillis: Long,
    ) : OffRouteState {
        init {
            require(firstSuspiciousObservation.routeId == confirmationObservation.routeId) {
                "confirmed observations must target the same route"
            }
            require(suspiciousCount in 2..OffRoutePolicy.MaxConsecutiveSuspicious) {
                "confirmed suspicious count lies outside the bounded policy range"
            }
            require(suspiciousDurationMillis > 0L) {
                "confirmed suspicious duration must be positive"
            }
        }
    }
}

enum class OffRouteTransition {
    StayedOnRoute,
    SuspicionStarted,
    SuspicionContinued,
    HeldIndeterminate,
    Recovered,
    ConfirmedNow,
    StayedConfirmed,
}

enum class OffRouteRejectionReason {
    WrongRoute,
    NonIncreasingSequence,
    NonIncreasingMonotonicTime,
}

sealed interface OffRouteDecision {
    val observation: OffRouteObservation

    data class Accepted(
        override val observation: OffRouteObservation,
        val previousState: OffRouteState,
        val state: OffRouteState,
        val transition: OffRouteTransition,
    ) : OffRouteDecision

    data class Rejected(
        override val observation: OffRouteObservation,
        val reason: OffRouteRejectionReason,
        val state: OffRouteState,
        val lastAcceptedObservation: OffRouteObservation?,
    ) : OffRouteDecision
}

data class RerouteCommand(
    val attemptId: RerouteAttemptId,
    val episodeId: OffRouteEpisodeId,
    val sourceRouteId: RouteId,
    val request: RouteRequest,
)

sealed interface RerouteOutcome {
    val attemptId: RerouteAttemptId
    val sourceRouteId: RouteId

    data class Planned(
        override val attemptId: RerouteAttemptId,
        override val sourceRouteId: RouteId,
        val route: RoutePlan,
    ) : RerouteOutcome

    data class Failed(
        override val attemptId: RerouteAttemptId,
        override val sourceRouteId: RouteId,
        val error: RoutePlanningError,
    ) : RerouteOutcome
}

internal fun LocationSequence.isStrictlyAfter(other: LocationSequence): Boolean = this > other

internal fun MonotonicInstant.isStrictlyAfter(other: MonotonicInstant): Boolean = this > other
