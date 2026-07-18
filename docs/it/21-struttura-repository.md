# Struttura del repository

## Obiettivo

Travel DNA usa un monorepo per mantenere vicini contratti, fake, adapter futuri,
fixture, laboratori, documentazione e test. Monorepo non significa dipendenze
libere: ogni modulo possiede un confine dichiarato e verificato.

## Albero concreto corrente

```text
tdna/
├── java/
│   └── reference-routing/
├── crates/
│   └── tdna-reference-routing/
├── shared/
│   ├── plugin-sdk/
│   ├── geo-contracts/
│   ├── routing-contracts/
│   ├── routing-testkit/
│   ├── fake-route-planner/
│   ├── map-contracts/
│   ├── map-testkit/
│   ├── fake-map-renderer/
│   ├── route-map-projector/
│   ├── location-contracts/
│   ├── location-replay/
│   ├── navigation-contracts/
│   ├── map-matching-contracts/
│   ├── map-matching-testkit/
│   ├── fake-map-matcher/
│   ├── route-progress/
│   ├── route-progress-map-projector/
│   ├── off-route-contracts/
│   ├── off-route-state-machine/
│   └── reroute-coordinator/
├── labs/
│   ├── routing-contracts-cli/
│   ├── map-scene-cli/
│   ├── location-replay-cli/
│   ├── route-progress-cli/
│   ├── map-matching-cli/
│   └── missed-exit-cli/
├── fixtures/
│   ├── routes/
│   ├── gps/
│   ├── navigation/
│   ├── journeys/
│   └── conversations/
├── tests/
│   └── contract/
├── tools/
├── docs/
├── .github/
├── settings.gradle.kts
├── build.gradle.kts
├── AGENTS.md
└── README.md
```

Una directory nasce quando possiede un contratto, un comportamento o una prova
reale. Non creiamo moduli vuoti per simulare avanzamento.

## Due linee didattiche complementari

### Reference routing Java/Rust

```text
fixture grafo
-> parser indipendenti
-> Dijkstra/A*
-> report confrontato byte-per-byte
```

Serve a studiare algoritmo, strutture dati e confronto cross-language. I tipi
sono fixture-scoped e non sono le API mobile.

### Kotlin Multiplatform provider-neutral

```text
contratti
-> testkit
-> fake
-> state machine/coordinator
-> Lab
-> benchmark
```

Serve a studiare architettura, sostituibilità, stato bounded, hot path e futura
integrazione Android/iOS.

## Contratti fondamentali

### `shared/plugin-sdk`

Possiede ID, capability, piattaforme, descriptor, notice di licenza e
`TravelDnaPlugin`. Non conosce routing, mappe o SDK mobili.

### `shared/geo-contracts`

Possiede `GeoPoint` WGS84 cross-domain.

### `shared/routing-contracts`

```text
RouteRequest
RoutePlan / RouteLeg / RouteManeuver
RouteProvenance
RoutePlannerPort
RoutePlanningResult / RoutePlanningError
```

Dipende da plugin SDK e geo contracts, non da provider.

### `shared/map-contracts`

Possiede `MapScene`, `MapSceneDelta`, overlay, marker e `MapRendererPort`. Non
contiene MapLibre, UIKit, Compose o codice GPU.

### `shared/location-contracts`

Possiede sequence, tempo monotono, `LocationSample` e ordering gate. Gli adapter
mobili futuri convertono `Location` e `CLLocation` prima di entrare qui.

### `shared/navigation-contracts`

Possiede `RouteCoordinate`, `MatchedRoutePosition`, confidence,
`RouteProgressSnapshot` e decisioni. Non esegue matching né progress.

## Map matching

### `shared/map-matching-contracts`

```text
MapMatcherPort
MapMatchSession
Matched / Unmatched / Failure
MapMatchError / Provenance
```

La route viene legata una volta; il loop usa `session.match(sample)`.

### `shared/map-matching-testkit`

Il probe usa direttamente `RoutePlan`, `LocationSample` e `MapMatcherPort`, perciò
li dichiara come dipendenze API dirette.

### `shared/fake-map-matcher`

Catalogo esatto route/sample → outcome, validazione dei matched, call window
bounded e fixture sintetica. Non è un algoritmo di matching.

## Route progress

### `shared/route-progress`

Tracker legato a una route con ordine sequence/tempo, progresso non regressivo,
active leg, upcoming maneuver, arrival e ultimo snapshot accepted.

### `shared/route-progress-map-projector`

Verifica route e overlay al binding e produce delta compatti per campione.

## Off-route e reroute

### `shared/off-route-contracts`

Possiede:

```text
OffRouteEvidence / Reason
OffRouteObservation
OffRoutePolicy
OffRouteState / Decision / Transition
OffRouteEpisodeId
RerouteAttemptId
RerouteCommand / RerouteOutcome
```

Dipendenze dirette: location e routing contracts. Non conosce matcher concreti o
provider.

### `shared/off-route-state-machine`

Possiede `OffRouteTracker`:

- `inspect` non mutante;
- `accept` con commit soltanto accepted;
- false-alarm recovery;
- indeterminate hold;
- conferma count+durata;
- stato sticky e bounded;
- nessun I/O.

### `shared/reroute-coordinator`

Possiede coordinator ed executor:

```text
Confirmed
-> RerouteCommand
-> RoutePlannerPort
-> correlated outcome
-> failure/stale oppure validated replacement
```

Il coordinator conserva la vecchia route, impedisce attempt paralleli e applica
la nuova route soltanto dopo identità, capability, provenance e canonical
postconditions. L'executor è l'unico punto sospendibile della slice.

## Testkit e fake

Convenzione:

```text
<capability>-contracts
<capability>-testkit
fake-<capability>
```

Il contratto definisce semantica e invarianti. Il testkit verifica implementazioni.
Il fake offre risultati deterministici ai test applicativi senza fingere un
provider reale.

## Laboratori

| Modulo | Percorso dimostrato |
| --- | --- |
| `routing-contracts-cli` | request → fake planner → route |
| `map-scene-cli` | route → scene/delta → fake renderer |
| `location-replay-cli` | fixture → gate → virtual replay |
| `route-progress-cli` | matched position → progress → map delta |
| `map-matching-cli` | sample → fake matcher → progress |
| `missed-exit-cli` | evidence → confirmation → reroute → replacement |

Le CLI JVM possono usare I/O, clock di benchmark e formattazione. I moduli common
restano indipendenti dalla piattaforma.

## Fixture

- `fixtures/routes`: grafo e route ground truth;
- `fixtures/gps`: sample raw/replay sintetici;
- `fixtures/navigation`: progress, matching e missed-exit;
- altre directory: future fixture journal, conversation e presence.

Ogni fixture dichiara ID, fonte, privacy, licenza, scopo, ground truth e
non-obiettivi. Nessuna fixture pubblica deve contenere un viaggio personale.

## Tooling e CI

`tools/tdna` orchestra documentazione, confini architetturali, Java/Rust, KMP,
Lab e benchmark. `.github/workflows/foundation-ci.yml` usa lo stesso entry point.
Gli output Lab/benchmark sono artifact diagnostici, non SLA.

## Albero target futuro

```text
apps/android
apps/ios
plugins/maplibre-android
plugins/maplibre-ios
plugins/valhalla-remote
plugins/ferrostar-android
plugins/ferrostar-ios
plugins/external-navigation-android
plugins/external-navigation-ios
backend/app
backend/modules
schemas/api
schemas/events
schemas/database
```

## Regole di dipendenza

```text
value contracts -> primitive/shared contracts
port -> contracts + plugin SDK
adapter/fake -> port + concrete dependency
application -> ports, non adapter concreti
app -> application + adapter scelti nel composition root
```

Vietato:

```text
routing-contracts -> Valhalla response
map-contracts -> MapLibre class
location-contracts -> android.location.Location
navigation-contracts -> CLLocation
route-progress -> renderer SDK
off-route-state-machine -> RoutePlannerPort
reroute-coordinator -> provider SDK
fake A -> internals of fake B
```

Binding FFI, API client e schemi generati devono vivere in directory riconoscibili,
avere una sorgente e un comando di rigenerazione e non essere modificati a mano.
