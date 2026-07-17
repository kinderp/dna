# Registro milestone di Travel DNA

## Ruolo

GitHub conserva lo stato vivo; questo documento conserva ordine, motivazione,
dipendenze, risultati e lavoro rimandato.

## Registro

| Ordine | Milestone | Stato | Perché ora | Deliverable principali | Non-obiettivi |
| --- | --- | --- | --- | --- | --- |
| 0 | Documentation Foundation v0 | done | Fonte stabile prima del codice. | Regole, architettura, ADR, Lab e roadmap. | Codice mobile/backend. |
| 1 | Foundations and Travel DNA Lab v0 | in-progress | Contratti e scenari prima degli SDK reali. | Build multi-language, canonical models, fakes, replay e CI. | GPS reale, provider reali, chat. |
| 2 | Canonical Route and Map Slice | planned | Verificare rendering e isolamento provider. | Adapter MapLibre seed e benchmark dispositivo. | Turn-by-turn completo. |
| 3 | Navigation Runtime Replay v0 | planned | Ridurre il rischio guidance. | Matched position, progress, off-route e reroute. | Traffico live. |
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

### Slice completate

| Slice | Issue / PR | Stato | Merge |
| --- | --- | --- | --- |
| Reference routing | #3 / #4 | merged | `d122f1b4871719087e79a50b185ab302d810cb20` |
| Routing contracts | #5 / #6 | merged | `2a28d1654988cef4188986342f76fd7d19be358f` |
| Review governance | #8 / #9 | merged | `044e0773dd9afb1530db35688a00c56bfbd5eace` |
| MapScene/fake renderer | #7 / #10 | merged | `2128f24b4a6ab00aeb437ed48a0af2910f33e9ec` |
| Serial PR governance | #14 / #15 | merged | `76680433089842db5805d28eb50416a23c7d0a88` |

### Slice F — LocationSample e replay deterministico

- issue [#11](https://github.com/kinderp/tdna/issues/11);
- PR [#16](https://github.com/kinderp/tdna/pull/16);
- branch `agent/location-sample-replay`;
- stato **pre-review complete; final CI and two clean rounds pending**.

Deliverable:

- [x] `GeoPoint` cross-domain e zero firmato canonico;
- [x] `LocationSample`, sequence, monotonic time e origin;
- [x] ordering gate con inspect/commit;
- [x] rate razionale e resto;
- [x] clock virtuale;
- [x] transizione accepted atomica;
- [x] state machine e summary bounded;
- [x] fixture sintetica e parser JVM;
- [x] Lab/report deterministico;
- [x] benchmark diagnostico e artifact CI;
- [x] chapter 46 e scenario Lab;
- [x] reading paths, tracepoint, tooling e indici;
- [x] report pre-review con finding history e benchmark;
- [ ] Foundation CI sul substantive head finale;
- [ ] clean review round 1;
- [ ] clean review round 2;
- [ ] merge e verifica `main`.

## Criteri di chiusura milestone

- [x] repository e CI multi-language;
- [x] Java/Rust reference routing;
- [x] canonical routing contract seed;
- [x] plugin descriptor e capability;
- [x] fake route planner;
- [x] `MapScene` e fake renderer;
- [ ] `LocationSample`, clock e replay su `main`;
- [ ] missed-exit scenario;
- [x] benchmark seed diagnostico;
- [ ] Gradle Wrapper;
- [ ] documentazione finale milestone.

## Regole

- ogni slice non banale ha issue e PR;
- nel repository può essere aperta al massimo una PR;
- ogni branch parte dal `main` verificato dopo l'ultimo merge;
- niente PR stacked, placeholder o `noop`;
- CI verde e due round puliti sullo stesso SHA sono obbligatori;
- finding o commit sostanziali azzerano il contatore;
- il merge autonomo usa expected-head guard;
- dopo il merge si verificano PR, issue e nuovo `main`;
- una chiusura amministrativa non equivale a una slice completata;
- deliverable significa evidenza, non percentuale vaga.
