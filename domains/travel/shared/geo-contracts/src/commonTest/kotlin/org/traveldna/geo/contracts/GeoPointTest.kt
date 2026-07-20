package org.traveldna.geo.contracts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GeoPointTest {
    @Test
    fun acceptsWgs84BoundariesAndSupportsCopyAndDestructuring() {
        val southWest = GeoPoint(-90.0, -180.0)
        val northEast = GeoPoint(90.0, 180.0)
        assertEquals(-90.0, southWest.latitude)
        assertEquals(180.0, northEast.longitude)

        val (latitude, longitude) = southWest
        assertEquals(-90.0, latitude)
        assertEquals(-180.0, longitude)
        assertEquals(GeoPoint(-89.0, -180.0), southWest.copy(latitude = -89.0))
    }

    @Test
    fun rejectsNonFiniteAndOutOfRangeCoordinates() {
        assertFailsWith<IllegalArgumentException> { GeoPoint(Double.NaN, 0.0) }
        assertFailsWith<IllegalArgumentException> { GeoPoint(Double.POSITIVE_INFINITY, 0.0) }
        assertFailsWith<IllegalArgumentException> { GeoPoint(91.0, 0.0) }
        assertFailsWith<IllegalArgumentException> { GeoPoint(0.0, 181.0) }
    }

    @Test
    fun normalizesSignedZeroForStableEqualityAndHashing() {
        val positive = GeoPoint(0.0, 0.0)
        val negative = GeoPoint(-0.0, -0.0)
        assertEquals(positive, negative)
        assertEquals(positive.hashCode(), negative.hashCode())
        assertEquals(0.0, negative.latitude)
        assertEquals(0.0, negative.longitude)
    }
}
