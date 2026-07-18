package org.traveldna.lab.matching

import kotlin.test.Test
import kotlin.test.assertFailsWith

class MapMatchingCliArgumentsTest {
    @Test
    fun benchmarkRequiresBoundedSamplesAndOddRuns() {
        assertFailsWith<IllegalArgumentException> { main(arrayOf("--benchmark", "1", "7")) }
        assertFailsWith<IllegalArgumentException> { main(arrayOf("--benchmark", "10", "2")) }
        assertFailsWith<IllegalArgumentException> { main(arrayOf("--benchmark", "50001", "7")) }
    }
}
