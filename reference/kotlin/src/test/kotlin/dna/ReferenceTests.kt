package dna

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
import dna.transport.DeliveryStatus
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
import java.time.Instant

private val now: Instant = Instant.parse("2026-07-19T10:30:00Z")

fun main() {
    testTravelCompatibility()
    testShoppingCompatibilityAndConsent()
    testPreferredTransportSelection()
    testFallbackAfterLoss()
    testDuplicateSuppression()
    testExpiredEnvelopeIsNotDelivered()
    println("All Kotlin reference tests passed")
}

private fun testTravelCompatibility() {
    val route = routeAnchor()
    val left = intent(
        id = "travel-a",
        owner = "ephemeral-a-001",
        domain = Domain.TRAVEL,
        type = "shared_transfer",
        attributes = mapOf("origin" to "Messina", "destination" to "CTA"),
        anchor = route
    )
    val right = left.copy(intentId = "travel-b", ownerEphemeralId = "ephemeral-b-002")
    val result = CompatibilityEngine().evaluate(left, right, now)
    check(result != null)
    check(result.score == 0.95)
    check(result.reasons.size == 3)
}

private fun testShoppingCompatibilityAndConsent() {
    val area = areaAnchor()
    val left = intent(
        id = "shopping-a",
        owner = "ephemeral-a-001",
        domain = Domain.SHOPPING,
        type = "group_purchase",
        attributes = mapOf("productKey" to "ean:123"),
        anchor = area
    )
    val right = left.copy(intentId = "shopping-b", ownerEphemeralId = "ephemeral-b-002")
    val compatibility = requireNotNull(CompatibilityEngine().evaluate(left, right, now))

    val registry = ConsentRegistry()
    val service = GeoRoomService(registry)
    check(service.createIntentRoom(compatibility, left, right, now) == null)

    listOf(left.ownerEphemeralId, right.ownerEphemeralId).forEach { owner ->
        registry.grant(
            ConsentGrant(
                consentId = "consent-$owner",
                subjectEphemeralId = owner,
                compatibilityId = compatibility.compatibilityId,
                purpose = "join_intent_room",
                grantedAt = now,
                expiresAt = now.plusSeconds(120)
            )
        )
    }
    val room = requireNotNull(service.createIntentRoom(compatibility, left, right, now))
    check(room.anchor.anchorId == area.anchorId)
    check(room.participantEphemeralIds.size == 2)
}

private fun testPreferredTransportSelection() {
    val ble = adapter("ble", loss = 0.0, duplicates = 0.0)
    val internet = adapter("internet", loss = 0.0, duplicates = 0.0, highBandwidth = true)
    val orchestrator = TransportOrchestrator(listOf(internet, ble))
    val result = orchestrator.advertise(envelope("preferred"), policy(listOf("ble", "internet")), now)
    check(result.acceptedBy == "ble")
}

private fun testFallbackAfterLoss() {
    val lossyBle = adapter("ble", loss = 1.0, duplicates = 0.0)
    val internet = adapter("internet", loss = 0.0, duplicates = 0.0, highBandwidth = true)
    val orchestrator = TransportOrchestrator(listOf(lossyBle, internet))
    val result = orchestrator.advertise(envelope("fallback"), policy(listOf("ble", "internet")), now)
    check(result.attempts.first().result.status == DeliveryStatus.DROPPED)
    check(result.acceptedBy == "internet")
}

private fun testDuplicateSuppression() {
    val duplicating = adapter("ble", loss = 0.0, duplicates = 1.0)
    val orchestrator = TransportOrchestrator(listOf(duplicating))
    orchestrator.advertise(envelope("duplicate"), policy(listOf("ble")), now)
    val received = orchestrator.receive(now)
    check(received.size == 1) { "Expected one envelope after deduplication, got ${received.size}" }
}

private fun testExpiredEnvelopeIsNotDelivered() {
    val adapter = adapter("ble", loss = 0.0, duplicates = 0.0)
    val orchestrator = TransportOrchestrator(listOf(adapter))
    val expiring = envelope("expired", expiresAt = now.plusSeconds(1))
    orchestrator.advertise(expiring, policy(listOf("ble"), expiresAt = now.plusSeconds(10)), now)
    val received = orchestrator.receive(now.plusSeconds(2))
    check(received.isEmpty())
}

private fun adapter(
    id: String,
    loss: Double,
    duplicates: Double,
    highBandwidth: Boolean = false
): MockTransportAdapter {
    val features = mutableSetOf(
        TransportFeature.BACKGROUND_DISCOVERY,
        TransportFeature.BROADCAST
    )
    if (highBandwidth) features += TransportFeature.HIGH_BANDWIDTH
    return MockTransportAdapter(
        id = id,
        capabilities = TransportCapabilities(
            features = features,
            maxPayloadBytes = if (highBandwidth) 1_000_000 else 96,
            rangeClass = if (highBandwidth) RangeClass.GLOBAL else RangeClass.NEAR,
            latencyClass = LatencyClass.REALTIME,
            energyCostClass = if (highBandwidth) CostClass.MEDIUM else CostClass.VERY_LOW,
            monetaryCostClass = if (highBandwidth) MonetaryCostClass.LOW else MonetaryCostClass.FREE,
            privacyExposureClass = if (highBandwidth) PrivacyExposureClass.HIGH else PrivacyExposureClass.MEDIUM
        ),
        config = MockTransportConfig(
            lossProbability = loss,
            duplicationProbability = duplicates,
            seed = 11
        )
    )
}

private fun policy(
    preferred: List<String>,
    expiresAt: Instant = now.plusSeconds(60)
): TransportPolicy = TransportPolicy(
    purpose = "test-discovery",
    requiredFeatures = setOf(TransportFeature.BACKGROUND_DISCOVERY),
    preferredTransports = preferred,
    maximumPayloadBytes = 96,
    maximumEnergyCost = CostClass.MEDIUM,
    maximumMonetaryCost = MonetaryCostClass.LOW,
    maximumPrivacyExposure = PrivacyExposureClass.HIGH,
    urgency = MessagePriority.NORMAL,
    expiresAt = expiresAt,
    fallbackAllowed = true,
    consentRef = "consent:test"
)

private fun envelope(
    id: String,
    expiresAt: Instant = now.plusSeconds(60)
): TransportEnvelope = TransportEnvelope(
    messageType = "dna_trace",
    messageId = id,
    ephemeralSenderId = "ephemeral-test-001",
    issuedAt = now,
    expiresAt = expiresAt,
    priority = MessagePriority.NORMAL,
    contentEncoding = ContentEncoding.CBOR,
    payload = ByteArray(48)
)

private fun intent(
    id: String,
    owner: String,
    domain: Domain,
    type: String,
    attributes: Map<String, String>,
    anchor: GeoAnchor
): DNAIntent = DNAIntent(
    intentId = id,
    ownerEphemeralId = owner,
    domain = domain,
    intentType = type,
    attributes = attributes,
    geoAnchor = anchor,
    validFrom = now.minusSeconds(60),
    validUntil = now.plusSeconds(3600)
)

private fun areaAnchor(): GeoAnchor.Area = GeoAnchor.Area(
    anchorId = "area:rometta-spadafora",
    polygon = listOf(
        GeoPoint(15.40, 38.22),
        GeoPoint(15.45, 38.22),
        GeoPoint(15.45, 38.27),
        GeoPoint(15.40, 38.22)
    )
)

private fun routeAnchor(): GeoAnchor.Route = GeoAnchor.Route(
    anchorId = "route:messina-cta",
    polyline = listOf(
        GeoPoint(15.55, 38.19),
        GeoPoint(15.07, 37.47)
    ),
    corridorMeters = 10_000.0
)
