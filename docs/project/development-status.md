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

**Canonical LocationSample and deterministic replay**

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
- explicit accepted/rejected ordering gate;
- rational playback rate with remainder preservation;
- virtual clock with first-sample baseline;
- deterministic replay state machine;
- bounded summary without event-history retention;
- strict versioned synthetic fixture parser;
- exact Lab report;
- diagnostic benchmark and retained CI artifact;
- common/JVM/Linux tests;
- architecture boundaries;
- implementation-backed chapter and scenario.

## Findings resolved

1. public replay summary counters were not bounded to scenario size;
2. fixture expectation counts and sums could exceed the scenario contract;
3. benchmark output was not retained as a CI observation.

All were corrected with regression evidence. Final review count remains zero
until the last substantive documentation commit and CI complete.

## Pull-request inventory

- PR #16 is the only open PR;
- old PR #12 remains closed and supplies no review evidence;
- branch starts from verified post-governance `main`.

## Remaining before merge

- acquire and record benchmark artifact result;
- finish final report/index consistency;
- Foundation CI green on final substantive head;
- clean review round 1;
- clean review round 2 on same SHA;
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
