# Guida alla lettura della documentazione

La documentazione ha pubblici diversi. Questo capitolo evita di leggere file a
caso o di confondere decisioni correnti con visioni future.

## Percorso 1: capire Travel DNA da zero

1. [Visione del prodotto](01-visione-prodotto.md)
2. [Glossario](02-glossario.md)
3. [Use case principali](11-use-case-principali.md)
4. [DDD e bounded context](10-ddd-bounded-context.md)
5. [Architettura generale](20-architettura-generale.md)
6. [Stato delle funzionalità](12-stato-funzionalita.md)
7. [Matrice tecnologie](52-matrice-tecnologie-decisioni.md)

Dopo questo percorso il lettore deve distinguere diario, guida, socialità,
navigazione interna, navigazione esterna, contratti e provider.

## Percorso 2: contribuire al progetto

1. [Regole operative](00-regole-operative.md)
2. [Review e merge](06-review-e-merge.md)
3. [Come contribuire](04-come-contribuire.md)
4. [Struttura del repository](21-struttura-repository.md)
5. [Registro milestone](50-registro-milestone.md)
6. [Stato dello sviluppo](../project/development-status.md)
7. issue e unica PR attiva della slice corrente
8. capitolo del componente interessato
9. [Strategia test](30-strategia-test.md)
10. [Stile dei commenti](../commenting-style.md)

Prima si comprende il contratto; poi si modifica; infine si aggiornano prove,
report e documentazione.

## Percorso 3: imparare come funziona un navigatore

1. [Routing Java/Rust](43-reference-routing-java-rust.md)
2. [Scenario reference routing](lab/scenarios/reference-routing-java-rust.md)
3. [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
4. [Scenario provider-neutral](lab/scenarios/routing-contracts-fake-provider.md)
5. [MapScene e fake renderer](45-map-scene-e-fake-renderer.md)
6. [Scenario MapScene](lab/scenarios/map-scene-fake-renderer.md)
7. [OpenStreetMap e cartografia](23-openstreetmap-e-cartografia.md)
8. [Routing e navigazione](24-routing-e-navigazione.md)
9. [GPS replay e fixture](31-gps-replay-e-fixture.md)
10. [Prestazioni](32-performance-budget.md)
11. [Tracepoint Model](41-tracepoint-model-v0.md)
12. [Scenario missed exit](lab/scenarios/navigation-missed-exit-reroute.md)

Ordine concettuale:

```text
grafo e algoritmo
-> contratto provider-neutral
-> adapter/provider
-> route canonica
-> MapScene e delta
-> renderer reale
-> campioni GPS
-> map matching
-> route progress
-> manovra
-> voce
-> deviazione e ricalcolo
```

## Percorso 4: imparare Kotlin e Kotlin Multiplatform

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Architettura plugin e provider](22-architettura-plugin-provider.md)
3. [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
4. [MapScene e fake renderer](45-map-scene-e-fake-renderer.md)
5. aprire `shared/plugin-sdk/src/commonMain`;
6. aprire `shared/routing-contracts/src/commonMain`;
7. aprire `shared/map-contracts/src/commonMain`;
8. confrontare `FakeRoutePlanner` e `FakeMapRenderer`;
9. eseguire `sh tools/tdna check-architecture`;
10. eseguire `sh tools/tdna check-kotlin`;
11. eseguire i due Lab KMP;
12. leggere i contract probe.

```bash
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
```

Questo percorso insegna modelli, invarianti, dipendenze, scene e fake prima di
Compose, Android lifecycle e iOS integration.

## Percorso 5: imparare Android

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
3. [MapScene e fake renderer](45-map-scene-e-fake-renderer.md)
4. [Architettura generale](20-architettura-generale.md)
5. [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
6. [Prestazioni](32-performance-budget.md)
7. [Debugging e strumenti](34-debugging-e-strumenti.md)

Ordine futuro:

```text
shared contracts
-> Android composition root
-> fake provider/renderer
-> MapLibre MapHost
-> ActiveTrip presentation
-> foreground/background lifecycle
-> Android Auto surface
```

## Percorso 6: imparare iOS e Swift

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
3. [MapScene e fake renderer](45-map-scene-e-fake-renderer.md)
4. [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
5. [Privacy e background](33-privacy-security-driving-safety.md)
6. [Prestazioni](32-performance-budget.md)
7. [Debugging e strumenti](34-debugging-e-strumenti.md)

Ordine futuro:

```text
shared framework
-> SwiftUI shell
-> UIKit MapLibre host
-> Core Location adapter
-> ActivityKit/CarPlay surfaces
```

## Percorso 7: imparare Java

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Routing Java/Rust](43-reference-routing-java-rust.md)
3. aprire `java/reference-routing/src/main/java`;
4. seguire parser, grafo, Dijkstra/A* e report;
5. eseguire `sh tools/tdna check-java`;
6. modificare una copia della fixture;
7. leggere [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
   per capire perché l'algoritmo non diventa automaticamente l'API applicativa.

## Percorso 8: imparare Rust

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Routing Java/Rust](43-reference-routing-java-rust.md)
3. aprire `crates/tdna-reference-routing/src/lib.rs`;
4. eseguire `sh tools/tdna check-rust`;
5. eseguire `sh tools/tdna check-contract`;
6. leggere [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md);
7. [GPS replay](31-gps-replay-e-fixture.md);
8. [Prestazioni](32-performance-budget.md);
9. [Roadmap librerie open source](51-roadmap-librerie-open-source.md).

Ordine didattico:

```text
CLI e tipi puri
-> unit/property test
-> benchmark
-> fixture/replay
-> adapter verso contratti canonici
-> API FFI stabile
-> binding mobile
```

## Percorso 9: capire porte, adapter e provider

1. [DDD e bounded context](10-ddd-bounded-context.md)
2. [Architettura generale](20-architettura-generale.md)
3. [Architettura plugin e provider](22-architettura-plugin-provider.md)
4. [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
5. [MapScene e fake renderer](45-map-scene-e-fake-renderer.md)
6. confrontare `RoutePlannerPort` e `MapRendererPort`;
7. confrontare fake e testkit;
8. aprire `tools/check_architecture.py`.

Domanda guida:

> Questa decisione appartiene al problema Travel DNA o soltanto al provider
> scelto oggi?

## Percorso 10: capire la mappa reattiva

1. [MapScene e fake renderer](45-map-scene-e-fake-renderer.md)
2. [Scenario MapScene](lab/scenarios/map-scene-fake-renderer.md)
3. [Prestazioni](32-performance-budget.md)
4. aprire `shared/map-contracts`;
5. aprire `shared/fake-map-renderer`;
6. eseguire `sh tools/tdna lab map-scene`;
7. seguire `RoutePlan -> RouteMapProjector -> MapScene -> delta -> snapshot`;
8. progettare un futuro adapter MapLibre senza modificare i contratti.

## Percorso 11: diario, fotografie e privacy

1. [Diario e Pagina del giorno](26-diario-media-pagina-giorno.md)
2. [Backend e local-first](29-backend-dati-sync.md)
3. [Privacy e sicurezza](33-privacy-security-driving-safety.md)
4. [Scenario diario](lab/scenarios/daily-page-photos-thoughts.md)

## Percorso 12: chat durante la guida

1. [Presenza, chat e DNA](27-presenza-chat-dna.md)
2. [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
3. [Privacy, sicurezza e guida](33-privacy-security-driving-safety.md)
4. [Scenario chat](lab/scenarios/chat-with-external-navigation.md)

## Percorso 13: riaprire una decisione tecnologica

1. leggere l'ADR corrispondente;
2. leggere [Matrice tecnologie](52-matrice-tecnologie-decisioni.md);
3. leggere [Riferimenti tecnici](53-riferimenti-tecnici.md);
4. identificare il problema nuovo;
5. raccogliere benchmark o vincoli aggiornati;
6. aprire una Discussion;
7. aggiornare ADR e documenti dopo la decisione.

## Percorso 14: riprendere il progetto dopo una pausa

1. `AGENTS.md`;
2. [Regole operative](00-regole-operative.md);
3. [Review e merge](06-review-e-merge.md);
4. [Stato documentazione](documentation-status.md);
5. [Stato sviluppo](../project/development-status.md);
6. [Indice report giornalieri](../project/daily/README.md);
7. [Registro milestone](50-registro-milestone.md);
8. issue e unica PR attiva;
9. capitolo e scenario della slice corrente.

## Percorso 15: eseguire i Lab da zero

### Lab Java/Rust

```bash
sh tools/tdna doctor
sh tools/tdna check-java
sh tools/tdna lab reference-routing dijkstra
```

Con Rust:

```bash
sh tools/tdna check-rust
sh tools/tdna check-contract
```

### Lab Kotlin Multiplatform

```bash
sh tools/tdna check-architecture
sh tools/tdna check-kotlin
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
```

La CI usa Java 21, Gradle 9.5.1, Kotlin 2.4.0 e Rust stable.

## Percorso 16: futuro studio LoRa

Per ora leggere soltanto [Spike LoRa](54-spike-lora-roadmap.md). È una roadmap di
ricerca, non una decisione. Separare LoRa fisico, LoRaWAN, mesh, regolamentazione,
hardware disponibile nei telefoni e reale utilità per Travel DNA.
