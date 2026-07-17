package org.traveldna.location.replay

import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleDecision
import org.traveldna.location.contracts.LocationSampleGate
import org.traveldna.location.contracts.LocationSampleRejectionReason

/**
 * Single-threaded deterministic replay state machine.
 *
 * The runner never sleeps and never reads files. Accepted transitions are
 * prepared without mutation and committed only after all arithmetic checks pass.
 */
class DeterministicReplayRunner(
    val scenario: LocationReplayScenario,
) {
    private val gate = LocationSampleGate()
    private val clock = VirtualReplayClock()
    private val delayScaler = ReplayDelayScaler(scenario.playbackRate)
    private val rejectionCounts = mutableMapOf<LocationSampleRejectionReason, Int>()

    private var nextIndex: Int = 0
    private var acceptedSamples: Int = 0
    private var rejectedSamples: Int = 0
    private var totalPlaybackDelayMilliseconds: Long = 0L

    var state: ReplayState = ReplayState.Ready
        private set

    fun start() {
        require(state == ReplayState.Ready) { "replay can start only from Ready" }
        state = ReplayState.Running
    }

    fun pause() {
        require(state == ReplayState.Running) { "replay can pause only while Running" }
        state = ReplayState.Paused
    }

    fun resume() {
        require(state == ReplayState.Paused) { "replay can resume only while Paused" }
        state = ReplayState.Running
    }

    fun cancel() {
        require(state != ReplayState.Completed && state != ReplayState.Cancelled) {
            "completed or cancelled replay cannot be cancelled"
        }
        state = ReplayState.Cancelled
    }

    /** Processes one sample only while Running. */
    fun advance(): ReplayEvent? {
        require(state == ReplayState.Running) { "advance requires a Running replay" }
        return processNext()
    }

    /**
     * Processes one sample while Ready or Paused and remains paused unless the
     * scenario completes. This is the deterministic teaching/debugging path.
     */
    fun step(): ReplayEvent? {
        require(state == ReplayState.Ready || state == ReplayState.Paused) {
            "step requires a Ready or Paused replay"
        }
        if (state == ReplayState.Ready) {
            state = ReplayState.Paused
        }
        return processNext()
    }

    /** Runs synchronously to completion without wall-clock sleeps. */
    fun runToEnd(): ReplaySummary {
        when (state) {
            ReplayState.Ready -> start()
            ReplayState.Running -> Unit
            ReplayState.Paused -> resume()
            ReplayState.Completed -> return summary()
            ReplayState.Cancelled -> error("cancelled replay cannot run to completion")
        }
        while (state == ReplayState.Running) {
            processNext()
        }
        return summary()
    }

    fun summary(): ReplaySummary = ReplaySummary(
        state = state,
        processedSamples = nextIndex,
        acceptedSamples = acceptedSamples,
        rejectedSamples = rejectedSamples,
        rejectionCounts = rejectionCounts,
        finalClock = clock.now,
        totalPlaybackDelayMilliseconds = totalPlaybackDelayMilliseconds,
        lastAcceptedSequence = gate.lastAccepted?.sequence,
    )

    private fun processNext(): ReplayEvent? {
        if (nextIndex >= scenario.samples.size) {
            state = ReplayState.Completed
            return null
        }

        val sample = scenario.samples[nextIndex]
        val event = when (val decision = gate.inspect(sample)) {
            is LocationSampleDecision.Accepted -> prepareAndCommitAccepted(decision.sample)
            is LocationSampleDecision.Rejected -> commitRejected(decision)
        }
        nextIndex += 1

        if (nextIndex == scenario.samples.size) {
            state = ReplayState.Completed
        }
        return event
    }

    private fun prepareAndCommitAccepted(sample: LocationSample): ReplayEvent.Accepted {
        val sourceDelta = clock.deltaTo(sample.monotonicTime)
        val delayPreview = delayScaler.preview(sourceDelta)
        require(
            totalPlaybackDelayMilliseconds <=
                Long.MAX_VALUE - delayPreview.delayMilliseconds,
        ) {
            "total playback delay overflows Long"
        }

        gate.commitAccepted(sample)
        val committedDelta = clock.accept(sample.monotonicTime)
        check(committedDelta == sourceDelta) { "virtual clock changed during accepted transition" }
        delayScaler.commit(delayPreview)
        totalPlaybackDelayMilliseconds += delayPreview.delayMilliseconds
        acceptedSamples += 1

        return ReplayEvent.Accepted(
            sample = sample,
            clock = checkNotNull(clock.now),
            sourceDeltaMilliseconds = sourceDelta,
            playbackDelayMilliseconds = delayPreview.delayMilliseconds,
        )
    }

    private fun commitRejected(decision: LocationSampleDecision.Rejected): ReplayEvent.Rejected {
        rejectedSamples += 1
        rejectionCounts[decision.reason] = rejectionCounts.getOrElse(decision.reason) { 0 } + 1
        return ReplayEvent.Rejected(
            sample = decision.sample,
            clock = clock.now,
            reason = decision.reason,
        )
    }
}
