# Mappa del codice e degli stati

## Stato del documento

La mappa contiene ora due livelli distinti:

- un percorso reale ed eseguibile per il routing di riferimento Java/Rust;
- le mappe target dei futuri runtime mobile, chat, diario e presenza.

Le sezioni target non devono essere lette come codice già disponibile. Il
[capitolo sullo stato delle funzionalità](12-stato-funzionalita.md) indica quali
percorsi sono soltanto documentali.

## Obiettivo

Aiutare lo studente a vedere Travel DNA come:

- dati che cambiano forma;
- stati che evolvono;
- responsabilità separate;
- percorsi caldi e percorsi asincroni;
- contratti protetti da test.

## Percorso reale: routing di riferimento

### Vista comune

```mermaid
flowchart LR
    F[reference-network-v0.tdna] --> P1[Java fixture parser]
    F --> P2[Rust fixture parser]
    P1 --> G1[Java RoadGraph]
    P2 --> G2[Rust RoadGraph]
    G1 --> A1[Dijkstra / A*]
    G2 --> A2[Dijkstra / A*]
    A1 --> R1[Java canonical report]
    A2 --> R2[Rust canonical report]
    R1 --> D[diff -u]
    R2 --> D
```

### Percorso Java

```text
ReferenceRoutingCli.main()
-> ReferenceFixtureParser.parse()
-> RoadGraph.addNode()/addBidirectionalRoad()
-> DijkstraRouter oppure AStarRouter
-> AbstractBestFirstRouter.route()
-> reconstructPath()
-> RouteReport.canonicalLine()
```

Responsabilità:

| Componente | Responsabilità | Stato modificato |
| --- | --- | --- |
| `ReferenceFixtureParser` | Validare sintassi, versione, query e ground truth. | Costruisce scenario e grafo. |
| `RoadGraph` | Possedere nodi e liste di adiacenza ordinate. | Mutabile solo durante il caricamento. |
| `AbstractBestFirstRouter` | Eseguire best-first search deterministica. | `frontier`, `bestCost`, `previous`. |
| `DijkstraRouter` | Fornire euristica zero. | Nessuno stato aggiuntivo. |
| `AStarRouter` | Fornire distanza WGS84 floored. | Nessuno stato aggiuntivo. |
| `RouteReport` | Produrre una riga JSON fixture-scoped. | Nessuno. |

### Percorso Rust

```text
main()
-> parse_fixture()
-> RoadGraph::add_node()/add_bidirectional_road()
-> route()
-> heuristic_metres()
-> reconstruct_path()
-> canonical_report()
```

La crate usa:

- `BTreeMap` per stato con ordine stabile;
- `BinaryHeap` con ordinamento invertito per estrarre la priorità minima;
- `checked_add` per intercettare overflow;
- nessuna dipendenza esterna;
- nessun blocco `unsafe`.

### Percorso degli strumenti

```text
sh tools/tdna check
-> check_docs.py
-> javac + Java test suite
-> cargo fmt + cargo test
-> Java/Rust report
-> diff -u
```

### Stato dell'algoritmo

```text
fixture text
-> parsed graph
-> best_cost[origin] = 0
-> frontier seeded
-> node selected
-> outgoing edges relaxed
-> destination reached
-> predecessor chain reversed
-> route result
-> deterministic report
```

Questo percorso non contiene GPS, provider, rete, database o UI. È la prima
mappa di codice verificabile del repository.

## Vista: avvio viaggio

```mermaid
flowchart TD
    A[User taps Start] --> B[StartTripSession use case]
    B --> C[Validate consent and roles]
    B --> D[Create local session]
    D --> E[Start journey recorder]
    D --> F[Start conversation delivery]
    D --> G[Select navigation provider]
    D --> H[Publish allowed road presence]
    G --> I{External or embedded?}
    I -->|External| J[Launch handoff]
    I -->|Embedded| K[Plan route and start runtime]
```

Futuri punti di ingresso da documentare:

```text
StartTripSession.execute()
ConsentPolicy.evaluate()
TripSessionStore.create()
NavigationModeSelector.select()
ExternalNavigationProvider.launch()
NavigationRuntime.start()
```

## Vista: loop navigation

```mermaid
flowchart LR
    GPS[LocationSource] --> VALIDATE[Sample validation]
    VALIDATE --> MATCH[Map match]
    MATCH --> PROGRESS[Route progress]
    PROGRESS --> MANEUVER[Maneuver selection]
    PROGRESS --> OFFROUTE[Off-route state]
    MANEUVER --> SNAPSHOT[NavigationSnapshot]
    OFFROUTE --> REROUTE[Rerouting coordinator]
    SNAPSHOT --> HUD[HUD state]
    SNAPSHOT --> MAP[Map delta]
    SNAPSHOT --> VOICE[Voice prompt scheduler]
```

Stato posseduto dal navigation actor:

```text
active route id
route geometry/index
last accepted sample
matched position
progress
current maneuver
announced prompts
off-route state
reroute request id
confidence
```

## Vista: mappa

```text
MapScene installed once
  base style
  route geometry
  static layers

MapSceneDelta repeated
  puck position
  completed range
  camera
  presence add/update/remove
  selected POI
```

Il renderer possiede handle e layer del provider; il core possiede solo ID e
delta canonici.

## Vista: chat

```mermaid
flowchart TD
    SERVER[Durable server message] --> SIGNAL[Push or live signal]
    SIGNAL --> SYNC[Fetch/sync]
    SYNC --> STORE[Local conversation store]
    STORE --> POLICY[DriveInteractionPolicy]
    POLICY --> VOICE[Voice/car notification]
    POLICY --> FULL[Passenger/full UI]
    VOICE --> REPLY[Voice reply]
    REPLY --> OUTBOX[Outgoing outbox]
    OUTBOX --> SERVER
```

State ownership:

- server: durable accepted order;
- local DB: cached conversation/outbox;
- surface: transient presentation;
- policy: allowed capability;
- navigator: unrelated, no dependency on message body.

## Vista: diario

```mermaid
flowchart TD
    EVENTS[Journey events] --> STORE[Journey event store]
    STORE --> STOPS[Stop/visit projector]
    MEDIA[Media observations] --> ASSOC[Media association]
    STOPS --> TIMELINE[Timeline projector]
    ASSOC --> TIMELINE
    TIMELINE --> PAGE[DailyPage draft]
    PAGE --> EDIT[User edits]
    EDIT --> CONFIRM[Confirmed page]
    CONFIRM --> DNA[Optional DnaCard projection]
```

## Vista: presenza

```text
precise sample local
-> road context
-> privacy approximation
-> ephemeral signal
-> backend TTL store
-> aggregation/matching
-> approximate companion projection
```

La funzione di privacy precede la trasmissione.

## State machine principali

### Trip session

```text
CREATED
-> STARTING
-> ACTIVE
-> PAUSED optional
-> ENDING
-> COMPLETED
-> FAILED_START
```

### Navigation

```text
IDLE
-> ROUTE_READY
-> NAVIGATING
-> SUSPECTED_OFF_ROUTE
-> CONFIRMED_OFF_ROUTE
-> REROUTING
-> NAVIGATING
-> ARRIVED
```

### Shadow route confidence

```text
UNKNOWN -> HIGH -> MEDIUM -> LOW -> UNKNOWN
```

Transizioni possono risalire quando una nuova route torna coerente.

### Message

```text
DRAFT -> QUEUED -> SENDING -> ACCEPTED
                         \-> FAILED_RETRYABLE
                         \-> FAILED_PERMANENT
```

### Presence

```text
DISABLED -> STARTING -> ACTIVE -> EXPIRING -> EXPIRED
                       \-> SUSPENDED
```

### Daily page

```text
NOT_CREATED -> AUTO_DRAFT -> USER_EDITED -> CONFIRMED -> ARCHIVED
```

## Mappa dati

| Dato | Owner | Lettori | Persistenza |
| --- | --- | --- | --- |
| Exact LocationSample | Navigation/Journey local | nav, recorder | breve/local |
| RoutePlan | Navigation | map, guide | cache/local/server optional |
| NavigationSnapshot | Navigation runtime | UI/voice | transient/snapshot |
| JourneyEvent | Journey | Journal projector | local durable |
| DailyPage | Journal | UI/export | user durable |
| PresenceSignal | Presence | backend matching | short TTL |
| Message | Conversation | UI/car | server/local durable |
| DnaCard | Travel DNA | recipients | revocable |
| Place | Guide | map/journal | catalog/cache |

## Regola di aggiornamento

Per ogni nuova vertical slice aggiungere:

- package e file;
- entry point;
- helper principali;
- struct/class mutate;
- thread/dispatcher;
- tracepoint;
- test;
- benchmark;
- link commit.

Evitare call graph globali illeggibili. Generare viste mirate.
