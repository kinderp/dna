# Scenario: uscita mancata, conferma off-route e ricalcolo

id: `lab.navigation.missed-exit-reroute.v0`

status: `executable`

scenario kind: `navigation-reroute-foundation`

## Learning goal

Seguire un falso allarme e un episodio persistente attraverso state machine,
comando correlato, provider fake, stale-outcome rejection e sostituzione atomica
della route.

## Prerequisites

- [Capitolo 46 — LocationSample](../../46-location-sample-e-replay-deterministico.md)
- [Capitolo 47 — Route progress](../../47-posizione-matched-e-route-progress.md)
- [Capitolo 48 — Porta map matching](../../48-porta-map-matching-e-fake-deterministico.md)
- [Capitolo 49 — Off-route e reroute](../../49-off-route-missed-exit-e-reroute.md)

## User story

Uno studente riproduce sette osservazioni sintetiche. Il primo sospetto recupera,
il secondo episodio sopravvive a un gap indeterminate e viene confermato dopo
count e durata. Parte un solo reroute; un outcome tardivo viene ignorato; la
vecchia route resta attiva fino al commit di una route nuova e canonica.

## Platforms

- Kotlin common per contratti, tracker e coordinator;
- test JVM e Linux x64;
- CLI JVM;
- nessun SDK mobile o provider reale.

## Fixture

- [`reference-missed-exit-v0.meta.yaml`](../../../../fixtures/navigation/reference-missed-exit-v0.meta.yaml)
- osservazioni e route in `labs/missed-exit-cli`;
- `FakeRoutePlanner` come provider deterministico.

Tutti i dati sono sintetici.

## Trigger

```bash
sh tools/tdna lab missed-exit
```

Verifica completa:

```bash
sh tools/tdna check
```

Benchmark:

```bash
sh tools/tdna bench off-route 10000 7
```

## Input

```text
seq 0, t=0     OnRoute
seq 1, t=1000  Suspicious MissedExpectedManeuver -> episode 1
seq 2, t=1500  OnRoute                           -> recovery
seq 3, t=2000  Suspicious                        -> episode 2
seq 4, t=2500  Suspicious                        -> count 2
seq 5, t=3000  Indeterminate                     -> hold
seq 6, t=4500  Suspicious                        -> count 3, duration 2500, confirm
```

Poi:

```text
begin attempt 1
begin duplicato -> AlreadyInFlight
outcome attempt 2 -> Stale
old route remains active
fake planner -> replacement route
postconditions -> pass
atomic replacement
new RouteProgressTracker -> empty
```

## Expected evidence

- due episodi;
- un recovery;
- un indeterminate hold;
- una sola conferma;
- un solo attempt;
- duplicate begin ignorato;
- stale outcome ignorato;
- vecchia route mantenuta mentre in flight;
- nuova route con ID diverso;
- route sostitutiva coerente con la request;
- nuovo tracker senza snapshot precedente.

## Logical tracepoints

```text
OFF_ROUTE_OBSERVATION_RECEIVED
OFF_ROUTE_SUSPICION_STARTED
OFF_ROUTE_SUSPICION_HELD
OFF_ROUTE_RECOVERED
OFF_ROUTE_CONFIRMED
REROUTE_COMMAND_CREATED
REROUTE_DUPLICATE_SUPPRESSED
REROUTE_OUTCOME_STALE
REROUTE_PROVIDER_COMPLETED
ROUTE_REPLACEMENT_VALIDATED
ROUTE_REPLACED
NAVIGATION_ROUTE_STATE_RECREATED
MISSED_EXIT_REPORT_EMITTED
```

## Function path

```text
Main.runLab
-> OffRouteTracker.accept
   -> rejection
   -> nextState
   -> continueSuspicion
-> RerouteCoordinator.begin
-> RerouteCoordinator.apply(stale)
-> RerouteCoordinator.executePending
   -> RerouteExecutor.execute
   -> FakeRoutePlanner.plan
   -> RerouteCoordinator.apply
   -> RoutePlan.requireMatches
-> new RouteProgressTracker
-> JSON report
```

## State ownership

| Stato | Owner | Durata |
| --- | --- | --- |
| route/evidence policy | runtime composition | route session |
| episode state/baseline | `OffRouteTracker` | route session |
| active route/pending command | `RerouteCoordinator` | navigation session |
| provider call | `RerouteExecutor` | una esecuzione |
| route catalog | `FakeRoutePlanner` | vita fake |
| new progress state | nuovo tracker | nuova route |
| report counters | CLI | una esecuzione |

## Expected output

```json
{"scenario":"reference-missed-exit-v0","episodes":2,"recoveries":1,"indeterminate":1,"confirmations":1,"attempts":1,"duplicate_begin":"AlreadyInFlight","stale_outcome_ignored":true,"old_route_held":true,"replaced":true,"new_route_id":"reference-rerouted-route-v0","new_tracker_empty":true}
```

## Performance properties

- tracker state bounded;
- nessuna history di osservazioni;
- nessun I/O nel tracker/coordinator puro;
- un solo comando in flight;
- route vecchia disponibile durante il provider call;
- benchmark della sola state machine senza planner;
- nessuna soglia CI.

## Privacy and safety

- coordinate sintetiche;
- nessun viaggio personale;
- nessuna rete reale;
- diagnostics bounded;
- soglie didattiche, non di produzione;
- nessuna indicazione da usare durante guida reale;
- nessuna promessa su accuratezza o batteria.

## Existing tests

- `OffRouteContractsTest`;
- `OffRouteTrackerTest`;
- `RerouteCoordinatorTest`;
- `MissedExitCliArgumentsTest`;
- architecture checker;
- JVM/Linux x64;
- Foundation CI.

## Missing tests

- property test su sequenze generate;
- adapter matcher reale;
- concorrenza vera fra route replacement e nuovi sample;
- retry/backoff;
- route alternatives/ranking;
- Android/iOS lifecycle;
- map/voice/HUD installation atomica;
- field replay.

## Future tests

- replay raw -> matcher -> evidence -> tracker -> reroute;
- cancellation quando torna OnRoute;
- provider timeout reale;
- route replacement con map binding;
- benchmark mobile;
- field trace anonimizzata;
- soak di viaggio lungo.

## Common failures

- confermare su un solo sospetto;
- contare indeterminate;
- azzerare lo stato su indeterminate;
- mutare su sample stale;
- creare attempt paralleli;
- perdere source route ID;
- applicare stale outcome;
- eliminare la vecchia route troppo presto;
- convertire cancellazione in failure;
- accettare provenance sbagliata;
- riutilizzare route ID/tracker;
- chiamare le soglie v0 “sicure”.

## Non-goals

- real off-route thresholds;
- GPS filtering;
- road matching;
- traffic;
- Android/iOS;
- MapLibre;
- voice;
- production performance.

## Questions for students

1. Perché servono count e durata?
2. Che cosa fa un indeterminate?
3. Quando nasce un nuovo episode ID?
4. Perché confirmed è sticky?
5. Perché route e attempt ID devono essere entrambi correlati?
6. Quale route è attiva durante il provider call?
7. Che cosa accade dopo cancellation?
8. Quali componenti vanno ricreati dopo la sostituzione?

## Related docs

- [Capitolo 49](../../49-off-route-missed-exit-e-reroute.md)
- [Code/state map](../../40-mappa-codice-e-stati.md)
- [Tracepoint Model](../../41-tracepoint-model-v0.md)
- [Performance budget](../../32-performance-budget.md)
