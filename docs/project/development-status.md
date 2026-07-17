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
- chapter `docs/it/44-contratti-routing-e-fake-provider.md`;
- scenario `docs/it/lab/scenarios/routing-contracts-fake-provider.md`.

## Active slice

**Governance: two consecutive clean review rounds — implementation complete,
final CI and clean reviews pending**

- issue [#8](https://github.com/kinderp/tdna/issues/8);
- draft PR [#9](https://github.com/kinderp/tdna/pull/9);
- branch `agent/two-clean-review-policy`;
- operational rule `docs/it/00-regole-operative.md`;
- detailed chapter `docs/it/06-review-e-merge.md`;
- report `docs/project/daily/2026-07-17-review-policy.md`.

## Implemented foundation capabilities

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
- executable Java/Rust and Kotlin Labs;
- architecture checker;
- extended Foundation CI;
- indexed daily reports;
- mandatory two-clean-review policy on the PR #9 branch.

## Review-policy hardening

The first audit of the new policy found that provider-controlled maneuver
`roadName` and `exitNumber` values were non-blank but unbounded. PR #9 adds:

```text
MaxRoadNameLength = 256
MaxExitNumberLength = 64
```

with common Kotlin regression tests. Because a finding required a substantive
change, the clean-review counter was reset to zero as the new policy requires.

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

After PR #9 reaches two clean rounds and is merged, continue issue
[#7](https://github.com/kinderp/tdna/issues/7): provider-neutral map-scene
contracts, route overlay projection and `FakeMapRenderer`.

## Maintainer decisions

No product or architectural decision is currently required. Merge authority
remains with the maintainer after each PR satisfies CI and two consecutive clean
review rounds.
