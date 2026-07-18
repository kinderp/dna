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
│   └── route-progress-map-projector/
├── labs/
│   ├── routing-contracts-cli/
│   ├── map-scene-cli/
│   ├── location-replay-cli/
│   ├── route-progress-cli/
│   └── map-matching-cli/
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
-> Lab
-> benchmark
```

Serve a studiare architettura, sostituibilità, stato, hot path e integrazione
futura Android/iOS.

## Moduli fondamentali

### `shared/plugin-sdk`

Possiede:

- `PluginId`;
- `CapabilityId`;
- `PlatformId`;
- `PluginDescriptor`;
- notice di licenza;
- interfaccia `TravelDnaPlugin`.

Non conosce routing, mappe, GPS o piattaforme SDK.

### `shared/geo-contracts`

Possiede `GeoPoint` WGS84 cross-domain. Routing, location, mappe, diario e
presence possono dipendere da questo modulo senza dipendere fra loro.

### `shared/routing-contracts`

Possiede:

```text
RouteRequest
RoutePlan
RouteLeg
RouteManeuver
RouteProvenance
RoutePlannerPort
RoutePlanningResult/Error
```

Dipende soltanto da plugin SDK e geo contracts.

### `shared/map-contracts`

Possiede:

```text
MapScene
MapSceneDelta
RouteOverlay
MapMarker
MapRendererPort
MapRenderResult/Error
```

Non contiene MapLibre, UIKit, Compose o codice GPU.

### `shared/location-contracts`

Possiede:

```text
LocationSequence
MonotonicInstant
LocationSample
LocationSampleGate
```

Gli adapter Android/iOS futuri convertiranno `Location` e `CLLocation` prima di
entrare qui.

### `shared/navigation-contracts`

Possiede modelli comuni del runtime:

```text
RouteCoordinate
MatchedRoutePosition
MatchConfidence
RouteProgressSnapshot
RouteProgressDecision
```

Non esegue matching né route progress.

## Moduli di map matching

### `shared/map-matching-contracts`

Possiede il confine provider-neutral:

```text
MapMatcherPort
MapMatchSession
MapMatchResult
MapMatchError
MapMatchUnmatched
MapMatchProvenance
```

Dipendenze dirette:

```text
plugin-sdk
location-contracts
navigation-contracts
routing-contracts
```

La route viene legata una volta. Il loop usa soltanto `session.match(sample)`.

### `shared/map-matching-testkit`

Possiede il conformance probe. La sua firma pubblica usa direttamente:

```text
RoutePlan
LocationSample
MapMatcherPort
```

Per questo dichiara location e routing contracts come dipendenze dirette; non si
affida a dipendenze transitive accidentali.

### `shared/fake-map-matcher`

Possiede:

- catalogo esatto route/sample -> result;
- validazione dei matched;
- call window bounded;
- counter diagnostico;
- fixture sintetica.

È single-threaded e non implementa ricerca stradale.

## Moduli di route progress

### `shared/route-progress`

Possiede un tracker legato a una route:

- ordine sequence/tempo;
- progresso non regressivo;
- active leg;
- upcoming maneuver;
- arrival;
- ultimo snapshot accepted.

Stato e preprocessing crescono con la route, non con la durata del viaggio.

### `shared/route-progress-map-projector`

Verifica route e overlay durante il binding e produce delta `O(1)` per accepted
sample. Non confronta né ricopia geometria nel loop.

## Testkit e fake

La convenzione è:

```text
<capability>-contracts
<capability>-testkit
fake-<capability>
```

Il contratto definisce semantica e invarianti. Il testkit verifica ogni
implementazione. Il fake offre risultati deterministici ai test applicativi.

Un fake non deve diventare una simulazione confusa del provider reale. Deve
rendere esplicito:

```text
dato input X -> risultato Y
```

## Laboratori

| Modulo | Percorso dimostrato |
| --- | --- |
| `routing-contracts-cli` | request -> fake planner -> route |
| `map-scene-cli` | route -> scene/delta -> fake renderer |
| `location-replay-cli` | fixture -> sample gate -> virtual replay |
| `route-progress-cli` | matched position -> progress -> map delta |
| `map-matching-cli` | sample -> fake matcher -> progress |

Le CLI JVM possono usare I/O, clock di benchmark e formattazione. I moduli common
restano indipendenti dalla piattaforma.

## Fixture

- `fixtures/routes`: grafo e route ground truth;
- `fixtures/gps`: sample raw/replay sintetici;
- `fixtures/navigation`: scenari code-defined di progress e matching;
- altre directory: future fixture journal, conversation e presence.

Ogni fixture dichiara:

```text
id
source
privacy
license
purpose
ground truth
non-goals
```

Nessuna fixture pubblica deve contenere un viaggio personale.

## Tooling

`tools/tdna` orchestra:

```text
documentation checks
architecture checks
Java/Rust tests
cross-language contract
KMP tests
Lab
benchmark diagnostici
```

`tools/check_architecture.py` controlla import e token vietati nei moduli common.
È un guardrail sorgente, non un sostituto del grafo Gradle o di una review.

## CI

`.github/workflows/foundation-ci.yml` usa lo stesso entry point del flusso locale:

```bash
sh tools/tdna check
```

Gli output Lab/benchmark vengono conservati come artifact diagnostici, non come
SLA.

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

Le directory vengono create soltanto quando una milestone introduce codice o
contratti reali.

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
map-matching-contracts -> provider road graph
fake A -> internals of fake B
```

## Codice generato

Binding FFI, API client e schema generati devono vivere in directory
riconoscibili, avere una sorgente e un comando di rigenerazione e non essere
modificati manualmente.
