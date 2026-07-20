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
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       ├── gradle-wrapper.properties
│       └── tdna-wrapper-policy.json
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── build.gradle.kts
├── AGENTS.md
└── README.md
```

Una directory nasce quando possiede un contratto, un comportamento o una prova
reale. Non creiamo moduli vuoti per simulare avanzamento.

## Tre linee didattiche complementari

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

### Bootstrap riproducibile

```text
Java 21
-> gradlew / gradlew.bat
-> Wrapper JAR e properties
-> policy checksum revisionata
-> stessi comandi in locale e CI
```

Il Wrapper è parte del codice di build. La sua presenza non rende il build
completamente ermetico, ma elimina la dipendenza da un'installazione Gradle
globale non dichiarata.

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
mobili futuri convertono `android.location.Location` o `CLLocation` prima di
entrare nel core condiviso.

### `shared/navigation-contracts`

Possiede `RouteCoordinate`, `MatchedRoutePosition`, confidence,
`RouteProgressSnapshot` e decisioni. Non esegue matching né progress.

## Matching, progress e reroute

### Map matching

`shared/map-matching-contracts` definisce porta, sessione route-bound ed esiti
`Matched`, `Unmatched` e `Failure`. Il testkit usa direttamente route e sample; il
fake usa un catalogo esatto e non finge un algoritmo stradale.

### Route progress

`shared/route-progress` controlla sequence, tempo e progresso non regressivo,
seleziona leg/manovra/arrival e conserva soltanto l'ultimo snapshot accepted.
`shared/route-progress-map-projector` produce delta compatti dopo un binding che
verifica route e geometria.

### Off-route e reroute

```text
OffRouteObservation
-> OffRouteTracker
-> Confirmed
-> RerouteCommand
-> RoutePlannerPort
-> correlated outcome
-> validated replacement
```

`shared/off-route-state-machine` non esegue I/O. `shared/reroute-coordinator`
isola il punto sospendibile, conserva la vecchia route e applica una sostituzione
soltanto dopo identità, capability, provenance e postcondizioni canoniche.

## Laboratori

| Modulo | Percorso dimostrato |
| --- | --- |
| `routing-contracts-cli` | request → fake planner → route |
| `map-scene-cli` | route → scene/delta → fake renderer |
| `location-replay-cli` | fixture → gate → virtual replay |
| `route-progress-cli` | matched position → progress → map delta |
| `map-matching-cli` | sample → fake matcher → progress |
| `missed-exit-cli` | evidence → confirmation → reroute → replacement |
| `tools/tdna lab build-bootstrap` | Wrapper policy → canonical build report |

Le CLI JVM possono usare I/O, clock di benchmark e formattazione. I moduli common
restano indipendenti dalla piattaforma.

## Fixture

- `fixtures/routes`: grafo e route ground truth;
- `fixtures/gps`: sample raw/replay sintetici;
- `fixtures/navigation`: progress, matching e missed-exit;
- altre directory: future fixture journal, conversation e presence.

Ogni fixture dichiara ID, fonte, privacy, licenza, scopo, ground truth e
non-obiettivi. Nessuna fixture pubblica deve contenere un viaggio personale.

## Tooling, Wrapper e CI

`tools/tdna` orchestra documentazione, confini architetturali, Wrapper, Java/Rust,
KMP, Lab e benchmark. `.github/workflows/foundation-ci.yml` usa lo stesso entry
point.

Comandi di bootstrap:

```bash
sh tools/tdna check-gradle-wrapper
sh tools/tdna check-ci-actions
sh tools/tdna lab build-bootstrap
./gradlew --version
```

La policy verifica versione, URL, checksum della distribuzione, digest del JAR e
dei launcher. Le GitHub Actions usate dal workflow sono allowlisted e bloccate a
SHA completi. Questi controlli rilevano drift; non costituiscono da soli una
firma dell'editore o una root of trust esterna.

Gli output Lab/benchmark sono artifact diagnostici, non SLA.

## Albero target successivo

La prima piattaforma scelta è Android.

```text
apps/android
plugins/location-android
plugins/external-navigation-android
plugins/maplibre-android
plugins/valhalla-remote
backend/app
backend/modules
schemas/api
schemas/events
schemas/database
```

`apps/ios` e gli adapter iOS restano nel disegno target, ma non vengono sviluppati
in parallelo alla prima shell Android.

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
