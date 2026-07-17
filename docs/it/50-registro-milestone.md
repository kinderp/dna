# Registro milestone di Travel DNA

## Registro

| Ordine | Milestone | Stato | Deliverable principali | Non-obiettivi |
| --- | --- | --- | --- | --- |
| 0 | Documentation Foundation v0 | done | Regole, architettura, ADR, Lab e roadmap. | Codice mobile/backend. |
| 1 | Foundations and Travel DNA Lab v0 | in-progress | Build multi-language, contratti, fake, replay e progress. | GPS/provider reali. |
| 2 | Canonical Route and Map Slice | planned | Adapter MapLibre seed e benchmark dispositivo. | Turn-by-turn completo. |
| 3 | Navigation Runtime Replay v0 | planned | Map matching, off-route e reroute. | Traffico live. |
| 4 | External Navigation Companion v0 | planned | Handoff, shadow route e recorder. | Automotive completo. |
| 5 | Journey Journal v0 | planned | Event store, soste, media e DailyPage. | Cloud media pubblico. |
| 6 | Conversation Core v0 | planned | Outbox, fake server e driver policy. | Discovery live. |
| 7 | Backend Modular Monolith v0 | planned | Auth dev, sync e routing gateway. | Microservizi. |
| 8 | OSM and Valhalla Integration v0 | planned | Adapter/gateway e normalizer. | Scala planetaria. |
| 9 | Automotive Messaging v0 | planned | Android Auto e CarPlay spike. | Split-screen arbitrario. |
| 10 | Road Presence v0 | planned | Coarse presence, TTL e abuse tests. | Tracking preciso. |
| 11 | DNA Cards v0 | planned | Sanitizer, publish e revoke. | Matching opaco. |
| 12 | Internal Navigation Beta | planned | Map, guidance, voice e field audit. | Parità traffico Waze. |
| 13 | Offline Region v0 | future | Package, tile, indice e routing data. | Whole-world offline. |
| 14 | Travel DNA Guidance Core | future | Rust shadow core se misurato. | Renderer riscritto. |
| 15 | LoRa Communication Spike | future/open | Esperimento e ADR. | Produzione senza prove. |

## Milestone 1 — slice completate

| Slice | Issue / PR | Merge |
| --- | --- | --- |
| Reference routing | #3 / #4 | `d122f1b4871719087e79a50b185ab302d810cb20` |
| Routing contracts | #5 / #6 | `2a28d1654988cef4188986342f76fd7d19be358f` |
| Review governance | #8 / #9 | `044e0773dd9afb1530db35688a00c56bfbd5eace` |
| MapScene | #7 / #10 | `2128f24b4a6ab00aeb437ed48a0af2910f33e9ec` |
| Serial PR governance | #14 / #15 | `76680433089842db5805d28eb50416a23c7d0a88` |
| LocationSample/replay | #11 / #16 | `020f8495f7fbbae81f1463b098b0ddd2a079c873` |

## Slice G — matched position e route progress

- issue [#17](https://github.com/kinderp/tdna/issues/17);
- PR [#18](https://github.com/kinderp/tdna/pull/18);
- branch `agent/matched-route-progress`;
- base `020f8495f7fbbae81f1463b098b0ddd2a079c873`;
- stato **substantive work complete; final CI and reviews pending**.

Deliverable:

- [x] `RouteCoordinate` e `MatchedRoutePosition`;
- [x] sequence/tempo/progresso separati;
- [x] stationary accepted e regression rejected;
- [x] active-leg boundary policy;
- [x] binary search leg/manovra;
- [x] tie-break `Arrive`;
- [x] route-overlay binding verificato;
- [x] compact map delta;
- [x] Lab e benchmark multi-leg;
- [x] chapter 47, scenario e code map;
- [x] tracepoint, indici e report;
- [ ] CI verde sul final substantive head;
- [ ] clean review round 1;
- [ ] clean review round 2 sullo stesso SHA;
- [ ] expected-head merge e verifica `main`.

## Criteri di chiusura milestone 1

- [x] repository e CI multi-language;
- [x] reference routing Java/Rust;
- [x] routing contracts e plugin SDK;
- [x] fake route planner;
- [x] MapScene e fake renderer;
- [x] LocationSample, clock e replay su `main`;
- [ ] matched position/route progress su `main`;
- [ ] missed-exit scenario;
- [x] benchmark seed diagnostici;
- [ ] Gradle Wrapper;
- [ ] documentazione finale milestone.

## Regole

- una sola PR aperta;
- branch dal `main` verificato;
- niente stacked, placeholder o `noop`;
- CI verde e due round puliti sullo stesso SHA;
- finding o commit sostanziali azzerano il contatore;
- merge autonomo soltanto con expected-head guard;
- verifica di PR, issue e `main` prima della slice successiva.
