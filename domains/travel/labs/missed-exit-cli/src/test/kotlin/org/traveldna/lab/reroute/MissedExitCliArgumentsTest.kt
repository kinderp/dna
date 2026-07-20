package org.traveldna.lab.reroute

import kotlin.test.Test
import kotlin.test.assertFailsWith

class MissedExitCliArgumentsTest {
    @Test
    fun benchmarkRequiresBoundedSamplesAndOddRuns() {
        assertFailsWith<IllegalArgumentException> { main(arrayOf("--benchmark", "1", "7")) }
        assertFailsWith<IllegalArgumentException> { main(arrayOf("--benchmark", "1000", "2")) }
        assertFailsWith<IllegalArgumentException> { main(arrayOf("--benchmark", "100001", "7")) }
    }

    @Test
    fun everyAcceptedSampleCountHasADeterministicFinalOnRouteState() {
        main(arrayOf("--benchmark", "2", "1"))
        main(arrayOf("--benchmark", "3", "1"))
    }
}
