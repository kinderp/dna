# Travel DNA development status

Last updated: 2026-07-17

## Current milestone

**Foundations and Travel DNA Lab v0 — in progress**

The milestone builds executable contracts, fakes, replay foundations and
teaching paths before mobile SDK integration.

## Completed slice

**Java/Rust reference-routing Lab — merged**

- issue: [#3](https://github.com/kinderp/tdna/issues/3);
- PR: [#4](https://github.com/kinderp/tdna/pull/4);
- merge commit: `d122f1b4871719087e79a50b185ab302d810cb20`;
- documentation: `docs/it/43-reference-routing-java-rust.md`.

## Active vertical slice

**Provider-neutral routing contracts and deterministic fake planner — in review**

- issue: [#5](https://github.com/kinderp/tdna/issues/5);
- draft PR: [#6](https://github.com/kinderp/tdna/pull/6);
- branch: `agent/provider-neutral-routing-contracts`;
- chapter: `docs/it/44-contratti-routing-e-fake-provider.md`;
- Lab: `docs/it/lab/scenarios/routing-contracts-fake-provider.md`.

## Implemented in the active slice

- Gradle/Kotlin Multiplatform root build;
- JVM and Linux x64 targets;
- generic plugin SDK;
- bounded plugin descriptors and runtime platform IDs;
- canonical geo and routing models;
- route request/result invariants;
- defensive collection snapshots;
- request-to-route waypoint postconditions;
- provider-neutral `RoutePlannerPort`;
- canonical success and error model;
- deterministic `FakeRoutePlanner`;
- call recording and catalog miss behavior;
- reusable `RoutePlannerContractProbe`;
- executable JVM Lab CLI;
- source-level architecture checker;
- extended project tooling and CI;
- second implementation-backed teaching chapter;
- permanent daily-report index.

## Verification observed

- Foundation CI run `#16`: initial KMP slice green;
- Foundation CI run `#17`: immutability and waypoint fixes green;
- Foundation CI run `#18`: failed only because the report index linked to the
  not-yet-committed `2026-07-17.md`; the current commit supplies that report.

A final green run is required before the slice review closes.

## Review findings resolved

1. mutable caller/provider collections could invalidate canonical values;
2. route validation could ignore requested waypoints;
3. a Kotlin common source set was incorrectly modelled as a runtime platform;
4. provider control metadata was not explicitly bounded.

Each finding has code and regression-test coverage.

## Milestone still missing

- `LocationSample` and deterministic clock;
- GPS replay runner;
- `MapScene` and fake map renderer;
- missed-exit guidance state machine;
- first benchmark report;
- committed Gradle Wrapper;
- Android and iOS targets;
- real provider adapter.

## Next executable step

Create provider-neutral map-scene contracts and `FakeMapRenderer`, then render a
canonical `RoutePlan` through deltas without importing MapLibre.

## Decisions currently required from the maintainer

None for continued development. PR #6 merge remains a maintainer action after
CI and autonomous review close.
