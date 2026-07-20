package org.traveldna.lab.location

import kotlin.test.Test
import kotlin.test.assertFailsWith
import org.traveldna.location.replay.LocationReplayScenario

class BenchmarkArgumentsTest {
    @Test
    fun acceptsOnlyOddBoundedIterationCounts() {
        validateBenchmarkArguments(sampleCount = 1, iterations = 1)
        validateBenchmarkArguments(sampleCount = 10_000, iterations = 7)
        validateBenchmarkArguments(
            sampleCount = LocationReplayScenario.MaxSamples,
            iterations = MaxBenchmarkIterations,
        )

        assertFailsWith<IllegalArgumentException> {
            validateBenchmarkArguments(sampleCount = 0, iterations = 7)
        }
        assertFailsWith<IllegalArgumentException> {
            validateBenchmarkArguments(
                sampleCount = LocationReplayScenario.MaxSamples + 1,
                iterations = 7,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            validateBenchmarkArguments(sampleCount = 10_000, iterations = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            validateBenchmarkArguments(sampleCount = 10_000, iterations = 2)
        }
        assertFailsWith<IllegalArgumentException> {
            validateBenchmarkArguments(sampleCount = 10_000, iterations = 26)
        }
    }
}
