# Travel DNA development status

Last updated: 2026-07-17

## Current milestone

**Foundations and Travel DNA Lab v0 — in progress**

## Completed slices

| Slice | Issue / PR | Merge |
| --- | --- | --- |
| Java/Rust reference routing | #3 / #4 | `d122f1b4871719087e79a50b185ab302d810cb20` |
| Provider-neutral routing contracts | #5 / #6 | `2a28d1654988cef4188986342f76fd7d19be358f` |
| Two-clean-review governance | #8 / #9 | `044e0773dd9afb1530db35688a00c56bfbd5eace` |
| MapScene and fake renderer | #7 / #10 | `2128f24b4a6ab00aeb437ed48a0af2910f33e9ec` |
| Serial PR governance | #14 / #15 | `76680433089842db5805d28eb50416a23c7d0a88` |
| LocationSample and deterministic replay | #11 / #16 | `020f8495f7fbbae81f1463b098b0ddd2a079c873` |

## Active slice

**Canonical matched position and route progress tracker — substantive work complete, final gate pending**

- issue [#17](https://github.com/kinderp/tdna/issues/17);
- PR [#18](https://github.com/kinderp/tdna/pull/18);
- branch `agent/matched-route-progress`;
- base `020f8495f7fbbae81f1463b098b0ddd2a079c873`;
- risk `R2`;
- chapter `docs/it/47-posizione-matched-e-route-progress.md`;
- scenario `docs/it/lab/scenarios/route-progress-tracker.md`;
- report `docs/project/daily/2026-07-17-route-progress.md`.

## Implemented

- canonical `RouteCoordinate` and `MatchedRoutePosition`;
- independent sequence/time/route-progress checks;
- stationary accepted and regressions rejected without mutation;
- deterministic active-leg boundary policy;
- binary-search leg and maneuver lookup;
- explicit `Arrive` tie-break;
- public snapshot invariants;
- route-overlay binding with full geometry verification at install time;
- compact `O(1)` map progress projection;
- common/JVM/Linux tests;
- deterministic Lab report;
- multi-leg diagnostic benchmark with preprocessing outside the timer;
- chapter, scenario, code map, tracepoints and indexed report.

## Findings resolved

1. linear leg/maneuver scans;
2. same-ID overlay with different geometry;
3. unrepresentative benchmark route;
4. tracker preprocessing inside the measured window;
5. boundary result absent from Lab evidence;
6. non-Arrive maneuver exposed after arrival;
7. projector test omitted same-length altered geometry;
8. intermediate Kotlin named-argument error caught by CI;
9. tracepoint incorrectly claimed distance updates;
10. stable README/feature status used merge-unstable PR wording.

Every substantive fix reset the clean-review counter.

## Final gate

- sole open PR: yes;
- code, tests, chapter, scenario, indexes and reports: complete;
- benchmark artifact: recorded from CI #110;
- final substantive SHA: determined by the commit containing this status record;
- CI on that exact SHA: pending;
- unresolved review threads: must be zero;
- clean review rounds: `0 / 2`;
- merge: only after ready state and expected-head guard.

## Milestone still missing after this slice

- real map matching or a separate fake matcher;
- missed-exit/off-route/reroute state machine;
- Gradle Wrapper;
- Android/iOS targets;
- real MapLibre/routing provider adapter.

## Maintainer decisions

None. Standing authorization permits autonomous merge only after all gates.
