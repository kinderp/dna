# Matrice delle tecnologie e delle decisioni

## Come leggere

- `Accepted baseline`: scelta iniziale, riesaminabile con ADR.
- `Proposed`: preferita ma richiede spike.
- `Alternative`: documentata, non scelta ora.
- `Future`: possibile dopo evidenza.

## Rendering mappe

### MapLibre Native — Accepted baseline

**Fa:** rendering vettoriale nativo, stili, layer, interazione.

**Pro:**

- open source BSD;
- GPU;
- Android/iOS;
- stile controllabile;
- layer sociali e turistici;
- niente lock-in di un unico tile provider.

**Contro:**

- non offre routing/search/traffic;
- integrazione nativa per piattaforma;
- tile/style infrastructure nostra;
- API e lifecycle da gestire;
- SwiftUI richiede wrapper.

**Mitigazione:** `MapSceneRendererPort`, map host persistente, contract test.

### Google Maps SDK — Alternative

**Pro:** ecosistema, copertura, supporto.
**Contro:** termini/costi, stile e dati controllati, lock-in, integrazione OSM e
offline limitata secondo prodotto.

### MapKit — Alternative iOS

Ottima integrazione Apple ma non cross-platform e non allineata a stile OSM unico.

## Routing

### Valhalla — Accepted baseline

**Pro:** OSM, open source MIT, route/matrix/isochrone/map matching, dynamic
costing, self-hosting, manovre.

**Contro:** graph tile build, operations, config, traffic esterno, mapping API.

### OSRM — Alternative

**Pro:** molto veloce, maturo per auto, route/table/match.
**Contro:** personalizzazione turistica e multi-modalità meno allineate al disegno
iniziale; va valutato con benchmark.

### GraphHopper — Alternative

**Pro:** Java, custom models, self-hosting, buon valore didattico.
**Contro:** verificare funzionalità/licenze dell'offerta e integrazione mobile.

### Google Routes/Navigation — Commercial alternative

**Pro:** dati/traffico e integrazione.
**Contro:** costo, termini, lock-in, caching/display rules.

## Guidance

### Ferrostar — Accepted prototype baseline

**Pro:** Rust core, Kotlin/Swift, vendor-neutral, UI componibile, voice.

**Contro:** beta, API non stabile 1.0, non routing/basemap/search.

**Mitigazione:** adapter e replay.

### Guidance Travel DNA Rust — Future

Proprietà e controllo; costo alto e rischio safety. Solo shadow dopo maturità.

### SDK commerciali integrati — Alternative

Google Navigation SDK, Sygic. Affidabilità e feature vs costo/lock-in.

## Mobile architecture

### KMP shared logic + native UI — Accepted baseline

**Pro:** condivisione selettiva, accesso nativo, UI idiomatica, test dominio.

**Contro:** bridge Swift, build e tooling, disciplina sui confini.

### Flutter — Alternative

Buona UI condivisa, ma platform view/map/automotive/FFI aggiungono complessità al
percorso critico scelto.

### React Native — Alternative

Ecosistema e velocità UI business; meno allineato a Kotlin/Java/Rust e SDK nativi.

### Compose Multiplatform UI — Future reassessment

Può maturare; non scelta per navigation screen v0.

## Android UI

### Jetpack Compose — Accepted baseline

Pro: moderno, state-driven, automotive ecosystem Kotlin.
Contro: recomposition e startup da misurare; MapView interop.

## iOS UI

### SwiftUI + UIKit map host — Accepted baseline

Pro: moderno, nativo, CarPlay/ActivityKit.
Contro: doppio paradigma UI, wrapper lifecycle.

## Rust integration

### UniFFI — Proposed

Pro: binding Kotlin/Swift, riduce JNI/manual boilerplate.
Contro: schema/API constraints, copie, debugging, release coordination.

Alternative:

- JNI + C ABI;
- Swift C FFI;
- cbindgen;
- Diplomat;
- generated custom bindings.

Decisione dopo core CLI e benchmark.

## Backend

### Java 21 + Spring Boot — Proposed A

Pro: didattica Java, ecosistema, security/data/observability, virtual threads.
Contro: peso, magia, memoria, disciplina modularità.

### Kotlin/JVM + Ktor — Proposed B

Pro: leggero, coroutine, coerenza Kotlin.
Contro: meno focus Java, più assembly manuale.

### Quarkus/Micronaut — Alternatives

Possibili per footprint/startup; aumentano matrice decisionale. Valutare solo con
requisito.

## Database

### PostgreSQL + PostGIS — Accepted baseline backend

Pro: transazioni, geospatial, ecosistema.
Contro: operations e query geospaziali da progettare.

### SQLite local — Accepted baseline

Pro: offline, affidabile, piattaforme.
Contro: migrazioni, concurrency e encryption da gestire.

Layer KMP tipizzato da decidere: SQLDelight candidato.

## Networking

Ktor client è candidato KMP; alternative native o generated clients. Il contratto
API non deve dipendere dal client.

## Serialization

- JSON per debug/API iniziali;
- Protobuf/CBOR per payload ad alta frequenza solo dopo misura;
- modelli esterni versionati;
- niente serialization nel hot path solo per comunicare nello stesso processo.

## External navigation

### Waze deep links — Accepted handoff

Semplice e limitato; nessun plugin UI.

### Google Maps URLs/intents — Accepted handoff

Cross-platform e fallback web; nessun feed route di ritorno.

### Sygic — Proposed adapter

Verificare API/licenza.

## Automotive

### Android Auto MessagingStyle — Future planned

Approccio ufficiale per messaggi vocali/risposta con navigatore aperto.

### Android Auto POI — Future planned

Guida e soste.

### CarPlay SiriKit/Widget/Live Activity — Future planned

Dipende da categorie ed entitlement.

## OpenStreetMap services

### Public tile/Nominatim — Development-only constrained

Non produzione; policy severe.

### Hosted OSM provider — Proposed prototype

Accelera sviluppo; dietro adapter.

### Self-hosted tiles/search — Future

Controllo e offline; costo operativo.

## Metodo

Una tecnologia cambia stato solo con:

- problema chiaro;
- ADR;
- spike;
- test;
- performance;
- privacy/licence;
- migration/fallback.
