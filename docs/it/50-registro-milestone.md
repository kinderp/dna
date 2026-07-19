# Registro milestone di Travel DNA

## Registro

| Ordine | Milestone | Stato | Deliverable | Non-obiettivi |
| --- | --- | --- | --- | --- |
| 0 | Documentation Foundation v0 | done | Regole, ADR, architettura e roadmap. | Codice mobile. |
| 1 | Foundations and Travel DNA Lab v0 | done | Build, contratti, fake, replay, matching, reroute e libro. | Provider/GPS reali. |
| 2 | Android Pilot 0 | in-progress | APK didattica installabile, Compose e shared contracts. | Uso su strada. |
| 3 | Android Pilot 1 | planned | Foreground trip companion e navigatori esterni. | Turn-by-turn TDNA. |
| 4 | Canonical Route and Map | planned | MapLibre adapter e benchmark device. | Mappa completa subito. |
| 5 | Journey Journal v0 | planned | Event store, soste, media e pagina del giorno. | Cloud pubblico. |
| 6 | Conversation Core v0 | planned | Outbox, fake server e driver policy. | Discovery live. |
| 7 | Backend Modular Monolith | planned | Auth, sync e routing gateway. | Microservizi. |
| 8 | OSM/Valhalla Integration | planned | Adapter e normalizer. | Scala planetaria. |
| 9 | Automotive Messaging | planned | Android Auto e CarPlay spike. | Split-screen arbitrario. |
| 10 | Road Presence | planned | Coarse presence, TTL e abuso. | Tracking esatto. |
| 11 | DNA Cards | planned | Sanitizer, publish e revoke. | Matching opaco. |
| 12 | Internal Navigation Beta | planned | Map, guidance, voice e field audit. | Parità Waze. |
| 13 | Offline Region | future | Package e routing data. | Mondo intero. |
| 14 | Guidance Core Rust | future | Shadow core misurato. | Renderer riscritto. |
| 15 | LoRa Spike | future/open | Esperimento e ADR. | Produzione senza prove. |

## Slice completate recenti

| Slice | Issue / PR | Merge |
| --- | --- | --- |
| Map matching | #19 / #20 | `95cf2f0d900b039efb86ba0a570e3ca3f8d8cef5` |
| Missed exit/reroute | #21 / #22 | `8570eb466b43384756f2678da2303929295e087a` |
| Wrapper/Foundations closure | #23 / #24 | `7090882b40e747a85d812decb0b3567a1276d701` |

## Slice attiva — Android Pilot 0 shell

- issue [#25](https://github.com/kinderp/tdna/issues/25);
- PR [#26](https://github.com/kinderp/tdna/pull/26);
- base `7090882b40e747a85d812decb0b3567a1276d701`;
- rischio `R2`;
- ADR [0010](../adr/0010-android-first-pilot-sequence.md).

Deliverable:

- [x] decisione Android-first e finestre Pilot 0/1/2;
- [x] roadmap stack/APK/requisiti/acquisti;
- [x] percorso studio Manning/Pluralsight/ufficiale;
- [x] protocollo del futuro pilot stradale;
- [x] modulo `apps/android` separato;
- [x] shell Compose permission-free;
- [x] consumo di contratti shared;
- [x] unit/instrumentation source;
- [x] CI Android e artifact path;
- [ ] CI verde sul final substantive head;
- [ ] installazione emulatore/telefono documentata;
- [ ] due round puliti sullo stesso SHA;
- [ ] expected-head merge.

## Regole

- una sola PR aperta;
- branch dal `main` verificato;
- CI esatta e due review pulite;
- finding o commit sostanziale azzera il conteggio;
- expected-head merge;
- pilot date ristimate in base alle prove.
