package dna.consent

import dna.model.Compatibility
import dna.model.ConsentGrant
import dna.model.DNAIntent
import dna.model.GeoAnchor
import dna.model.GeoRoom
import dna.model.GeoRoomType
import java.time.Duration
import java.time.Instant
import java.util.UUID

class ConsentRegistry {
    private val grantsByCompatibility = mutableMapOf<String, MutableMap<String, ConsentGrant>>()

    fun grant(grant: ConsentGrant) {
        grantsByCompatibility
            .getOrPut(grant.compatibilityId) { mutableMapOf() }[grant.subjectEphemeralId] = grant
    }

    fun hasValidConsent(
        compatibilityId: String,
        subjects: Set<String>,
        purpose: String,
        now: Instant
    ): Boolean {
        val grants = grantsByCompatibility[compatibilityId] ?: return false
        return subjects.all { subject ->
            grants[subject]?.let { it.purpose == purpose && it.isValid(now) } == true
        }
    }
}

class GeoRoomService(
    private val consentRegistry: ConsentRegistry
) {
    fun createIntentRoom(
        compatibility: Compatibility,
        left: DNAIntent,
        right: DNAIntent,
        now: Instant,
        lifetime: Duration = Duration.ofHours(6)
    ): GeoRoom? {
        val participants = setOf(left.ownerEphemeralId, right.ownerEphemeralId)
        val purpose = "join_intent_room"
        if (!consentRegistry.hasValidConsent(compatibility.compatibilityId, participants, purpose, now)) {
            return null
        }

        val anchor = chooseAnchor(left.geoAnchor, right.geoAnchor) ?: return null
        val topic = when (compatibility.domain.name) {
            "TRAVEL" -> "shared-transfer"
            "SHOPPING" -> "group-purchase"
            "SOCIAL" -> "shared-interests"
            else -> "intent"
        }

        return GeoRoom(
            roomId = UUID.randomUUID().toString(),
            roomType = GeoRoomType.INTENT,
            anchor = anchor,
            domains = setOf(compatibility.domain),
            topics = setOf(topic),
            participantEphemeralIds = participants,
            sourceCompatibilityId = compatibility.compatibilityId,
            createdAt = now,
            expiresAt = now.plus(lifetime)
        )
    }

    private fun chooseAnchor(left: GeoAnchor?, right: GeoAnchor?): GeoAnchor? {
        if (left != null && right != null && left.anchorId == right.anchorId) return left
        return left ?: right
    }
}
