package org.traveldna.lab.progress

import kotlin.test.Test
import kotlin.test.assertFailsWith

class RouteProgressCliArgumentsTest {
    @Test
    fun benchmarkRejectsInvalidValuesThroughMain() {
        assertFailsWith<IllegalArgumentException> { main(arrayOf("--benchmark", "1", "7")) }
        assertFailsWith<IllegalArgumentException> { main(arrayOf("--benchmark", "1000", "2")) }
        assertFailsWith<IllegalArgumentException> { main(arrayOf("--benchmark", "100001", "7")) }
    }
}
