# Guida alla lettura della documentazione

La documentazione ha pubblici diversi. Questo capitolo evita di leggere file a
caso o di confondere visione futura, contratto corrente e lavoro già eseguibile.

## Percorso 1 — capire Travel DNA da zero

1. [Visione del prodotto](01-visione-prodotto.md)
2. [Glossario](02-glossario.md)
3. [Use case principali](11-use-case-principali.md)
4. [DDD e bounded context](10-ddd-bounded-context.md)
5. [Architettura generale](20-architettura-generale.md)
6. [Stato delle funzionalità](12-stato-funzionalita.md)
7. [Matrice tecnologie](52-matrice-tecnologie-decisioni.md)

Al termine devi distinguere diario, guida, socialità, navigazione, contratti,
provider, adapter e piattaforma.

## Percorso 2 — contribuire

1. [Regole operative](00-regole-operative.md)
2. [Review e merge](06-review-e-merge.md)
3. [Come contribuire](04-come-contribuire.md)
4. [Struttura repository](21-struttura-repository.md)
5. [Registro milestone](50-registro-milestone.md)
6. [Stato sviluppo](../project/development-status.md)
7. issue e unica PR aperta
8. capitolo e scenario della slice
9. [Strategia test](30-strategia-test.md)
10. [Stile commenti](../commenting-style.md)

Il flusso è:

```text
capire contratto
-> implementare una slice
-> test e documentazione
-> CI
-> due review pulite
-> merge
-> riallineamento di main
```

## Percorso 3 — capire come nasce un navigatore

1. [Routing Java/Rust](43-reference-routing-java-rust.md)
2. [Scenario reference routing](lab/scenarios/reference-routing-java-rust.md)
3. [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
4. [Scenario provider-neutral](lab/scenarios/routing-contracts-fake-provider.md)
5. [MapScene e fake renderer](45-map-scene-e-fake-renderer.md)
6. [Scenario MapScene](lab/scenarios/map-scene-fake-renderer.md)
7. [LocationSample e replay](46-location-sample-e-replay-deterministico.md)
8. [Scenario location replay](lab/scenarios/location-replay-deterministico.md)
9. [OpenStreetMap e cartografia](23-openstreetmap-e-cartografia.md)
10. [Routing e navigazione](24-routing-e-navigazione.md)
11. [GPS replay e fixture](31-gps-replay-e-fixture.md)
12. [Prestazioni](32-performance-budget.md)
13. [Tracepoint Model](41-tracepoint-model-v0.md)
14. [Scenario missed exit](lab/scenarios/navigation-missed-exit-reroute.md)

Ordine concettuale:

```text
grafo
-> algoritmo
-> contratto provider-neutral
-> route canonica
-> scena e delta
-> campione di posizione
-> filtro e map matching
-> route progress
-> manovra
-> voce
-> off-route e ricalcolo
```

## Percorso 4 — Kotlin e Kotlin Multiplatform

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Plugin e provider](22-architettura-plugin-provider.md)
3. [Contratti routing](44-contratti-routing-e-fake-provider.md)
4. [MapScene](45-map-scene-e-fake-renderer.md)
5. [LocationSample e replay](46-location-sample-e-replay-deterministico.md)
6. aprire `shared/plugin-sdk`;
7. aprire `shared/geo-contracts`;
8. aprire `shared/routing-contracts`;
9. aprire `shared/map-contracts`;
10. aprire `shared/location-contracts` e `shared/location-replay`;
11. confrontare fake, testkit e runner;
12. eseguire architecture check e test KMP.

```bash
sh tools/tdna check-architecture
sh tools/tdna check-kotlin
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
sh tools/tdna lab location-replay
```

## Percorso 5 — LocationSample e tempo monotono

1. [Capitolo 46](46-location-sample-e-replay-deterministico.md)
2. [Scenario eseguibile](lab/scenarios/location-replay-deterministico.md)
3. aprire `GeoPoint.kt`;
4. aprire `LocationModels.kt`;
5. seguire `LocationSampleGate.evaluate()`;
6. aprire `VirtualReplayClock.kt`;
7. seguire `DeterministicReplayRunner`;
8. leggere fixture e metadati;
9. eseguire Lab e benchmark;
10. confrontare accepted, rejected, source delta e playback delay.

```bash
sh tools/tdna lab location-replay
sh tools/tdna bench location-replay 10000 7
```

Domande guida:

- perché non usare l'ora del calendario?
- perché sequence e tempo devono crescere entrambi?
- quale stato cambia dopo un rifiuto?
- perché il primo timestamp produce delay zero?
- che cosa non misura il benchmark?

## Percorso 6 — Android

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Contratti routing](44-contratti-routing-e-fake-provider.md)
3. [MapScene](45-map-scene-e-fake-renderer.md)
4. [LocationSample](46-location-sample-e-replay-deterministico.md)
5. [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
6. [Prestazioni](32-performance-budget.md)
7. [Debugging](34-debugging-e-strumenti.md)

Ordine futuro:

```text
shared contracts
-> Android adapters
-> fake runtime
-> MapLibre MapHost
-> active-trip presentation
-> background lifecycle
-> Android Auto
```

## Percorso 7 — iOS e Swift

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [MapScene](45-map-scene-e-fake-renderer.md)
3. [LocationSample](46-location-sample-e-replay-deterministico.md)
4. [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
5. [Privacy e background](33-privacy-security-driving-safety.md)
6. [Prestazioni](32-performance-budget.md)

Ordine futuro:

```text
shared framework
-> SwiftUI shell
-> Core Location adapter
-> UIKit MapLibre host
-> ActivityKit/CarPlay
```

## Percorso 8 — Java

1. [Stack linguaggi](28-stack-linguaggi-e-gui.md)
2. [Routing Java/Rust](43-reference-routing-java-rust.md)
3. aprire `java/reference-routing`;
4. seguire parser, grafo e Dijkstra/A*;
5. eseguire `sh tools/tdna check-java`;
6. modificare una copia della fixture;
7. confrontare algoritmo e porta applicativa del capitolo 44.

## Percorso 9 — Rust

1. [Stack linguaggi](28-stack-linguaggi-e-gui.md)
2. [Routing Java/Rust](43-reference-routing-java-rust.md)
3. aprire `crates/tdna-reference-routing`;
4. eseguire test e contract diff;
5. leggere replay e benchmark;
6. [Roadmap librerie](51-roadmap-librerie-open-source.md).

```text
CLI e tipi puri
-> test
-> fixture/replay
-> benchmark
-> API stabile
-> FFI
-> binding mobile
```

## Percorso 10 — porte, adapter e provider

1. [DDD](10-ddd-bounded-context.md)
2. [Architettura generale](20-architettura-generale.md)
3. [Plugin e provider](22-architettura-plugin-provider.md)
4. [Routing contracts](44-contratti-routing-e-fake-provider.md)
5. [MapScene](45-map-scene-e-fake-renderer.md)
6. [LocationSample](46-location-sample-e-replay-deterministico.md)
7. confrontare `RoutePlannerPort`, `MapRendererPort` e adapter futuri;
8. aprire `tools/check_architecture.py`.

Domanda guida:

> La decisione appartiene a Travel DNA o soltanto alla libreria scelta oggi?

## Percorso 11 — mappa reattiva

1. [MapScene](45-map-scene-e-fake-renderer.md)
2. [Scenario MapScene](lab/scenarios/map-scene-fake-renderer.md)
3. [Prestazioni](32-performance-budget.md)
4. eseguire `sh tools/tdna lab map-scene`;
5. seguire `RoutePlan -> RouteOverlay -> MapScene -> delta -> snapshot`;
6. collegare in seguito `LocationSample -> progress -> MapSceneDelta`.

## Percorso 12 — diario e privacy

1. [Diario e Pagina del giorno](26-diario-media-pagina-giorno.md)
2. [Backend e local-first](29-backend-dati-sync.md)
3. [Privacy e sicurezza](33-privacy-security-driving-safety.md)
4. [Scenario diario](lab/scenarios/daily-page-photos-thoughts.md)

## Percorso 13 — chat durante la guida

1. [Presenza, chat e DNA](27-presenza-chat-dna.md)
2. [Navigatori esterni](25-navigatori-esterni-e-automotive.md)
3. [Privacy e guida](33-privacy-security-driving-safety.md)
4. [Scenario chat](lab/scenarios/chat-with-external-navigation.md)

## Percorso 14 — riaprire una decisione tecnologica

1. leggere l'ADR;
2. leggere [Matrice tecnologie](52-matrice-tecnologie-decisioni.md);
3. leggere [Riferimenti](53-riferimenti-tecnici.md);
4. definire il problema nuovo;
5. raccogliere prove;
6. aprire una Discussion;
7. aggiornare ADR e documenti dopo la decisione.

## Percorso 15 — riprendere il progetto dopo una pausa

1. `AGENTS.md`;
2. [Regole operative](00-regole-operative.md);
3. [Review e merge](06-review-e-merge.md);
4. [Stato documentazione](documentation-status.md);
5. [Stato sviluppo](../project/development-status.md);
6. [Indice report](../project/daily/README.md);
7. [Registro milestone](50-registro-milestone.md);
8. issue e unica PR aperta;
9. capitolo e scenario correnti.

## Percorso 16 — eseguire tutti i Lab

```bash
sh tools/tdna doctor
sh tools/tdna check
sh tools/tdna lab reference-routing astar
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
sh tools/tdna lab location-replay
```

La CI usa Java 21, Gradle 9.5.1, Kotlin 2.4.0 e Rust stable.

## Percorso 17 — futuro studio LoRa

Leggere [Spike LoRa](54-spike-lora-roadmap.md). È una roadmap di ricerca, non una
decisione. Separare LoRa fisico, LoRaWAN, mesh, regolamentazione, hardware dei
telefoni e valore reale per Travel DNA.
