package org.traveldna.location.contracts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import org.traveldna.geo.contracts.GeoPoint

class LocationModelsTest {
    @Test
    fun monotonicTimeAndSequenceRejectNegativeValues() {
        assertFailsWith<IllegalArgumentException> { MonotonicInstant(-1L) }
        assertFailsWith<IllegalArgumentException> { LocationSequence(-1L) }
        assertEquals(500L, MonotonicInstant(1_500L).elapsedSince(MonotonicInstant(1_000L)))
        assertFailsWith<IllegalArgumentException> {
            MonotonicInstant(999L).elapsedSince(MonotonicInstant(1_000L))
        }
    }

    @Test
    fun sampleValidatesAccuracySpeedAndBearing() {
        val valid = sample(sequence = 0, time = 0)
        assertEquals(5.0, valid.horizontalAccuracyMeters)

        assertFailsWith<IllegalArgumentException> { sample(0, 0, accuracy = 0.0) }
        assertFailsWith<IllegalArgumentException> { sample(0, 0, accuracy = Double.NaN) }
        assertFailsWith<IllegalArgumentException> { sample(0, 0, speed = -1.0) }
        assertFailsWith<IllegalArgumentException> { sample(0, 0, speed = 201.0) }
        assertFailsWith<IllegalArgumentException> { sample(0, 0, bearing = 360.0) }
        assertFailsWith<IllegalArgumentException> { sample(0, 0, bearing = Double.POSITIVE_INFINITY) }
    }

    @Test
    fun gateRejectsNonIncreasingSequenceWithoutChangingBaseline() {
        val gate = LocationSampleGate()
        val first = sample(10, 1_000)
        val duplicate = sample(10, 1_500)
        val next = sample(11, 2_000)

        assertIs<LocationSampleDecision.Accepted>(gate.evaluate(first))
        val rejected = assertIs<LocationSampleDecision.Rejected>(gate.evaluate(duplicate))
        assertEquals(LocationSampleRejectionReason.NonIncreasingSequence, rejected.reason)
        assertEquals(first, gate.lastAccepted)
        assertIs<LocationSampleDecision.Accepted>(gate.evaluate(next))
    }

    @Test
    fun gateRejectsNonIncreasingTimeWithoutSortingOrAdvancing() {
        val gate = LocationSampleGate()
        val first = sample(0, 2_000)
        val backwards = sample(1, 1_500)
        val recovered = sample(2, 3_000)

        gate.evaluate(first)
        val rejected = assertIs<LocationSampleDecision.Rejected>(gate.evaluate(backwards))
        assertEquals(LocationSampleRejectionReason.NonIncreasingMonotonicTime, rejected.reason)
        assertEquals(LocationSequence(0), rejected.lastAcceptedSequence)
        assertEquals(MonotonicInstant(2_000L), rejected.lastAcceptedTime)
        assertEquals(first, gate.lastAccepted)
        assertIs<LocationSampleDecision.Accepted>(gate.evaluate(recovered))
    }

    @Test
    fun gateUsesSequenceAsTheFirstExplicitRejectionWhenBothRegress() {
        val gate = LocationSampleGate()
        gate.evaluate(sample(5, 5_000))
        val rejected = assertIs<LocationSampleDecision.Rejected>(gate.evaluate(sample(4, 4_000)))
        assertEquals(LocationSampleRejectionReason.NonIncreasingSequence, rejected.reason)
    }

    @Test
    fun resetRemovesTheAcceptedBaseline() {
        val gate = LocationSampleGate()
        gate.evaluate(sample(9, 9_000))
        gate.reset()
        assertEquals(null, gate.lastAccepted)
        assertIs<LocationSampleDecision.Accepted>(gate.evaluate(sample(0, 0)))
    }

    private fun sample(
        sequence: Long,
        time: Long,
        accuracy: Double = 5.0,
        speed: Double? = 20.0,
        bearing: Double? = 90.0,
    ): LocationSample = LocationSample(
        sequence = LocationSequence(sequence),
        monotonicTime = MonotonicInstant(time),
        position = GeoPoint(37.5 + sequence * 0.0001, 15.1),
        horizontalAccuracyMeters = accuracy,
        speedMetersPerSecond = speed,
        bearingDegrees = bearing,
        origin = LocationSampleOrigin.Replay,
    )
}
