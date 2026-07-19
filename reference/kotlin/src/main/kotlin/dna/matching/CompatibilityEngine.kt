package dna.matching

import dna.model.Compatibility
import dna.model.DNAIntent
import dna.model.Domain
import java.time.Instant
import java.util.UUID

class CompatibilityEngine {
    fun evaluate(left: DNAIntent, right: DNAIntent, now: Instant): Compatibility? {
        if (left.ownerEphemeralId == right.ownerEphemeralId) return null
        if (left.domain != right.domain) return null
        if (!left.isActive(now) || !right.isActive(now)) return null

        return when (left.domain) {
            Domain.TRAVEL -> matchTravel(left, right, now)
            Domain.SHOPPING -> matchShopping(left, right, now)
            Domain.SOCIAL -> matchSocial(left, right, now)
            else -> null
        }
    }

    private fun matchTravel(left: DNAIntent, right: DNAIntent, now: Instant): Compatibility? {
        if (left.intentType != "shared_transfer" || right.intentType != "shared_transfer") return null
        val sameOrigin = equalAttribute(left, right, "origin")
        val sameDestination = equalAttribute(left, right, "destination")
        val overlaps = left.validFrom < right.validUntil && right.validFrom < left.validUntil
        if (!sameOrigin || !sameDestination || !overlaps) return null

        return Compatibility(
            compatibilityId = UUID.randomUUID().toString(),
            leftIntentId = left.intentId,
            rightIntentId = right.intentId,
            domain = Domain.TRAVEL,
            score = 0.95,
            reasons = listOf("same origin", "same destination", "overlapping time window"),
            detectedAt = now
        )
    }

    private fun matchShopping(left: DNAIntent, right: DNAIntent, now: Instant): Compatibility? {
        if (left.intentType != "group_purchase" || right.intentType != "group_purchase") return null
        val sameProduct = equalAttribute(left, right, "productKey")
        val sameArea = left.geoAnchor?.anchorId != null && left.geoAnchor.anchorId == right.geoAnchor?.anchorId
        if (!sameProduct || !sameArea) return null

        return Compatibility(
            compatibilityId = UUID.randomUUID().toString(),
            leftIntentId = left.intentId,
            rightIntentId = right.intentId,
            domain = Domain.SHOPPING,
            score = 0.90,
            reasons = listOf("same product", "same purchasing area"),
            detectedAt = now
        )
    }

    private fun matchSocial(left: DNAIntent, right: DNAIntent, now: Instant): Compatibility? {
        if (left.intentType != "discover_interests" || right.intentType != "discover_interests") return null
        val leftInterests = splitSet(left.attributes["interestCodes"])
        val rightInterests = splitSet(right.attributes["interestCodes"])
        val common = leftInterests intersect rightInterests
        if (common.isEmpty()) return null

        val unionSize = (leftInterests union rightInterests).size.coerceAtLeast(1)
        val score = (common.size.toDouble() / unionSize).coerceIn(0.1, 1.0)
        return Compatibility(
            compatibilityId = UUID.randomUUID().toString(),
            leftIntentId = left.intentId,
            rightIntentId = right.intentId,
            domain = Domain.SOCIAL,
            score = score,
            reasons = listOf("${common.size} shared interest cluster(s)"),
            detectedAt = now
        )
    }

    private fun equalAttribute(left: DNAIntent, right: DNAIntent, key: String): Boolean {
        val l = left.attributes[key]?.trim()?.lowercase()
        val r = right.attributes[key]?.trim()?.lowercase()
        return l != null && l == r
    }

    private fun splitSet(value: String?): Set<String> = value
        ?.split(',')
        ?.map { it.trim().lowercase() }
        ?.filter { it.isNotEmpty() }
        ?.toSet()
        ?: emptySet()
}
