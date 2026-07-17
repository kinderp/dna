# Scenario: LocationSample e replay deterministico

id: `lab.location.replay-deterministic.v0`

status: `executable`

scenario kind: `navigation-runtime-foundation`

## Learning goal

Seguire una fixture sintetica dalla lettura JVM fino a decisioni accepted/rejected,
clock virtuale, rate scaling e summary bounded senza GPS reale o wall-clock sleep.

## Prerequisites

- [LocationSample, tempo monotono e replay](../../46-location-sample-e-replay-deterministico.md)
- [GPS replay e fixture](../../31-gps-replay-e-fixture.md)
- [Performance budget](../../32-performance-budget.md)
- [Tracepoint Model](../../41-tracepoint-model-v0.md)

## User story

Uno studente esegue una traccia sintetica a velocità doppia. La traccia contiene
sei campioni, una sequence duplicata e un timestamp che torna indietro. Il sistema
accetta quattro campioni, rifiuta due anomalie e produce sempre lo stesso report.

## Platforms

- Kotlin common;
- test JVM e Linux x64;
- parser e CLI JVM;
- nessun SDK mobile.

## Fixture

- [`reference-location-replay-v0.tdna`](../../../../fixtures/gps/reference-location-replay-v0.tdna)
- [`reference-location-replay-v0.meta.yaml`](../../../../fixtures/gps/reference-location-replay-v0.meta.yaml)

La fixture è sintetica e pubblicabile. Non rappresenta un viaggio reale.

## Trigger

```bash
sh tools/tdna lab location-replay
```

Verifica completa:

```bash
sh tools/tdna check
```

Benchmark diagnostico:

```bash
sh tools/tdna bench location-replay 10000 7
```

## Input

```text
rate 2/1
sample 0 t=0       accepted
sample 1 t=1000    accepted
sample 1 t=1500    rejected: sequence non crescente
sample 2 t=2000    accepted
sample 3 t=1500    rejected: tempo non crescente
sample 4 t=3000    accepted
```

## Expected evidence

- il parser conserva l'ordine dichiarato;
- tutti i campioni hanno origine `Replay`;
- il primo accepted stabilisce la baseline con delay zero;
- sequence duplicata produce `NonIncreasingSequence`;
- tempo che torna indietro produce `NonIncreasingMonotonicTime`;
- i rifiuti non muovono clock o delay scaler;
- il rate `2/1` dimezza gli intervalli accettati;
- il summary non conserva la lista degli eventi;
- accepted e rejected sono bounded;
- le aspettative della fixture coincidono con il summary;
- il report è deterministico.

## Logical tracepoints

```text
LOCATION_REPLAY_FIXTURE_PARSED
LOCATION_SAMPLE_RECEIVED
LOCATION_SAMPLE_ACCEPTED
LOCATION_SAMPLE_REJECTED
REPLAY_CLOCK_BASELINE_ESTABLISHED
REPLAY_CLOCK_ADVANCED
REPLAY_STATE_CHANGED
LOCATION_REPLAY_COMPLETED
LOCATION_REPLAY_REPORT_EMITTED
```

Sono nomi documentali, non telemetria runtime.

## Function and module path

```text
labs/location-replay-cli Main.main()
-> ReplayFixtureParser.parse()
-> LocationReplayScenario
-> DeterministicReplayRunner.runToEnd()
   -> LocationSampleGate.evaluate()
   -> VirtualReplayClock.accept()
   -> ReplayDelayScaler.scale()
   -> acceptedEvent()/rejectedEvent()
-> ReplaySummary
-> ReplayExpectations.requireMatches()
-> canonicalReplayReport()
```

## State ownership

| Stato | Owner | Durata |
| --- | --- | --- |
| testo fixture | parser JVM | parsing |
| sample snapshot | `LocationReplayScenario` | vita scenario |
| ultimo accepted | `LocationSampleGate` | vita runner |
| clock accepted | `VirtualReplayClock` | vita runner |
| resto rate | `ReplayDelayScaler` | vita runner |
| indice e contatori | `DeterministicReplayRunner` | vita runner |
| ground truth | `ReplayExpectations` | verifica CLI/test |
| report | CLI | output |

## State machine

```text
Ready -> Running -> Paused -> Running -> Completed
  |         |          |
  +-------> Cancelled <-+

Ready -> step -> Paused o Completed
Paused -> step -> Paused o Completed
```

## Expected output

```json
{"scenario":"reference-location-replay-v0","rate":"2/1","state":"completed","processed":6,"accepted":4,"rejected":2,"rejection_counts":{"non_increasing_monotonic_time":1,"non_increasing_sequence":1},"final_time_ms":3000,"playback_delay_ms":1500,"last_sequence":4}
```

## Performance properties

- nessun wall-clock sleep;
- nessun file I/O dopo il parsing;
- nessuna rete o database;
- stato runtime bounded;
- massimo 100.000 campioni per scenario Lab;
- rate arithmetic intera con overflow check;
- benchmark solo diagnostico;
- nessuna soglia CI.

## Privacy and safety properties

- coordinate sintetiche;
- nessun dato personale;
- nessuna posizione domestica;
- nessun utilizzo durante guida reale;
- nessun upload;
- origine `Replay` obbligatoria;
- metadati della fixture dichiarano provenance e non-obiettivi.

## Existing tests

- `GeoPointTest`;
- `LocationModelsTest`;
- `DeterministicReplayRunnerTest`;
- `ReplayFixtureParserTest`;
- report esatto della fixture;
- architecture checker;
- JVM e Linux x64 Gradle tests;
- Foundation CI.

## Missing tests

- property test su sequenze generate;
- test concorrenti;
- scheduler asincrono;
- adapter Android/iOS;
- map matching;
- soak su stream oltre la fixture in-memory;
- benchmark su dispositivi mobili.

## Future tests

- `LocationSample` da adapter Android e iOS;
- raw versus filtered versus matched position;
- replay -> route progress;
- missed exit e off-route state machine;
- confronto core Kotlin/Rust;
- batteria e memoria durante viaggio lungo;
- fixture anonimizzata con caratteristiche stradali reali.

## Common failures

- usare epoch time nel core;
- ordinare la fixture;
- avanzare il clock per un rejected;
- trattare `sequence` come ID globale;
- perdere il resto del rate;
- accettare `bearing = 360`;
- permettere contatori non bounded;
- conservare tutti gli eventi nel runner;
- includere parser o JSON nel hot path;
- chiamare il benchmark una prova di affidabilità stradale.

## Non-goals

- GPS reale;
- map matching;
- route progress;
- off-route/reroute;
- UI;
- MapLibre;
- accuratezza o batteria;
- tracce personali;
- schema pubblico di telemetria.

## Questions for students

1. Perché servono sequence e timestamp?
2. Quale stato cambia dopo un rifiuto?
3. Perché il primo timestamp produce delta zero?
4. Che differenza c'è tra source time e playback delay?
5. Perché il parser è JVM e il runner common?
6. Che cosa impedisce una crescita di memoria lineare con gli eventi?
7. Quale componente futuro trasformerà raw position in matched position?
8. Perché un benchmark JVM CI non dimostra prestazioni Android/iOS?

## Related docs

- [Capitolo 46](../../46-location-sample-e-replay-deterministico.md)
- [Mappa codice e stati](../../40-mappa-codice-e-stati.md)
- [Tracepoint Model](../../41-tracepoint-model-v0.md)
- [Registro milestone](../../50-registro-milestone.md)
