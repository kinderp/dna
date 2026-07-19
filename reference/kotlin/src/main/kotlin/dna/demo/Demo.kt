package dna.demo

import dna.consent.ConsentRegistry
import dna.consent.GeoRoomService
import dna.matching.CompatibilityEngine
import dna.model.ConsentGrant
import dna.model.DNAIntent
import dna.model.Domain
import dna.model.GeoAnchor
import dna.model.GeoPoint
import dna.transport.ContentEncoding
import dna.transport.CostClass
import dna.transport.LatencyClass
import dna.transport.MessagePriority
import dna.transport.MockTransportAdapter
import dna.transport.MockTransportConfig
import dna.transport.MonetaryCostClass
import dna.transport.PrivacyExposureClass
import dna.transport.RangeClass
import dna.transport.TransportCapabilities
import dna.transport.TransportEnvelope
import dna.transport.TransportFeature
import dna.transport.TransportOrchestrator
import dna.transport.TransportPolicy
import java.time.Duration
import java.time.Instant

fun main() {
    val now = Instant.parse("2026-07-19T10:30:00Z")
    val area = GeoAnchor.Area(
        anchorId = "area:rometta-spadafora",
        polygon = listOf(
            GeoPoint(15.40, 38.22),
            GeoPoint(15.45, 38.22),
            GeoPoint(15.45, 38.27),
            GeoPoint(15.40, 38.22)
        ),
        label = "Rometta–Spadafora"
    )

    val alice = DNAIntent(
        intentId = "shopping-a",
        ownerEphemeralId = "ephemeral-alice-01",
        domain = Domain.SHOPPING,
        intentType = "group_purchase",
        attributes = mapOf("productKey" to "ean:800000000001", "quantity" to "3"),
        geoAnchor = area,
        validFrom = now.minusSeconds(60),
        validUntil = now.plus(Duration.ofHours(24))
    )
    val bob = alice.copy(
        intentId = "shopping-b",
        ownerEphemeralId = "ephemeral-bob-02",
        attributes = mapOf("productKey" to "ean:800000000001", "quantity" to "2")
    )

    val compatibility = requireNotNull(CompatibilityEngine().evaluate(alice, bob, now))
    println("Compatibility ${compatibility.score}: ${compatibility.reasons.joinToString()}")

    val consentRegistry = ConsentRegistry()
    for (subject in setOf(alice.ownerEphemeralId, bob.ownerEphemeralId)) {
        consentRegistry.grant(
            ConsentGrant(
                consentId = "consent-$subject",
                subjectEphemeralId = subject,
                compatibilityId = compatibility.compatibilityId,
                purpose = "join_intent_room",
                grantedAt = now,
                expiresAt = now.plus(Duration.ofMinutes(30))
            )
        )
    }
    val room = requireNotNull(GeoRoomService(consentRegistry).createIntentRoom(compatibility, alice, bob, now))
    println("GeoRoom ${room.roomId} on ${room.anchor.anchorId}, topic=${room.topics.first()}")

    val ble = MockTransportAdapter(
        id = "ble",
        capabilities = TransportCapabilities(
            features = setOf(
                TransportFeature.BROADCAST,
                TransportFeature.PEER_TO_PEER,
                TransportFeature.BACKGROUND_DISCOVERY,
                TransportFeature.OFFLINE_OPERATION
            ),
            maxPayloadBytes = 96,
            rangeClass = RangeClass.NEAR,
            latencyClass = LatencyClass.INTERACTIVE,
            energyCostClass = CostClass.VERY_LOW,
            monetaryCostClass = MonetaryCostClass.FREE,
            privacyExposureClass = PrivacyExposureClass.MEDIUM
        ),
        config = MockTransportConfig(latency = Duration.ofMillis(50), seed = 7)
    )
    val internet = MockTransportAdapter(
        id = "internet",
        capabilities = TransportCapabilities(
            features = setOf(
                TransportFeature.INFRASTRUCTURE,
                TransportFeature.BACKGROUND_DISCOVERY,
                TransportFeature.ACKNOWLEDGEMENT,
                TransportFeature.HIGH_BANDWIDTH
            ),
            maxPayloadBytes = 1_000_000,
            rangeClass = RangeClass.GLOBAL,
            latencyClass = LatencyClass.REALTIME,
            energyCostClass = CostClass.MEDIUM,
            monetaryCostClass = MonetaryCostClass.LOW,
            privacyExposureClass = PrivacyExposureClass.HIGH
        )
    )

    val policy = TransportPolicy(
        purpose = "discover-group-purchase",
        requiredFeatures = setOf(TransportFeature.BACKGROUND_DISCOVERY),
        preferredTransports = listOf("ble", "internet"),
        maximumPayloadBytes = 96,
        maximumEnergyCost = CostClass.MEDIUM,
        maximumMonetaryCost = MonetaryCostClass.LOW,
        maximumPrivacyExposure = PrivacyExposureClass.HIGH,
        urgency = MessagePriority.NORMAL,
        expiresAt = now.plusSeconds(60),
        fallbackAllowed = true,
        consentRef = "consent:nearby-discovery"
    )
    val envelope = TransportEnvelope(
        messageType = "dna_trace",
        messageId = "message-001",
        ephemeralSenderId = alice.ownerEphemeralId,
        issuedAt = now,
        expiresAt = now.plusSeconds(60),
        priority = MessagePriority.NORMAL,
        contentEncoding = ContentEncoding.CBOR,
        payload = ByteArray(48) { it.toByte() }
    )

    val orchestrator = TransportOrchestrator(listOf(ble, internet))
    val result = orchestrator.advertise(envelope, policy, now)
    println("Trace published via ${result.acceptedBy}; attempts=${result.attempts.size}")
    val received = orchestrator.receive(now.plusMillis(50))
    println("Received ${received.size} deduplicated envelope(s)")
}
