# Travel DNA development status

Last updated: 2026-07-17

## Current milestone

**Foundations and Travel DNA Lab v0 — in progress**

## Completed slice

**Java/Rust reference-routing Lab — merged**

- issue [#3](https://github.com/kinderp/tdna/issues/3);
- PR [#4](https://github.com/kinderp/tdna/pull/4);
- merge commit `d122f1b4871719087e79a50b185ab302d810cb20`;
- chapter `docs/it/43-reference-routing-java-rust.md`.

## Active slice

**Provider-neutral routing contracts and deterministic fake planner — review complete, merge pending**

- issue [#5](https://github.com/kinderp/tdna/issues/5);
- draft PR [#6](https://github.com/kinderp/tdna/pull/6);
- branch `agent/provider-neutral-routing-contracts`;
- implementation/documentation commit `ece0c6e26773b0b409f18e723b7e6249a3c3ee9a`;
- chapter `docs/it/44-contratti-routing-e-fake-provider.md`;
- scenario `docs/it/lab/scenarios/routing-contracts-fake-provider.md`.

## Implemented

- Gradle/Kotlin Multiplatform bootstrap;
- JVM and Linux x64 targets;
- generic plugin SDK;
- bounded runtime platform/capability metadata;
- canonical route request, plan, legs, maneuvers and provenance;
- defensive collection snapshots;
- request-to-route waypoint postconditions;
- canonical error/result model;
- provider-neutral `RoutePlannerPort`;
- deterministic fake planner and call recording;
- reusable conformance probe;
- executable JVM Lab;
- architecture checker;
- extended Foundation CI;
- chapter 44 and second executable Lab;
- indexed daily reports.

## Verification

Foundation CI run `#19` passed all steps on commit
`ece0c6e26773b0b409f18e723b7e6249a3c3ee9a`:

```text
documentation             success
architecture boundaries   success
Java                       success
Rust                       success
Java/Rust contract         success
Kotlin Multiplatform       success
```

## Review

- round 1: fixed mutable collection ownership and ignored waypoint risks;
- round 2: fixed runtime-platform semantics and bounded metadata;
- round 3: no new blocking findings;
- privacy/safety: synthetic data only, no network or user information.

## Milestone still missing

- `LocationSample` and deterministic clock;
- GPS replay runner;
- `MapScene` and fake map renderer;
- missed-exit guidance state machine;
- first benchmark report;
- committed Gradle Wrapper;
- Android/iOS targets;
- real provider adapter.

## Next executable step

Create provider-neutral map-scene contracts and `FakeMapRenderer`, then project a
canonical `RoutePlan` through scene deltas without MapLibre imports.

## Maintainer decision

Only the merge of PR #6. No product or architectural decision blocks subsequent
planning.
