# Travel DNA Lab roadmap

## Obiettivo

Travel DNA Lab trasforma architettura e test in percorsi didattici. Uno studente
deve seguire un fatto dall'input all'output, capire ownership e stati, eseguire
una fixture e modificare un componente senza conoscere tutto il sistema.

## Stati

| Stato | Significato |
| --- | --- |
| `draft` | Struttura o semantica incompleta. |
| `stable-doc` | Percorso consolidato, codice non necessariamente presente. |
| `executable` | Comando, fixture/fake e test esistono. |
| `public-output` | Formato macchina versionato; non ancora usato. |
| `deprecated` | Scenario sostituito con successore. |

## Formato scenario

```text
id, status, learning goal, prerequisites, user story, platforms
fixture/fake, trigger, expected evidence, tracepoints
module/function path, state ownership, output
performance, privacy/safety, existing/missing/future tests
common failures, non-goals, related docs
```

## Scenari eseguibili

| Scenario | Cosa insegna | Comando | Capitolo |
| --- | --- | --- | --- |
| [Reference routing](lab/scenarios/reference-routing-java-rust.md) | Grafo, Dijkstra, A* e contract diff. | `sh tools/tdna lab reference-routing astar` | [43](43-reference-routing-java-rust.md) |
| [Routing contracts](lab/scenarios/routing-contracts-fake-provider.md) | Porta, capability, provenance e fake. | `sh tools/tdna lab routing-contracts` | [44](44-contratti-routing-e-fake-provider.md) |
| [MapScene](lab/scenarios/map-scene-fake-renderer.md) | Scena, delta e renderer contract. | `sh tools/tdna lab map-scene` | [45](45-map-scene-e-fake-renderer.md) |
| [Location replay](lab/scenarios/location-replay-deterministico.md) | Tempo monotono, gate, clock e rate. | `sh tools/tdna lab location-replay` | [46](46-location-sample-e-replay-deterministico.md) |
| [Route progress](lab/scenarios/route-progress-tracker.md) | Matched position, leg, manovra, arrival e map binding. | `sh tools/tdna lab route-progress` | [47](47-posizione-matched-e-route-progress.md) |
| [Map matching boundary](lab/scenarios/map-matching-fake-provider.md) | Sessione route-bound, esiti, fake e downstream progress. | `sh tools/tdna lab map-matching` | [48](48-porta-map-matching-e-fake-deterministico.md) |
| [Missed exit and reroute](lab/scenarios/navigation-missed-exit-reroute.md) | Evidenza normalizzata, count+duration, un attempt e replacement. | `sh tools/tdna lab missed-exit` | [49](49-off-route-missed-exit-e-reroute.md) |

Progressione:

```text
43: calcolare una route
44: richiedere una route senza dipendere dal provider
45: installare route/scena e applicare delta
46: normalizzare e riprodurre campioni di posizione
47: accettare una posizione matched e produrre progresso/delta
48: ottenere un matched validato dietro una porta sostituibile
49: confermare una deviazione e sostituire la route in modo correlato
```

## Scenari `stable-doc` successivi

| Scenario | Cosa insegna | Dipendenza |
| --- | --- | --- |
| [Render canonical route](lab/scenarios/render-canonical-route.md) | Adapter MapLibre e confronto fake/reale. | Adapter grafico. |
| [Chat with external navigation](lab/scenarios/chat-with-external-navigation.md) | Push, store e voice reply. | Conversation core. |
| [Daily page](lab/scenarios/daily-page-photos-thoughts.md) | Eventi, media e controllo utente. | Journey store. |
| [DNA exchange](lab/scenarios/dna-exchange-privacy.md) | Approssimazione, consenso e revoca. | Presence/privacy filter. |

## Livelli didattici

- **A — leggere:** diagramma, glossario, output e non-obiettivi;
- **B — eseguire:** fixture/fake, test, report e CLI;
- **C — modificare:** caso limite, policy o test;
- **D — misurare:** benchmark, memoria, frame, batteria e FFI;
- **E — progettare:** ADR, contratto, threat model o provider replacement.

## Tracciabilità della slice missed exit

```text
issue #21 -> PR #22
-> OffRouteObservation / OffRoutePolicy
-> OffRouteTracker
-> RerouteCommand / RerouteOutcome
-> RerouteCoordinator / RerouteExecutor
-> RoutePlannerPort
-> route replacement
-> Lab/benchmark
-> capitolo 49 e report del 18 luglio
```

## Evoluzione strumenti

### Fase 1 — corrente

- Markdown/Mermaid;
- fixture e fake;
- Java, Rust e Kotlin CLI;
- report JSON ristretti;
- test common/JVM/Linux;
- architecture checks;
- benchmark diagnostici senza threshold.

### Fase 2

- timeline raw/filtered/matched/progress/off-route;
- state inspector;
- primo adapter matcher controllato;
- fake runtime che ricrea matcher/progress/map binding dopo replacement.

### Fase 3

- adapter reali;
- link generati al codice;
- call graph mirati;
- benchmark comparison e preview PR.

### Fase 4

- esercizi autovalutativi;
- notebook;
- dataset challenge;
- plugin starter kit.

## Regola

Prima pochi scenari spiegati bene, poi copertura ampia. Un Lab eseguibile non è
una promessa di capacità mobile o affidabilità su strada.
