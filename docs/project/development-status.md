# Travel DNA development status

Last updated: 2026-07-18

## Current milestone

**Foundations and Travel DNA Lab v0 — closure candidate in PR #24**

The implementation scope of Navigation Runtime Replay v0 that belongs to the
foundation is complete: matching boundary, route progress, off-route evidence and
reroute coordination are already on `main`.

## Completed and merged slices

| Slice | Issue / PR | Merge |
| --- | --- | --- |
| Java/Rust reference routing | #3 / #4 | `d122f1b4871719087e79a50b185ab302d810cb20` |
| Provider-neutral routing contracts | #5 / #6 | `2a28d1654988cef4188986342f76fd7d19be358f` |
| Two-clean-review governance | #8 / #9 | `044e0773dd9afb1530db35688a00c56bfbd5eace` |
| MapScene and fake renderer | #7 / #10 | `2128f24b4a6ab00aeb437ed48a0af2910f33e9ec` |
| Serial PR governance | #14 / #15 | `76680433089842db5805d28eb50416a23c7d0a88` |
| LocationSample and deterministic replay | #11 / #16 | `020f8495f7fbbae81f1463b098b0ddd2a079c873` |
| Matched position and route progress | #17 / #18 | `9921fbcc1da1000e6434bdae49646122cae8f0e0` |
| Map-matching port and fake | #19 / #20 | `95cf2f0d900b039efb86ba0a570e3ca3f8d8cef5` |
| Missed exit and reroute | #21 / #22 | `8570eb466b43384756f2678da2303929295e087a` |

## Active slice

**Reproducible Gradle Wrapper and Foundations v0 closure**

- issue [#23](https://github.com/kinderp/tdna/issues/23);
- PR [#24](https://github.com/kinderp/tdna/pull/24);
- branch `agent/gradle-wrapper-foundation-closure`;
- verified base `8570eb466b43384756f2678da2303929295e087a`;
- risk `R2`;
- closure report `docs/project/foundation-v0-closure.md`;
- daily report `docs/project/daily/2026-07-18-gradle-wrapper-foundation-closure.md`.

## Implemented in PR #24

- committed Gradle 9.5.1 Wrapper for POSIX and Windows;
- distribution checksum and reviewed Wrapper-JAR digest;
- repository policy for wrapper files and hashes;
- fail-closed Wrapper drift checker;
- immutable allowlisted GitHub Actions references;
- Kotlin build and Lab commands through `./gradlew`;
- global Gradle made optional;
- build-bootstrap Lab with canonical JSON output;
- chapter 37, executable scenario and supply-chain explanation;
- Foundation v0 closure report;
- clean-checkout-oriented CI bootstrap and diagnostic artifacts.

## Final gate for PR #24

- sole open PR: yes;
- wrapper, checks, code and teaching documentation: present;
- project records and indexes: being finalized on the same branch;
- final substantive SHA: set after the last record alignment;
- CI: required on that exact SHA;
- unresolved review threads: must be zero;
- clean review rounds: `0 / 2` until the final SHA is fixed;
- merge: only after ready state and expected-head guard.

## Product direction after Foundations v0

The maintainer selected **Android-first** on 2026-07-18.

The next milestone will start only after PR #24 is merged and `main` is verified.
Its first goal is an installable Android internal-pilot shell that consumes the
existing shared contracts without introducing a production navigator prematurely.

Initial pilot scope candidate:

```text
Android app shell
-> Compose navigation and design tokens
-> create/select a synthetic or local trip
-> show canonical route/map state through a platform adapter or controlled placeholder
-> launch an external navigator through an adapter
-> keep a bounded local trip session
-> replay synthetic location data for demos
-> expose diagnostics and teaching screens
```

GPS field recording, MapLibre integration and external-navigation intents will be
added in subsequent gated slices rather than hidden inside the shell bootstrap.

## Still absent after Foundations v0

- production Android or iOS app;
- real GPS lifecycle and permissions;
- MapLibre adapter;
- Valhalla/Ferrostar adapter;
- real map matching and production off-route thresholds;
- backend, account, chat and journal;
- Android Auto and CarPlay;
- road-tested reliability, battery and accessibility evidence.

## Maintainer decisions

Android-first is decided. No further product decision blocks closure of PR #24.
The exact pilot release channel and tester group can be chosen when the first APK
or App Bundle is reproducible in CI.
