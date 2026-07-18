package org.traveldna.navigation.reroute

import kotlin.coroutines.cancellation.CancellationException
import org.traveldna.navigation.offroute.contracts.OffRouteState
import org.traveldna.navigation.offroute.contracts.RerouteAttemptId
import org.traveldna.navigation.offroute.contracts.RerouteCommand
import org.traveldna.navigation.offroute.contracts.RerouteOutcome
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RoutePlannerPort
import org.traveldna.routing.contracts.RoutePlanningError
import org.traveldna.routing.contracts.RoutePlanningErrorCode
import org.traveldna.routing.contracts.RoutePlanningResult
import org.traveldna.routing.contracts.RouteRequest
import org.traveldna.routing.contracts.RoutingProfile
import org.traveldna.routing.contracts.requireMatches

sealed interface RerouteCoordinatorState {
    val activeRoute: RoutePlan

    data class Ready(
        override val activeRoute: RoutePlan,
        val lastFailure: RoutePlanningError? = null,
    ) : RerouteCoordinatorState

    data class InFlight(
        override val activeRoute: RoutePlan,
        val command: RerouteCommand,
    ) : RerouteCoordinatorState
}

enum class RerouteBeginIgnoreReason {
    WrongRoute,
    AlreadyInFlight,
    AlreadyAtDestination,
}

sealed interface RerouteBeginDecision {
    data class Started(
        val command: RerouteCommand,
        val state: RerouteCoordinatorState.InFlight,
    ) : RerouteBeginDecision

    data class Ignored(
        val reason: RerouteBeginIgnoreReason,
        val state: RerouteCoordinatorState,
    ) : RerouteBeginDecision
}

sealed interface RerouteApplyDecision {
    val activeRoute: RoutePlan

    data class Replaced(
        val attemptId: RerouteAttemptId,
        val previousRoute: RoutePlan,
        override val activeRoute: RoutePlan,
    ) : RerouteApplyDecision

    data class Failed(
        val attemptId: RerouteAttemptId,
        override val activeRoute: RoutePlan,
        val error: RoutePlanningError,
    ) : RerouteApplyDecision

    data class InvalidReplacement(
        val attemptId: RerouteAttemptId,
        override val activeRoute: RoutePlan,
        val error: RoutePlanningError,
    ) : RerouteApplyDecision

    data class Cancelled(
        val attemptId: RerouteAttemptId,
        override val activeRoute: RoutePlan,
    ) : RerouteApplyDecision

    data class Stale(
        val attemptId: RerouteAttemptId,
        override val activeRoute: RoutePlan,
    ) : RerouteApplyDecision
}

/**
 * Single-owner coordinator that retains the old route until a correlated,
 * canonical replacement has been fully validated.
 */
class RerouteCoordinator(
    initialRoute: RoutePlan,
    private val profile: RoutingProfile = RoutingProfile.Driving,
) {
    var state: RerouteCoordinatorState = RerouteCoordinatorState.Ready(initialRoute)
        private set

    private var attemptCounter: Long = 0L

    val activeRoute: RoutePlan get() = state.activeRoute
    val pendingCommand: RerouteCommand?
        get() = (state as? RerouteCoordinatorState.InFlight)?.command

    fun begin(confirmation: OffRouteState.Confirmed): RerouteBeginDecision {
        val current = state
        if (current is RerouteCoordinatorState.InFlight) {
            return RerouteBeginDecision.Ignored(RerouteBeginIgnoreReason.AlreadyInFlight, current)
        }
        val route = current.activeRoute
        if (confirmation.confirmationObservation.routeId != route.id) {
            return RerouteBeginDecision.Ignored(RerouteBeginIgnoreReason.WrongRoute, current)
        }
        val origin = confirmation.confirmationObservation.sample.position
        if (origin == route.destination) {
            return RerouteBeginDecision.Ignored(RerouteBeginIgnoreReason.AlreadyAtDestination, current)
        }
        val attemptId = nextAttemptId()
        val command = RerouteCommand(
            attemptId = attemptId,
            episodeId = confirmation.episodeId,
            sourceRouteId = route.id,
            request = RouteRequest(
                origin = origin,
                destination = route.destination,
                profile = profile,
                requestedAlternatives = 1,
            ),
        )
        val next = RerouteCoordinatorState.InFlight(route, command)
        attemptCounter = attemptId.value
        state = next
        return RerouteBeginDecision.Started(command, next)
    }

    fun apply(outcome: RerouteOutcome): RerouteApplyDecision {
        val current = state
        if (
            current !is RerouteCoordinatorState.InFlight ||
            outcome.attemptId != current.command.attemptId ||
            outcome.sourceRouteId != current.command.sourceRouteId
        ) {
            return RerouteApplyDecision.Stale(outcome.attemptId, activeRoute)
        }
        return when (outcome) {
            is RerouteOutcome.Failed -> {
                state = RerouteCoordinatorState.Ready(current.activeRoute, outcome.error)
                RerouteApplyDecision.Failed(outcome.attemptId, current.activeRoute, outcome.error)
            }
            is RerouteOutcome.Planned -> applyReplacement(current, outcome)
        }
    }

    fun cancelAttempt(attemptId: RerouteAttemptId): RerouteApplyDecision {
        val current = state
        if (
            current !is RerouteCoordinatorState.InFlight ||
            current.command.attemptId != attemptId
        ) {
            return RerouteApplyDecision.Stale(attemptId, activeRoute)
        }
        state = RerouteCoordinatorState.Ready(current.activeRoute)
        return RerouteApplyDecision.Cancelled(attemptId, current.activeRoute)
    }

    suspend fun executePending(planner: RoutePlannerPort): RerouteApplyDecision {
        val command = checkNotNull(pendingCommand) { "no reroute command is pending" }
        return try {
            apply(RerouteExecutor(planner).execute(command))
        } catch (cancelled: CancellationException) {
            cancelAttempt(command.attemptId)
            throw cancelled
        }
    }

    private fun applyReplacement(
        current: RerouteCoordinatorState.InFlight,
        outcome: RerouteOutcome.Planned,
    ): RerouteApplyDecision {
        val validationError = validateReplacement(current.command, current.activeRoute, outcome.route)
        if (validationError != null) {
            state = RerouteCoordinatorState.Ready(current.activeRoute, validationError)
            return RerouteApplyDecision.InvalidReplacement(
                current.command.attemptId,
                current.activeRoute,
                validationError,
            )
        }
        state = RerouteCoordinatorState.Ready(outcome.route)
        return RerouteApplyDecision.Replaced(
            attemptId = current.command.attemptId,
            previousRoute = current.activeRoute,
            activeRoute = outcome.route,
        )
    }

    private fun validateReplacement(
        command: RerouteCommand,
        oldRoute: RoutePlan,
        replacement: RoutePlan,
    ): RoutePlanningError? {
        if (replacement.id == oldRoute.id) {
            return invalidReplacement("replacement route must use a new route id")
        }
        return try {
            replacement.requireMatches(command.request)
            null
        } catch (_: IllegalArgumentException) {
            invalidReplacement("replacement route violates canonical request postconditions")
        }
    }

    private fun invalidReplacement(message: String): RoutePlanningError = RoutePlanningError(
        code = RoutePlanningErrorCode.Internal,
        message = message,
        retryable = false,
        providerDiagnosticCode = "reroute.invalid-replacement",
    )

    private fun nextAttemptId(): RerouteAttemptId {
        check(attemptCounter < Long.MAX_VALUE) { "reroute attempt id overflow" }
        return RerouteAttemptId(attemptCounter + 1L)
    }
}

class RerouteExecutor(
    private val planner: RoutePlannerPort,
) {
    suspend fun execute(command: RerouteCommand): RerouteOutcome = try {
        when (val result = planner.plan(command.request)) {
            is RoutePlanningResult.Success -> {
                if (result.routes.size > command.request.requestedAlternatives) {
                    unexpectedFailure(command, "planner returned more alternatives than requested")
                } else {
                    RerouteOutcome.Planned(
                        attemptId = command.attemptId,
                        sourceRouteId = command.sourceRouteId,
                        route = result.routes.first(),
                    )
                }
            }
            is RoutePlanningResult.Failure -> RerouteOutcome.Failed(
                attemptId = command.attemptId,
                sourceRouteId = command.sourceRouteId,
                error = result.error,
            )
        }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Throwable) {
        unexpectedFailure(command, "route planner threw an unexpected exception")
    }

    private fun unexpectedFailure(
        command: RerouteCommand,
        message: String,
    ): RerouteOutcome.Failed = RerouteOutcome.Failed(
        attemptId = command.attemptId,
        sourceRouteId = command.sourceRouteId,
        error = RoutePlanningError(
            code = RoutePlanningErrorCode.Internal,
            message = message,
            retryable = true,
            providerDiagnosticCode = "reroute.executor-exception",
        ),
    )
}
