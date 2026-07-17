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

### Slice B — provider-neutral routing contracts

- issue [#5](https://github.com/kinderp/tdna/issues/5);
- PR [#6](https://github.com/kinderp/tdna/pull/6);
- stato **merged**;
- merge commit `2a28d1654988cef4188986342f76fd7d19be358f`.

### Slice C — review governance e contract hardening

- issue [#8](https://github.com/kinderp/tdna/issues/8);
- PR [#9](https://github.com/kinderp/tdna/pull/9);
- stato **merged**;
- merge commit `044e0773dd9afb1530db35688a00c56bfbd5eace`.

Completati:

- [x] due round consecutivi obbligatori;
- [x] substantive head, finding e reset;
- [x] capitolo `06-review-e-merge.md`;
- [x] contributor guide, agent rules e PR template;
- [x] `roadName` ed `exitNumber` bounded;
- [x] CI verde e due round puliti;
- [x] merge.

### Slice D — provider-neutral MapScene e fake renderer

- issue [#7](https://github.com/kinderp/tdna/issues/7);
- draft PR [#10](https://github.com/kinderp/tdna/pull/10);
- branch `agent/provider-neutral-map-scene`;
- stato **realigned; final CI and two new reviews pending**.

Deliverable:

- [x] `MapScene`, camera, route e marker semantici;
- [x] delta bounded separati dalla scena statica;
- [x] `MapRendererPort` e capability;
- [x] `FakeMapRenderer` e snapshot;
- [x] renderer contract probe;
- [x] route projector;
- [x] CLI Lab, capitolo 45 e scenario;
- [x] rimozione dei contratti MapScene duplicati;
- [x] riallineamento ai file di governance e routing di `main`;
- [ ] CI verde sul nuovo substantive head;
- [ ] clean review round 1;
- [ ] clean review round 2;
- [ ] merge.

### Criteri di chiusura milestone

- [x] repository e CI multi-language;
- [x] Java/Rust reference routing;
- [x] canonical routing contract seed;
- [x] plugin descriptor e capability;
- [x] fake route planner;
- [ ] `MapScene` e fake renderer su `main`;
- [ ] `LocationSample` e clock;
- [ ] GPS replay;
- [ ] missed-exit scenario;
- [ ] benchmark report;
- [ ] Gradle Wrapper;
- [ ] documentazione finale milestone.

## Regole

- ogni slice non banale ha issue e PR;
- ogni PR richiede CI verde e due review round consecutivi senza finding sullo
  stesso substantive head;
- finding o commit sostanziali resettano il clean-review counter;
- una sola PR può essere aperta alla volta salvo eccezione esplicita del maintainer;
- dopo ogni merge il lavoro successivo parte dal nuovo `main` riallineato;
- la chiusura registra prove e lavoro rimandato;
- l'issue si chiude con il merge, non con la sola prontezza tecnica;
- milestone future non autorizzano codice anticipato;
- deliverable significa evidenza, non percentuale vaga.
