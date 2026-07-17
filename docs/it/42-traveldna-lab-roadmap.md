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
| `draft` | Struttura o semantica ancora incompleta. |
| `stable-doc` | Percorso didattico consolidato, codice non necessariamente presente. |
| `executable` | Comando, fixture e test esistono nel repository. |
| `public-output` | Eventuale formato macchina versionato; non ancora usato. |
| `deprecated` | Scenario sostituito, con indicazione del successore. |

La promozione `stable-doc -> executable` richiede che il documento venga
riallineato ai nomi reali di moduli, funzioni, test e artifact.

## Formato scenario

Ogni Markdown contiene:

```text
id
title
status
learning goal
prerequisites
user story
platforms
scenario kind
fixture
trigger
expected evidence
expected domain events
logical tracepoints
function/module path
state changes
expected UI/output
performance properties
privacy/safety properties
existing tests
missing tests
future tests
common failures
non-goals
related docs
```

## Primo scenario eseguibile

| Scenario | Stato | Cosa insegna | Comando |
| --- | --- | --- | --- |
| [Reference routing Java/Rust](lab/scenarios/reference-routing-java-rust.md) | executable | Grafo, parser, Dijkstra, A*, euristica, determinismo e contract test cross-language. | `sh tools/tdna lab reference-routing astar` |

Il capitolo di accompagnamento è
[Primo laboratorio eseguibile: routing di riferimento](43-reference-routing-java-rust.md).

Il primo scenario non usa Android, iOS, OpenStreetMap o provider. Questa scelta
riduce il numero di variabili e rende osservabile l'algoritmo.

## Scenari `stable-doc` successivi

| Scenario | Cosa insegna | Dipendenza per diventare eseguibile |
| --- | --- | --- |
| [Missed exit and reroute](lab/scenarios/navigation-missed-exit-reroute.md) | GPS, map matching, state machine e route replacement. | Replay clock, fake guidance e route model. |
| [Chat with external navigation](lab/scenarios/chat-with-external-navigation.md) | Navigatore foreground, push, local store, driving policy e voice reply. | Conversation core e platform fake. |
| [Daily page photos and thoughts](lab/scenarios/daily-page-photos-thoughts.md) | Eventi, proiezione, media e controllo utente. | Journey event store e media fake. |
| [DNA exchange privacy](lab/scenarios/dna-exchange-privacy.md) | Approssimazione, consenso, Cartolina e revoca. | Privacy filter e presence fake. |
| [Render canonical route](lab/scenarios/render-canonical-route.md) | Contratto map scene e adapter MapLibre. | Canonical route/map contracts e fake renderer. |

## Livelli didattici

### Livello A: leggere

- diagramma;
- glossario;
- output atteso;
- non-obiettivi.

### Livello B: eseguire

- fixture;
- test;
- report;
- replay o CLI.

### Livello C: modificare

- cambiare una strada sintetica;
- aggiungere un caso limite;
- implementare un fake;
- confrontare output.

### Livello D: misurare

- benchmark;
- memoria;
- frame;
- batteria;
- FFI.

### Livello E: progettare

- riaprire un ADR;
- definire un nuovo contratto;
- threat model;
- provider replacement.

## Tracciabilità

Ogni scenario deve collegare:

```text
use case
-> bounded context
-> tracepoint
-> modulo/funzione
-> state/data
-> test
-> benchmark
-> issue/PR
```

Per il reference routing:

```text
issue #3
-> fixture reference-network-v0
-> Java/Rust parser
-> Dijkstra/A*
-> report
-> contract diff
-> capitolo 43
-> scenario executable
```

## Evoluzione strumenti

### Fase 1 — corrente

- Markdown;
- Mermaid;
- fixture;
- CLI;
- report JSON ristretto;
- test e CI.

### Fase 2

- GPS replay con clock virtuale;
- timeline degli stati;
- visualizzazione raw vs matched;
- state inspector.

### Fase 3

- link generati al codice;
- call graph mirati;
- benchmark comparison;
- preview nelle pull request.

### Fase 4

- esercizi autovalutativi;
- notebook didattici;
- dataset challenge;
- plugin starter kit.

## Regola

Prima pochi scenari spiegati bene, poi copertura ampia. Un Lab che tenta di
mostrare tutto diventa incomprensibile.
