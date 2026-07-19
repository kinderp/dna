# Implementazione di riferimento Kotlin

Questo modulo è un simulatore eseguibile e non costituisce ancora l'SDK Android definitivo. Usa soltanto Kotlin/JVM e la libreria standard per mantenere verificabili i concetti della baseline v0.1.

## Contenuti

- modelli di `DNAIntent`, `DNATrace`, `Compatibility`, consenso e `GeoRoom`;
- contratto `CommunicationTransport`;
- capability e policy indipendenti dalla tecnologia;
- `TransportOrchestrator` con selezione e fallback;
- `MockTransportAdapter` con latenza, perdita, duplicazione e partizioni;
- matching spiegabile Travel, Shopping e Social;
- demo end-to-end e test senza framework esterni.

## Esecuzione

```bash
./scripts/test-reference.sh
./scripts/run-demo.sh
```

Richiede JDK 21 o compatibile e `kotlinc` disponibile nel `PATH`.
