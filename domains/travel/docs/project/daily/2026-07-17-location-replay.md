# Report finale — 17 luglio 2026 — LocationSample e replay deterministico

## Esito

La quarta vertical slice della fondazione è stata completata e mergiata.

- issue: [#11](https://github.com/kinderp/tdna/issues/11), chiusa;
- PR: [#16](https://github.com/kinderp/tdna/pull/16), merged;
- base: `76680433089842db5805d28eb50416a23c7d0a88`;
- final substantive head: `97b15bf8accf440118d20eb8e4848c26dc61cb50`;
- Foundation CI: run
  [#100](https://github.com/kinderp/tdna/actions/runs/29577722826), verde;
- review round 1: `4722346834`, clean;
- review round 2: `4722349391`, clean sullo stesso SHA;
- merge: `020f8495f7fbbae81f1463b098b0ddd2a079c873`.

## Obiettivo raggiunto

```text
fixture sintetica
-> GeoPoint cross-domain
-> LocationSample
-> ordering gate
-> clock virtuale
-> rate razionale
-> replay state machine
-> summary bounded
-> report deterministico
```

## Moduli

### `shared/geo-contracts`

- `GeoPoint` WGS84;
- zero firmato normalizzato;
- uguaglianza/hash stabili;
- alias temporaneo per il routing.

### `shared/location-contracts`

- `MonotonicInstant`;
- `LocationSequence`;
- `LocationSampleOrigin`;
- `LocationSample` bounded;
- gate con ispezione non mutante e commit esplicito.

### `shared/location-replay`

- `PlaybackRate` razionale;
- scenario immutabile e bounded;
- clock virtuale;
- delay scaler con preview/commit e resto;
- state machine `Ready/Running/Paused/Completed/Cancelled`;
- transizione accepted atomica;
- summary senza event history crescente.

### CLI e fixture

- formato `TDNA_LOCATION_REPLAY_V0`;
- parser JVM rigoroso;
- sei campioni sintetici e due rifiuti intenzionali;
- report JSON esatto;
- benchmark diagnostico;
- artifact CI conservato per 14 giorni.

## Decisioni

1. Il runtime usa tempo monotono; il tempo civile resta a diario/UI.
2. Sequence e timestamp crescono entrambi.
3. Il primo accepted stabilisce la baseline con delay zero.
4. Il replay non ordina o corregge input stantii.
5. Il rate è una frazione intera con resto conservato.
6. Parser e file I/O restano fuori dal common hot path.
7. Una transizione viene verificata interamente prima di mutare stato.
8. Il benchmark è una osservazione diagnostica, non un gate.

## Finding risolti

1. contatori pubblici non bounded;
2. aspettative fixture oltre il massimo dello scenario;
3. benchmark non conservato;
4. mediana ambigua con un numero pari di run;
5. summary processato senza accepted baseline;
6. overflow che poteva mutare parzialmente gate, clock e indice;
7. README e commenting status obsoleti;
8. indice italiano legato allo stato draft e quindi instabile dopo il merge.

Ogni finding ha azzerato il contatore; i due round finali sono stati eseguiti
soltanto sullo SHA `97b15bf8…`.

## Benchmark registrato

Evidenza tecnica:

- run [#97](https://github.com/kinderp/tdna/actions/runs/29577113153);
- head `2b27a731e98c0456e2532ef3ebfc850523ab0ef4`;
- artifact `8405564857`;
- digest `sha256:f9fa90fdbce6631c5f1f473f9d26768c16d737ccabcc01249b4a0390071bda80`.

```json
{"benchmark":"location-replay-v0","samples":10000,"warmups":3,"iterations":7,"min_elapsed_ns":400325,"median_elapsed_ns":2973838,"max_elapsed_ns":11811276,"median_ns_per_sample":297.38}
```

Ambiente: Ubuntu 24.04, Java 21, Gradle 9.5.1, Kotlin 2.4.0.

Il dato non include parser, GPS, rete, database, map matching, route progress,
MapLibre, Android/iOS o batteria.

## Documentazione

- [Capitolo 46](../../it/46-location-sample-e-replay-deterministico.md)
- [Scenario Lab](../../it/lab/scenarios/location-replay-deterministico.md)
- [Tracepoint Model](../../it/41-tracepoint-model-v0.md)
- [Mappa del codice](../../it/40-mappa-codice-e-stati.md)

## Debito rimandato

- adapter Android/iOS;
- GPS reale;
- filtro e map matcher;
- route progress, poi realizzato nella slice successiva;
- off-route/reroute;
- Gradle Wrapper;
- benchmark dispositivo/batteria.

Nessuna decisione del maintainer rimane aperta per questa slice.
