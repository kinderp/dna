# Documentazione didattica di Travel DNA

Questa cartella contiene il libro vivo in italiano. I commenti nel codice restano
in inglese e più sintetici.

## Percorsi principali

- [Guida alla lettura](03-guida-lettura-documentazione.md)
- [Regole operative](00-regole-operative.md)
- [Review e merge](06-review-e-merge.md)
- [Architettura generale](20-architettura-generale.md)
- [Stato funzionalità](12-stato-funzionalita.md)

## Engineering e fondazione

| Capitolo | Tema |
| --- | --- |
| [37](37-build-riproducibile-gradle-wrapper.md) | Wrapper, checksum, Action pinning e trust model. |
| [43](43-reference-routing-java-rust.md) | Dijkstra/A* Java e Rust. |
| [44](44-contratti-routing-e-fake-provider.md) | Routing provider-neutral e fake. |
| [45](45-map-scene-e-fake-renderer.md) | MapScene e fake renderer. |
| [46](46-location-sample-e-replay-deterministico.md) | LocationSample e replay. |
| [47](47-posizione-matched-e-route-progress.md) | Route progress. |
| [48](48-porta-map-matching-e-fake-deterministico.md) | Porta map matching. |
| [49](49-off-route-missed-exit-e-reroute.md) | Missed exit e reroute. |

## Android-first

| Capitolo | Tema |
| --- | --- |
| [55](55-roadmap-android-first-e-pilot.md) | Roadmap Pilot 0/1/2, stack, APK e requisiti. |
| [56](56-percorso-studio-android-first.md) | Percorso narrativo di studio Manning/Pluralsight/ufficiale. |
| [57](57-protocollo-pilot-stradale-android.md) | Protocollo del futuro Pilot 1 su strada. |
| [58](58-shell-android-pilot0.md) | Prima shell Compose, build, stato e test. |
| **[59](59-materiali-didattici-e-acquisti-pilot-android.md)** | **Guida unica con link diretti, ordine di studio e acquisti per APK/Pilot.** |
| [60](60-emulator-smoke-e-navigazione-pilot0.md) | Navigazione bounded, Activity recreation e test instrumentation su AVD. |

Decisione: [ADR-0010 Android-first](../adr/0010-android-first-pilot-sequence.md).

## Altri capitoli

- prodotto/DDD/use case: `01`, `02`, `10`, `11`;
- mappe/navigazione/auto: `23`, `24`, `25`;
- diario/social/backend: `26`, `27`, `29`;
- stack/test/performance/privacy: `28`, `30`–`36`;
- codice/tracepoint/Lab: `40`–`42`;
- milestone/librerie/tecnologie/riferimenti/LoRa: `50`–`54`.

## Stato

Consultare:

- [documentation-status.md](documentation-status.md)
- [development-status.md](../project/development-status.md)
- [report giornalieri](../project/daily/README.md)
- [Travel DNA Lab](lab/README.md)
