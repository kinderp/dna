# Registro milestone di Travel DNA

## Registro

| Ordine | Milestone | Stato | Deliverable | Non-obiettivi |
| --- | --- | --- | --- | --- |
| 0 | Documentation Foundation v0 | done | Regole, ADR, architettura e roadmap. | Codice mobile. |
| 1 | Foundations and Travel DNA Lab v0 | done | Build, contratti, fake, replay, matching, reroute e libro. | Provider/GPS reali. |
| 2 | Android Pilot 0 | in-progress | APK didattica, Compose, shared contracts e runtime evidence. | Uso su strada. |
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
| Android Pilot 0 shell | #25 / #26 | `cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f` |

Le issue Foundation aggregate #1 e #2 sono state chiuse come completate dopo la
verifica del merge Android-first e dell'inventario del tracker.

## Slice seriale — Android Pilot 0 v0.2 emulator smoke

- issue [#27](https://github.com/kinderp/tdna/issues/27);
- PR [#28](https://github.com/kinderp/tdna/pull/28);
- branch `agent/android-pilot0-emulator-smoke`;
- base `cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f`;
- rischio `R2`;
- capitolo [60](60-emulator-smoke-e-navigazione-pilot0.md).

Deliverable implementati:

- [x] destinazioni tipizzate e fallback sicuro;
- [x] semantic tag stabili per navigation e screen;
- [x] unit test route/tag/fallback;
- [x] instrumentation test delle quattro superfici;
- [x] test di Activity recreation;
- [x] runner AVD basato su Android SDK ufficiale;
- [x] AVD path/discovery, boot e diagnostica bounded;
- [x] installazione, Activity start e package assertions;
- [x] SHA sostanziale nel report;
- [x] AVD usa-e-getta escluso dagli artifact;
- [x] job CI emulatore separato;
- [x] capitolo e scenario implementation-backed.

La chiusura operativa — SHA finale, due job verdi, artifact bounded, due review
pulite, expected-head merge e verifica post-merge — è registrata in PR #28. Il
registro non duplica checkbox transitorie che diventerebbero obsolete dopo il
merge.

## Stato del Pilot 0

```text
0.1 shell/build/APK                 merged
0.2 navigation/runtime emulator     review-gated in PR #28
0.3 replay e route progress UI      planned
0.4 missed-exit/reroute UI          planned
```

L'esecuzione su emulatore non sostituisce l'installazione su un telefono fisico.
La prova fisica, font scaling e TalkBack restano un gate successivo del Pilot 0.

## Regole

- una sola PR aperta;
- branch dal `main` verificato;
- CI esatta e due review pulite;
- finding o commit sostanziale azzera il conteggio;
- expected-head merge;
- pilot date ristimate in base alle prove.
