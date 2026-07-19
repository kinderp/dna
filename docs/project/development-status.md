# Travel DNA development status

Last updated: 2026-07-19

## Milestones

- **Foundations and Travel DNA Lab v0 — done**
- **Android-first Pilot 0 — in progress**
- **Pilot 1 controlled road companion — planned**

Recent merges:

```text
Foundations closure  7090882b40e747a85d812decb0b3567a1276d701
Pilot 0 shell        cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f
```

## Active slice

**Android Pilot 0 v0.2 — emulator smoke, bounded navigation and install evidence**

- issue [#27](https://github.com/kinderp/tdna/issues/27);
- PR [#28](https://github.com/kinderp/tdna/pull/28);
- branch `agent/android-pilot0-emulator-smoke`;
- base `cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f`;
- risk `R2`;
- chapter `60`;
- scenario `docs/it/lab/scenarios/android-pilot0-emulator-smoke.md`;
- report `docs/project/daily/2026-07-19-android-emulator-smoke.md`.

## Current deliverables

### Merged shell

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

### Active emulator slice

- bounded `PilotScreen` route model with safe Home fallback;
- stable semantic tags for navigation items and screen roots;
- JVM tests for route restoration and tag uniqueness;
- instrumentation coverage of all four surfaces;
- Activity recreation test for selected destination;
- official-SDK AVD runner using API 35 x86_64;
- bounded boot timeout, logcat, device properties and screenshot diagnostics;
- dedicated CI job and emulator evidence artifact.

## Gate

- sole open PR: #28;
- final substantive SHA: not fixed while findings/documentation can change;
- build CI: development run required;
- emulator CI: development run required;
- unresolved threads: none at slice start;
- clean reviews: `0 / 2`;
- merge only after both jobs are green on the exact head and expected-head guard.

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
