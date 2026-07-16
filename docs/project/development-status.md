# Travel DNA development status

Last updated: 2026-07-16

## Current milestone

**Foundations and Travel DNA Lab v0 — in progress**

The milestone is building executable contracts and teaching paths before mobile
SDK integration.

## Active vertical slice

**Executable Java/Rust reference-routing Lab — implementation and autonomous review complete**

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
- numerically bounded Haversine computation for extreme valid coordinates;
- expected-path adjacency and checked-cost validation;
- deterministic route reconstruction;
- byte-comparable Lab report;
- six Java reference scenarios;
- six Rust unit scenarios;
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

GitHub Actions run `#13` passed on Ubuntu 24.04 with Java 21 and stable Rust:

```text
Check documentation             success
Check Java reference routing    success
Check Rust reference routing    success
Check Java and Rust contract    success
```

The CI result proves that `rustfmt`, Rust unit tests, the Java regression suite
and byte-identical Java/Rust reports pass on commit
`3d19262e3a4f48bbc6eacad408be2c2e6665b3c9`.

## Review status

Autonomous review round 1 found and fixed two issues:

1. clamp the Haversine intermediate value to prevent floating-point drift from
   producing an invalid square root for extreme valid coordinates;
2. validate that every consecutive edge in fixture ground truth exists and that
   its checked total equals the declared expected cost.

Review rounds 2 and 3 found no new correctness, architecture, documentation,
privacy or scope issues. The pull request remains draft because merge is a
maintainer decision under the repository rules.

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

None for continued development. Merge of pull request `#4` remains intentionally
separate from implementation work.
