# Stack, linguaggi e progettazione della GUI

## Obiettivo

Scegliere tecnologie che consentano prestazioni, accesso nativo alle piattaforme,
apprendimento e sostituibilità. Nessun linguaggio viene introdotto solo per moda.

## Baseline proposta

| Area | Tecnologia | Motivo |
| --- | --- | --- |
| Android | Kotlin + Jetpack Compose | Ecosistema nativo moderno e Android Auto. |
| iOS | Swift + SwiftUI/UIKit | Lifecycle, CarPlay e integrazioni Apple. |
| Shared application logic | Kotlin Multiplatform | Condivisione selettiva, non UI critica. |
| Map rendering | MapLibre Native | Renderer GPU open source. |
| Guidance iniziale | Ferrostar adapter | Core Rust e binding mobili. |
| Guidance futura | Rust `tdna-guidance` | Determinismo e performance. |
| Java didattico | Reference router/backend modules | On-ramp e confronto algoritmico. |
| Local DB | SQLite con layer tipizzato | Offline e reattività. |
| Backend | JVM modular monolith, decisione finale da confermare | Ecosistema e didattica. |

## Perché UI native

La schermata di navigazione interagisce con:

- map view nativa;
- lifecycle;
- background location;
- audio focus;
- Android Auto/CarPlay;
- accessibilità;
- GPU e frame timing.

Un layer UI cross-platform completo aggiungerebbe un confine in più nel percorso
critico. Non è necessariamente lento, ma rende più difficile diagnosticare e
ottimizzare.

Decisione:

```text
condividere dominio, use case e presentation model
non condividere obbligatoriamente il rendering critico
```

## Kotlin Multiplatform

Condivide:

- modelli canonici;
- use case;
- state machine;
- sincronizzazione;
- plugin contracts;
- policy;
- presentation models.

Mantiene `expect/actual` o adapter per:

- clock;
- storage;
- location;
- notifications;
- secure storage;
- media;
- connectivity.

Rischi:

- bridge Swift/Kotlin;
- API esportate non sempre idiomatiche;
- debugging multi-runtime;
- build complexity;
- tentazione di mettere troppa UI nel shared.

Mitigazioni:

- API strette;
- snapshot compatti;
- niente flusso a 60 fps attraverso KMP;
- benchmark dei bridge;
- wrapper Swift dove serve.

## Android: Compose + MapView

Struttura:

```text
Compose HUD and panels
  over
persistent native MapLibre MapView
```

Regole:

- modelli stabili e piccoli;
- componenti osservano solo stato necessario;
- calcoli fuori dai composable;
- no parsing GeoJSON sul main thread;
- `MapView` non ricreata al cambio pannello;
- layer map-based, non migliaia di composable marker.

## iOS: SwiftUI + UIKit map host

SwiftUI gestisce:

- HUD;
- sheet;
- diario;
- guida;
- chat;
- impostazioni.

UIKit ospita MapLibre dove il controllo lifecycle/rendering è più diretto.

Usare state object e adapter per non ricreare la mappa quando cambia una view.

## Java

Ruoli reali:

### Reference routing

Implementazione leggibile di:

- graph;
- Dijkstra;
- A*;
- route reconstruction;
- costing semplice.

### Backend

Alternative:

- Java 21 + Spring Boot;
- Kotlin/JVM + Ktor;
- combinazione con moduli Java/Kotlin.

La decisione finale richiede spike su:

- semplicità didattica;
- WebSocket;
- modularità;
- startup/memoria;
- PostGIS;
- test;
- osservabilità;
- interoperabilità.

### Android interop

Piccoli adapter Java possono mostrare interoperabilità, ma la nuova UI non viene
duplicata in Java.

## Rust

Adatto a:

- geometria;
- map matching;
- route progress;
- off-route state machine;
- replay;
- algoritmi deterministici;
- benchmark;
- futuro routing locale selettivo.

Non adatto inizialmente a:

- CRUD account;
- UI;
- diario editoriale;
- orchestration semplice;
- codice che richiede molta integrazione piattaforma e cambia rapidamente.

## FFI

Regole:

- core Rust prima CLI e test;
- API coarse-grained;
- evitare chiamate per ogni punto di geometria;
- snapshot strutturati;
- ownership chiara;
- error enum stabile;
- nessun panic oltre il boundary;
- cancellazione definita;
- benchmark crossing overhead.

UniFFI è un candidato da valutare; non è decisione irreversibile.

## GUI di navigazione

Elementi permanenti minimi:

```text
maneuver banner
map
one travel opportunity rail
ETA/distance
chat pulse
few large actions
```

La guida non diventa un feed sovrapposto alla strada.

## Stati UI

### Drive

- pochi elementi;
- voce;
- grandi touch target;
- nessuna lista lunga;
- animazioni brevi;
- POI limitati.

### Explore/Stop

- schede ricche;
- chat completa;
- album;
- editor diario;
- itinerari;
- mappa sociale dettagliata ma sempre approssimata.

## Stato granulare

Evitare:

```text
OneHugeScreenState(map, navigation, users, messages, photos, diary)
```

Preferire:

```text
NavigationHudState
TravelEstimateState
MapSceneDelta
SocialOverlayState
ChatAlert event
DiaryRecordingState
```

## Reattività percepita

- feedback immediato al tocco;
- optimistic local update quando sicuro;
- skeleton solo dove serve;
- nessun blocco UI per server;
- preload controllato;
- transizioni preservano contesto;
- messaggio queued visibile;
- mappa già pronta quando si apre il viaggio attivo.

## Alternative valutate

### Flutter

Pro:

- UI condivisa;
- tooling produttivo;
- buone performance in molti scenari.

Contro per Travel DNA:

- native map/platform view boundary;
- automotive e lifecycle specifici;
- bridge verso Rust/SDK;
- meno controllo diretto del percorso critico.

### React Native

Pro:

- ecosistema JS;
- UI condivisa;
- sviluppo rapido.

Contro:

- bridge/new architecture da gestire;
- mappe e navigation SDK nativi;
- complessità di dipendenze;
- non allineato all'obiettivo didattico Java/Kotlin/Rust.

### Compose Multiplatform UI

Interessante e da rivalutare. Per v0 non è la base della schermata di navigazione
per ridurre rischio e mantenere UI iOS idiomatica.

## Accessibilità

- contrasto;
- dynamic type;
- screen reader;
- non dipendere solo dal colore;
- istruzioni vocali;
- motion reduction;
- pulsanti grandi;
- localizzazione;
- test con rumore e luce reale.

## Misure

- startup;
- frame time;
- recomposition/update count;
- map render CPU/GPU;
- snapshot latency;
- memory growth;
- bridge cost;
- battery.
