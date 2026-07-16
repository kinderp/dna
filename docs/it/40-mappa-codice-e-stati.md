# Mappa del codice e degli stati

## Stato del documento

La codebase non è ancora stata creata. Questo capitolo definisce la mappa logica
che dovrà essere aggiornata con funzioni, package e call graph reali dopo ogni
vertical slice architetturale.

## Obiettivo

Aiutare lo studente a vedere Travel DNA come:

- dati che cambiano forma;
- stati che evolvono;
- responsabilità separate;
- percorsi caldi e percorsi asincroni;
- contratti protetti da test.

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

## Aggiornamento futuro

Quando esiste codice, aggiungere per ogni scenario:

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
