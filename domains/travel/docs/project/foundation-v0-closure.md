# Foundations and Travel DNA Lab v0 — rapporto di chiusura

## Stato

`closure candidate — il ledger finale di CI, review e merge è la PR #24`

Questo rapporto consolida ciò che la milestone ha realmente costruito e ciò che
resta fuori. Diventa parte di `main` soltanto dopo il gate completo della
[pull request #24](https://github.com/kinderp/tdna/pull/24).

La milestone non consegna un navigatore mobile. Consegna una fondazione
architetturale, eseguibile e didattica sulla quale costruirlo senza dipendere
subito da provider o SDK concreti.

## Obiettivo originario

La milestone doveva dimostrare che Travel DNA può essere progettato come sistema
sostituibile e studiabile:

```text
idea di prodotto
-> bounded context e regole
-> contratti provider-neutral
-> fake e testkit
-> algoritmi e state machine deterministici
-> fixture e replay
-> benchmark diagnostici
-> CI multi-language
-> capitoli e Lab eseguibili
```

## Criteri di chiusura

| Criterio | Evidenza | Stato |
| --- | --- | --- |
| Repository e CI multi-language | Java 21, Rust stable, Kotlin Multiplatform JVM/Linux | completato |
| Reference routing | Dijkstra/A* Java e Rust, report byte-identico | completato |
| Plugin SDK | ID, capability, descriptor, piattaforme e notice | completato |
| Routing contracts | request, plan, legs, maneuvers, provenance, port | completato |
| Fake route planner/testkit | catalogo deterministico e conformance probe | completato |
| Map contracts | `MapScene`, delta, renderer port e fake | completato |
| Location contracts/replay | sequence, tempo monotono, gate, clock, replay | completato |
| Matched position/progress | coordinate route, leg, maneuver, arrival, map delta | completato |
| Map-matching boundary | route-bound session, esiti, fake e postcondizioni | completato |
| Missed-exit/reroute | evidenza, conferma, correlazione e replacement | completato |
| Benchmark seed | replay, progress, matching e off-route senza threshold | completato |
| Build bootstrap | Wrapper 9.5.1, hash, action pinning, clone smoke | candidato PR #24 |
| Documentazione didattica | capitoli 37 e 43–49, scenari, mappe e report | completato |
| Processo di engineering | una PR, due round puliti, expected-head merge | completato |

La riga build-bootstrap viene considerata chiusa soltanto dopo CI e merge della
PR #24. La timeline della PR conserva SHA, artifact e review finali senza
richiedere un commit successivo che invaliderebbe il gate.

## Sequenza implementation-backed

### 1. Reference routing Java/Rust

```text
grafo sintetico
-> parser indipendenti
-> Dijkstra / A*
-> ricostruzione route
-> report Java/Rust confrontato
```

Insegna algoritmi, priority queue, euristica ammissibile, overflow e
determinismo.

### 2. Contratti routing e provider

```text
RouteRequest
-> RoutePlannerPort
-> fake planner
-> RoutePlanningResult
-> RoutePlan canonico
```

Insegna dependency inversion, capability, provenance, error model e contract
test.

### 3. MapScene e fake renderer

```text
RoutePlan
-> RouteOverlay
-> MapScene installata
-> MapSceneDelta
-> fake renderer snapshot
```

Insegna separazione fra scena statica, delta frequenti e SDK grafico futuro.

### 4. LocationSample e replay

```text
fixture sintetica
-> ordering gate
-> clock virtuale
-> playback rate razionale
-> state machine bounded
-> report
```

Insegna tempo monotono, input non ordinati, commit atomico e replay
riproducibile.

### 5. Posizione matched e route progress

```text
MatchedRoutePosition
-> RouteProgressTracker
-> leg / manovra / arrival
-> binding route-overlay
-> delta mappa O(1)
```

Insegna che matching e progress sono problemi diversi e che indice geometrico,
distanza e tempo non sono sinonimi.

### 6. Porta di map matching

```text
LocationSample
-> MapMatchSession
-> Matched / Unmatched / Failure
-> postcondition
-> RouteProgressTracker
```

Insegna binding della route fuori dal loop, cancellazione, determinismo con
sessioni fresche e fake che non finge un algoritmo reale.

### 7. Missed exit e reroute

```text
evidenza normalizzata
-> falso allarme / hold / conferma
-> un attempt correlato
-> RoutePlannerPort
-> route replacement validato
```

Insegna count più durata, vecchia route autorevole, outcome stale, capability,
provenance e cleanup della cancellazione.

### 8. Build bootstrap riproducibile

```text
Java 21
-> wrapper committato
-> verifica file e checksum
-> distribuzione Gradle 9.5.1
-> build comune locale/CI
```

Insegna bootstrap, drift, checksum, riferimenti Action immutabili e limiti della
catena di fiducia.

## Moduli consegnati

### Java

```text
java/reference-routing
```

Reference implementation didattica senza dipendenze esterne.

### Rust

```text
crates/tdna-reference-routing
```

Seconda implementazione indipendente dello stesso contratto fixture-scoped.

### Kotlin Multiplatform

```text
shared/plugin-sdk
shared/geo-contracts
shared/routing-contracts
shared/routing-testkit
shared/fake-route-planner
shared/map-contracts
shared/map-testkit
shared/fake-map-renderer
shared/route-map-projector
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

### Laboratori

```text
labs/routing-contracts-cli
labs/map-scene-cli
labs/location-replay-cli
labs/route-progress-cli
labs/map-matching-cli
labs/missed-exit-cli
```

Il build-bootstrap Lab vive negli strumenti del repository perché precede Gradle
stesso.

## Tracciabilità delle pull request

| Slice | Issue / PR | Merge |
| --- | --- | --- |
| Reference routing | #3 / #4 | `d122f1b4871719087e79a50b185ab302d810cb20` |
| Routing contracts | #5 / #6 | `2a28d1654988cef4188986342f76fd7d19be358f` |
| Review governance | #8 / #9 | `044e0773dd9afb1530db35688a00c56bfbd5eace` |
| MapScene | #7 / #10 | `2128f24b4a6ab00aeb437ed48a0af2910f33e9ec` |
| Serial PR governance | #14 / #15 | `76680433089842db5805d28eb50416a23c7d0a88` |
| Location replay | #11 / #16 | `020f8495f7fbbae81f1463b098b0ddd2a079c873` |
| Route progress | #17 / #18 | `9921fbcc1da1000e6434bdae49646122cae8f0e0` |
| Map matching boundary | #19 / #20 | `95cf2f0d900b039efb86ba0a570e3ca3f8d8cef5` |
| Missed exit/reroute | #21 / #22 | `8570eb466b43384756f2678da2303929295e087a` |
| Wrapper e closure | #23 / #24 | ledger finale nella PR #24 |

## Prove automatiche

Il comando consolidato è:

```bash
sh tools/tdna check
```

Controlla:

```text
link e code fence Markdown
confini architetturali Kotlin
Gradle Wrapper e hash
riferimenti GitHub Actions
Java reference routing
Rust reference routing
contratto Java/Rust
KMP common/JVM/Linux
Lab eseguibili
benchmark diagnostici
```

La CI usa lo stesso entry point. Non esiste un build alternativo mantenuto
soltanto nel workflow.

## Fixture e privacy

Le fixture pubbliche sono sintetiche:

```text
grafo stradale didattico
campioni location replay
route progress
map matching a catalogo
missed-exit evidence
```

Non contengono:

- viaggi reali;
- account;
- fotografie;
- messaggi;
- targhe;
- identificatori personali;
- coordinate raccolte da un utente.

L'esistenza di fixture sintetiche non risolve la futura privacy del prodotto.
Serve a impedire che il laboratorio inizi già con dati personali.

## Prestazioni: che cosa è stato misurato

Sono disponibili benchmark JVM diagnostici per:

- replay di campioni;
- route progress;
- fake map matching più progress;
- state machine off-route.

Le misure hanno:

```text
warm-up
numero dispari di run
minimo / mediana / massimo
setup fuori dal timer
correctness pass fuori dal timer
nessuna soglia CI
```

Non sono confrontabili con prestazioni mobile e non dimostrano:

- consumo batteria;
- frame time;
- latenza GPS;
- accuratezza del matching;
- velocità Valhalla;
- comportamento su una rete stradale nazionale;
- affidabilità durante la guida.

## Decisioni consolidate

1. Kotlin Multiplatform per contratti e core condivisibili.
2. UI native future per Android e iOS.
3. Java per reference implementation e interoperabilità didattica.
4. Rust per core deterministici soltanto quando benchmark e confine lo
   giustificano.
5. Provider dietro porte Travel DNA.
6. Fake e testkit separati dai contratti.
7. Navigatore esterno come modalità di prima classe.
8. Local-first come direzione dei dati mobili.
9. Nessun microservizio prematuro.
10. Nessuna riscrittura di librerie prima di prove e shadow comparison.
11. Una sola PR aperta e merge soltanto dopo due round puliti sullo stesso SHA.
12. Gradle Wrapper committato come entry point del build.

## Cosa non è stato costruito

La milestone non contiene:

- app Android;
- app iOS;
- Jetpack Compose o SwiftUI;
- Android Auto o CarPlay;
- MapLibre reale;
- Valhalla reale;
- Ferrostar reale;
- Waze/Google Maps/Sygic adapter;
- GPS di dispositivo;
- filtro di posizione reale;
- map matching geometrico o probabilistico;
- soglie off-route di produzione;
- routing nazionale;
- traffico live;
- backend;
- account;
- chat;
- diario automatico;
- fotografie e Pagina del giorno;
- presenza o Cartoline DNA;
- offline region;
- LoRa;
- contenuti Touring licenziati.

Questa distinzione impedisce di chiamare “MVP” una fondazione tecnica.

## Debito tecnico esplicito

### Build e supply chain

- dependency verification Gradle/Maven;
- dependency locking completo;
- SBOM;
- attestazioni firmate;
- verifica Windows del Wrapper;
- policy di aggiornamento automatizzata ma reviewata.

### Architettura

- controllo del grafo Gradle oltre al source scanner;
- serializzazione/versionamento dei contratti;
- compatibility test binari/pubblici;
- composition root mobile.

### Navigation

- filtro GPS;
- matcher reale;
- distanze cumulative ed ETA;
- voice prompt state;
- runtime actor completo;
- threshold tuning con replay realistici;
- field audit.

### Prodotto

- validazione UX;
- ricerca utenti;
- contenuti turistici;
- moderation e abuso;
- consenso e retention reali.

## Decisione successiva

Dopo la chiusura della fondazione, la scelta che modifica davvero toolchain e
ordine di prodotto è la prima shell mobile:

```text
Android-first
oppure
iOS-first
oppure
doppia shell minima
```

La raccomandazione architetturale corrente resta Android-first perché riutilizza
immediatamente Kotlin/Java e permette il bootstrap iniziale su CI Linux, ma la
scelta appartiene al product owner.

Nessuna nuova PR mobile deve essere aperta prima che:

```text
PR #24 merged
-> issue #23 closed
-> main verificato
-> nessuna PR aperta
-> decisione piattaforma registrata
```

## Conclusione

Foundations v0 ha trasformato una conversazione di prodotto in un repository con:

```text
regole
contratti
algoritmi
state machine
fixture
fake
testkit
CI
benchmark
capitoli
Lab
report
```

Il risultato non è ancora Travel DNA installabile. È una base che rende il
prossimo codice mobile più controllabile, sostituibile e insegnabile.
