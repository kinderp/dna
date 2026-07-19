package dna.transport

import java.time.Instant

private fun CostClass.rank(): Int = ordinal
private fun MonetaryCostClass.rank(): Int = ordinal
private fun PrivacyExposureClass.rank(): Int = ordinal

data class TransportAttempt(
    val transportId: String,
    val result: DeliveryResult
)

data class OrchestrationResult(
    val acceptedBy: String?,
    val attempts: List<TransportAttempt>
)

class TransportOrchestrator(
    transports: Collection<CommunicationTransport>
) {
    private val transportsById = transports.associateBy { it.id }
    private val seenMessageIds = mutableSetOf<String>()

    init {
        require(transportsById.size == transports.size) { "Transport IDs must be unique" }
    }

    fun eligibleTransports(policy: TransportPolicy, payloadBytes: Int): List<CommunicationTransport> =
        transportsById.values
            .asSequence()
            .filter { it.id !in policy.forbiddenTransports }
            .filter { it.state() != ConnectivityState.UNAVAILABLE }
            .filter { it.capabilities.features.containsAll(policy.requiredFeatures) }
            .filter { payloadBytes <= it.capabilities.maxPayloadBytes }
            .filter { payloadBytes <= policy.maximumPayloadBytes }
            .filter { it.capabilities.energyCostClass.rank() <= policy.maximumEnergyCost.rank() }
            .filter { it.capabilities.monetaryCostClass.rank() <= policy.maximumMonetaryCost.rank() }
            .filter { it.capabilities.privacyExposureClass.rank() <= policy.maximumPrivacyExposure.rank() }
            .sortedWith(
                compareBy<CommunicationTransport> { preferredRank(policy, it.id) }
                    .thenBy { it.capabilities.energyCostClass.rank() }
                    .thenBy { it.capabilities.privacyExposureClass.rank() }
                    .thenBy { it.id }
            )
            .toList()

    private fun preferredRank(policy: TransportPolicy, id: String): Int {
        val index = policy.preferredTransports.indexOf(id)
        return if (index >= 0) index else Int.MAX_VALUE
    }

    fun advertise(
        envelope: TransportEnvelope,
        policy: TransportPolicy,
        now: Instant
    ): OrchestrationResult {
        val attempts = mutableListOf<TransportAttempt>()
        val candidates = eligibleTransports(policy, envelope.payload.size)
        for (transport in candidates) {
            val (_, result) = transport.advertise(envelope, policy, now)
            attempts += TransportAttempt(transport.id, result)
            if (result.status == DeliveryStatus.ACCEPTED) {
                return OrchestrationResult(transport.id, attempts)
            }
            if (!policy.fallbackAllowed) break
        }
        return OrchestrationResult(null, attempts)
    }

    fun receive(now: Instant): List<ReceivedEnvelope> {
        val received = transportsById.values.flatMap { it.scan(now) }
        return received
            .asSequence()
            .filter { !it.envelope.isExpired(now) }
            .filter { seenMessageIds.add(it.envelope.messageId) }
            .sortedBy { it.receivedAt }
            .toList()
    }
}
