package org.traveldna.location.replay

import org.traveldna.location.contracts.MonotonicInstant

/**
 * Deterministic source clock advanced only by accepted replay samples.
 *
 * The first accepted sample establishes the baseline and yields zero elapsed
 * time. The clock never reads wall time and rejected samples never advance it.
 */
class VirtualReplayClock {
    var now: MonotonicInstant? = null
        private set

    fun accept(target: MonotonicInstant): Long {
        val current = now
        if (current == null) {
            now = target
            return 0L
        }
        val delta = target.elapsedSince(current)
        require(delta > 0L) { "accepted replay time must increase strictly" }
        now = target
        return delta
    }

    fun reset() {
        now = null
    }
}

/**
 * Converts source-time deltas to playback delays while preserving fractional
 * remainder across samples and avoiding cumulative integer-rounding drift.
 */
internal class ReplayDelayScaler(
    private val rate: PlaybackRate,
) {
    private var remainder: Long = 0L

    fun scale(sourceDeltaMilliseconds: Long): Long {
        require(sourceDeltaMilliseconds >= 0L) { "source delta must be non-negative" }
        require(sourceDeltaMilliseconds <= (Long.MAX_VALUE - remainder) / rate.denominator) {
            "playback delay scaling overflows Long"
        }
        val scaledNumerator = sourceDeltaMilliseconds * rate.denominator + remainder
        val delay = scaledNumerator / rate.numerator
        remainder = scaledNumerator % rate.numerator
        return delay
    }
}
