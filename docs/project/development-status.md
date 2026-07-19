# Travel DNA development status

Last updated: 2026-07-19

## Milestones

- **Foundations and Travel DNA Lab v0 — done**
- **Android-first Pilot 0 — in progress**
- **Pilot 1 controlled road companion — planned**

Recent verified merges:

```text
Foundations closure  7090882b40e747a85d812decb0b3567a1276d701
Pilot 0 shell        cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f
```

## Current serial slice

**Android Pilot 0 v0.2 — emulator smoke, bounded navigation and install evidence**

- issue [#27](https://github.com/kinderp/tdna/issues/27);
- PR [#28](https://github.com/kinderp/tdna/pull/28);
- branch `agent/android-pilot0-emulator-smoke`;
- verified base `cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f`;
- risk `R2`;
- chapter `60`;
- scenario `docs/it/lab/scenarios/android-pilot0-emulator-smoke.md`;
- report `docs/project/daily/2026-07-19-android-emulator-smoke.md`.

PR #28 is the durable operational ledger for its exact head, CI, bounded artifact,
clean reviews and merge. This file intentionally does not duplicate transient
checkboxes that would become stale after merge.

## Implementation delivered by the slice

### Existing merged shell

- separate `apps/android` application composition root;
- AGP 9.3.0, Gradle 9.5.1, SDK 37, min SDK 26;
- AGP built-in Kotlin and Compose compiler plugin 2.4.0;
- Compose BOM 2026.06.00 and Material 3;
- permission-free single-activity shell;
- Home/Pilot/Demo/Studio screens;
- shared-contract deterministic snapshot;
- unit tests, lint, debug APK and instrumentation APK;
- manifest and plugin-classpath guards;
- direct Manning/Pluralsight/free learning and purchase guide.

### Emulator/runtime slice

- bounded `PilotScreen` route model with safe Home fallback;
- stable semantic tags for navigation items and screen roots;
- JVM tests for route restoration and tag uniqueness;
- instrumentation coverage of all four surfaces;
- Activity recreation test for selected destination;
- official-SDK AVD runner using API 35 x86_64;
- explicit AVD path and discovery preflight;
- bounded SDK install, ADB registration, boot, diagnostics and cleanup;
- install, Activity start and package visibility assertions;
- exact-head JSON report and screenshot;
- AVD disks excluded from bounded review artifacts;
- dedicated CI job after the complete Foundation/Android build gate.

## Operational closure rule

The slice is considered complete only when PR #28 records:

```text
one exact substantive head
-> Foundation/build job green
-> emulator runtime job green
-> bounded artifact inspected
-> no unresolved threads
-> two clean reviews on the same SHA
-> expected-head merge
-> issue/main/open-PR verification
```

Until that ledger is complete, merge remains forbidden. After merge, the same PR
continues to be the authoritative evidence without requiring a post-review commit.

## Pilot windows

```text
Pilot 0  2026-08-10 .. 2026-08-21
Pilot 1  2026-09-21 .. 2026-10-09
Pilot 2  2026-11-02 .. 2026-12-11, re-estimated after Pilot 1
```

## Still absent

- physical-device installation evidence;
- manual font scaling, dark/light and TalkBack audit;
- real GPS and permission UX;
- foreground service;
- external-navigation Intent adapter;
- MapLibre/Valhalla/Ferrostar;
- durable trip recorder;
- backend, chat and journal;
- Android Auto;
- signed/public distribution;
- road reliability and battery evidence.

## Next planned Android slices

```text
Pilot 0 v0.3  interactive deterministic replay and route progress
Pilot 0 v0.4  missed-exit/reroute teaching state in the app
Pilot 0 gate  physical-device/manual accessibility checklist
Pilot 1       external navigation, foreground location and bounded recorder
```

## Maintainer preparation

No new course purchase is required. Follow chapters 56 and 59 using existing
Manning and Pluralsight access plus official Android training. Before Pilot 1,
provide at least one physical Android device and participate in the written field
checklist.
