package org.traveldna.navigation.offroute

import org.traveldna.navigation.offroute.contracts.OffRouteDecision
import org.traveldna.navigation.offroute.contracts.OffRouteEpisodeId
import org.traveldna.navigation.offroute.contracts.OffRouteEvidence
import org.traveldna.navigation.offroute.contracts.OffRouteObservation
import org.traveldna.navigation.offroute.contracts.OffRoutePolicy
import org.traveldna.navigation.offroute.contracts.OffRouteRejectionReason
import org.traveldna.navigation.offroute.contracts.OffRouteState
import org.traveldna.navigation.offroute.contracts.OffRouteTransition
import org.traveldna.routing.contracts.RouteId

/**
 * Single-owner, bounded-state off-route confirmation tracker.
 *
 * Evidence is already normalized by an upstream policy. One suspicious sample
 * starts an episode but confirmation requires both the configured count and
 * monotonic duration. Rejections never mutate state or the accepted baseline.
 */
class OffRouteTracker(
    val routeId: RouteId,
    val policy: OffRoutePolicy = OffRoutePolicy(),
) {
    var state: OffRouteState = OffRouteState.OnRoute
        private set

    var lastAcceptedObservation: OffRouteObservation? = null
        private set

    private var episodeCounter: Long = 0L

    fun inspect(observation: OffRouteObservation): OffRouteDecision {
        rejection(observation)?.let { reason ->
            return OffRouteDecision.Rejected(
                observation = observation,
                reason = reason,
                state = state,
                lastAcceptedObservation = lastAcceptedObservation,
            )
        }
        val previous = state
        val (next, transition) = nextState(previous, observation)
        return OffRouteDecision.Accepted(observation, previous, next, transition)
    }

    fun accept(observation: OffRouteObservation): OffRouteDecision {
        val decision = inspect(observation)
        if (decision is OffRouteDecision.Accepted) {
            if (decision.transition == OffRouteTransition.SuspicionStarted) {
                episodeCounter = (decision.state as OffRouteState.Suspected).episodeId.value
            }
            state = decision.state
            lastAcceptedObservation = observation
        }
        return decision
    }

    /** Starts a new evidence stream for the same installed route. */
    fun reset() {
        state = OffRouteState.OnRoute
        lastAcceptedObservation = null
        episodeCounter = 0L
    }

    private fun rejection(observation: OffRouteObservation): OffRouteRejectionReason? {
        if (observation.routeId != routeId) return OffRouteRejectionReason.WrongRoute
        val previous = lastAcceptedObservation ?: return null
        if (observation.sample.sequence <= previous.sample.sequence) {
            return OffRouteRejectionReason.NonIncreasingSequence
        }
        if (observation.sample.monotonicTime <= previous.sample.monotonicTime) {
            return OffRouteRejectionReason.NonIncreasingMonotonicTime
        }
        return null
    }

    private fun nextState(
        current: OffRouteState,
        observation: OffRouteObservation,
    ): Pair<OffRouteState, OffRouteTransition> = when (current) {
        OffRouteState.OnRoute -> when (observation.evidence) {
            OffRouteEvidence.OnRoute -> current to OffRouteTransition.StayedOnRoute
            is OffRouteEvidence.Indeterminate -> current to OffRouteTransition.HeldIndeterminate
            is OffRouteEvidence.Suspicious -> {
                val suspected = OffRouteState.Suspected(
                    episodeId = nextEpisodeId(),
                    firstObservation = observation,
                    lastSuspiciousObservation = observation,
                    suspiciousCount = 1,
                )
                suspected to OffRouteTransition.SuspicionStarted
            }
        }

        is OffRouteState.Suspected -> when (observation.evidence) {
            OffRouteEvidence.OnRoute -> OffRouteState.OnRoute to OffRouteTransition.Recovered
            is OffRouteEvidence.Indeterminate -> current to OffRouteTransition.HeldIndeterminate
            is OffRouteEvidence.Suspicious -> continueSuspicion(current, observation)
        }

        is OffRouteState.Confirmed -> current to OffRouteTransition.StayedConfirmed
    }

    private fun continueSuspicion(
        current: OffRouteState.Suspected,
        observation: OffRouteObservation,
    ): Pair<OffRouteState, OffRouteTransition> {
        val nextCount = minOf(
            current.suspiciousCount + 1,
            policy.requiredConsecutiveSuspicious,
        )
        val duration = observation.sample.monotonicTime.elapsedSince(
            current.firstObservation.sample.monotonicTime,
        )
        return if (
            nextCount >= policy.requiredConsecutiveSuspicious &&
            duration >= policy.minimumSuspiciousDurationMillis
        ) {
            OffRouteState.Confirmed(
                episodeId = current.episodeId,
                firstSuspiciousObservation = current.firstObservation,
                confirmationObservation = observation,
                suspiciousCount = nextCount,
                suspiciousDurationMillis = duration,
            ) to OffRouteTransition.ConfirmedNow
        } else {
            current.copy(
                lastSuspiciousObservation = observation,
                suspiciousCount = nextCount,
            ) to OffRouteTransition.SuspicionContinued
        }
    }

    private fun nextEpisodeId(): OffRouteEpisodeId {
        check(episodeCounter < Long.MAX_VALUE) { "off-route episode id overflow" }
        return OffRouteEpisodeId(episodeCounter + 1L)
    }
}
