# Implementazione di riferimento v0.1

## Obiettivo

La prima implementazione non è ancora un SDK Android né un backend di produzione. È un simulatore Kotlin/JVM senza framework esterni che rende eseguibili le decisioni della baseline architetturale.

## Flusso coperto

```text
DNAIntent Travel o Shopping
→ Compatibility spiegabile
→ consenso bilaterale
→ GeoRoom ancorata a una tratta o area
→ DNATrace in TransportEnvelope
→ TransportOrchestrator
→ MockTransportAdapter
→ ricezione, TTL e deduplicazione
```

## Componenti

- modelli comuni di profilo, fragment, trace, intenti, ancore e stanze;
- `CommunicationTransport` indipendente dal mezzo;
- `TransportCapabilities` e `TransportPolicy`;
- orchestratore con selezione per capability, costi e privacy;
- adapter simulato con perdita, duplicazione, latenza e partizioni;
- matching iniziale per transfer condivisi, acquisti di gruppo e interessi sociali;
- registro del consenso e creazione di Intent Room;
- schemi JSON v0.1 ed esempi validabili.

## Cosa dimostra

- il verticale non nomina BLE, NFC, Wi-Fi o LoRa;
- la scelta del trasporto dipende da capability e policy;
- un adapter fallito può essere sostituito da un fallback senza modificare il dominio;
- una compatibilità non crea una stanza prima del consenso di entrambi;
- messaggi scaduti e duplicati non vengono consegnati all'applicazione.

## Limiti intenzionali

- niente crittografia reale: `authenticator` e `authenticationData` sono placeholder di contratto;
- niente persistenza;
- niente server Internet reale;
- matching basato su regole dimostrative;
- niente UI o mappa Android;
- niente adapter hardware;
- nessun workflow GitHub Actions automatico, per evitare consumo involontario di minuti nella fase iniziale.

## Esecuzione

```bash
python3 -m pip install -r requirements-dev.txt
./scripts/test-reference.sh
./scripts/run-demo.sh
```

## Passo successivo

Implementare un `InternetTransportAdapter` locale con API HTTP/WebSocket e una piccola mappa Android che mostri una GeoRoom generata dai casi Travel e Shopping.
