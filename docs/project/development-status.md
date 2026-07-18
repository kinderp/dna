# Travel DNA development status

Last updated: 2026-07-18

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
| Matched position and route progress | #17 / #18 | `9921fbcc1da1000e6434bdae49646122cae8f0e0` |

## Active slice

**Provider-neutral map-matcher port and deterministic fake — documentation/review gate in progress**

- issue [#19](https://github.com/kinderp/tdna/issues/19);
- PR [#20](https://github.com/kinderp/tdna/pull/20);
- branch `agent/map-matcher-port`;
- base `9921fbcc1da1000e6434bdae49646122cae8f0e0`;
- risk `R2`;
- chapter `docs/it/48-porta-map-matching-e-fake-deterministico.md`;
- scenario `docs/it/lab/scenarios/map-matching-fake-provider.md`;
- report `docs/project/daily/2026-07-18-map-matcher-port.md`.

## Implemented

- route-bound `MapMatcherPort` / `MapMatchSession`;
- capability IDs;
- `Matched`, `Unmatched` and `Failure` outcomes;
- bounded error/provenance metadata;
- strict result postconditions;
- exact-catalog `FakeMapMatcher`;
- amortized `O(1)` bounded call window;
- fresh-session determinism semantics;
- reusable contract probe;
- matched/unmatched/failure fixture;
- downstream route-progress Lab;
- diagnostic pipeline benchmark;
- common/JVM/Linux tests;
- architecture boundaries;
- chapter, scenario, metadata and indexed report.

## Findings resolved

1. testkit location/routing dependencies were implicit;
2. deterministic repeat used the same potentially stateful session;
3. fake call-window eviction used linear list removal;
4. provider failure was not demonstrated by fixture/probe/Lab;
5. contract report retained a potentially mutable checks list;
6. benchmark could validate only the final snapshot rather than every untimed pipeline decision.

Each substantive fix resets the clean-review counter.

## Gate status

- sole open PR: yes;
- code, tests, chapter, scenario, indexes and report: complete in branch;
- CI on current code hardening head: running/green evidence must be superseded by final SHA;
- unresolved review threads: must be zero;
- clean review rounds: `0 / 2`;
- merge: only after ready state and expected-head guard.

## Milestone still missing after this slice

- off-route/missed-exit/reroute state machine;
- Gradle Wrapper;
- Android/iOS targets;
- real MapLibre/routing/map-matching provider adapter.

## Maintainer decisions

None. Standing authorization permits autonomous merge only after all gates.
