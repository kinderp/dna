# Travel DNA development status

Last updated: 2026-07-18

## Current milestones

- **Foundations and Travel DNA Lab v0 — in progress**
- **Navigation Runtime Replay v0 — in progress**

## Completed slices

| Slice | Issue / PR | Merge |
| --- | --- | --- |
| Java/Rust reference routing | #3 / #4 | `d122f1b4871719087e79a50b185ab302d810cb20` |
| Provider-neutral routing contracts | #5 / #6 | `2a28d1654988cef4188986342f76fd7d19be358f` |
| Two-clean-review governance | #8 / #9 | `044e0773dd9afb1530db35688a00c56bfbd5eace` |
| MapScene and fake renderer | #7 / #10 | `2128f24b4a6ab00aeb437ed48a0af2910f33e9ec` |
| Serial PR governance | #14 / #15 | `76680433089842db5805d28eb50416a23c7d0a88` |
| LocationSample and deterministic replay | #11 / #16 | `020f8495f7fbbae81f1463b098b0ddd2a079c873` |
| Matched position and route progress | #17 / #18 | `9921fbcc1da1000e6434bdae49646122cae8f0e0` |
| Map-matching port and fake | #19 / #20 | `95cf2f0d900b039efb86ba0a570e3ca3f8d8cef5` |

## Active slice

**Deterministic off-route evidence, missed exit and reroute — final candidate rebuilding after review findings**

- issue [#21](https://github.com/kinderp/tdna/issues/21);
- PR [#22](https://github.com/kinderp/tdna/pull/22);
- branch `agent/missed-exit-reroute`;
- base `95cf2f0d900b039efb86ba0a570e3ca3f8d8cef5`;
- risk `R2`;
- chapter `docs/it/49-off-route-missed-exit-e-reroute.md`;
- scenario `docs/it/lab/scenarios/navigation-missed-exit-reroute.md`;
- report `docs/project/daily/2026-07-18-missed-exit-reroute.md`.

## Implemented

- normalized `OnRoute`, `Suspicious` and `Indeterminate` evidence;
- bounded count-plus-duration policy;
- false-alarm recovery and indeterminate hold;
- sticky confirmation with explicit episode ID;
- route/sequence/time rejection without mutation;
- correlated reroute command and outcome;
- one in-flight attempt;
- stale outcome rejection;
- old-route retention during in-flight and failure;
- cancellation cleanup and ordinary-exception mapping;
- canonical route request from the confirmation position;
- provider `routing.plan` capability check before invocation;
- maneuver-capability postcondition;
- public `InFlight` state invariants;
- provider provenance and route-request postconditions;
- new route ID and atomic replacement;
- new progress tracker boundary after replacement;
- synthetic Lab, tests and diagnostic benchmark;
- chapter, scenario, code map, tracepoints and indexed report.

## Review findings resolved after the first green head

1. public `InFlight` state admitted a command for a different source route;
2. public `InFlight` state admitted a destination different from the active route;
3. a planner without `routing.plan` could still be invoked;
4. a planner declaring `routing.maneuvers` could return empty maneuver legs;
5. `lastObservation` obscured that the state retains the last suspicious observation;
6. repository and Lab indexes/status records did not yet include the slice.

All fixes are substantive. Earlier CI remains historical evidence only and the
clean-review counter is reset.

## Final gate

- sole open PR: yes;
- code, tests, chapter, scenario, indexes and report: complete in branch;
- candidate final SHA: determined after the final documentation integration;
- CI on that exact SHA: pending;
- unresolved review threads: must be zero;
- clean review rounds: `0 / 2`;
- merge: only after ready state and expected-head guard.

## Foundations still missing after PR #22

- committed Gradle Wrapper and supply-chain verification;
- final closure report and milestone status update;
- Android/iOS targets;
- real MapLibre/routing/map-matching provider adapters.

## Maintainer decisions

None for PR #22. Standing authorization permits autonomous merge only after all
gates. The first mobile platform remains a product/architecture decision for the
milestone following Foundations v0.
