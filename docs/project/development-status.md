# Travel DNA development status

Last updated: 2026-07-16

## Current milestone

**Foundations and Travel DNA Lab v0 — in progress**

The milestone is building executable contracts and teaching paths before mobile
SDK integration.

## Active vertical slice

**Executable Java/Rust reference-routing Lab — implementation complete, draft PR under review**

Tracking:

- GitHub issue: `#3`;
- draft pull request: `#4`;
- branch: `agent/foundation-reference-routing`;
- primary teaching chapter: `docs/it/43-reference-routing-java-rust.md`;
- executable scenario: `docs/it/lab/scenarios/reference-routing-java-rust.md`.

## Completed in the slice

- synthetic versioned road-graph fixture;
- strict fixture parsers in Java and Rust;
- Dijkstra implementations;
- A* implementations with admissible WGS84 heuristic;
- deterministic route reconstruction;
- byte-comparable Lab report;
- dependency-free Java 21 test suite;
- Rust unit tests;
- cross-language contract script;
- project command `tools/tdna`;
- documentation link/fence checker;
- GitHub Actions foundation workflow;
- teaching chapter and executable Lab scenario.

## Verification

Passed locally in the available Java/Python environment:

```text
sh tools/tdna check-docs
sh tools/tdna check-java
sh tools/tdna lab reference-routing astar
```

GitHub Actions run `#10` passed on Ubuntu 24.04 with Java 21 and stable Rust:

```text
Check documentation             success
Check Java reference routing    success
Check Rust reference routing    success
Check Java and Rust contract    success
```

The CI result proves that `rustfmt`, Rust unit tests and byte-identical Java/Rust
reports pass on the committed branch. The local absence of Rust remains only an
environment limitation of the interactive development container.

## Milestone still missing

- production-oriented canonical KMP geo/routing contracts;
- plugin descriptor and capability model;
- fake route planner and fake map renderer against those contracts;
- deterministic GPS replay clock;
- missed-exit guidance state machine;
- benchmark report;
- reproducible Gradle/KMP bootstrap.

## Next executable step

Introduce the provider-neutral Kotlin/JVM contract seed and a fake route planner,
then map the reference-routing result into that contract without allowing the
Lab fixture model to become the mobile API accidentally.

## Decisions currently required from the maintainer

None. The current slice does not change product scope, licensing, provider
strategy or public architecture.
