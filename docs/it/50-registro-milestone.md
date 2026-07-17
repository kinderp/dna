# Registro milestone di Travel DNA

## Ruolo

GitHub conserva lo stato vivo; questo documento conserva ordine, motivazione,
dipendenze, risultati e lavoro rimandato.

## Registro

| Ordine | Milestone | Stato | Perché ora | Deliverable principali | Non-obiettivi |
| --- | --- | --- | --- | --- | --- |
| 0 | Documentation Foundation v0 | done | Fonte stabile prima del codice e degli studenti. | Regole, architettura, ADR, Lab e roadmap. | Codice mobile/backend. |
| 1 | Foundations and Travel DNA Lab v0 | in-progress | Contratti e scenari eseguibili prima degli SDK reali. | Multi-language build, canonical models, fakes, replay seed, CI. | GPS reale, MapLibre, Valhalla, chat. |
| 2 | Canonical Route and Map Slice | planned | Verificare rendering e isolamento provider. | MapScene, fake renderer, adapter MapLibre seed. | Turn-by-turn completo. |
| 3 | Navigation Runtime Replay v0 | planned | Ridurre rischio guidance prima della strada. | Clock, replay, off-route e reroute scenario. | Traffico live. |
| 4 | External Navigation Companion v0 | planned | Valore con Waze/Maps prima del navigatore interno. | Handoff, shadow route e recorder. | Automotive completo. |
| 5 | Journey Journal v0 | planned | Valore autonomo e dati per DNA. | Event store, soste, media fake e DailyPage. | Cloud media pubblico. |
| 6 | Conversation Core v0 | planned | Messaggistica durable e driver policy. | Outbox, fake server e voice surface. | Discovery live. |
| 7 | Backend Modular Monolith v0 | planned | Supportare sync/chat con confini chiari. | Auth dev, sync, conversation e routing gateway. | Microservizi. |
| 8 | OSM and Valhalla Integration v0 | planned | Route e POI reali dietro contratti. | Valhalla adapter/gateway e place normalizer. | Scala planetaria. |
| 9 | Automotive Messaging v0 | planned | Chat sicura con navigatore esterno. | Android Auto e CarPlay/SiriKit spike. | Split-screen arbitrario. |
| 10 | Road Presence and Greetings v0 | planned | Prima funzione social con privacy forte. | Coarse presence, TTL, greeting e abuse tests. | Tracking preciso. |
| 11 | DNA Cards v0 | planned | Condivisione derivata dal diario. | Sanitizer, publish e revoke. | Matching ML opaco. |
| 12 | Internal Navigation Beta | planned | Solo dopo prove di affidabilità e prestazioni. | Map, guidance, voice e field audit. | Parità traffico Waze. |
| 13 | Offline Region v0 | future | Viaggio senza rete. | Package, tile, indice e routing data. | Whole-world offline. |
| 14 | Travel DNA Guidance Core | future | Sostituire Ferrostar solo con valore misurato. | Rust shadow core e rollout. | Renderer riscritto. |
| 15 | LoRa Communication Spike | future/open | Valutare casi senza copertura. | Esperimento e ADR. | Produzione senza prove. |

## Milestone 1 — Foundations and Travel DNA Lab v0

Avvio: **2026-07-16**

### Slice A — reference routing

- issue: [#3](https://github.com/kinderp/tdna/issues/3);
- PR: [#4](https://github.com/kinderp/tdna/pull/4);
- stato: **merged** il 17 luglio 2026;
- merge commit: `d122f1b4871719087e79a50b185ab302d810cb20`.

Deliverable completati:

- [x] fixture sintetica versionata;
- [x] parser Java/Rust;
- [x] Dijkstra e A*;
- [x] route deterministica;
- [x] report byte-identico;
- [x] test e CI;
- [x] capitolo 43 e scenario Lab.

### Slice B — provider-neutral routing contracts

- issue: [#5](https://github.com/kinderp/tdna/issues/5);
- PR draft: [#6](https://github.com/kinderp/tdna/pull/6);
- branch: `agent/provider-neutral-routing-contracts`;
- stato: **in review**.

Deliverable:

- [x] bootstrap Gradle/KMP;
- [x] JVM e Linux x64 targets;
- [x] plugin ID, capability, runtime platform e descriptor;
- [x] canonical `GeoPoint`, `RouteRequest`, `RoutePlan`, legs e manovre;
- [x] provenance e result/error model;
- [x] `RoutePlannerPort`;
- [x] defensive immutable snapshots;
- [x] request/waypoint postconditions;
- [x] bounded provider metadata;
- [x] deterministic fake route planner;
- [x] reusable conformance probe;
- [x] architecture checker;
- [x] executable CLI Lab;
- [x] capitolo 44 e scenario;
- [x] indice report giornalieri;
- [ ] final CI after documentation commit;
- [ ] two consecutive clean final review rounds;
- [ ] merge.

### Criteri di chiusura della milestone

- [x] repository e CI multi-language;
- [x] Java 21 e Rust reference routing;
- [x] canonical routing contract seed;
- [x] plugin descriptor e capability;
- [x] fake route planner;
- [ ] `LocationSample` e clock virtuale;
- [ ] GPS replay runner;
- [ ] MapScene e fake map renderer;
- [ ] missed-exit scenario eseguibile;
- [ ] primo benchmark report;
- [ ] Gradle Wrapper committato;
- [ ] documentazione di milestone finale.

## Regole

- ogni milestone ha issue madre o registro equivalente;
- ogni slice non banale ha issue e PR;
- la chiusura registra prove e lavoro rimandato;
- una milestone futura non autorizza codice anticipato;
- deliverable significa evidenza, non percentuale vaga.
