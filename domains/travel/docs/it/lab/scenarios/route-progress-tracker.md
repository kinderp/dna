# Scenario: matched position e route progress

id: `lab.navigation.route-progress.v0`

status: `executable`

scenario kind: `navigation-progress-foundation`

## Learning goal

Seguire una posizione già associata alla route attraverso validazione, leg
attiva, manovra, arrival e delta mappa senza implementare il map matching.

## Prerequisites

- [Capitolo 47](../../47-posizione-matched-e-route-progress.md)
- [LocationSample e replay](../../46-location-sample-e-replay-deterministico.md)
- [MapScene e fake renderer](../../45-map-scene-e-fake-renderer.md)

## User story

Uno studente invia sei posizioni sintetiche lungo una route a due leg. Cinque
sono accettate; una torna indietro ed è rifiutata. Al confine viene scelta la
seconda leg; all'arrivo un binding già verificato produce un delta compatto.

## Platforms

- Kotlin common;
- test JVM e Linux x64;
- CLI JVM;
- nessun SDK mobile o renderer reale.

## Fixture

- [`reference-route-progress-v0.meta.yaml`](../../../../fixtures/navigation/reference-route-progress-v0.meta.yaml)
- [`fixtures/navigation/README.md`](../../../../fixtures/navigation/README.md)
- route e posizioni definite in `labs/route-progress-cli`.

La fixture è sintetica e non contiene un viaggio reale.

## Trigger

```bash
sh tools/tdna lab route-progress
sh tools/tdna check
sh tools/tdna bench route-progress 10000 7
```

## Input

```text
seq 0, t=0,    index 0, fraction 0.0  -> accept
seq 1, t=1000, index 0, fraction 0.5  -> accept
seq 2, t=2000, index 2, fraction 0.0  -> accept; leg 1; Continue
seq 3, t=3000, index 1, fraction 0.75 -> reject regression
seq 4, t=4000, index 2, fraction 0.5  -> accept
seq 5, t=5000, index 3, fraction 0.0  -> accept; Arrive
```

## Expected evidence

- 5 accepted;
- 1 rejected `RegressedAlongRoute`;
- rifiuto senza mutare l'ultimo snapshot;
- leg attiva 1 al confine index 2;
- `Continue` della nuova leg prevale sulla manovra della leg precedente;
- coordinata finale `(3, 0.0)`;
- `Arrive` preferita a eventuali altre manovre sul punto finale;
- `arrived = true`;
- binding route-overlay verificato una volta;
- delta mappa con index 3 e fraction 0.

## Logical tracepoints

```text
MATCHED_ROUTE_POSITION_RECEIVED
MATCHED_ROUTE_POSITION_REJECTED
ROUTE_PROGRESS_ACCEPTED
ROUTE_ACTIVE_LEG_CHANGED
ROUTE_UPCOMING_MANEUVER_CHANGED
ROUTE_ARRIVAL_REACHED
MAP_ROUTE_PROGRESS_BOUND
MAP_PROGRESS_DELTA_PROJECTED
```

## Function path

```text
Main.runLab
-> referenceRoute
-> RouteProgressTracker.accept
   -> rejection
   -> findActiveLegIndex
   -> findUpcomingManeuver
   -> RouteProgressSnapshot
-> RouteProgressMapProjector.bind
-> RouteProgressMapProjector.project
-> JSON report
```

## State ownership

| Stato | Owner | Durata |
| --- | --- | --- |
| route geometry/legs | `RoutePlan` | vita tracker |
| maneuver cursors/arrival cursor | tracker | vita tracker |
| last accepted snapshot | tracker | sessione |
| route-overlay binding | projector/caller | installazione scena |
| candidate position | caller | una decisione |
| map delta | projector/caller | una emissione |
| rendered geometry | renderer | scena installata |

## Expected output

```json
{"scenario":"reference-route-progress-v0","accepted":5,"rejected":1,"rejection":"RegressedAlongRoute","boundary_leg":1,"boundary_maneuver":"Continue","completed_index":3,"fraction":0.0,"active_leg":1,"upcoming_maneuver":"Arrive","arrived":true,"delta_index":3}
```

## Performance properties

- stato bounded dalla route;
- nessun I/O o rete nel tracker;
- nessuna copia della route per sample;
- leg lookup `O(log L)`;
- maneuver lookup `O(log M)` più soli duplicati di confine;
- full geometry check soltanto nel binding;
- projector update `O(1)`;
- benchmark con preprocessing fuori dal timer;
- nessuna soglia CI.

## Privacy and safety

- dati sintetici;
- nessuna posizione raw personale;
- lateral distance e confidence non vengono pubblicati;
- nessuna guida reale;
- nessuna decisione off-route;
- nessuna interazione auto.

## Existing tests

- `NavigationProgressModelsTest`;
- `RouteProgressTrackerTest`;
- `RouteProgressMapProjectorTest`;
- `RouteProgressCliArgumentsTest`;
- architecture checker;
- JVM/Linux x64;
- Foundation CI.

## Missing tests

- property test su progressioni generate;
- route con migliaia di manovre duplicate;
- concorrenza;
- route replacement coordinator;
- parser di posizioni matched;
- map matcher reale;
- distanza cumulativa.

## Future tests

- raw `LocationSample` -> matched position;
- matched position -> progress -> fake renderer;
- confidence policy;
- off-route state machine;
- missed exit/reroute;
- benchmark mobile;
- soak su viaggio lungo.

## Common failures

- confondere matched position e map matching;
- rifiutare coordinate uguali;
- correggere regressioni;
- usare scan lineari nel hot path;
- scegliere la leg precedente al confine;
- esporre una manovra non-arrival dopo l'arrivo;
- verificare tutta la geometria a ogni sample;
- usare un overlay same-ID non bound;
- ricostruire geometria nel projector;
- inventare distanza o ETA.

## Non-goals

- map matching;
- GPS filtering;
- distance/ETA;
- off-route/reroute;
- Android/iOS;
- MapLibre;
- road accuracy;
- production benchmark.

## Questions for students

1. Quale componente produce `MatchedRoutePosition`?
2. Perché coordinate route uguali sono valide?
3. Quali tre monotonicità vengono controllate?
4. Perché il confine appartiene alla nuova leg?
5. Perché il binding confronta la geometria una sola volta?
6. Quale stato cambia dopo un rifiuto?
7. Che cosa contiene il delta mappa?
8. Perché `arrived` non implica viaggio concluso?

## Related docs

- [Capitolo 47](../../47-posizione-matched-e-route-progress.md)
- [Tracepoint Model](../../41-tracepoint-model-v0.md)
- [Scenario missed exit](navigation-missed-exit-reroute.md)
- [Registro milestone](../../50-registro-milestone.md)
