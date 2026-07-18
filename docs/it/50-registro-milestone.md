# Registro milestone di Travel DNA

## Registro

| Ordine | Milestone | Stato | Deliverable principali | Non-obiettivi |
| --- | --- | --- | --- | --- |
| 0 | Documentation Foundation v0 | done | Regole, architettura, ADR, Lab e roadmap. | Codice mobile/backend. |
| 1 | Foundations and Travel DNA Lab v0 | in-progress | Build multi-language, contratti, fake, replay, matching, progress e reroute. | GPS/provider reali. |
| 2 | Canonical Route and Map Slice | planned | Adapter MapLibre seed e benchmark dispositivo. | Turn-by-turn completo. |
| 3 | Navigation Runtime Replay v0 | in-progress | Matching boundary, off-route, missed exit e reroute. | Traffico live. |
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

## Slice completate e mergiate

| Slice | Issue / PR | Merge |
| --- | --- | --- |
| Reference routing | #3 / #4 | `d122f1b4871719087e79a50b185ab302d810cb20` |
| Routing contracts | #5 / #6 | `2a28d1654988cef4188986342f76fd7d19be358f` |
| Review governance | #8 / #9 | `044e0773dd9afb1530db35688a00c56bfbd5eace` |
| MapScene | #7 / #10 | `2128f24b4a6ab00aeb437ed48a0af2910f33e9ec` |
| Serial PR governance | #14 / #15 | `76680433089842db5805d28eb50416a23c7d0a88` |
| LocationSample/replay | #11 / #16 | `020f8495f7fbbae81f1463b098b0ddd2a079c873` |
| Matched position/route progress | #17 / #18 | `9921fbcc1da1000e6434bdae49646122cae8f0e0` |
| Map-matching boundary | #19 / #20 | `95cf2f0d900b039efb86ba0a570e3ca3f8d8cef5` |

## Slice I — missed exit e reroute deterministico

- issue [#21](https://github.com/kinderp/tdna/issues/21);
- PR [#22](https://github.com/kinderp/tdna/pull/22);
- branch `agent/missed-exit-reroute`;
- base `95cf2f0d900b039efb86ba0a570e3ca3f8d8cef5`;
- rischio `R2`;
- stato **finding corretti; nuovo final gate richiesto**.

Deliverable:

- [x] evidenza `OnRoute`, `Suspicious`, `Indeterminate`;
- [x] policy bounded count+duration;
- [x] state machine con falso allarme, hold e conferma sticky;
- [x] rifiuti route/sequence/time non mutanti;
- [x] episode e attempt ID;
- [x] un solo reroute in flight;
- [x] command/outcome correlation e stale rejection;
- [x] old-route retention durante in-flight/failure;
- [x] cancellation cleanup e ordinary-exception mapping;
- [x] capability `routing.plan` verificata prima della chiamata;
- [x] capability manovre verificata sui risultati;
- [x] public `InFlight` state invariants;
- [x] provenance e canonical replacement postconditions;
- [x] nuovo route ID e replacement atomico;
- [x] Lab, benchmark, capitolo 49, scenario e report indicizzato;
- [ ] CI verde sul nuovo final substantive head;
- [ ] clean review round 1;
- [ ] clean review round 2 sullo stesso SHA;
- [ ] expected-head merge e verifica `main`.

## Criteri di chiusura Foundations v0

- [x] repository e CI multi-language;
- [x] reference routing Java/Rust;
- [x] routing contracts e plugin SDK;
- [x] fake route planner;
- [x] MapScene e fake renderer;
- [x] LocationSample, clock e replay;
- [x] matched position/route progress;
- [x] map-matching boundary su `main`;
- [ ] missed-exit scenario su `main`;
- [x] benchmark seed diagnostici;
- [ ] Gradle Wrapper;
- [ ] rapporto finale di chiusura milestone.

## Regole

- una sola PR aperta;
- branch dal `main` verificato;
- niente stacked, placeholder o `noop`;
- CI verde e due round puliti sullo stesso SHA;
- finding o commit sostanziali azzerano il contatore;
- merge autonomo soltanto con expected-head guard;
- verifica di PR, issue e `main` prima della slice successiva.
