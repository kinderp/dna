package dna.transport

import java.time.Duration
import java.time.Instant

/** Capabilities are requested by the domain; technology names stay inside adapters. */
enum class TransportFeature {
    BROADCAST,
    PEER_TO_PEER,
    INFRASTRUCTURE,
    BACKGROUND_DISCOVERY,
    ACKNOWLEDGEMENT,
    MULTICAST,
    HIGH_BANDWIDTH,
    OFFLINE_OPERATION,
    RANGE_ESTIMATE,
    PROXIMITY_PROOF,
    MUTUAL_AUTHENTICATION,
    EXPLICIT_GESTURE
}

enum class RangeClass { TOUCH, NEAR, LOCAL, REGIONAL, GLOBAL }
enum class LatencyClass { REALTIME, INTERACTIVE, DELAYED, OPPORTUNISTIC }
enum class CostClass { VERY_LOW, LOW, MEDIUM, HIGH }
enum class MonetaryCostClass { FREE, LOW, METERED, HIGH }
enum class PrivacyExposureClass { LOW, MEDIUM, HIGH }
enum class ConnectivityState { AVAILABLE, DEGRADED, UNAVAILABLE }
enum class MessagePriority { BACKGROUND, NORMAL, URGENT, EMERGENCY }
enum class ContentEncoding { JSON, CBOR, PROTOBUF, OPAQUE }

data class TransportCapabilities(
    val features: Set<TransportFeature>,
    val maxPayloadBytes: Int,
    val rangeClass: RangeClass,
    val latencyClass: LatencyClass,
    val energyCostClass: CostClass,
    val monetaryCostClass: MonetaryCostClass,
    val privacyExposureClass: PrivacyExposureClass,
    val regulatoryConstraints: Set<String> = emptySet()
) {
    init {
        require(maxPayloadBytes > 0)
    }
}

data class RetryPolicy(
    val maxAttempts: Int = 1,
    val backoff: Duration = Duration.ZERO
) {
    init {
        require(maxAttempts > 0)
        require(!backoff.isNegative)
    }
}

data class TransportPolicy(
    val purpose: String,
    val requiredFeatures: Set<TransportFeature>,
    val preferredTransports: List<String> = emptyList(),
    val forbiddenTransports: Set<String> = emptySet(),
    val maximumPayloadBytes: Int,
    val maximumEnergyCost: CostClass,
    val maximumMonetaryCost: MonetaryCostClass,
    val maximumPrivacyExposure: PrivacyExposureClass,
    val urgency: MessagePriority,
    val retryPolicy: RetryPolicy = RetryPolicy(),
    val expiresAt: Instant,
    val fallbackAllowed: Boolean,
    val consentRef: String
) {
    init {
        require(maximumPayloadBytes > 0)
        require(consentRef.isNotBlank())
    }
}

data class TransportEnvelope(
    val envelopeVersion: String = "0.1",
    val messageType: String,
    val messageId: String,
    val ephemeralSenderId: String,
    val correlationId: String? = null,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val priority: MessagePriority,
    val hopLimit: Int = 0,
    val contentEncoding: ContentEncoding,
    val payload: ByteArray,
    val authenticationData: ByteArray? = null
) {
    init {
        require(messageId.isNotBlank())
        require(ephemeralSenderId.isNotBlank())
        require(hopLimit in 0..255)
        require(expiresAt.isAfter(issuedAt))
    }

    fun isExpired(now: Instant): Boolean = !expiresAt.isAfter(now)
}

data class PublicationHandle(val transportId: String, val publicationId: String)

data class ReceivedEnvelope(
    val transportId: String,
    val envelope: TransportEnvelope,
    val receivedAt: Instant
)

enum class DeliveryStatus { ACCEPTED, DROPPED, REJECTED, UNAVAILABLE }

data class DeliveryResult(
    val transportId: String,
    val status: DeliveryStatus,
    val detail: String? = null
)

interface CommunicationTransport {
    val id: String
    val capabilities: TransportCapabilities

    fun state(): ConnectivityState

    fun advertise(
        envelope: TransportEnvelope,
        policy: TransportPolicy,
        now: Instant
    ): Pair<PublicationHandle?, DeliveryResult>

    fun send(
        endpoint: String,
        envelope: TransportEnvelope,
        policy: TransportPolicy,
        now: Instant
    ): DeliveryResult

    fun scan(now: Instant): List<ReceivedEnvelope>

    fun stop(handle: PublicationHandle)
}
