# Guida alla lettura della documentazione

La documentazione ha pubblici diversi. Questo capitolo evita di confondere
visione futura, contratti correnti, Lab eseguibili e capacità di prodotto.

## Percorso 1 — capire Travel DNA

1. [Visione del prodotto](01-visione-prodotto.md)
2. [Glossario](02-glossario.md)
3. [Use case](11-use-case-principali.md)
4. [DDD e bounded context](10-ddd-bounded-context.md)
5. [Architettura generale](20-architettura-generale.md)
6. [Stato funzionalità](12-stato-funzionalita.md)
7. [Matrice tecnologie](52-matrice-tecnologie-decisioni.md)

Al termine devi distinguere diario, guida, navigazione, socialità, contratto,
provider, adapter e piattaforma.

## Percorso 2 — contribuire

1. [Regole operative](00-regole-operative.md)
2. [Review e merge](06-review-e-merge.md)
3. [Come contribuire](04-come-contribuire.md)
4. [Struttura repository](21-struttura-repository.md)
5. [Registro milestone](50-registro-milestone.md)
6. [Stato sviluppo](../project/development-status.md)
7. issue e unica PR aperta;
8. capitolo e scenario della slice;
9. [Strategia test](30-strategia-test.md)
10. [Stile commenti](../commenting-style.md)

```text
capire il contratto
-> implementare una vertical slice
-> test, benchmark e documentazione
-> CI sul substantive head
-> review pulita 1
-> review pulita 2 sullo stesso SHA
-> merge con expected-head
-> riallineamento di main
```

## Percorso 3 — come nasce un navigatore

1. [Routing Java/Rust](43-reference-routing-java-rust.md)
2. [Scenario reference routing](lab/scenarios/reference-routing-java-rust.md)
3. [Contratti routing](44-contratti-routing-e-fake-provider.md)
4. [Scenario fake planner](lab/scenarios/routing-contracts-fake-provider.md)
5. [MapScene](45-map-scene-e-fake-renderer.md)
6. [Scenario MapScene](lab/scenarios/map-scene-fake-renderer.md)
7. [LocationSample e replay](46-location-sample-e-replay-deterministico.md)
8. [Scenario replay](lab/scenarios/location-replay-deterministico.md)
9. [Posizione matched e route progress](47-posizione-matched-e-route-progress.md)
10. [Scenario route progress](lab/scenarios/route-progress-tracker.md)
11. [Porta map matching e fake](48-porta-map-matching-e-fake-deterministico.md)
12. [Scenario map matching](lab/scenarios/map-matching-fake-provider.md)
13. [Off-route, missed exit e reroute](49-off-route-missed-exit-e-reroute.md)
14. [Scenario missed exit](lab/scenarios/navigation-missed-exit-reroute.md)
15. [OpenStreetMap e cartografia](23-openstreetmap-e-cartografia.md)
16. [Routing e navigazione](24-routing-e-navigazione.md)
17. [Prestazioni](32-performance-budget.md)
18. [Tracepoint Model](41-tracepoint-model-v0.md)

Ordine concettuale:

```text
grafo
-> algoritmo
-> contratto routing provider-neutral
-> RoutePlan
-> MapScene e renderer
-> LocationSample
-> filtro futuro
-> MapMatcherPort / MapMatchSession
-> Matched | Unmatched | Failure
-> MatchedRoutePosition
-> RouteProgressSnapshot
-> evidenza off-route
-> conferma count + durata
-> reroute correlato
-> route replacement
-> delta mappa / HUD / voce
```

## Percorso 4 — Kotlin Multiplatform

1. [Stack linguaggi](28-stack-linguaggi-e-gui.md)
2. [Plugin e provider](22-architettura-plugin-provider.md)
3. capitoli 44–49;
4. aprire nell'ordine:

```text
shared/plugin-sdk
shared/geo-contracts
shared/routing-contracts
shared/map-contracts
shared/location-contracts
shared/location-replay
shared/navigation-contracts
shared/map-matching-contracts
shared/map-matching-testkit
shared/fake-map-matcher
shared/route-progress
shared/route-progress-map-projector
shared/off-route-contracts
shared/off-route-state-machine
shared/reroute-coordinator
```

5. confrontare contratti, testkit, fake, runner, tracker e coordinator;
6. eseguire:

```bash
sh tools/tdna check-architecture
sh tools/tdna check-kotlin
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
sh tools/tdna lab location-replay
sh tools/tdna lab route-progress
sh tools/tdna lab map-matching
sh tools/tdna lab missed-exit
```

## Percorso 5 — posizione e tempo monotono

1. [Capitolo 46](46-location-sample-e-replay-deterministico.md)
2. [Scenario replay](lab/scenarios/location-replay-deterministico.md)
3. `GeoPoint.kt`;
4. `LocationModels.kt`;
5. `LocationSampleGate`;
6. `VirtualReplayClock`;
7. `DeterministicReplayRunner`;
8. fixture, Lab e benchmark.

Domande guida:

- perché non usare l'ora del calendario nel loop?
- perché sequence e tempo devono crescere entrambi?
- quale stato cambia dopo un rifiuto?
- perché il primo timestamp produce delay zero?

## Percorso 6 — matched position e route progress

1. [Capitolo 47](47-posizione-matched-e-route-progress.md)
2. [Scenario route progress](lab/scenarios/route-progress-tracker.md)
3. `NavigationProgressModels.kt`;
4. `RouteProgressTracker.kt`;
5. `RouteProgressMapProjector.kt`;
6. test del confine fra leg e del tie-break `Arrive`;
7. Lab e benchmark.

```bash
sh tools/tdna lab route-progress
sh tools/tdna bench route-progress 10000 7
```

## Percorso 7 — porta di map matching

1. [Capitolo 48](48-porta-map-matching-e-fake-deterministico.md)
2. [Scenario map matching](lab/scenarios/map-matching-fake-provider.md)
3. `MapMatchingContracts.kt`;
4. `MapMatcherContractProbe.kt`;
5. `FakeMapMatcher.kt`;
6. `FakeMapMatchFixtures.kt`;
7. CLI e benchmark.

```bash
sh tools/tdna lab map-matching
sh tools/tdna bench map-matching 10000 7
```

Domande guida:

- perché `bind(route)` precede `match(sample)`?
- perché unmatched, failure e cancellation sono distinti?
- quali identità protegge `requireMatches`?
- perché il determinismo usa una sessione fresca?

## Percorso 8 — off-route e reroute

1. [Capitolo 49](49-off-route-missed-exit-e-reroute.md)
2. [Scenario missed exit](lab/scenarios/navigation-missed-exit-reroute.md)
3. `OffRouteContracts.kt`;
4. `OffRouteTracker.kt`;
5. `RerouteCoordinator.kt`;
6. test di recovery, indeterminate, stale outcome e cancellation;
7. Lab e benchmark.

```bash
sh tools/tdna lab missed-exit
sh tools/tdna bench off-route 10000 7
```

Domande guida:

- perché evidenza e conferma sono moduli diversi?
- perché count e durata devono essere entrambi soddisfatti?
- che cosa fa `Indeterminate`?
- perché la vecchia route resta attiva durante il reroute?
- quali identità rendono un outcome applicabile?
- perché capability e provenance sono postcondizioni del provider?
- quali componenti devono essere ricreati dopo il replacement?

## Percorso 9 — mappe reattive

1. [Capitolo 45](45-map-scene-e-fake-renderer.md)
2. [Scenario MapScene](lab/scenarios/map-scene-fake-renderer.md)
3. [Capitolo 47](47-posizione-matched-e-route-progress.md)
4. seguire:

```text
RoutePlan
-> RouteOverlay
-> MapScene installata
-> RouteProgressMapBinding
-> RouteProgressSnapshot
-> MapSceneDelta.UpdateRouteProgress
-> FakeMapRenderer snapshot
```

La geometria si installa raramente; il progresso usa delta compatti.

## Percorso 10 — Android

1. [Stack](28-stack-linguaggi-e-gui.md)
2. capitoli 44–49;
3. [Navigatori esterni e auto](25-navigatori-esterni-e-automotive.md)
4. [Privacy e sicurezza](33-privacy-security-driving-safety.md)
5. [Debugging](34-debugging-e-strumenti.md)

Ordine futuro:

```text
shared contracts
-> Android location/map adapters
-> fake navigation runtime
-> MapLibre MapHost
-> active-trip presentation
-> foreground/background lifecycle
-> Android Auto
```

## Percorso 11 — iOS e Swift

1. [Stack](28-stack-linguaggi-e-gui.md)
2. capitoli 45–49;
3. [Navigatori esterni](25-navigatori-esterni-e-automotive.md)
4. [Privacy](33-privacy-security-driving-safety.md)

```text
shared framework
-> Core Location adapter
-> SwiftUI shell
-> UIKit MapLibre host
-> ActivityKit / CarPlay
```

## Percorso 12 — Java e Rust

### Java

1. [Capitolo 43](43-reference-routing-java-rust.md)
2. aprire `java/reference-routing`;
3. eseguire `sh tools/tdna check-java`;
4. confrontare algoritmo e porte applicative dei capitoli 44, 48 e 49.

### Rust

1. [Capitolo 43](43-reference-routing-java-rust.md)
2. aprire `crates/tdna-reference-routing`;
3. eseguire test e contract diff;
4. leggere replay, benchmark e [roadmap librerie](51-roadmap-librerie-open-source.md).

```text
tipi puri -> test -> fixture/replay -> benchmark -> API stabile -> FFI -> mobile
```

## Percorso 13 — diario, chat e privacy

### Diario

1. [Diario e Pagina del giorno](26-diario-media-pagina-giorno.md)
2. [Backend local-first](29-backend-dati-sync.md)
3. [Privacy](33-privacy-security-driving-safety.md)
4. [Scenario diario](lab/scenarios/daily-page-photos-thoughts.md)

### Chat in viaggio

1. [Presenza, chat e DNA](27-presenza-chat-dna.md)
2. [Navigatori esterni](25-navigatori-esterni-e-automotive.md)
3. [Privacy e guida](33-privacy-security-driving-safety.md)
4. [Scenario chat](lab/scenarios/chat-with-external-navigation.md)

## Percorso 14 — riprendere dopo una pausa

1. `AGENTS.md`;
2. [Regole operative](00-regole-operative.md);
3. [Review e merge](06-review-e-merge.md);
4. [Stato documentazione](documentation-status.md);
5. [Stato sviluppo](../project/development-status.md);
6. [Indice report](../project/daily/README.md);
7. [Registro milestone](50-registro-milestone.md);
8. issue, unica PR, capitolo e scenario correnti.

## Eseguire tutti i Lab

```bash
sh tools/tdna doctor
sh tools/tdna check
sh tools/tdna lab reference-routing astar
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
sh tools/tdna lab location-replay
sh tools/tdna lab route-progress
sh tools/tdna lab map-matching
sh tools/tdna lab missed-exit
```

La CI usa Java 21, Gradle 9.5.1, Kotlin 2.4.0 e Rust stable.

## Futuro studio LoRa

[Spike LoRa](54-spike-lora-roadmap.md) è una roadmap di ricerca, non una
decisione. Separare LoRa fisico, LoRaWAN, mesh, regolamentazione, hardware dei
telefoni e valore reale per Travel DNA.
