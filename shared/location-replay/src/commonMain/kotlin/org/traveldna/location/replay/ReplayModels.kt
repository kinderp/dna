package org.traveldna.location.replay

import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSampleRejectionReason
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant

private val STABLE_REPLAY_ID = Regex("[A-Za-z0-9][A-Za-z0-9._:-]{0,127}")

/** Positive rational playback speed, normalized to lowest terms. */
class PlaybackRate private constructor(
    val numerator: Long,
    val denominator: Long,
) {
    override fun equals(other: Any?): Boolean =
        other is PlaybackRate && numerator == other.numerator && denominator == other.denominator

    override fun hashCode(): Int = 31 * numerator.hashCode() + denominator.hashCode()

    override fun toString(): String = "$numerator/$denominator"

    companion object {
        const val MaxTerm: Long = 1_000L

        val HalfSpeed: PlaybackRate = of(1L, 2L)
        val RealTime: PlaybackRate = of(1L, 1L)
        val DoubleSpeed: PlaybackRate = of(2L, 1L)

        fun of(numerator: Long, denominator: Long): PlaybackRate {
            require(numerator in 1L..MaxTerm) {
                "playback numerator must be within [1, $MaxTerm]"
            }
            require(denominator in 1L..MaxTerm) {
                "playback denominator must be within [1, $MaxTerm]"
            }
            val divisor = greatestCommonDivisor(numerator, denominator)
            return PlaybackRate(numerator / divisor, denominator / divisor)
        }

        private fun greatestCommonDivisor(first: Long, second: Long): Long {
            var left = first
            var right = second
            while (right != 0L) {
                val remainder = left % right
                left = right
                right = remainder
            }
            return left
        }
    }
}

/** Immutable synthetic replay input. Samples remain in declared order. */
class LocationReplayScenario(
    val id: String,
    samples: List<LocationSample>,
    val playbackRate: PlaybackRate = PlaybackRate.RealTime,
) {
    val samples: List<LocationSample> = samples.toList()

    init {
        require(STABLE_REPLAY_ID.matches(id)) {
            "replay scenario id contains unsupported characters"
        }
        require(this.samples.isNotEmpty()) {
            "replay scenario must contain at least one sample"
        }
        require(this.samples.size <= MaxSamples) {
            "replay scenario may contain at most $MaxSamples samples"
        }
        require(this.samples.all { it.origin == LocationSampleOrigin.Replay }) {
            "replay scenario samples must use Replay origin"
        }
    }

    companion object {
        const val MaxSamples: Int = 100_000
    }
}

enum class ReplayState {
    Ready,
    Running,
    Paused,
    Completed,
    Cancelled,
}

sealed interface ReplayEvent {
    val sample: LocationSample
    val clock: MonotonicInstant?

    data class Accepted(
        override val sample: LocationSample,
        override val clock: MonotonicInstant,
        val sourceDeltaMilliseconds: Long,
        val playbackDelayMilliseconds: Long,
    ) : ReplayEvent

    data class Rejected(
        override val sample: LocationSample,
        override val clock: MonotonicInstant?,
        val reason: LocationSampleRejectionReason,
    ) : ReplayEvent
}

/** Immutable bounded summary; individual replay events are not retained. */
class ReplaySummary(
    val state: ReplayState,
    val processedSamples: Int,
    val acceptedSamples: Int,
    val rejectedSamples: Int,
    rejectionCounts: Map<LocationSampleRejectionReason, Int>,
    val finalClock: MonotonicInstant?,
    val totalPlaybackDelayMilliseconds: Long,
    val lastAcceptedSequence: LocationSequence?,
) {
    val rejectionCounts: Map<LocationSampleRejectionReason, Int> = rejectionCounts.toMap()

    init {
        require(processedSamples >= 0) { "processed sample count must be non-negative" }
        require(acceptedSamples >= 0) { "accepted sample count must be non-negative" }
        require(rejectedSamples >= 0) { "rejected sample count must be non-negative" }
        require(acceptedSamples + rejectedSamples == processedSamples) {
            "accepted and rejected counts must equal processed samples"
        }
        require(this.rejectionCounts.values.all { it > 0 }) {
            "rejection counts must contain only positive values"
        }
        require(this.rejectionCounts.values.sum() == rejectedSamples) {
            "rejection reason counts must equal rejected samples"
        }
        require(totalPlaybackDelayMilliseconds >= 0L) {
            "playback delay must be non-negative"
        }
        require((acceptedSamples == 0) == (finalClock == null)) {
            "final clock must be present exactly when an accepted sample exists"
        }
        require((acceptedSamples == 0) == (lastAcceptedSequence == null)) {
            "last accepted sequence must be present exactly when an accepted sample exists"
        }
    }
}
