# Performance budget e metodo di misura

## Principio

> Una funzione non è conclusa se è corretta ma rende il navigatore lento,
> instabile o energivoro.

Le prestazioni sono un contratto separato dalla correttezza.

## Percorsi misurati

### Navigation core

```text
LocationSample -> NavigationSnapshot
```

### Rendering

```text
snapshot/delta -> visible frame
```

### Routing

```text
RouteRequest -> canonical RoutePlan
```

### Conversation

```text
incoming durable message -> car-safe surface
```

### Journal

```text
journey event -> bounded local append
```

## Budget iniziali orientativi

Questi valori sono obiettivi di sviluppo, non promesse pubbliche. Vanno calibrati
su dispositivi di riferimento.

| Indicatore | Obiettivo v0 |
| --- | ---: |
| Feedback visivo al tocco | <= 50 ms |
| Lavoro app sul main thread per frame | idealmente <= 8 ms |
| Rendering target | 60 fps dove hardware consente |
| Frame lenti in guida normale | < 1% sul device target |
| Warm open viaggio attivo | <= 1 s |
| Cold map usable | <= 2 s sul device target |
| Location to snapshot p95 | <= 100 ms |
| Snapshot to HUD p95 | <= 50 ms |
| Chat panel open | <= 150 ms warm |
| Memory growth soak | bounded/flat after warmup |
| Route geometry rebuild per sample | 0 |
| Plugin registry lookup per sample | 0 |

## Device matrix

Definire almeno:

- Android fascia media supportata;
- Android high-end;
- iPhone minimo supportato;
- iPhone recente;
- head unit/simulator per auto.

Non ottimizzare solo sul telefono del maintainer.

## Build

Misurare release/profile, non debug. Annotare:

- compiler;
- optimization;
- symbols;
- OS;
- battery/thermal;
- network;
- map style;
- cache state.

## Metriche

### Latenza

- median;
- p95;
- p99 dove sufficiente sample;
- max con cautela;
- distribuzione.

### Rendering

- frame time;
- jank;
- dropped frames;
- CPU/GPU;
- tile decoding;
- style evaluation.

### Memoria

- resident set;
- heap;
- native/GPU;
- cache;
- growth over time;
- peak route load.

### Energia

- GPS;
- screen;
- CPU;
- network wakeups;
- background;
- TTS;
- media.

### Rete

- tile bytes;
- route response;
- chat;
- sync;
- retry;
- cache hit.

## Baseline

Ogni report indica:

```text
date
commit
branch
device
OS
build
scenario
commands
runs
warmup
results
interpretation
limitations
```

## Refresh policy

Rinfrescare se cambia:

- navigation core;
- geometry representation;
- FFI;
- MapScene mapping;
- map style significativo;
- route normalization;
- DB schema nel recorder;
- location frequency;
- chat delivery;
- queue/buffer;
- compiler o SDK per confronto importante.

## Architecture must pay rent

Una nuova astrazione nel hot path dichiara:

- dipendenza rimossa;
- test reso possibile;
- costo atteso;
- copie/allocazioni;
- benchmark;
- beneficio.

Se non compra nulla di concreto, resta roadmap.

## Threading

Main thread:

- input;
- rendering commands piccoli;
- UI state application.

Fuori main:

- route parsing;
- DB;
- media;
- network;
- POI ranking;
- heavy geometry;
- serialization.

## Geometry

Regole:

- decode una volta;
- storage compatto;
- indice segmenti;
- non convertire JSON/GeoJSON per sample;
- passare index/fraction;
- semplificare per zoom in modo precomputato;
- evitare copie FFI.

## Map rendering

- persistent map;
- fixed source/layer structure;
- incremental feature updates;
- clustering;
- drive style ridotto;
- device quality profile;
- cache policy.

## Compose/SwiftUI

- state granular;
- no giant screen state;
- stable/immutable models;
- no DB/network in render;
- monitor recomposition/body evaluation;
- isolate map host.

## Rust FFI

Benchmark:

- call overhead;
- payload size;
- copy count;
- string conversion;
- error mapping;
- batch API vs fine-grained.

Preferire:

```text
advance(sample) -> compact snapshot
```

non:

```text
get every field with dozens of calls
```

## Backend performance

Separata dal mobile:

- route p95;
- WebSocket fan-out;
- presence query;
- PostGIS corridor;
- outbox sync;
- media processing;
- push queue.

## Battery budget

Da definire con field test. Obiettivo qualitativo:

- nessun wakeup inutile;
- presence frequency bassa;
- push invece di polling;
- location profile adattivo;
- media solo in carica/Wi-Fi quando appropriato;
- screen-off behavior dichiarato.

## Performance regression

La CI generica può usare smoke benchmark e soglie larghe. Gate seri richiedono
hardware controllato. Una regressione sospetta apre issue; non si corregge
abbassando silenziosamente la soglia.

## Report

Conservare numeri vecchi come storia, senza considerarli confrontabili se cambia
scenario. Il baseline corrente deve essere esplicito.
