# Travel DNA development status

Last updated: 2026-07-17

## Current milestone

**Foundations and Travel DNA Lab v0 — in progress**

## Completed slices

### Java/Rust reference-routing Lab

- issue [#3](https://github.com/kinderp/tdna/issues/3);
- PR [#4](https://github.com/kinderp/tdna/pull/4);
- merge commit `d122f1b4871719087e79a50b185ab302d810cb20`;
- chapter `docs/it/43-reference-routing-java-rust.md`.

### Provider-neutral routing contracts and deterministic fake planner

- issue [#5](https://github.com/kinderp/tdna/issues/5);
- PR [#6](https://github.com/kinderp/tdna/pull/6);
- merge commit `2a28d1654988cef4188986342f76fd7d19be358f`;
- chapter `docs/it/44-contratti-routing-e-fake-provider.md`.

### Review governance and route-maneuver hardening

- issue [#8](https://github.com/kinderp/tdna/issues/8);
- PR [#9](https://github.com/kinderp/tdna/pull/9);
- merge commit `044e0773dd9afb1530db35688a00c56bfbd5eace`;
- two consecutive clean review rounds are now mandatory on every PR;
- `roadName` and `exitNumber` are bounded and regression-tested.

## Active slice

**Provider-neutral MapScene and deterministic fake renderer — draft, realigned to current `main`**

- issue [#7](https://github.com/kinderp/tdna/issues/7);
- draft PR [#10](https://github.com/kinderp/tdna/pull/10);
- branch `agent/provider-neutral-map-scene`;
- chapter `docs/it/45-map-scene-e-fake-renderer.md`;
- scenario `docs/it/lab/scenarios/map-scene-fake-renderer.md`;
- report `docs/project/daily/2026-07-17-map-scene.md`.

## Implemented in the active slice

- provider-neutral `MapScene`, camera, route overlay and marker contracts;
- bounded immutable scene and delta collections;
- static-scene versus frequent-delta split;
- canonical route progress in `[0, 1)`;
- map renderer capability and error model;
- deterministic fake renderer with semantic snapshot and call history;
- reusable renderer conformance probe;
- route-to-overlay projector;
- executable map-scene JVM Lab;
- architecture checker that ignores explanatory comments and literals.

## Findings and realignment

1. the first checker treated provider names in comments as executable dependencies;
2. route progress admitted the duplicate representation `fraction = 1.0`;
3. the full probe exercised capabilities it did not require;
4. a first-pass set of MapScene files remained beside the reviewed model and
   caused Kotlin redeclarations;
5. the first merge-tree realignment preserved the MapScene code but selected
   obsolete governance and routing files from the feature branch.

The final corrective commit restores `main` as source of truth for governance
and routing hardening, removes duplicate contracts and merges only the MapScene
content and its documentation. Every prior clean review of PR #10 is invalidated;
CI and two fresh rounds are required on the new substantive head.

## Pull-request discipline

- only PR #10 is open;
- accidental PR #13 was closed without merge;
- stacked PR #12 was closed without merge and its branch was preserved;
- no next PR will be opened until PR #10 is merged or explicitly abandoned.

## Milestone still missing

- `LocationSample` and deterministic clock;
- GPS replay runner;
- missed-exit guidance state machine;
- first benchmark report;
- committed Gradle Wrapper;
- Android/iOS targets;
- real MapLibre or routing provider adapter.

## Next executable step

Obtain green CI and two consecutive clean review rounds for PR #10 on one final
substantive head. After merge, realign from `main` before restoring the location
replay work.

## Maintainer decisions

No product or architecture decision is required. The maintainer has granted
standing authorization for autonomous merge after every documented gate is met.
