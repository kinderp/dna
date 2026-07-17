package org.traveldna.location.replay

import org.traveldna.location.contracts.MonotonicInstant

/**
 * Deterministic source clock advanced only by accepted replay samples.
 *
 * `deltaTo` is non-mutating so a runner can validate arithmetic before it
 * commits gate, clock and scaler state as one logical transition.
 */
class VirtualReplayClock {
    var now: MonotonicInstant? = null
        private set

    fun deltaTo(target: MonotonicInstant): Long {
        val current = now ?: return 0L
        val delta = target.elapsedSince(current)
        require(delta > 0L) { "accepted replay time must increase strictly" }
        return delta
    }

    fun accept(target: MonotonicInstant): Long {
        val delta = deltaTo(target)
        now = target
        return delta
    }

    fun reset() {
        now = null
    }
}

internal data class ReplayDelayPreview(
    val delayMilliseconds: Long,
    val baseRemainder: Long,
    val nextRemainder: Long,
)

/**
 * Converts source-time deltas to playback delays while preserving fractional
 * remainder across samples and avoiding cumulative integer-rounding drift.
 */
internal class ReplayDelayScaler(
    private val rate: PlaybackRate,
) {
    private var remainder: Long = 0L

    fun preview(sourceDeltaMilliseconds: Long): ReplayDelayPreview {
        require(sourceDeltaMilliseconds >= 0L) { "source delta must be non-negative" }
        require(sourceDeltaMilliseconds <= (Long.MAX_VALUE - remainder) / rate.denominator) {
            "playback delay scaling overflows Long"
        }
        val scaledNumerator = sourceDeltaMilliseconds * rate.denominator + remainder
        return ReplayDelayPreview(
            delayMilliseconds = scaledNumerator / rate.numerator,
            baseRemainder = remainder,
            nextRemainder = scaledNumerator % rate.numerator,
        )
    }

    fun commit(preview: ReplayDelayPreview) {
        require(preview.baseRemainder == remainder) {
            "playback delay preview is stale"
        }
        remainder = preview.nextRemainder
    }

    fun scale(sourceDeltaMilliseconds: Long): Long {
        val preview = preview(sourceDeltaMilliseconds)
        commit(preview)
        return preview.delayMilliseconds
    }
}
