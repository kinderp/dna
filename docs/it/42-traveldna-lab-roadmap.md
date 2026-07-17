# Travel DNA Lab roadmap

## Obiettivo

Travel DNA Lab trasforma architettura e test in percorsi didattici. Uno studente
deve poter seguire un fatto dall'input all'output, capire gli stati, eseguire una
fixture e modificare un componente senza conoscere tutto il sistema.

## Cosa non è il Lab v0

- dashboard di produzione;
- tracing sempre attivo;
- simulatore 3D;
- secondo modello dati del prodotto;
- scusa per aggiungere I/O al percorso caldo;
- dimostrazione automatica di affidabilità stradale.

## Stati

| Stato | Significato |
| --- | --- |
| `draft` | Struttura o semantica incompleta. |
| `stable-doc` | Percorso consolidato, codice non necessariamente presente. |
| `executable` | Comando, fixture/fake e test esistono. |
| `public-output` | Formato macchina versionato; non ancora usato. |
| `deprecated` | Scenario sostituito con successore indicato. |

## Formato scenario

```text
id, status, learning goal, prerequisites, user story, platforms
fixture/fake, trigger, expected evidence, tracepoints
module/function path, state changes, output
performance, privacy/safety, existing/missing/future tests
common failures, non-goals, related docs
```

## Scenari eseguibili

| Scenario | Cosa insegna | Comando | Capitolo |
| --- | --- | --- | --- |
| [Reference routing Java/Rust](lab/scenarios/reference-routing-java-rust.md) | Grafo, Dijkstra, A*, determinismo e contract diff. | `sh tools/tdna lab reference-routing astar` | [43](43-reference-routing-java-rust.md) |
| [Routing contracts e fake provider](lab/scenarios/routing-contracts-fake-provider.md) | Porta, capability, provenance, fake e conformance. | `sh tools/tdna lab routing-contracts` | [44](44-contratti-routing-e-fake-provider.md) |
| [MapScene e fake renderer](lab/scenarios/map-scene-fake-renderer.md) | Scena statica, delta, marker semantici e renderer contract. | `sh tools/tdna lab map-scene` | [45](45-map-scene-e-fake-renderer.md) |
| [LocationSample e replay](lab/scenarios/location-replay-deterministico.md) | Tempo monotono, ordering gate, clock virtuale, rate e fixture. | `sh tools/tdna lab location-replay` | [46](46-location-sample-e-replay-deterministico.md) |

I quattro scenari mostrano una progressione:

```text
43: come si calcola una route
44: come l'app richiede una route
45: come la route diventa scena e delta
46: come arrivano e si riproducono i campioni di posizione
```

## Scenari `stable-doc` successivi

| Scenario | Cosa insegna | Dipendenza per diventare eseguibile |
| --- | --- | --- |
| [Render canonical route](lab/scenarios/render-canonical-route.md) | Adapter grafico e verifica fake/reale. | Adapter MapLibre. |
| [Missed exit and reroute](lab/scenarios/navigation-missed-exit-reroute.md) | Map matching, progress, off-route e route replacement. | Location replay, matched position e fake guidance. |
| [Chat with external navigation](lab/scenarios/chat-with-external-navigation.md) | Foreground navigator, push, store e voice reply. | Conversation core e platform fake. |
| [Daily page](lab/scenarios/daily-page-photos-thoughts.md) | Eventi, media e controllo utente. | Journey store e media fake. |
| [DNA exchange](lab/scenarios/dna-exchange-privacy.md) | Approssimazione, consenso e revoca. | Privacy filter e presence fake. |

## Livelli didattici

- **A — leggere:** diagramma, glossario, output e non-obiettivi;
- **B — eseguire:** fixture/fake, test, report e CLI;
- **C — modificare:** cambiare scenario, aggiungere caso limite o test;
- **D — misurare:** benchmark, memoria, frame, batteria e FFI;
- **E — progettare:** ADR, contratto, threat model o sostituzione provider.

## Tracciabilità

```text
use case
-> bounded context
-> contratto/tracepoint
-> modulo/funzione
-> stato/dato
-> test
-> benchmark
-> issue/PR
```

### Location replay

```text
issue #11 -> PR #16
-> TDNA_LOCATION_REPLAY_V0
-> GeoPoint/LocationSample
-> LocationSampleGate
-> VirtualReplayClock/PlaybackRate
-> DeterministicReplayRunner
-> report e benchmark
-> capitolo 46
```

## Evoluzione strumenti

### Fase 1 — corrente

- Markdown e Mermaid;
- fixture e fake;
- Java, Rust e Kotlin CLI;
- report JSON ristretto;
- test common/JVM/Linux;
- architecture checks;
- benchmark diagnostico senza threshold.

### Fase 2

- timeline visuale accepted/rejected;
- raw, filtered e matched position;
- route progress;
- state inspector del missed-exit scenario.

### Fase 3

- link generati al codice;
- call graph mirati;
- benchmark comparison;
- preview PR.

### Fase 4

- esercizi autovalutativi;
- notebook;
- dataset challenge;
- plugin starter kit.

## Regola

Prima pochi scenari spiegati bene, poi copertura ampia. Un Lab che tenta di
mostrare tutto diventa incomprensibile.
