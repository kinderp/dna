# Registro milestone di Travel DNA

## Ruolo

GitHub conserva lo stato vivo; questo documento conserva ordine, motivazione,
dipendenze e risultati consolidati.

## Campi

| Campo | Significato |
| --- | --- |
| Ordine | Sequenza logica. |
| Nome | Milestone stabile. |
| Stato | planned, in-progress, done, paused, superseded. |
| Perché ora | Rischio o capacità sbloccata. |
| Dipendenze | Cosa deve esistere. |
| Deliverable | Evidenza di chiusura. |
| Non-obiettivi | Scope escluso. |
| GitHub | Milestone/issue/PR future. |
| Documenti | Contratti e roadmap. |

Le date vengono assegnate quando la milestone viene aperta su GitHub. Questo
pacchetto non inventa date senza team e repository operativo.

## Registro iniziale

| Ordine | Milestone | Stato | Perché ora | Dipendenze | Deliverable principali | Non-obiettivi | Documenti |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 0 | Documentation Foundation v0 | done | Serve una fonte stabile prima del codice e prima di coinvolgere studenti. | Progettazione iniziale. | Regole, architettura, tracepoint, Lab, ADR, roadmap. | Codice mobile/backend. | Tutto il pacchetto v0. |
| 1 | Foundations and Travel DNA Lab v0 | planned | Dimostrare contratti, replay e struttura multi-language senza dipendere da SDK reali. | Milestone 0. | Monorepo bootstrap, canonical geo, plugin SDK, fake providers, Java reference graph, Rust replay CLI, CI. | GPS reale, MapLibre, Valhalla, chat. | `20`, `21`, `22`, `30`, `31`, `41`, `42`. |
| 2 | Canonical Route and Map Slice | planned | Verificare rendering nativo e provider isolation. | 1. | RoutePlan, MapScene, fake route, MapLibre adapters Android/iOS, benchmark base. | Turn-by-turn completo. | `23`, `24`, scenario render. |
| 3 | Navigation Runtime Replay v0 | planned | Eliminare rischio guidance prima della strada. | 1–2. | Ferrostar adapter, replay missed exit, state machine, voice fake, performance report. | Produzione, traffic live. | `24`, `31`, `32`, scenario reroute. |
| 4 | External Navigation Companion v0 | planned | Garantire valore con Waze/Maps prima del navigatore interno maturo. | 1, Journey session. | handoff adapters, shadow route v0, background recorder fake/integration, return flow. | Android Auto/CarPlay completi. | `25`, ADR 0003. |
| 5 | Journey Journal v0 | planned | Valore autonomo e dati per DNA. | 1. | event store, stop fixture, media fake, DailyPage, edit preservation. | Cloud media, public sharing. | `26`, scenario diario. |
| 6 | Conversation Core v0 | planned | Definire messaggistica durable e driver policy prima dell'auto. | 1, backend decision. | local outbox, fake server, voice surface fake, safety tests. | Social discovery live. | `27`, `29`, scenario chat. |
| 7 | Backend Modular Monolith v0 | planned | Supportare sync/chat con confini chiari. | 1, ADR backend. | auth dev, sync, conversations, routing gateway, PostGIS setup. | Microservices. | `29`. |
| 8 | OSM and Valhalla Integration v0 | planned | Route e POI reali dietro contratti. | 2, 7. | provider tile dev, Valhalla gateway, place normalizer, attribution. | Planet-scale production. | `23`, `24`, `36`. |
| 9 | Automotive Messaging v0 | planned | Chat sicura con navigatore esterno. | 4, 6. | Android Auto notifications, CarPlay/SiriKit spike, simulator tests. | Arbitrary split-screen. | `25`, `33`. |
| 10 | Road Presence and Greetings v0 | planned | Prima funzione social live con privacy forte. | 6, 7, threat model. | coarse presence, TTL, greeting, block, abuse tests. | Exact map tracking. | `27`, `33`, scenario DNA. |
| 11 | DNA Cards v0 | planned | Condivisione derivata dal diario. | 5, 10. | sanitizer, preview, publish/revoke, compatibility v0. | Opaque ML matching. | `26`, `27`. |
| 12 | Internal Navigation Beta | planned | Solo dopo reliability/performance evidence. | 2, 3, 8. | integrated route, guidance, map, voice, field audits, fallback. | Waze-equivalent traffic claim. | `24`, `32`. |
| 13 | Offline Region v0 | future | Indipendenza e viaggio senza rete. | 8, 12, legal/licence design. | package format, tiles, place index, routing data, updater. | Whole-world offline. | `23`, `36`, `51`. |
| 14 | Travel DNA Guidance Core | future | Ridurre dipendenza Ferrostar solo se valore misurato. | replay maturity, shadow comparison. | Rust core in shadow, rollout, fallback. | Map renderer rewrite. | `51`. |
| 15 | LoRa Communication Spike | future/open | Valutare casi senza copertura con vincoli reali. | Discussione dedicata. | experiment report and ADR. | Produzione prima di prove. | `54`. |

## Milestone 1: criteri di chiusura

`Foundations and Travel DNA Lab v0` è conclusa quando esistono:

- repository buildabile;
- Java 21 e Rust toolchain documentati;
- canonical `GeoPoint`, `RouteRequest`, `RoutePlan`, `LocationSample`;
- plugin descriptor e capability;
- fake route planner;
- fake map renderer;
- Java A* reference su piccolo grafo;
- Rust replay runner con clock virtuale;
- scenario missed exit eseguibile con fake guidance;
- contract test;
- CI Linux;
- documentazione aggiornata;
- primo benchmark report con limiti.

## Regole

- ogni milestone ha issue madre;
- ogni micro-step non banale ha issue/PR;
- chiusura aggiorna stato, esito, lavoro rimandato;
- una milestone futura non autorizza codice anticipato;
- i deliverable sono prove, non percentuali vaghe.
