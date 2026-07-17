# Travel DNA development status

Last updated: 2026-07-17

## Current milestone

**Foundations and Travel DNA Lab v0 — in progress**

## Completed slices

### Java/Rust reference-routing Lab

- issue [#3](https://github.com/kinderp/tdna/issues/3);
- PR [#4](https://github.com/kinderp/tdna/pull/4);
- merge `d122f1b4871719087e79a50b185ab302d810cb20`;
- chapter `docs/it/43-reference-routing-java-rust.md`.

### Provider-neutral routing contracts and fake planner

- issue [#5](https://github.com/kinderp/tdna/issues/5);
- PR [#6](https://github.com/kinderp/tdna/pull/6);
- merge `2a28d1654988cef4188986342f76fd7d19be358f`;
- chapter `docs/it/44-contratti-routing-e-fake-provider.md`.

### Two-clean-review governance and routing hardening

- issue [#8](https://github.com/kinderp/tdna/issues/8);
- PR [#9](https://github.com/kinderp/tdna/pull/9);
- merge `044e0773dd9afb1530db35688a00c56bfbd5eace`;
- two clean rounds required on every shipping PR.

### Provider-neutral MapScene and fake renderer

- issue [#7](https://github.com/kinderp/tdna/issues/7);
- PR [#10](https://github.com/kinderp/tdna/pull/10);
- final head `a18e73ad015951545c808d489ee66c57c5d1c80d`;
- Foundation CI #80 green;
- reviews `4719736270` and `4719739041` clean;
- merge `2128f24b4a6ab00aeb437ed48a0af2910f33e9ec`;
- chapter `docs/it/45-map-scene-e-fake-renderer.md`.

## Active slice

**Serial PR governance and autonomous gated merge**

- issue [#14](https://github.com/kinderp/tdna/issues/14);
- PR [#15](https://github.com/kinderp/tdna/pull/15);
- branch `agent/serial-pr-governance`;
- report `docs/project/daily/2026-07-17-pr-discipline.md`;
- risk `R1`, documentation and workflow governance.

## Active deliverables

- one ordinary open PR at a time;
- no stacked, placeholder or `noop` PRs;
- next branch from the verified post-merge `main`;
- explicit administrative-closure exception for non-shipping PRs;
- standing autonomous merge authority constrained by every gate;
- expected-head merge guard;
- post-merge verification before the next PR;
- aligned AGENTS, contributor guide, operational rules and PR template.

## Pull-request inventory

At PR creation:

- PR #10 was merged;
- PR #12 and #13 were closed without merge;
- no ordinary PR remained open;
- PR #15 becomes the single active PR.

## Milestone still missing

- `LocationSample` and deterministic clock;
- GPS replay runner;
- missed-exit guidance state machine;
- first benchmark report;
- committed Gradle Wrapper;
- Android/iOS targets;
- real MapLibre or routing provider adapter.

## Next executable step

Complete CI and two clean review rounds for PR #15, merge it with expected-head
guard, verify the new `main`, then create a fresh LocationSample/replay branch
from that exact commit.

## Maintainer decisions

None. The maintainer has granted standing authorization for autonomous merge
after every documented gate is met.
