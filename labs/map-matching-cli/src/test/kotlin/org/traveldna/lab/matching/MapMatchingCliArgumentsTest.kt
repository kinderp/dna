package org.traveldna.lab.matching

import kotlin.test.Test
import kotlin.test.assertFailsWith

class MapMatchingCliArgumentsTest {
    @Test
    fun benchmarkRequiresBoundedSamplesAndOddRuns() {
        assertFailsWith<IllegalArgumentException> { invokeBenchmark(1, 7) }
        assertFailsWith<IllegalArgumentException> { invokeBenchmark(10, 2) }
        assertFailsWith<IllegalArgumentException> { invokeBenchmark(50_001, 7) }
    }

    private fun invokeBenchmark(samples: Int, iterations: Int) {
        val mainClass = Class.forName("org.traveldna.lab.matching.MainKt")
        val method = mainClass.getDeclaredMethod("runBenchmark", Int::class.java, Int::class.java)
        method.isAccessible = true
        try {
            method.invoke(null, samples, iterations)
        } catch (exception: java.lang.reflect.InvocationTargetException) {
            throw exception.targetException
        }
    }
}
