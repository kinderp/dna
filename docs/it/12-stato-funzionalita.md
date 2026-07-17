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
| Canonical `GeoPoint`/`RouteRequest`/`RoutePlan` v0 | executable lab | Contratti KMP provider-neutral, non API pubblica congelata. |
| `RoutePlannerPort` | executable lab | Porta `suspend` indipendente dai provider. |
| Fake route planner | executable lab | Catalogo deterministico, call recording e `NoRoute`. |
| Route-planner testkit | executable lab | Probe riutilizzabile per provider controllati. |
| Fixture route sintetica v0 | executable lab | Formato versionato, metadati e ground truth. |
| Dijkstra/A* Java | executable lab | Reference implementation Java 21. |
| Dijkstra/A* Rust | executable lab | Implementazione indipendente standard-library. |
| Report cross-language | executable lab | Java/Rust confrontati byte-per-byte. |
| Tooling foundation | executable lab | Documentazione, architettura, Java, Rust e KMP. |
| Gradle/KMP bootstrap | executable lab | JVM/Linux CI; wrapper locale ancora mancante. |
| Fake map renderer/MapScene | prototype planned | Prossima slice. |
| `LocationSample` e clock virtuale | prototype planned | Prima del replay GPS. |
| GPS replay | prototype planned | Non confondere con la fixture di grafo. |
| MapLibre adapter | prototype planned | Dopo `MapScene` e fake renderer. |
| Valhalla adapter | prototype planned | Dietro `RoutePlannerPort`/gateway. |
| Ferrostar adapter | prototype planned | Dietro navigation runtime contracts. |
| Navigatore esterno | prototype planned | Deep link e sessione Companion. |
| Percorso ombra | prototype planned | Confidenza esplicita e degradazione. |
| Diario automatico | prototype planned | Fixture/replay prima del GPS reale. |
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

### Algoritmo

```text
fixture sintetica
-> Java/Rust parser
-> Dijkstra/A*
-> percorso
-> report identico
```

### Contratto e provider

```text
RouteRequest
-> RoutePlannerPort
-> FakeRoutePlanner
-> RoutePlan canonico
-> conformance probe
-> JVM/Linux tests
```

Non è ancora dimostrato:

- routing OpenStreetMap;
- route reali utilizzabili da un veicolo;
- prestazioni su grafi grandi;
- app Android/iOS;
- rendering cartografico;
- GPS, map matching, guidance o ricalcolo;
- affidabilità su strada.

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
