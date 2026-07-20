# Guida alla lettura della documentazione

La documentazione ha pubblici diversi. Questa guida evita di confondere visione
futura, contratti correnti, Lab eseguibili e capacità di prodotto.

## Percorso 1 — capire Travel DNA

1. [Visione del prodotto](01-visione-prodotto.md)
2. [Glossario](02-glossario.md)
3. [Use case](11-use-case-principali.md)
4. [DDD e bounded context](10-ddd-bounded-context.md)
5. [Architettura generale](20-architettura-generale.md)
6. [Stato funzionalità](12-stato-funzionalita.md)
7. [Matrice tecnologie](52-matrice-tecnologie-decisioni.md)

Al termine devi distinguere diario, guida, navigazione, socialità, contratto,
provider, adapter e piattaforma.

## Percorso 2 — contribuire

1. [Regole operative](00-regole-operative.md)
2. [Review e merge](06-review-e-merge.md)
3. [Come contribuire](04-come-contribuire.md)
4. [Struttura repository](21-struttura-repository.md)
5. [Registro milestone](50-registro-milestone.md)
6. [Stato sviluppo](../project/development-status.md)
7. issue e unica PR aperta;
8. capitolo e scenario della slice;
9. [Strategia test](30-strategia-test.md)
10. [Stile commenti](../commenting-style.md)

```text
capire il contratto
-> implementare una vertical slice
-> test, benchmark e documentazione
-> CI sul substantive head
-> review pulita 1
-> review pulita 2 sullo stesso SHA
-> merge con expected-head
-> riallineamento di main
```

## Percorso 3 — build riproducibile

1. [Capitolo 37 — Gradle Wrapper](37-build-riproducibile-gradle-wrapper.md)
2. [Scenario build-bootstrap](lab/scenarios/gradle-wrapper-riproducibile.md)
3. `gradle/wrapper/tdna-wrapper-policy.json`;
4. `tools/check_gradle_wrapper.py`;
5. `tools/check_ci_actions.py`;
6. `.github/workflows/foundation-ci.yml`;
7. [Supply chain](36-licenze-dati-supply-chain.md);
8. [Rapporto di chiusura Foundations v0](../project/foundation-v0-closure.md).

Comandi:

```bash
sh tools/tdna check-gradle-wrapper
sh tools/tdna check-ci-actions
sh tools/tdna lab build-bootstrap
./gradlew --version
```

Domande guida:

- perché il Wrapper JAR viene committato?
- che differenza c'è fra checksum, firma e root of trust?
- perché una GitHub Action viene bloccata a SHA completo?
- perché `gradle` globale è opzionale ma Java è obbligatorio?
- che cosa rimane non ermetico nel build?

## Percorso 4 — come nasce un navigatore

1. [Routing Java/Rust](43-reference-routing-java-rust.md)
2. [Scenario reference routing](lab/scenarios/reference-routing-java-rust.md)
3. [Contratti routing](44-contratti-routing-e-fake-provider.md)
4. [Scenario fake planner](lab/scenarios/routing-contracts-fake-provider.md)
5. [MapScene](45-map-scene-e-fake-renderer.md)
6. [Scenario MapScene](lab/scenarios/map-scene-fake-renderer.md)
7. [LocationSample e replay](46-location-sample-e-replay-deterministico.md)
8. [Scenario replay](lab/scenarios/location-replay-deterministico.md)
9. [Posizione matched e route progress](47-posizione-matched-e-route-progress.md)
10. [Scenario route progress](lab/scenarios/route-progress-tracker.md)
11. [Porta map matching e fake](48-porta-map-matching-e-fake-deterministico.md)
12. [Scenario map matching](lab/scenarios/map-matching-fake-provider.md)
13. [Off-route, missed exit e reroute](49-off-route-missed-exit-e-reroute.md)
14. [Scenario missed exit](lab/scenarios/navigation-missed-exit-reroute.md)
15. [OpenStreetMap e cartografia](23-openstreetmap-e-cartografia.md)
16. [Routing e navigazione](24-routing-e-navigazione.md)
17. [Prestazioni](32-performance-budget.md)
18. [Tracepoint Model](41-tracepoint-model-v0.md)

Ordine concettuale:

```text
grafo
-> algoritmo
-> contratto routing provider-neutral
-> RoutePlan
-> MapScene e renderer
-> LocationSample
-> filtro futuro
-> MapMatcherPort / MapMatchSession
-> Matched | Unmatched | Failure
-> MatchedRoutePosition
-> RouteProgressSnapshot
-> evidenza off-route
-> conferma count + durata
-> reroute correlato
-> route replacement
-> delta mappa / HUD / voce
```

## Percorso 5 — Kotlin Multiplatform

1. [Stack linguaggi](28-stack-linguaggi-e-gui.md)
2. [Plugin e provider](22-architettura-plugin-provider.md)
3. capitoli 44–49;
4. aprire nell'ordine:

```text
shared/plugin-sdk
shared/geo-contracts
shared/routing-contracts
shared/map-contracts
shared/location-contracts
shared/location-replay
shared/navigation-contracts
shared/map-matching-contracts
shared/map-matching-testkit
shared/fake-map-matcher
shared/route-progress
shared/route-progress-map-projector
shared/off-route-contracts
shared/off-route-state-machine
shared/reroute-coordinator
```

Eseguire:

```bash
sh tools/tdna check-architecture
sh tools/tdna check-kotlin
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
sh tools/tdna lab location-replay
sh tools/tdna lab route-progress
sh tools/tdna lab map-matching
sh tools/tdna lab missed-exit
```

## Percorso 6 — posizione, matching e reroute

Ordine consigliato:

```text
46: LocationSample, sequence, tempo monotono e replay
47: posizione matched, leg, manovra, arrival e delta
48: porta route-bound, esiti, fake e postcondizioni
49: evidenza off-route, conferma, correlazione e replacement
```

Comandi:

```bash
sh tools/tdna bench location-replay 10000 7
sh tools/tdna bench route-progress 10000 7
sh tools/tdna bench map-matching 10000 7
sh tools/tdna bench off-route 10000 7
```

I benchmark sono diagnostici JVM CI. Non misurano dispositivo, batteria, GPS,
rete o affidabilità su strada.

## Percorso 7 — mappe reattive

1. [Capitolo 45](45-map-scene-e-fake-renderer.md)
2. [Scenario MapScene](lab/scenarios/map-scene-fake-renderer.md)
3. [Capitolo 47](47-posizione-matched-e-route-progress.md)
4. seguire:

```text
RoutePlan
-> RouteOverlay
-> MapScene installata
-> RouteProgressMapBinding
-> RouteProgressSnapshot
-> MapSceneDelta.UpdateRouteProgress
-> FakeMapRenderer snapshot
```

La geometria si installa raramente; il progresso usa delta compatti.

## Percorso 8 — Android-first

La prima piattaforma mobile scelta dal maintainer è **Android**.

1. [Stack](28-stack-linguaggi-e-gui.md)
2. capitoli 37 e 44–49;
3. [Navigatori esterni e auto](25-navigatori-esterni-e-automotive.md)
4. [Privacy e sicurezza](33-privacy-security-driving-safety.md)
5. [Debugging](34-debugging-e-strumenti.md)
6. [Stato sviluppo](../project/development-status.md)

Ordine previsto:

```text
shell Android e composition root
-> design system e navigazione Compose
-> fake runtime/replay integrato
-> adapter navigatori esterni
-> location permission/lifecycle
-> recorder locale bounded
-> MapLibre MapHost
-> active-trip presentation
-> foreground/background service
-> pilot controllato
-> Android Auto dopo core mobile stabile
```

La prima shell non è un navigatore di produzione. Deve dimostrare build,
installazione, stato locale, uso dei contratti condivisi e percorsi di test.

## Percorso 9 — iOS e Swift

La piattaforma iOS resta nel disegno, ma non viene sviluppata in parallelo alla
prima shell Android.

1. [Stack](28-stack-linguaggi-e-gui.md)
2. capitoli 45–49;
3. [Navigatori esterni](25-navigatori-esterni-e-automotive.md)
4. [Privacy](33-privacy-security-driving-safety.md)

```text
shared framework
-> Core Location adapter
-> SwiftUI shell
-> UIKit MapLibre host
-> ActivityKit / CarPlay
```

## Percorso 10 — Java e Rust

### Java

1. [Capitolo 43](43-reference-routing-java-rust.md)
2. aprire `java/reference-routing`;
3. eseguire `sh tools/tdna check-java`;
4. confrontare algoritmo e porte applicative dei capitoli 44, 48 e 49.

### Rust

1. [Capitolo 43](43-reference-routing-java-rust.md)
2. aprire `crates/tdna-reference-routing`;
3. eseguire test e contract diff;
4. leggere replay, benchmark e [roadmap librerie](51-roadmap-librerie-open-source.md).

```text
tipi puri -> test -> fixture/replay -> benchmark -> API stabile -> FFI -> mobile
```

## Percorso 11 — diario, chat e privacy

### Diario

1. [Diario e Pagina del giorno](26-diario-media-pagina-giorno.md)
2. [Backend local-first](29-backend-dati-sync.md)
3. [Privacy](33-privacy-security-driving-safety.md)
4. [Scenario diario](lab/scenarios/daily-page-photos-thoughts.md)

### Chat in viaggio

1. [Presenza, chat e DNA](27-presenza-chat-dna.md)
2. [Navigatori esterni](25-navigatori-esterni-e-automotive.md)
3. [Privacy e guida](33-privacy-security-driving-safety.md)
4. [Scenario chat](lab/scenarios/chat-with-external-navigation.md)

## Percorso 12 — riprendere dopo una pausa

1. `AGENTS.md`;
2. [Regole operative](00-regole-operative.md);
3. [Review e merge](06-review-e-merge.md);
4. [Stato documentazione](documentation-status.md);
5. [Stato sviluppo](../project/development-status.md);
6. [Indice report](../project/daily/README.md);
7. [Registro milestone](50-registro-milestone.md);
8. issue, unica PR, capitolo e scenario correnti.

## Eseguire tutti i Lab

```bash
sh tools/tdna doctor
sh tools/tdna check
sh tools/tdna lab build-bootstrap
sh tools/tdna lab reference-routing astar
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
sh tools/tdna lab location-replay
sh tools/tdna lab route-progress
sh tools/tdna lab map-matching
sh tools/tdna lab missed-exit
```

## Futuro studio LoRa

[Spike LoRa](54-spike-lora-roadmap.md) è una roadmap di ricerca, non una
decisione. Separare LoRa fisico, LoRaWAN, mesh, regolamentazione, hardware dei
telefoni e valore reale per Travel DNA.
