# Travel DNA development status

Last updated: 2026-07-18

## Milestones

- **Foundations and Travel DNA Lab v0 — done**
- **Android-first Pilot 0 — in progress**
- **Pilot 1 controlled road companion — planned**

Foundations closure merge: `7090882b40e747a85d812decb0b3567a1276d701`.

## Active slice

**Android-first pilot roadmap and installable teaching shell**

- issue [#25](https://github.com/kinderp/tdna/issues/25);
- PR [#26](https://github.com/kinderp/tdna/pull/26);
- branch `agent/android-first-pilot-shell`;
- base `7090882b40e747a85d812decb0b3567a1276d701`;
- risk `R2`;
- ADR `0010`;
- chapters `55`–`58`;
- report `docs/project/daily/2026-07-18-android-first-pilot-shell.md`.

## Current deliverables

- separate `apps/android` application composition root;
- AGP 9.3.0, Gradle 9.5.1, SDK 37, min SDK 26;
- AGP built-in Kotlin and Compose compiler plugin 2.4.0;
- Compose BOM 2026.06.00 and Material 3;
- permission-free single-activity shell;
- Home/Pilot/Demo/Studio screens;
- real shared-contract demo snapshot;
- Android unit and instrumentation smoke sources;
- lint, debug APK and test APK build;
- CI artifact and checksum path;
- pilot roadmap, study path and future road protocol.

## Gate

- sole open PR: yes;
- first Android CI: finding-driven iterations in progress;
- final substantive SHA: not fixed;
- clean reviews: `0 / 2`;
- merge only after exact-head CI, no threads and expected-head guard.

## Pilot windows

```text
Pilot 0  2026-08-10 .. 2026-08-21
Pilot 1  2026-09-21 .. 2026-10-09
Pilot 2  2026-11-02 .. 2026-12-11, re-estimated after Pilot 1
```

## Still absent

- emulator/device execution evidence;
- real GPS and permission UX;
- foreground service;
- external-navigation Intent adapter;
- MapLibre/Valhalla/Ferrostar;
- durable trip recorder;
- backend, chat and journal;
- Android Auto;
- signed/public distribution;
- road reliability and battery evidence.

## Maintainer preparation

No new course purchase is required. Follow chapter 56 using existing Manning and
Pluralsight access plus official Android training. Before Pilot 1, provide at
least one physical Android device and participate in the written field checklist.
