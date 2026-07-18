# Registro milestone di Travel DNA

## Registro

| Ordine | Milestone | Stato | Deliverable principali | Non-obiettivi |
| --- | --- | --- | --- | --- |
| 0 | Documentation Foundation v0 | done | Regole, architettura, ADR, Lab e roadmap. | Codice mobile/backend. |
| 1 | Foundations and Travel DNA Lab v0 | done on merge of PR #24 | Build multi-language, Wrapper, contratti, fake, replay, matching, progress e reroute. | GPS/provider reali. |
| 2 | Canonical Route and Map Slice | planned | Adapter MapLibre seed e benchmark dispositivo. | Turn-by-turn completo. |
| 3 | Navigation Runtime Replay v0 | partial/done foundation scope | Matching boundary, off-route, missed exit e reroute deterministici. | Traffico live e soglie reali. |
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
| Missed exit/reroute | #21 / #22 | `8570eb466b43384756f2678da2303929295e087a` |

## Slice J — Gradle Wrapper e chiusura Foundations v0

- issue [#23](https://github.com/kinderp/tdna/issues/23);
- PR [#24](https://github.com/kinderp/tdna/pull/24);
- branch `agent/gradle-wrapper-foundation-closure`;
- base `8570eb466b43384756f2678da2303929295e087a`;
- rischio `R2`;
- stato **substantive work/documentation in progress; final gate richiesto**.

Deliverable:

- [x] `gradlew`, `gradlew.bat`, JAR e properties Gradle 9.5.1;
- [x] checksum distribuzione configurato;
- [x] policy revisionata per JAR, launcher e properties;
- [x] checker Wrapper fail-closed;
- [x] GitHub Actions allowlisted e pin a SHA completo;
- [x] workflow finale `contents: read`;
- [x] build Kotlin tramite `./gradlew`, Gradle globale non richiesto;
- [x] build-bootstrap Lab;
- [x] capitolo 37 e scenario;
- [x] rapporto di chiusura Foundations v0;
- [x] report giornaliero e indici;
- [ ] CI verde sul final substantive head;
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
- [x] missed-exit scenario su `main`;
- [x] benchmark seed diagnostici;
- [x] Gradle Wrapper committato e verificato nella PR #24;
- [x] rapporto finale di chiusura milestone;
- [ ] PR #24: CI e due review pulite sul final head;
- [ ] PR #24: merge e verifica post-merge.

Il rapporto consolidato è
[`docs/project/foundation-v0-closure.md`](../project/foundation-v0-closure.md).

## Regole

- una sola PR aperta;
- branch dal `main` verificato;
- niente stacked, placeholder o `noop`;
- CI verde e due round puliti sullo stesso SHA;
- finding o commit sostanziali azzerano il contatore;
- merge autonomo soltanto con expected-head guard;
- verifica di PR, issue e `main` prima della slice successiva.
