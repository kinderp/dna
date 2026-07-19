package dna.transport

import java.time.Duration
import java.time.Instant
import java.util.ArrayDeque
import java.util.Random
import java.util.UUID

data class MockTransportConfig(
    val latency: Duration = Duration.ZERO,
    val lossProbability: Double = 0.0,
    val duplicationProbability: Double = 0.0,
    val queueCapacity: Int = 1024,
    val seed: Long = 1L,
    val initiallyPartitioned: Boolean = false
) {
    init {
        require(lossProbability in 0.0..1.0)
        require(duplicationProbability in 0.0..1.0)
        require(queueCapacity > 0)
        require(!latency.isNegative)
    }
}

private data class QueuedEnvelope(
    val deliverAt: Instant,
    val envelope: TransportEnvelope
)

class MockTransportAdapter(
    override val id: String,
    override val capabilities: TransportCapabilities,
    private val config: MockTransportConfig = MockTransportConfig()
) : CommunicationTransport {
    private val random = Random(config.seed)
    private val queue = ArrayDeque<QueuedEnvelope>()
    private val activePublications = mutableSetOf<String>()
    private var partitioned: Boolean = config.initiallyPartitioned

    override fun state(): ConnectivityState = if (partitioned) {
        ConnectivityState.UNAVAILABLE
    } else {
        ConnectivityState.AVAILABLE
    }

    fun setPartitioned(value: Boolean) {
        partitioned = value
    }

    override fun advertise(
        envelope: TransportEnvelope,
        policy: TransportPolicy,
        now: Instant
    ): Pair<PublicationHandle?, DeliveryResult> {
        val publicationId = UUID.randomUUID().toString()
        val result = enqueue(envelope, policy, now)
        return if (result.status == DeliveryStatus.ACCEPTED) {
            activePublications += publicationId
            PublicationHandle(id, publicationId) to result
        } else {
            null to result
        }
    }

    override fun send(
        endpoint: String,
        envelope: TransportEnvelope,
        policy: TransportPolicy,
        now: Instant
    ): DeliveryResult = enqueue(envelope, policy, now)

    private fun enqueue(
        envelope: TransportEnvelope,
        policy: TransportPolicy,
        now: Instant
    ): DeliveryResult {
        if (partitioned) {
            return DeliveryResult(id, DeliveryStatus.UNAVAILABLE, "simulated network partition")
        }
        if (policy.expiresAt <= now || envelope.isExpired(now)) {
            return DeliveryResult(id, DeliveryStatus.REJECTED, "expired request")
        }
        if (envelope.payload.size > capabilities.maxPayloadBytes || envelope.payload.size > policy.maximumPayloadBytes) {
            return DeliveryResult(id, DeliveryStatus.REJECTED, "payload exceeds limit")
        }
        if (queue.size >= config.queueCapacity) {
            return DeliveryResult(id, DeliveryStatus.DROPPED, "queue full")
        }
        if (random.nextDouble() < config.lossProbability) {
            return DeliveryResult(id, DeliveryStatus.DROPPED, "simulated packet loss")
        }

        val deliverAt = now.plus(config.latency)
        queue.addLast(QueuedEnvelope(deliverAt, envelope))
        if (queue.size < config.queueCapacity && random.nextDouble() < config.duplicationProbability) {
            queue.addLast(QueuedEnvelope(deliverAt, envelope))
        }
        return DeliveryResult(id, DeliveryStatus.ACCEPTED)
    }

    override fun scan(now: Instant): List<ReceivedEnvelope> {
        if (partitioned) return emptyList()
        val result = mutableListOf<ReceivedEnvelope>()
        while (queue.isNotEmpty() && !queue.first().deliverAt.isAfter(now)) {
            val queued = queue.removeFirst()
            if (!queued.envelope.isExpired(now)) {
                result += ReceivedEnvelope(id, queued.envelope, now)
            }
        }
        return result
    }

    override fun stop(handle: PublicationHandle) {
        require(handle.transportId == id)
        activePublications.remove(handle.publicationId)
    }
}
