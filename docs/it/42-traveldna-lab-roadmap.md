# Travel DNA Lab roadmap

## Obiettivo

Travel DNA Lab trasforma architettura e test in percorsi didattici. Uno studente
deve poter seguire un fatto dall'input all'output, capire gli stati, eseguire una
fixture e modificare un componente senza conoscere tutto il sistema.

## Cosa non è il Lab v0

- dashboard di produzione;
- tracing sempre attivo;
- simulatore 3D;
- generatore automatico perfetto;
- secondo modello dati del prodotto;
- scusa per aggiungere I/O al percorso caldo.

## Stati degli scenari

| Stato | Significato |
| --- | --- |
| `draft` | Struttura o semantica incompleta. |
| `stable-doc` | Percorso consolidato, codice non necessariamente presente. |
| `executable` | Comando, fixture/fake e test esistono. |
| `public-output` | Formato macchina versionato; non ancora usato. |
| `deprecated` | Scenario sostituito con successore indicato. |

## Formato scenario

Ogni scenario documenta almeno:

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
| [Reference routing Java/Rust](lab/scenarios/reference-routing-java-rust.md) | Grafo, parser, Dijkstra, A*, euristica, determinismo e contract diff. | `sh tools/tdna lab reference-routing astar` | [43](43-reference-routing-java-rust.md) |
| [Routing contracts e fake provider](lab/scenarios/routing-contracts-fake-provider.md) | KMP, porta, capability, invarianti, provenance, fake e conformance probe. | `sh tools/tdna lab routing-contracts` | [44](44-contratti-routing-e-fake-provider.md) |

I due scenari mostrano livelli diversi:

```text
capitolo 43: come viene calcolato un percorso
capitolo 44: come l'app chiede un percorso senza dipendere dal calcolatore
```

## Scenari `stable-doc` successivi

| Scenario | Cosa insegna | Dipendenza per diventare eseguibile |
| --- | --- | --- |
| [Render canonical route](lab/scenarios/render-canonical-route.md) | MapScene, delta e renderer provider-neutral. | Map contracts e fake renderer. |
| [Missed exit and reroute](lab/scenarios/navigation-missed-exit-reroute.md) | GPS, map matching, state machine e route replacement. | Clock, LocationSample e fake guidance. |
| [Chat with external navigation](lab/scenarios/chat-with-external-navigation.md) | Foreground navigator, push, local store e voice reply. | Conversation core e platform fake. |
| [Daily page](lab/scenarios/daily-page-photos-thoughts.md) | Eventi, proiezione, media e controllo utente. | Journey store e media fake. |
| [DNA exchange](lab/scenarios/dna-exchange-privacy.md) | Approssimazione, consenso, Cartolina e revoca. | Privacy filter e presence fake. |

## Livelli didattici

### A — leggere

Diagramma, glossario, output e non-obiettivi.

### B — eseguire

Fixture/fake, test, report e CLI.

### C — modificare

Cambiare scenario, aggiungere caso limite, provider fake o test.

### D — misurare

Benchmark, memoria, frame, batteria e FFI.

### E — progettare

ADR, nuovo contratto, threat model o sostituzione provider.

## Tracciabilità

Ogni scenario collega:

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

### Reference routing

```text
issue #3 -> PR #4
-> fixture
-> Java/Rust
-> Dijkstra/A*
-> report diff
-> capitolo 43
```

### Routing contracts

```text
issue #5 -> PR #6
-> plugin-sdk
-> routing-contracts
-> fake planner
-> conformance probe
-> CLI
-> capitolo 44
```

## Evoluzione strumenti

### Fase 1 — corrente

- Markdown e Mermaid;
- fixture e fake;
- Java, Rust e Kotlin CLI;
- report JSON ristretto;
- test common/JVM/Linux;
- CI e architecture checks.

### Fase 2

- MapScene inspector;
- GPS replay con clock virtuale;
- timeline degli stati;
- raw vs matched position.

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
