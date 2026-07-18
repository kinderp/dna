# Mappa del codice e degli stati

## Scopo

Questa mappa mostra percorsi reali ed eseguibili separandoli dalle viste target.
Ogni sezione dichiara ownership, stato mutato e confini.

## 1. Reference routing Java/Rust

```text
reference-network-v0.tdna
-> parser Java / parser Rust
-> RoadGraph
-> Dijkstra o A*
-> predecessor chain
-> route result
-> report byte-identico
```

Stato temporaneo: frontier, best cost e previous. Nessun GPS, provider, rete o UI
entra nel Lab.

## 2. Routing provider-neutral

```text
RouteRequest
-> RoutePlannerPort
-> FakeRoutePlanner
-> RoutePlanningResult
-> RoutePlan
-> RoutePlannerContractProbe
```

| Componente | Possiede |
| --- | --- |
| `RouteRequest` | origine, destinazione, tappe e profilo |
| `RoutePlan` | geometria, leg, manovre e provenance |
| provider | traduzione e comportamento concreto |
| application layer | scelta del provider e fallback |

Il dominio non importa modelli Valhalla, Google, Sygic o altri vendor.

## 3. MapScene e renderer

```text
RoutePlan
-> RouteMapProjector
-> MapScene
-> FakeMapRenderer.install
-> MapSceneDelta
-> FakeMapRenderer.apply
-> snapshot semantico
```

```text
MapScene installata raramente:
  camera, route, marker, selezione

MapSceneDelta frequente:
  camera, progress, marker changes, selection
```

Il renderer possiede gli handle concreti; i contratti condivisi possiedono ID e
modelli dichiarativi.

## 4. LocationSample e replay

```text
TDNA_LOCATION_REPLAY_V0
-> ReplayFixtureParser JVM
-> LocationSample list
-> DeterministicReplayRunner
   -> LocationSampleGate.inspect
   -> VirtualReplayClock.preview
   -> ReplayDelayScaler.preview
   -> commit atomico
-> ReplaySummary
-> report JSON
```

Stato bounded del runner:

```text
next index
accepted/rejected counters
last accepted sample
virtual clock
rate remainder
rejection counts
replay state
```

Il runner non conserva la cronologia degli eventi e non legge il wall clock.

## 5. Porta map matching e fake

### Percorso reale

```mermaid
flowchart LR
    S[LocationSample] --> M[MapMatchSession.match]
    M --> R{MapMatchResult}
    R -->|Matched| V[requireMatches]
    R -->|Unmatched| U[normal domain outcome]
    R -->|Failure| F[provider failure]
    V --> P[RouteProgressTracker.accept]
```

### Function path del Lab

```text
Main.runLab
-> MapMatcherContractProbe.verify
-> FakeMapMatcher.bind(RoutePlan)
-> Session.match(LocationSample)
   -> Key(routeId, sample) lookup
   -> bounded ArrayDeque record
-> Matched.requireMatches
-> RouteProgressTracker.accept
-> report JSON
```

### Ownership

| Componente | Stato posseduto | Frequenza |
| --- | --- | --- |
| `RoutePlan` | geometria/leg/manovre immutabili | sessione |
| `MapMatcherPort` | descriptor e factory | composizione |
| `MapMatchSession` | route legata e futuro stato matcher | sessione |
| `FakeMapMatcher` | catalogo, counter e call window | vita fake |
| `MapMatchResult` | un esito immutabile | campione |
| testkit | nessuno oltre al report locale | test |

### Esiti

```text
Matched
  -> route/sample/provider postcondition
  -> downstream progress

Unmatched
  -> nessun match affidabile
  -> non muta progress
  -> non equivale automaticamente a off-route

Failure
  -> provider non ha completato l'operazione
  -> resta distinto da Unmatched
```

### Determinismo

```text
session A nuova + route R + sample S -> result X
session B nuova + route R + sample S -> result X
```

Il probe non ripete lo stesso sample nella stessa sessione stateful.

### Stato bounded e costo

```text
catalog <= 100.000 entry
call window <= 10.000
exact lookup medio O(1)
ArrayDeque eviction ammortizzata O(1)
nessuna route copiata per sample
```

Il fake non esegue ricerca geometrica e non misura accuratezza.

## 6. Matched position e route progress

### Percorso reale

```mermaid
flowchart LR
    MP[MatchedRoutePosition] --> T[RouteProgressTracker.accept]
    T --> S[RouteProgressSnapshot]
    S --> P[RouteProgressMapProjector.project]
    P --> D[MapSceneDelta.UpdateRouteProgress]
    D --> R[Renderer]
```

### Function path del Lab

```text
Main.runLab
-> referenceRoute
-> MatchedRoutePosition candidates
-> RouteProgressTracker.accept
   -> rejection precedence
   -> findActiveLegIndex (binary search)
   -> findUpcomingManeuver (binary search)
   -> RouteProgressSnapshot
-> RouteProgressMapProjector.bind
-> RouteProgressMapProjector.project
-> report JSON
```

### Ownership

| Componente | Stato posseduto | Frequenza |
| --- | --- | --- |
| `RoutePlan` | geometria, leg e manovre immutabili | installazione |
| `MatchedRoutePosition` | una ipotesi del matcher | campione |
| `RouteProgressTracker` | cursori precomputati e ultimo snapshot | sessione |
| `RouteProgressMapBinding` | scene/overlay/route ID e point count | installazione |
| projector update | nessuno | accepted sample |
| renderer | geometria installata e progresso | scena |

Transizioni:

```text
nessun snapshot
-> prima posizione valida accepted
-> stationary accepted
-> advance accepted
-> leg boundary accepted
-> regression rejected, stato invariato
-> arrival accepted
```

Rifiuti:

```text
RouteMismatch
GeometryIndexOutOfBounds
FinalPointHasFraction
NonIncreasingSequence
NonIncreasingMonotonicTime
RegressedAlongRoute
```

La geometria route-overlay viene verificata una volta nel binding. Il delta per
campione è compatto e `O(1)`.

## 7. Navigation runtime target

```text
LocationSource
-> sample validation/filter
-> MapMatcherPort session
-> matched/unmatched/failure policy
-> route progress
-> confidence/off-route policy
-> maneuver and prompt state
-> NavigationSnapshot
-> HUD / map delta / voice
```

Stato futuro single-owner:

```text
active route
last accepted LocationSample
map-match session
matched position
progress snapshot
current/announced maneuver
off-route evidence
reroute request/version
confidence
```

## 8. Chat durante la guida target

```text
server message
-> push/live signal
-> local sync/store
-> DriveInteractionPolicy
-> voice/car notification oppure full passenger UI
-> outbox
```

Server e local DB possiedono durata e ordine; la superficie possiede soltanto
stato di presentazione.

## 9. Diario target

```text
JourneyEvent
-> event store
-> stop/visit projection
-> media association
-> DailyPage draft
-> user edits
-> optional DNA card sanitization
```

La condivisione deriva da una proiezione minimizzata, non dal diario privato
completo.

## 10. Presenza target

```text
exact local sample
-> road context
-> privacy approximation
-> ephemeral TTL signal
-> aggregate companion projection
```

La minimizzazione precede la rete.

## 11. Mappa dati

| Dato | Owner | Persistenza |
| --- | --- | --- |
| `LocationSample` esatto | navigation/journey locale | breve/local |
| `MapMatchSession` | matcher/runtime | sessione |
| `MapMatchResult` | matcher/caller | transiente |
| `MatchedRoutePosition` | matcher/runtime | transiente |
| `RouteProgressSnapshot` | progress tracker | ultimo snapshot |
| `RoutePlan` | navigation | cache/sessione |
| `MapScene` | presentation/renderer | scena |
| `MapSceneDelta` | presentation | transiente |
| `JourneyEvent` | journey | locale durable |
| `DailyPage` | journal | user durable |
| `PresenceSignal` | presence/backend | TTL breve |
| `Message` | conversation | server/local durable |

## Regola di aggiornamento

Ogni vertical slice aggiunge:

- package e file;
- entry point e function path;
- stato mutato e owner;
- thread/dispatcher;
- tracepoint;
- test;
- benchmark e limiti;
- issue, PR e report.

Evitare call graph globali illeggibili: generare viste mirate al comportamento.
