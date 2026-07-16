# Travel DNA Lab roadmap

## Obiettivo

Travel DNA Lab trasforma architettura e test in percorsi didattici. Uno studente
deve poter seguire un fatto dall'input alla UI, capire gli stati, eseguire una
fixture e modificare un componente senza conoscere tutto il sistema.

## Cosa non è il Lab v0

- dashboard di produzione;
- tracing sempre attivo;
- simulatore 3D;
- generatore automatico perfetto;
- secondo modello dati;
- scusa per aggiungere I/O al hot path.

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

## Primo set

| Scenario | Cosa insegna |
| --- | --- |
| [Missed exit and reroute](lab/scenarios/navigation-missed-exit-reroute.md) | GPS, map matching, state machine e route replacement. |
| [Chat with external navigation](lab/scenarios/chat-with-external-navigation.md) | Waze foreground, push, local store, driving policy e voice reply. |
| [Daily page photos and thoughts](lab/scenarios/daily-page-photos-thoughts.md) | Eventi, proiezione, media e controllo utente. |
| [DNA exchange privacy](lab/scenarios/dna-exchange-privacy.md) | Approssimazione, consenso, Cartolina e revoca. |
| [Render canonical route](lab/scenarios/render-canonical-route.md) | Contratto map scene e adapter MapLibre. |

## Livelli didattici

### Livello A: leggere

- diagramma;
- glossario;
- output atteso;
- non-obiettivi.

### Livello B: eseguire

- replay;
- test;
- report;
- visualizer.

### Livello C: modificare

- cambiare soglia;
- implementare fake;
- aggiungere provider;
- confrontare output.

### Livello D: misurare

- benchmark;
- memoria;
- frame;
- battery;
- FFI.

### Livello E: progettare

- riaprire un ADR;
- definire nuovo contract;
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

## Evoluzione strumenti

Fase 1:

- Markdown;
- Mermaid;
- fixture;
- CLI replay;
- report JSON/CSV.

Fase 2:

- visualizer web/local;
- timeline interattiva;
- map raw vs matched;
- state inspector.

Fase 3:

- generated code links;
- call graph mirati;
- benchmark comparison;
- PR preview.

Fase 4:

- esercizi autovalutativi;
- notebook didattici;
- dataset challenge;
- plugin starter kit.

## Regola

Prima pochi scenari spiegati bene, poi copertura ampia. Un Lab che tenta di
mostrare tutto diventa incomprensibile.
