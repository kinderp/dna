/// WGS84 coordinate used by the synthetic reference fixture.
#[derive(Clone, Copy, Debug, PartialEq)]
pub struct GeoPoint {
    pub(crate) latitude: f64,
    pub(crate) longitude: f64,
}

impl GeoPoint {
    /// Creates a validated WGS84 point.
    ///
    /// # Errors
    ///
    /// Returns an error for non-finite or out-of-range coordinates.
    pub fn new(latitude: f64, longitude: f64) -> Result<Self, String> {
        if !latitude.is_finite() || !(-90.0..=90.0).contains(&latitude) {
            return Err("latitude must be finite and within [-90, 90]".to_owned());
        }
        if !longitude.is_finite() || !(-180.0..=180.0).contains(&longitude) {
            return Err("longitude must be finite and within [-180, 180]".to_owned());
        }
        Ok(Self {
            latitude,
            longitude,
        })
    }

    /// Returns the great-circle distance to `other`, rounded down to metres.
    #[must_use]
    pub fn distance_metres_to(self, other: Self) -> u64 {
        const EARTH_RADIUS_METRES: f64 = 6_371_000.0;
        let latitude_1 = self.latitude.to_radians();
        let latitude_2 = other.latitude.to_radians();
        let delta_latitude = (other.latitude - self.latitude).to_radians();
        let delta_longitude = (other.longitude - self.longitude).to_radians();
        let sin_latitude = (delta_latitude / 2.0).sin();
        let sin_longitude = (delta_longitude / 2.0).sin();
        let a = sin_latitude * sin_latitude
            + latitude_1.cos() * latitude_2.cos() * sin_longitude * sin_longitude;
        let bounded_a = a.clamp(0.0, 1.0);
        let central_angle = 2.0 * bounded_a.sqrt().atan2((1.0 - bounded_a).sqrt());
        (EARTH_RADIUS_METRES * central_angle).floor() as u64
    }
}
