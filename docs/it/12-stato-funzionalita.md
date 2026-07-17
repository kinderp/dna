# Stato delle funzionalità

Questo documento impedisce di confondere visione, decisione architetturale,
laboratorio eseguibile e software di prodotto già funzionante.

## Legenda

- `documented`: comportamento descritto, nessun codice;
- `executable lab`: codice, fixture/test e comando eseguibili, ma non capacità di prodotto;
- `prototype planned`: previsto nella milestone tecnica;
- `future`: direzione approvata ma non pianificata ora;
- `open`: richiede discussione o spike;
- `non-goal`: escluso dal perimetro corrente.

## Stato corrente

| Funzionalità | Stato | Nota |
| --- | --- | --- |
| Visione Travel DNA | documented | Diario, guida, navigazione e socialità. |
| Modello DDD | documented | Bounded context iniziali. |
| Plugin SDK v0 | executable lab | ID, capability, runtime platform, descriptor e notice. |
| Canonical geo/routing contracts v0 | executable lab | `GeoPoint`, request, plan, legs, maneuvers e provenance KMP. |
| `RoutePlannerPort` e fake planner | executable lab | Porta provider-neutral, catalogo deterministico e conformance probe. |
| Fixture route sintetica v0 | executable lab | Formato versionato, metadati e ground truth. |
| Dijkstra/A* Java e Rust | executable lab | Due implementazioni indipendenti confrontate byte-per-byte. |
| `MapScene` e `MapRendererPort` | executable lab | Scena statica, delta bounded, capability e error model. |
| Fake map renderer | executable lab | Stato semantico, snapshot, call history e conformance probe. |
| `LocationSample` e ordering gate | executable lab | Sequence e tempo monotono strettamente crescenti. |
| Clock virtuale e replay deterministico | executable lab | Rate razionale, pause/step/resume/cancel e stato bounded. |
| Fixture location replay v0 | executable lab | Sei campioni sintetici, due rifiuti e ground truth. |
| Benchmark replay diagnostico | executable lab | JVM CI, nessuna soglia e nessuna pretesa mobile/stradale. |
| Tooling foundation | executable lab | Documentazione, architettura, Java, Rust, KMP e Lab. |
| Gradle/KMP bootstrap | executable lab | JVM/Linux CI; wrapper locale ancora mancante. |
| MapLibre adapter | prototype planned | Dopo contratti/fake renderer e benchmark su dispositivo. |
| Valhalla adapter | prototype planned | Dietro `RoutePlannerPort`/gateway. |
| Ferrostar adapter | prototype planned | Dietro navigation runtime contracts. |
| Map matching e route progress | prototype planned | Prossima fase dopo il sample replay. |
| Off-route e rerouting | prototype planned | Scenario missed-exit deterministico. |
| Navigatore esterno | prototype planned | Deep link e sessione Companion. |
| Percorso ombra | prototype planned | Confidenza esplicita e degradazione. |
| Diario automatico | prototype planned | Event store e stop detection dopo le fondazioni. |
| Pagina del giorno | prototype planned | Foto e pensieri locali. |
| Chat reale | future | Prima core e policy di guida. |
| Android Auto messaging | future | Dopo chat mobile stabile. |
| CarPlay messaging/widget | future | Richiede capability e review Apple. |
| Road presence | future | Richiede backend e threat model. |
| Cartolina DNA | future | Dopo diario e privacy model. |
| Mappe/routing offline | future | Provider, licenza e distribuzione da progettare. |
| Guidance core Rust proprio | future | Dopo replay e shadow comparison. |
| Traffico crowdsourced | future | Richiede massa critica e dati. |
| Contenuti Touring | open | Solo partnership/licenza. |
| LoRa/LoRaWAN | open | Spike dedicato. |
| Profili minori | non-goal | Fuori MVP. |
| Verticali shopping/study | non-goal | Fuori Travel DNA iniziale. |

## Cosa è dimostrato oggi

### Algoritmo di routing

```text
fixture sintetica
-> Java/Rust parser
-> Dijkstra/A*
-> percorso
-> report identico
```

### Contratti e provider

```text
RouteRequest
-> RoutePlannerPort
-> FakeRoutePlanner
-> RoutePlan
-> conformance probe
```

### Rendering semantico

```text
RoutePlan
-> RouteOverlay
-> MapScene
-> MapSceneDelta
-> FakeMapRenderer
-> snapshot
```

### Posizione e replay

```text
fixture sintetica
-> ReplayFixtureParser
-> LocationSample
-> ordering gate
-> virtual clock/rate scaler
-> ReplaySummary
-> report deterministico
```

Non è ancora dimostrato:

- routing OpenStreetMap;
- route reali utilizzabili da un veicolo;
- adapter Android/iOS;
- MapLibre reale;
- GPS reale;
- map matching, guidance o ricalcolo;
- batteria e affidabilità su strada.

## Regola di comunicazione

Non dire “Travel DNA supporta” una capacità finché non esistono implementazione,
test, documentazione corrente, degraded mode e review privacy/performance quando
necessaria.

Usare formulazioni precise:

```text
è documentato
è un laboratorio eseguibile
è pianificato
è in prototipo
è sperimentale
è disponibile
```
