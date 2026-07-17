# Travel DNA development status

Last updated: 2026-07-17

## Current milestone

**Foundations and Travel DNA Lab v0 — in progress**

## Completed slices

- Java/Rust reference routing — issue #3, PR #4;
- provider-neutral routing contracts — issue #5, PR #6;
- two-clean-review governance — issue #8, PR #9;
- provider-neutral MapScene — issue #7, PR #10;
- serial PR governance — issue #14, PR #15, merge
  `76680433089842db5805d28eb50416a23c7d0a88`.

## Active slice

**Canonical LocationSample and deterministic replay — pre-review complete**

- issue [#11](https://github.com/kinderp/tdna/issues/11);
- PR [#16](https://github.com/kinderp/tdna/pull/16);
- branch `agent/location-sample-replay`;
- risk `R2`;
- chapter `docs/it/46-location-sample-e-replay-deterministico.md`;
- scenario `docs/it/lab/scenarios/location-replay-deterministico.md`;
- report `docs/project/daily/2026-07-17-location-replay.md`.

## Implemented

- cross-domain `GeoPoint` with signed-zero normalization;
- bounded `LocationSample` with monotonic time and sequence;
- non-mutating inspection and explicit accepted/rejected gate;
- rational playback rate with remainder preservation;
- virtual clock with first-sample baseline;
- atomic accepted transition after arithmetic preview;
- deterministic replay state machine;
- bounded summary without event-history retention;
- strict versioned synthetic fixture parser;
- exact Lab report;
- diagnostic benchmark retained as CI artifact;
- common/JVM/Linux tests;
- architecture boundaries;
- chapter, Lab scenario, tracepoints, tooling and guided reading paths.

## Findings resolved

1. public summary counters were not bounded;
2. fixture expectation counts could exceed the scenario;
3. benchmark output was not retained;
4. even iteration counts made median semantics ambiguous;
5. public summary allowed processed samples without an accepted baseline;
6. playback overflow could partially mutate runner state;
7. repository entry points and commenting status were stale.

Every substantive fix reset the clean-review count. No final round has been
claimed yet.

## Benchmark observation

Run #97 on technical head `2b27a731e98c0456e2532ef3ebfc850523ab0ef4`:

```text
10.000 samples
7 measured iterations after 3 warm-ups
median 2.973.838 ns
median 297,38 ns/sample
```

Diagnostic only; no mobile, GPS, battery or road claim.

## Pull-request inventory

- PR #16 is the only open PR;
- old PR #12 remains closed and supplies no review evidence;
- branch starts from verified post-governance `main`.

## Remaining before merge

- Foundation CI green on the final substantive head;
- clean review round 1;
- clean review round 2 on the same SHA;
- no substantive commit after the rounds;
- ready and expected-head merge;
- verify issue closure and new `main`.

## Milestone still missing after this slice

- matched-position/map-matching seed;
- missed-exit/off-route state machine;
- Gradle Wrapper;
- Android/iOS targets;
- real MapLibre or routing provider adapter.

## Maintainer decisions

None. Standing authorization permits autonomous merge only after all gates.
