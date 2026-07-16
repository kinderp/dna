# Stato delle funzionalità

Questo documento impedisce di confondere visione, decisione architetturale,
prototipo didattico e software di prodotto già funzionante.

## Legenda

- `documented`: comportamento e contratto descritti, nessun codice;
- `executable lab`: fixture, codice e test eseguibili, ma non capacità di prodotto;
- `prototype planned`: previsto nella milestone tecnica indicata;
- `future`: direzione approvata ma non pianificata ora;
- `open`: richiede discussione o spike;
- `non-goal`: escluso dal perimetro corrente.

## Stato corrente

| Funzionalità | Stato | Nota |
| --- | --- | --- |
| Visione Travel DNA | documented | Diario, guida, navigazione e socialità. |
| Modello DDD | documented | Bounded context iniziali. |
| Contratti plugin | documented | Nessuna implementazione. |
| Fixture route sintetica v0 | executable lab | Formato versionato, metadati e ground truth. |
| Dijkstra Java | executable lab | Reference implementation Java 21, non produzione. |
| A* Java | executable lab | Euristica WGS84 ammissibile sulla fixture v0. |
| Dijkstra Rust | executable lab | Implementazione standard-library; verifica affidata anche alla CI. |
| A* Rust | executable lab | Seconda implementazione indipendente dello stesso contratto Lab. |
| Report cross-language | executable lab | Java/Rust confrontati byte-per-byte. |
| Tooling foundation | executable lab | `sh tools/tdna` per doctor, test, Lab e documentazione. |
| Canonical `GeoPoint`/`RoutePlan` di produzione | prototype planned | I tipi del Lab sono esplicitamente fixture-scoped. |
| Plugin descriptor/capability | prototype planned | Prossimo micro-step della milestone 1. |
| Fake route planner/map renderer | prototype planned | Dopo i contratti canonici. |
| GPS replay con clock virtuale | prototype planned | Non confondere con la fixture di grafo corrente. |
| MapLibre adapter | prototype planned | Dopo modelli canonici e fake renderer. |
| Valhalla adapter | prototype planned | Via routing gateway, non chiamata diretta dal dominio. |
| Ferrostar adapter | prototype planned | Incapsulato dietro i contratti Travel DNA. |
| Navigatore esterno | prototype planned | Deep link e sessione Companion. |
| Percorso ombra | prototype planned | Prima versione senza promesse di precisione forte. |
| Diario automatico | prototype planned | Fixture e replay prima del GPS reale. |
| Pagina del giorno | prototype planned | Foto e pensieri locali. |
| Chat reale | future | Prima fake/in-memory e policy di guida. |
| Android Auto messaging | future | Dopo chat mobile stabile. |
| CarPlay messaging/widget | future | Richiede capability e review Apple. |
| Road presence | future | Richiede backend e threat model. |
| Cartolina DNA | future | Dopo diario e privacy model. |
| Mappe offline | future | Richiede provider/licenza/distribuzione. |
| Routing offline | future | Valhalla locale o altro provider da valutare. |
| Guidance core Rust proprio | future | Dopo replay e shadow comparison. |
| Traffico crowdsourced | future | Richiede massa critica e dati. |
| Contenuti Touring | open | Solo partnership/licenza. |
| LoRa/LoRaWAN | open | Spike dedicato, nessuna decisione. |
| Profili minori | non-goal | Fuori MVP. |
| Verticali shopping/study | non-goal | Fuori Travel DNA iniziale. |

## Cosa è dimostrato oggi

Il repository dimostra soltanto il seguente percorso:

```text
fixture sintetica
-> parser rigoroso
-> grafo in memoria
-> Dijkstra o A*
-> percorso ricostruito
-> report deterministico
-> confronto Java/Rust
```

Non dimostra ancora:

- routing OpenStreetMap;
- route reali utilizzabili da un veicolo;
- prestazioni su grafi grandi;
- app Android o iOS;
- GPS, map matching, manovre o ricalcolo;
- affidabilità su strada.

## Regola di comunicazione

Non dire “Travel DNA supporta” una capacità finché non esistono:

- implementazione;
- test;
- documentazione corrente;
- criteri di degraded mode;
- review privacy/performance quando necessaria.

Usare formulazioni come:

```text
è documentato
è un laboratorio eseguibile
è pianificato
è in prototipo
è sperimentale
è disponibile
```

con significato esplicito.
