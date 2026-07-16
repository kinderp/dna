package org.traveldna.reference.routing;

/**
 * WGS84 coordinate used by the deterministic reference graph.
 *
 * <p>This type is fixture-scoped. It is intentionally not presented as the
 * future canonical mobile geo contract.</p>
 */
public record GeoPoint(double latitude, double longitude) {
    public GeoPoint {
        if (!Double.isFinite(latitude) || latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("latitude must be finite and within [-90, 90]");
        }
        if (!Double.isFinite(longitude) || longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("longitude must be finite and within [-180, 180]");
        }
    }

    /** Returns the great-circle distance to {@code other}, rounded down to metres. */
    public long distanceMetresTo(GeoPoint other) {
        final double earthRadiusMetres = 6_371_000.0;
        final double latitude1 = Math.toRadians(latitude);
        final double latitude2 = Math.toRadians(other.latitude);
        final double deltaLatitude = Math.toRadians(other.latitude - latitude);
        final double deltaLongitude = Math.toRadians(other.longitude - longitude);
        final double sinLatitude = Math.sin(deltaLatitude / 2.0);
        final double sinLongitude = Math.sin(deltaLongitude / 2.0);
        final double a = sinLatitude * sinLatitude
                + Math.cos(latitude1) * Math.cos(latitude2)
                * sinLongitude * sinLongitude;
        final double boundedA = Math.clamp(a, 0.0, 1.0);
        final double centralAngle = 2.0 * Math.atan2(
                Math.sqrt(boundedA), Math.sqrt(1.0 - boundedA));
        return (long) Math.floor(earthRadiusMetres * centralAngle);
    }
}
