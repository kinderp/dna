package org.traveldna.location.replay

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSampleRejectionReason
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant

class DeterministicReplayRunnerTest {
    @Test
    fun playbackRateNormalizesAndPreservesRemainder() {
        assertEquals(PlaybackRate.RealTime, PlaybackRate.of(2, 2))
        val scaler = ReplayDelayScaler(PlaybackRate.of(3, 2))
        assertEquals(0L, scaler.scale(1L))
        assertEquals(1L, scaler.scale(1L))
        assertEquals(1L, scaler.scale(1L))
        assertFailsWith<IllegalArgumentException> { PlaybackRate.of(0, 1) }
        assertFailsWith<IllegalArgumentException> { PlaybackRate.of(1, 1_001) }
    }

    @Test
    fun replayReportsAcceptedAndRejectedSamplesWithoutSorting() {
        val summary = DeterministicReplayRunner(referenceScenario()).runToEnd()

        assertEquals(ReplayState.Completed, summary.state)
        assertEquals(6, summary.processedSamples)
        assertEquals(4, summary.acceptedSamples)
        assertEquals(2, summary.rejectedSamples)
        assertEquals(
            mapOf(
                LocationSampleRejectionReason.NonIncreasingSequence to 1,
                LocationSampleRejectionReason.NonIncreasingMonotonicTime to 1,
            ),
            summary.rejectionCounts,
        )
        assertEquals(MonotonicInstant(3_000L), summary.finalClock)
        assertEquals(1_500L, summary.totalPlaybackDelayMilliseconds)
        assertEquals(LocationSequence(4), summary.lastAcceptedSequence)
    }

    @Test
    fun firstAcceptedSampleEstablishesNonZeroBaselineWithZeroDelay() {
        val runner = DeterministicReplayRunner(
            LocationReplayScenario(
                id = "nonzero-baseline-v0",
                playbackRate = PlaybackRate.DoubleSpeed,
                samples = listOf(sample(0, 5_000), sample(1, 6_000)),
            ),
        )
        runner.start()
        val first = assertIs<ReplayEvent.Accepted>(runner.advance())
        val second = assertIs<ReplayEvent.Accepted>(runner.advance())

        assertEquals(MonotonicInstant(5_000L), first.clock)
        assertEquals(0L, first.sourceDeltaMilliseconds)
        assertEquals(0L, first.playbackDelayMilliseconds)
        assertEquals(1_000L, second.sourceDeltaMilliseconds)
        assertEquals(500L, second.playbackDelayMilliseconds)
        assertEquals(500L, runner.summary().totalPlaybackDelayMilliseconds)
    }

    @Test
    fun rejectedSamplesDoNotMoveVirtualClockOrDelayRemainder() {
        val runner = DeterministicReplayRunner(referenceScenario())
        runner.start()
        assertIs<ReplayEvent.Accepted>(runner.advance())
        assertIs<ReplayEvent.Accepted>(runner.advance())
        val duplicate = assertIs<ReplayEvent.Rejected>(runner.advance())
        assertEquals(MonotonicInstant(1_000L), duplicate.clock)
        assertEquals(500L, runner.summary().totalPlaybackDelayMilliseconds)
    }

    @Test
    fun pauseStepAndResumeHaveExplicitStateTransitions() {
        val runner = DeterministicReplayRunner(referenceScenario())
        runner.start()
        runner.advance()
        runner.pause()
        assertEquals(ReplayState.Paused, runner.state)
        assertFailsWith<IllegalArgumentException> { runner.advance() }
        assertIs<ReplayEvent.Accepted>(runner.step())
        assertEquals(ReplayState.Paused, runner.state)
        runner.resume()
        assertEquals(ReplayState.Running, runner.state)
    }

    @Test
    fun stepFromReadyPausesAndCompletesOnTheLastSample() {
        val runner = DeterministicReplayRunner(
            LocationReplayScenario("single-step-v0", listOf(sample(0, 10_000))),
        )
        val event = assertIs<ReplayEvent.Accepted>(runner.step())
        assertEquals(0L, event.playbackDelayMilliseconds)
        assertEquals(ReplayState.Completed, runner.state)
        assertEquals(1, runner.summary().processedSamples)
    }

    @Test
    fun cancellationBeforeAndDuringReplayStopsProcessing() {
        val beforeStart = DeterministicReplayRunner(referenceScenario())
        beforeStart.cancel()
        assertEquals(ReplayState.Cancelled, beforeStart.state)
        assertEquals(0, beforeStart.summary().processedSamples)
        assertNull(beforeStart.summary().finalClock)
        assertFailsWith<IllegalStateException> { beforeStart.runToEnd() }

        val running = DeterministicReplayRunner(referenceScenario())
        running.start()
        running.advance()
        running.cancel()
        assertEquals(ReplayState.Cancelled, running.state)
        assertFailsWith<IllegalArgumentException> { running.advance() }
        assertFailsWith<IllegalArgumentException> { running.cancel() }
        assertEquals(1, running.summary().processedSamples)
    }

    @Test
    fun scenarioSnapshotsInputAndRequiresReplayOrigin() {
        val mutableSamples = mutableListOf(sample(0, 0))
        val scenario = LocationReplayScenario("snapshot.v0", mutableSamples)
        mutableSamples.clear()
        assertEquals(1, scenario.samples.size)
        assertFailsWith<IllegalArgumentException> {
            LocationReplayScenario("empty.v0", emptyList())
        }
        assertFailsWith<IllegalArgumentException> {
            LocationReplayScenario(
                "platform-origin.v0",
                listOf(sample(0, 0).copy(origin = LocationSampleOrigin.Platform)),
            )
        }
    }

    @Test
    fun summarySnapshotsCountsAndRejectsUnboundedOrImpossibleState() {
        val mutableCounts = mutableMapOf(LocationSampleRejectionReason.NonIncreasingSequence to 1)
        val summary = ReplaySummary(
            state = ReplayState.Completed,
            processedSamples = 2,
            acceptedSamples = 1,
            rejectedSamples = 1,
            rejectionCounts = mutableCounts,
            finalClock = MonotonicInstant(100L),
            totalPlaybackDelayMilliseconds = 0L,
            lastAcceptedSequence = LocationSequence(0L),
        )
        mutableCounts.clear()
        assertEquals(1, summary.rejectionCounts.size)

        assertFailsWith<IllegalArgumentException> {
            ReplaySummary(
                state = ReplayState.Ready,
                processedSamples = 0,
                acceptedSamples = 0,
                rejectedSamples = 0,
                rejectionCounts = emptyMap(),
                finalClock = MonotonicInstant.Zero,
                totalPlaybackDelayMilliseconds = 0L,
                lastAcceptedSequence = null,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            ReplaySummary(
                state = ReplayState.Completed,
                processedSamples = 0,
                acceptedSamples = 0,
                rejectedSamples = 0,
                rejectionCounts = emptyMap(),
                finalClock = null,
                totalPlaybackDelayMilliseconds = 0L,
                lastAcceptedSequence = null,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            ReplaySummary(
                state = ReplayState.Running,
                processedSamples = 1,
                acceptedSamples = 0,
                rejectedSamples = 1,
                rejectionCounts = mapOf(LocationSampleRejectionReason.NonIncreasingSequence to 1),
                finalClock = null,
                totalPlaybackDelayMilliseconds = 0L,
                lastAcceptedSequence = null,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            ReplaySummary(
                state = ReplayState.Running,
                processedSamples = Int.MAX_VALUE,
                acceptedSamples = Int.MAX_VALUE,
                rejectedSamples = 0,
                rejectionCounts = emptyMap(),
                finalClock = MonotonicInstant.Zero,
                totalPlaybackDelayMilliseconds = 0L,
                lastAcceptedSequence = LocationSequence(0L),
            )
        }
    }

    private fun referenceScenario(): LocationReplayScenario = LocationReplayScenario(
        id = "reference-location-replay-v0",
        playbackRate = PlaybackRate.DoubleSpeed,
        samples = listOf(
            sample(0, 0),
            sample(1, 1_000),
            sample(1, 1_500),
            sample(2, 2_000),
            sample(3, 1_500),
            sample(4, 3_000),
        ),
    )

    private fun sample(sequence: Long, time: Long): LocationSample = LocationSample(
        sequence = LocationSequence(sequence),
        monotonicTime = MonotonicInstant(time),
        position = GeoPoint(37.5 + sequence * 0.001, 15.1),
        horizontalAccuracyMeters = 5.0,
        speedMetersPerSecond = 20.0,
        bearingDegrees = 90.0,
        origin = LocationSampleOrigin.Replay,
    )
}
