# Registro milestone di Travel DNA

## Ruolo

GitHub conserva lo stato vivo; questo documento conserva ordine, motivazione,
dipendenze, risultati e lavoro rimandato.

## Registro

| Ordine | Milestone | Stato | Perché ora | Deliverable principali | Non-obiettivi |
| --- | --- | --- | --- | --- | --- |
| 0 | Documentation Foundation v0 | done | Fonte stabile prima del codice. | Regole, architettura, ADR, Lab e roadmap. | Codice mobile/backend. |
| 1 | Foundations and Travel DNA Lab v0 | in-progress | Contratti e scenari prima degli SDK reali. | Multi-language build, canonical models, fakes, replay seed e CI. | GPS reale, MapLibre, Valhalla, chat. |
| 2 | Canonical Route and Map Slice | planned | Verificare rendering e isolamento provider. | MapScene, fake renderer e adapter seed. | Turn-by-turn completo. |
| 3 | Navigation Runtime Replay v0 | planned | Ridurre il rischio guidance. | Clock, replay, off-route e reroute. | Traffico live. |
| 4 | External Navigation Companion v0 | planned | Valore con navigatori maturi. | Handoff, shadow route e recorder. | Automotive completo. |
| 5 | Journey Journal v0 | planned | Valore autonomo e dati per DNA. | Event store, soste, media fake e DailyPage. | Cloud media pubblico. |
| 6 | Conversation Core v0 | planned | Messaggistica durable e driver policy. | Outbox, fake server e voice surface. | Discovery live. |
| 7 | Backend Modular Monolith v0 | planned | Supportare sync/chat. | Auth dev, sync e routing gateway. | Microservizi. |
| 8 | OSM and Valhalla Integration v0 | planned | Route e POI reali dietro contratti. | Adapter/gateway e normalizer. | Scala planetaria. |
| 9 | Automotive Messaging v0 | planned | Chat sicura con navigatore esterno. | Android Auto e CarPlay spike. | Split-screen arbitrario. |
| 10 | Road Presence and Greetings v0 | planned | Prima funzione social live. | Coarse presence, TTL e abuse tests. | Tracking preciso. |
| 11 | DNA Cards v0 | planned | Condivisione derivata dal diario. | Sanitizer, publish e revoke. | Matching opaco. |
| 12 | Internal Navigation Beta | planned | Dopo prove di affidabilità. | Map, guidance, voice e field audit. | Parità traffico Waze. |
| 13 | Offline Region v0 | future | Viaggio senza rete. | Package, tile, indice e routing data. | Whole-world offline. |
| 14 | Travel DNA Guidance Core | future | Sostituire solo con valore misurato. | Rust shadow core. | Renderer riscritto. |
| 15 | LoRa Communication Spike | future/open | Valutare casi senza copertura. | Esperimento e ADR. | Produzione senza prove. |

## Milestone 1 — Foundations and Travel DNA Lab v0

Avvio: **2026-07-16**

### Slice A — reference routing

- issue [#3](https://github.com/kinderp/tdna/issues/3);
- PR [#4](https://github.com/kinderp/tdna/pull/4);
- stato **merged**;
- merge commit `d122f1b4871719087e79a50b185ab302d810cb20`.

Completati:

- [x] fixture sintetica;
- [x] Java/Rust parser;
- [x] Dijkstra e A*;
- [x] report deterministico;
- [x] contract diff;
- [x] test, CI, capitolo 43 e Lab.

### Slice B — provider-neutral routing contracts

- issue [#5](https://github.com/kinderp/tdna/issues/5);
- PR draft [#6](https://github.com/kinderp/tdna/pull/6);
- branch `agent/provider-neutral-routing-contracts`;
- stato **review complete — merge pending**.

Completati:

- [x] Gradle/KMP bootstrap;
- [x] JVM e Linux x64;
- [x] plugin SDK e runtime platform semantics;
- [x] canonical routing models;
- [x] immutable snapshots e bounded metadata;
- [x] request/waypoint postconditions;
- [x] `RoutePlannerPort`;
- [x] fake planner e call recording;
- [x] reusable conformance probe;
- [x] architecture checker;
- [x] CLI Lab;
- [x] capitolo 44 e scenario;
- [x] indice report giornalieri;
- [x] CI completa verde, run `#19`;
- [x] tre review round, ultimi senza finding bloccanti;
- [ ] merge.

### Criteri di chiusura milestone

- [x] repository e CI multi-language;
- [x] Java/Rust reference routing;
- [x] canonical routing contract seed;
- [x] plugin descriptor e capability;
- [x] fake route planner;
- [ ] `LocationSample` e clock;
- [ ] GPS replay;
- [ ] MapScene e fake renderer;
- [ ] missed-exit scenario;
- [ ] benchmark report;
- [ ] Gradle Wrapper;
- [ ] documentazione finale milestone.

## Regole

- ogni slice non banale ha issue e PR;
- la chiusura registra prove e lavoro rimandato;
- milestone future non autorizzano codice anticipato;
- deliverable significa evidenza, non percentuale vaga.
