package dna.model

import java.time.Instant

enum class Domain {
    TRAVEL,
    SHOPPING,
    SOCIAL,
    MOBILITY,
    LOCAL_SERVICES
}

enum class Visibility {
    PRIVATE,
    MATCHED_ONLY,
    GROUP,
    PUBLIC_AGGREGATE
}

data class GeoPoint(
    val longitude: Double,
    val latitude: Double
) {
    init {
        require(longitude in -180.0..180.0) { "Longitude out of range" }
        require(latitude in -90.0..90.0) { "Latitude out of range" }
    }
}

sealed interface GeoAnchor {
    val anchorId: String
    val label: String?

    data class Point(
        override val anchorId: String,
        val coordinate: GeoPoint,
        val radiusMeters: Double = 0.0,
        override val label: String? = null
    ) : GeoAnchor {
        init {
            require(radiusMeters >= 0.0)
        }
    }

    data class Area(
        override val anchorId: String,
        val polygon: List<GeoPoint>,
        override val label: String? = null
    ) : GeoAnchor {
        init {
            require(polygon.size >= 4) { "An area polygon needs at least four coordinates" }
        }
    }

    data class Route(
        override val anchorId: String,
        val polyline: List<GeoPoint>,
        val corridorMeters: Double = 0.0,
        override val label: String? = null
    ) : GeoAnchor {
        init {
            require(polyline.size >= 2) { "A route needs at least two coordinates" }
            require(corridorMeters >= 0.0)
        }
    }
}

data class DNAProfile(
    val profileId: String,
    val interests: Set<String>,
    val preferences: Map<String, String>,
    val version: Long = 1
)

data class DNAFragment(
    val fragmentId: String,
    val subjectEphemeralId: String,
    val purpose: String,
    val claims: Map<String, Any>,
    val audience: Set<String>,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val revocable: Boolean = true,
    val consentRef: String? = null
) {
    fun isExpired(now: Instant): Boolean = !expiresAt.isAfter(now)
}

data class DNAIntent(
    val intentId: String,
    val ownerEphemeralId: String,
    val domain: Domain,
    val intentType: String,
    val attributes: Map<String, String>,
    val geoAnchor: GeoAnchor?,
    val validFrom: Instant,
    val validUntil: Instant,
    val visibility: Visibility = Visibility.MATCHED_ONLY,
    val revocable: Boolean = true
) {
    init {
        require(validUntil.isAfter(validFrom)) { "Intent validity interval must be positive" }
    }

    fun isActive(now: Instant): Boolean = !now.isBefore(validFrom) && now.isBefore(validUntil)
}

data class DNATrace(
    val traceId: String,
    val ephemeralSenderId: String,
    val domains: Set<Domain>,
    val intentCodes: Set<Int>,
    val coarseGeoCell: String?,
    val rendezvousCapabilities: Set<String>,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val nonce: Long,
    val authenticator: ByteArray
) {
    init {
        require(domains.isNotEmpty())
        require(intentCodes.all { it in 0..65535 })
        require(expiresAt.isAfter(issuedAt))
    }

    fun isExpired(now: Instant): Boolean = !expiresAt.isAfter(now)
}

data class Compatibility(
    val compatibilityId: String,
    val leftIntentId: String,
    val rightIntentId: String,
    val domain: Domain,
    val score: Double,
    val reasons: List<String>,
    val detectedAt: Instant
) {
    init {
        require(score in 0.0..1.0)
        require(reasons.isNotEmpty())
    }
}

data class ConsentGrant(
    val consentId: String,
    val subjectEphemeralId: String,
    val compatibilityId: String,
    val purpose: String,
    val grantedAt: Instant,
    val expiresAt: Instant,
    val revokedAt: Instant? = null
) {
    fun isValid(now: Instant): Boolean = revokedAt == null && expiresAt.isAfter(now)
}

data class RendezvousDescriptor(
    val rendezvousId: String,
    val oneTimeToken: String,
    val supportedChannels: Set<String>,
    val preferredChannel: String?,
    val endpointHints: Map<String, String>,
    val expiresAt: Instant,
    val requiredConsent: Boolean
)

enum class GeoRoomType {
    PLACE,
    AREA,
    ROUTE,
    INTENT,
    TRANSACTION
}

data class GeoRoom(
    val roomId: String,
    val roomType: GeoRoomType,
    val anchor: GeoAnchor,
    val domains: Set<Domain>,
    val topics: Set<String>,
    val participantEphemeralIds: Set<String>,
    val sourceCompatibilityId: String?,
    val createdAt: Instant,
    val expiresAt: Instant
) {
    fun isExpired(now: Instant): Boolean = !expiresAt.isAfter(now)
}
