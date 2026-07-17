# Debugging e strumenti

Questo capitolo separa gli strumenti già disponibili da quelli previsti per le
future app mobili. La prima regola di debugging è non usare un tool più complesso
del problema che si sta osservando.

## Entry point corrente

Il repository espone un wrapper POSIX sottile:

```bash
sh tools/tdna COMMAND
```

Comandi disponibili:

```text
doctor
check-docs
check-java
check-rust
check-contract
check
lab reference-routing [dijkstra|astar]
clean
```

Esempio di bootstrap:

```bash
sh tools/tdna doctor
sh tools/tdna check-docs
sh tools/tdna check-java
sh tools/tdna lab reference-routing astar
```

Con Rust stable installato:

```bash
sh tools/tdna check-rust
sh tools/tdna check-contract
sh tools/tdna check
```

Il wrapper viene invocato tramite `sh` per non dipendere dal bit eseguibile,
che può essere perso da archivi ZIP o operazioni GitHub sui contenuti.

## Che cosa fa `doctor`

`doctor` mostra la prima riga della versione di:

- `java`;
- `javac`;
- `python3`;
- `cargo`;
- `rustc`.

Non installa nulla e non modifica il sistema. Un componente mancante è riportato
come `MISSING`. I singoli comandi falliscono soltanto quando hanno davvero
bisogno di quel componente.

Esempio: è possibile eseguire `check-java` senza Rust, ma `check` completo deve
fallire finché `cargo` e `rustc` non sono disponibili.

## Debug del primo laboratorio Java

Compilazione e test normali:

```bash
sh tools/tdna check-java
```

Il wrapper:

1. raccoglie i sorgenti Java;
2. compila con Java 21;
3. abilita `-Xlint:all`;
4. trasforma i warning in errori con `-Werror`;
5. esegue `ReferenceRoutingTestSuite`;
6. salva i report sotto `build/java/reference-routing/`.

Per eseguire un singolo algoritmo:

```bash
sh tools/tdna lab reference-routing dijkstra
sh tools/tdna lab reference-routing astar
```

Per un debug manuale dopo la compilazione:

```bash
java -cp build/java/reference-routing/classes \
  org.traveldna.reference.routing.ReferenceRoutingCli \
  fixtures/routes/reference-network-v0.tdna \
  astar
```

Punti utili per un breakpoint IDE:

```text
ReferenceFixtureParser.parse()
RoadGraph.addBidirectionalRoad()
AbstractBestFirstRouter.route()
AbstractBestFirstRouter.reconstructPath()
RouteReport.canonicalLine()
```

## Debug del primo laboratorio Rust

Verifica normale:

```bash
sh tools/tdna check-rust
```

Il comando esegue:

```text
cargo fmt --check
cargo test
```

con `CARGO_TARGET_DIR` diretto sotto `build/rust`, così gli artifact restano
raccolti nella directory di build del progetto.

Esecuzione manuale:

```bash
CARGO_TARGET_DIR=build/rust cargo run --quiet \
  --manifest-path crates/tdna-reference-routing/Cargo.toml -- \
  fixtures/routes/reference-network-v0.tdna astar
```

Punti da seguire:

```text
parse_fixture()
RoadGraph::add_bidirectional_road()
route()
heuristic_metres()
reconstruct_path()
canonical_report()
```

La crate vieta `unsafe` e usa solo la standard library nella slice corrente.

## Confronto Java/Rust

```bash
sh tools/tdna check-contract
```

Il controllo esegue entrambi gli algoritmi in entrambi i linguaggi e applica
`diff -u` ai report. Gli artifact sono salvati sotto:

```text
build/contract/
```

Quando il confronto fallisce, non correggere subito l'output. Distinguere:

- algoritmo diverso;
- tie-break diverso;
- parser diverso;
- ground truth incoerente;
- serializzazione diversa;
- newline o ordine campi;
- errore reale nella fixture.

Il byte comparison è appropriato per il piccolo report v0. Motori di routing
reali richiederanno confronti semantici più ricchi.

## Controllo documentazione

```bash
sh tools/tdna check-docs
```

`tools/check_docs.py` controlla:

- link Markdown locali;
- link che escono accidentalmente dal repository;
- code fence non bilanciate.

Non effettua richieste di rete e non valida ancora anchor interni o URL esterni.

## Artifact correnti

Tutti gli output generati vivono sotto `build/`:

```text
build/java/reference-routing/
build/rust/
build/contract/
```

Per pulire:

```bash
sh tools/tdna clean
```

La CI carica `build/` come artifact solo in caso di fallimento.

## JVM/Kotlin/Java futuri

Quando verrà introdotto il build JVM/KMP, valutare:

- Gradle build scans;
- JUnit;
- Kotest/property testing dove utile;
- Java Flight Recorder;
- heap dump;
- dependency analysis;
- Detekt/ktlint;
- Error Prone o SpotBugs per Java.

L'attuale test runner Java senza dipendenze è un bootstrap, non un rifiuto di
JUnit.

## Rust futuro

Oltre a `cargo test` e `cargo fmt`:

- `cargo clippy` con policy graduale;
- benchmark riproducibili;
- Miri per subset compatibili;
- sanitizer dove supportati;
- flamegraph;
- `cargo audit`/`cargo deny` dopo la decisione supply-chain;
- rustdoc.

## Android

- Android Studio profiler;
- Perfetto;
- Macrobenchmark;
- Baseline Profiles;
- Layout Inspector;
- Compose recomposition tools;
- logcat con redaction;
- Network Inspector;
- battery profiler;
- Desktop Head Unit Android Auto.

## iOS

- Xcode debugger;
- Instruments Time Profiler;
- Allocations/Leaks;
- Core Animation;
- Energy Log;
- Network;
- MetricKit;
- CarPlay simulator;
- View hierarchy debugger.

## MapLibre

- render statistics;
- source/layer inspection;
- tile request logging redatto;
- style validation;
- Tracy quando applicabile al core;
- offline cache inspection.

## Valhalla

- one-shot requests;
- API fixture;
- graph tile build log;
- route/matching debug output;
- Gurka integration test upstream;
- gateway timing.

## Backend

- Docker Compose locale;
- PostgreSQL/PostGIS query plan;
- log strutturati;
- WebSocket trace;
- push fake;
- object storage emulator;
- migration test.

## Network fault injection

Simulare:

- offline;
- latenza;
- packet loss;
- captive portal;
- provider `429`;
- timeout;
- risposta parziale;
- WebSocket reconnect.

## Location simulation

- Android emulator location;
- Xcode GPX routes;
- Travel DNA virtual replay;
- device mock location in build di test;
- clock deterministico.

Il replay interno sarà la fonte principale per test algoritmici; emulatori e
dispositivi verificheranno l'integrazione con il sistema operativo.

## Logging policy

Livelli:

```text
ERROR WARN INFO DEBUG TRACE
```

`TRACE` resta disabilitato in produzione salvo sessioni controllate. Nessun
payload personale deve entrare nei log per comodità di debug.

Ogni log strutturato futuro dovrebbe includere:

- component;
- event code;
- correlation ID;
- contesto redatto;
- categoria di errore;
- nessun segreto libero.

## Crash diagnostics

- breadcrumb non sensibili;
- provider/capability;
- route ID interno, non coordinate;
- versione app/OS;
- stato della state machine;
- memory pressure;
- dati compatibili con il consenso.

## Code browsing

Strumenti candidati:

- navigazione IDE;
- Sourcegraph/Sourcebot;
- rustdoc, Dokka e DocC;
- dependency graph generati;
- call graph mirati;
- C4/Mermaid;
- indici semantici.

Gli strumenti orientano; sorgente, fixture e test restano la fonte di verità.

## Debug playbook

1. riprodurre;
2. ridurre a fixture;
3. identificare lo stage logico;
4. confrontare il contratto;
5. osservare stato e dati;
6. aggiungere un test di regressione;
7. correggere;
8. misurare quando il percorso è prestazionale;
9. aggiornare la documentazione.

## Bug di navigazione futuri

Non correggere soglie a caso sulla singola traccia. Chiedere:

- qualità del campione;
- map match;
- geometria route;
- transizione di stato;
- clock;
- dati provider;
- interpolazione UI.

## Regola sugli artifact

Su failure CI conservare selettivamente:

- report test;
- trace redatto;
- output replay;
- screenshot simulatore;
- performance summary;
- crash stack.

Non caricare automaticamente dati personali reali.
