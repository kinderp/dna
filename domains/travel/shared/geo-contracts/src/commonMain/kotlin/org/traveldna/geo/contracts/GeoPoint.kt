package org.traveldna.geo.contracts

/**
 * Immutable WGS84 coordinate shared by routing, location and map contracts.
 *
 * Signed zero is normalized so semantically equal coordinates remain equal and
 * stable when used in requests, fixtures or map keys.
 */
class GeoPoint(
    latitude: Double,
    longitude: Double,
) {
    val latitude: Double = canonicalZero(latitude)
    val longitude: Double = canonicalZero(longitude)

    init {
        require(latitude.isFinite() && latitude in -90.0..90.0) {
            "latitude must be finite and within [-90, 90]"
        }
        require(longitude.isFinite() && longitude in -180.0..180.0) {
            "longitude must be finite and within [-180, 180]"
        }
    }

    operator fun component1(): Double = latitude

    operator fun component2(): Double = longitude

    fun copy(
        latitude: Double = this.latitude,
        longitude: Double = this.longitude,
    ): GeoPoint = GeoPoint(latitude, longitude)

    override fun equals(other: Any?): Boolean =
        other is GeoPoint && latitude == other.latitude && longitude == other.longitude

    override fun hashCode(): Int = 31 * latitude.hashCode() + longitude.hashCode()

    override fun toString(): String = "GeoPoint(latitude=$latitude, longitude=$longitude)"

    private companion object {
        fun canonicalZero(value: Double): Double = if (value == 0.0) 0.0 else value
    }
}
