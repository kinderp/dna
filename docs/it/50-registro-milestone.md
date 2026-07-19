# Registro milestone di Travel DNA

## Registro

| Ordine | Milestone | Stato | Deliverable | Non-obiettivi |
| --- | --- | --- | --- | --- |
| 0 | Documentation Foundation v0 | done | Regole, ADR, architettura e roadmap. | Codice mobile. |
| 1 | Foundations and Travel DNA Lab v0 | done | Build, contratti, fake, replay, matching, reroute e libro. | Provider/GPS reali. |
| 2 | Android Pilot 0 | in-progress | APK didattica, Compose, shared contracts e runtime evidence. | Uso su strada. |
| 3 | Android Pilot 1 | planned | Foreground trip companion, navigatori esterni e POI Android Auto. | Turn-by-turn TDNA. |
| 4 | Canonical Route and Map | planned | MapLibre adapter e benchmark device. | Mappa completa subito. |
| 5 | Journey Journal v0 | planned | Event store, soste, media e pagina del giorno. | Cloud pubblico. |
| 6 | Conversation Core v0 | planned | Outbox, fake server e driver policy. | Discovery live. |
| 7 | Backend Modular Monolith | planned | Auth, sync e routing gateway. | Microservizi. |
| 8 | OSM/Valhalla Integration | planned | Adapter e normalizer. | Scala planetaria. |
| 9 | Automotive Readiness | planned | Car App Library POI spike, DHU e driver-safe snapshot. | Categoria Navigation. |
| 10 | Road Presence | planned | Coarse presence, TTL e abuso. | Tracking esatto. |
| 11 | DNA Cards | planned | Sanitizer, publish e revoke. | Matching opaco. |
| 12 | Internal Navigation Beta | planned | Map, guidance, voice, AUTO_DRIVE e car review gates. | Parità Waze. |
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
| Android emulator/navigation v0.2 | #27 / #28 | `6198bd0a6c5a89f0e32fa218015125b5d452703e` |

Le issue Foundation aggregate #1 e #2 sono state chiuse come completate dopo la
verifica del merge Android-first e dell'inventario del tracker.

## Slice seriale — Android Auto compliance roadmap

- issue [#29](https://github.com/kinderp/tdna/issues/29);
- branch `agent/android-auto-compliance-roadmap`;
- base `6198bd0a6c5a89f0e32fa218015125b5d452703e`;
- rischio `R1`;
- ADR [0011](../adr/0011-android-auto-poi-first-and-car-surfaces.md);
- capitolo [61](61-android-auto-compliance-e-roadmap-automotive.md).

Decisioni:

- [x] Android Auto usa un renderer Car App Library separato dalla UI Compose;
- [x] Pilot 1 è POI-first con navigatore esterno come autorità delle manovre;
- [x] categoria Navigation rinviata alla Internal Navigation Beta;
- [x] v0.3 prepara un `DriverJourneySnapshot` provider-neutral;
- [x] replay deterministico candidato per il futuro `AUTO_DRIVE`;
- [x] DHU, Android Automotive OS e veicolo reale hanno evidenze separate;
- [x] nessuna dipendenza o dichiarazione automotive entra in questa slice docs-only.

Il ledger operativo finale di SHA, CI, review e merge resta nella PR della slice.

## Stato del Pilot 0 e percorso automotive

```text
0.1 shell/build/APK                         merged
0.2 navigation/runtime emulator             merged
0.3 replay + route progress + driver snapshot planned
0.4 missed-exit/reroute + degraded state    planned
0.5 navigatori esterni                      planned
Automotive readiness POI/DHU spike          planned after driver-safe contracts
Pilot 1 POI companion                       planned
Internal Navigation Beta                    later, review-gated
```

L'esecuzione su emulatore non sostituisce l'installazione su un telefono fisico.
Il DHU non sostituisce un veicolo reale; AUTO_DRIVE non costituisce road evidence.

## Regole

- una sola PR aperta;
- branch dal `main` verificato;
- CI esatta e due review pulite;
- finding o commit sostanziale azzera il conteggio;
- expected-head merge;
- categorie e quality policy Android for Cars ricontrollate prima di ogni slice;
- pilot date ristimate in base alle prove.
