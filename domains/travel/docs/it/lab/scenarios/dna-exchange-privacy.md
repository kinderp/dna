# Scenario: exchange a DNA card without exact location

id: `lab.dna.privacy-exchange.v0`
status: `stable-doc`
scenario kind: `presence-dna-privacy`

## Learning goal

Capire la separazione tra posizione precisa, presenza approssimata, profilo DNA
privato e Cartolina condivisibile.

## User story

Due famiglie percorrono lo stesso corridoio. Una riceve una Cartolina su una
sosta, senza conoscere coordinate live o identità completa dell'altra.

## Trigger

- active consent;
- compatible corridor;
- sender publishes sanitized DnaCard;
- recipient allows automatic receipt.

## Expected evidence

- exact location processed locally;
- presence approximated;
- ephemeral ID;
- compatibility evaluated;
- DnaCard sanitized;
- recipient receives card;
- sender revokes;
- future access denied according to policy.

## Tracepoints

```text
PRESENCE_SIGNAL_APPROXIMATED
PRESENCE_SIGNAL_PUBLISHED
DNA_CARD_SANITIZED
COMPANION_AGGREGATE_RECEIVED
```

## Privacy properties

- no exact coordinates in network payload;
- short TTL;
- no hotel/home;
- no full DNA;
- block applied before matching;
- revocation propagated.

## Missing tests

- cell boundary;
- repeated encounters correlation;
- spoof location;
- block race;
- offline revoke;
- k-anonymity/aggregation threshold.

## Non-goals

- cryptographic private set intersection;
- Bluetooth/LoRa transport;
- permanent friendship;
- exact realtime tracking.
