# Debugging e strumenti

Questo capitolo verrà aggiornato con comandi reali dopo il bootstrap del codice.
Definisce fin da ora la cassetta degli attrezzi.

## Wrapper progetto

Obiettivo:

```text
./tdna doctor
./tdna build
./tdna test
./tdna replay
./tdna bench
./tdna docs
```

`doctor` verifica Java, Android SDK, Rust, Xcode su macOS, Docker e tool opzionali.
Non installa automaticamente tool di sistema privilegiati.

## JVM/Kotlin/Java

- Gradle build scans;
- JUnit;
- Kotest candidato;
- property testing;
- async profiler;
- Java Flight Recorder;
- heap dump;
- dependency analysis;
- Detekt/ktlint;
- Error Prone/SpotBugs per Java dove utile.

## Rust

- `cargo test`;
- `cargo clippy`;
- `cargo fmt`;
- `cargo bench`/Criterion candidato;
- Miri per subset;
- sanitizers dove supportati;
- flamegraph;
- `cargo deny`/audit;
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
- battery historian/profiler;
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
- API fixtures;
- graph tile build logs;
- route/matching debug output;
- Gurka integration tests upstream;
- gateway timing.

## Backend

- Docker Compose locale;
- PostgreSQL/PostGIS query plan;
- structured logs;
- OpenTelemetry candidato;
- WebSocket trace;
- push fake;
- object storage emulator;
- migration test.

## Network fault injection

Simulare:

- offline;
- latency;
- packet loss;
- captive portal;
- provider 429;
- timeout;
- partial response;
- WebSocket reconnect.

## Location simulation

- Android emulator location;
- Xcode GPX routes;
- Travel DNA virtual replay;
- device mock location in test builds;
- deterministic clock.

Il replay interno è la fonte principale per test algoritmici; emulatori verificano
integrazione OS.

## Logging policy

Livelli:

```text
ERROR WARN INFO DEBUG TRACE
```

TRACE disabilitato in produzione salvo sessioni controllate. Nessun payload
personale.

Ogni log include:

- component;
- event code;
- correlation ID;
- redacted context;
- error category;
- no free-form secrets.

## Crash diagnostics

- breadcrumb non sensibili;
- provider/capability;
- route ID interno non posizione;
- app/OS version;
- state machine state;
- memory pressure;
- consent-safe.

## Code browsing

Come Alfred, valutare:

- IDE navigation;
- Sourcegraph/Sourcebot;
- rustdoc/Dokka/DocC;
- generated dependency graph;
- call graph mirati;
- C4/Mermaid;
- semantic indexes.

Gli strumenti orientano; il sorgente e i test restano fonte di verità.

## Debug playbook

1. riprodurre;
2. ridurre a fixture;
3. identificare stage logico;
4. confrontare contratto;
5. osservare stato e dati;
6. aggiungere test regressione;
7. correggere;
8. misurare;
9. aggiornare doc.

## Bug navigation

Non correggere soglie a caso sulla singola traccia. Chiedere:

- sample quality;
- map match;
- route geometry;
- state transition;
- clock;
- provider data;
- UI interpolation.

## Artifact

Su failure CI conservare selettivamente:

- test report;
- redacted trace;
- replay output;
- screenshot simulator;
- performance summary;
- crash stack.

Mai upload automatico di dati personali reali.
