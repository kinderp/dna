# Scenario: missed motorway exit and reroute

id: `lab.navigation.missed-exit-reroute.v0`
status: `stable-doc`
scenario kind: `navigation`

## Learning goal

Capire come un campione GPS diventa posizione matched, progresso, sospetto di
deviazione, conferma e sostituzione della route.

## Prerequisites

- glossario;
- routing e navigazione;
- replay;
- tracepoint model.

## User story

Un conducente perde un'uscita. Travel DNA non deve ricalcolare su un solo punto
rumoroso; deve confermare la deviazione e mantenere la route precedente fino a
quando la nuova è valida.

## Platforms

Core deterministico; UI Android/iOS come consumer.

## Fixture

`fixtures/gps/highway-missed-exit-v0.jsonl` futura, sintetica.

## Trigger

Replay 1 Hz con deviazione su rampa non prevista.

## Expected evidence

- campioni iniziali on-route;
- uscita prevista superata;
- distanza/direzione incoerenti persistenti;
- `OFF_ROUTE_SUSPECTED`;
- `OFF_ROUTE_CONFIRMED`;
- una sola request reroute;
- nuova route valida;
- applicazione atomica;
- snapshot aggiornato.

## Logical tracepoints

```text
LOCATION_SAMPLE_RECEIVED
LOCATION_SAMPLE_ACCEPTED
POSITION_MAP_MATCHED
ROUTE_PROGRESS_UPDATED
MANEUVER_SELECTED
OFF_ROUTE_SUSPECTED
OFF_ROUTE_CONFIRMED
REROUTE_REQUESTED
ROUTE_REPLACED
NAVIGATION_SNAPSHOT_PUBLISHED
```

## Module path target

```text
ReplayLocationSource
-> NavigationRuntimePort
-> GuidanceEngine
-> MapMatcher
-> RouteProgressTracker
-> OffRouteStateMachine
-> ReroutingCoordinator
-> RoutePlannerPort
-> NavigationSnapshotPublisher
```

## State changes

```text
NAVIGATING
-> SUSPECTED_OFF_ROUTE
-> CONFIRMED_OFF_ROUTE
-> REROUTING
-> NAVIGATING(new route)
```

## Performance properties

- no DB/network in `advance` except reroute command emitted asynchronously;
- no route geometry rebuild per sample;
- p95 location-to-snapshot within budget;
- one active reroute;
- bounded history.

## Privacy/safety

- fixture synthetic;
- exact sample remains local;
- UI avoids distracting modal;
- old guidance is marked low-confidence during reroute.

## Existing tests

None: code not implemented.

## Missing tests

- state-machine unit;
- replay golden;
- provider contract;
- cancellation;
- latency benchmark;
- parallel road false-positive.

## Common failures

- reroute on one bad sample;
- multiple concurrent requests;
- clearing old route early;
- maneuver announcement from stale route after replacement;
- progress oscillation.

## Non-goals

- live traffic;
- real motorway data;
- MapLibre rendering details;
- external navigator;
- production thresholds.
