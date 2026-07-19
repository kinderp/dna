# Travel DNA development status

Last updated: 2026-07-19

## Milestones

- **Foundations and Travel DNA Lab v0 — done**
- **Android-first Pilot 0 — in progress**
- **Pilot 1 controlled road companion + Android Auto POI — planned**
- **Internal Navigation Beta — later, evidence-gated**

Recent verified merges:

```text
Foundations closure       7090882b40e747a85d812decb0b3567a1276d701
Pilot 0 shell             cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f
Pilot 0 emulator v0.2     6198bd0a6c5a89f0e32fa218015125b5d452703e
```

## Current serial slice

**Android Auto compliance roadmap and POI-first strategy**

- issue [#29](https://github.com/kinderp/tdna/issues/29);
- branch `agent/android-auto-compliance-roadmap`;
- verified base `6198bd0a6c5a89f0e32fa218015125b5d452703e`;
- risk `R1`;
- ADR `0011`;
- chapter `61`.

The slice is documentation and architecture only. It adds no Car App Library
dependency, manifest category, `CarAppService`, Android Automotive OS package or
Google Play submission.

## Delivered Android baseline

### Mobile shell and runtime evidence

- separate `apps/android` application composition root;
- AGP 9.3.0, Gradle 9.5.1, SDK 37, min SDK 26;
- AGP built-in Kotlin and Compose compiler plugin 2.4.0;
- Compose BOM 2026.06.00 and Material 3;
- permission-free single-activity shell;
- Home/Pilot/Demo/Studio screens;
- exact bounded routes and semantic identifiers;
- unit tests, lint, debug APK and instrumentation APK;
- isolated API 35 emulator smoke;
- exact-head install, Activity, package and screenshot evidence;
- bounded artifact without disposable AVD disks.

## Accepted automotive direction

```text
Pilot 0 v0.3
    interactive replay and route progress
    + provider-neutral DriverJourneySnapshot

Pilot 0 v0.4
    missed-exit/reroute and degraded driver-safe state

Automotive readiness spike
    Car App Library POI prototype + DHU

Pilot 1
    POI companion + external navigator handoff

Internal Navigation Beta
    category Navigation + guidance + AUTO_DRIVE + voice + car quality gates
```

Guardrails:

- Android Auto is not the phone Compose UI projected to the car;
- Pilot 1 declares no turn-by-turn authority for TDNA;
- `androidx.car.app` stays out of shared modules;
- no automotive manifest declaration enters before a dedicated runtime slice;
- Android emulator, DHU, Automotive OS emulator, vehicle and road evidence remain
  separate;
- policy and quality requirements are rechecked before each automotive slice.

## Pilot windows

```text
Pilot 0  2026-08-10 .. 2026-08-21
Pilot 1  2026-09-21 .. 2026-10-09
Pilot 2  2026-11-02 .. 2026-12-11, re-estimated after Pilot 1
```

The windows are planning targets, not public-release promises. The new automotive
readiness work must not silently consume the Pilot 1 field-validation scope; dates
are re-estimated after v0.3/v0.4 and the POI spike.

## Still absent

- physical-device installation evidence;
- manual font scaling, dark/light and TalkBack audit;
- interactive replay/progress UI;
- driver-safe shared snapshot;
- real GPS and permission UX;
- foreground service;
- external-navigation Intent adapter;
- Car App Library runtime module;
- Android Auto POI experience;
- Desktop Head Unit evidence;
- Android Automotive OS package;
- MapLibre/Valhalla/Ferrostar runtime;
- durable trip recorder;
- backend, chat and journal;
- signed/public distribution;
- road reliability and battery evidence.

## Next planned Android slices

```text
Pilot 0 v0.3  interactive deterministic replay, route progress, driver snapshot
Pilot 0 v0.4  missed-exit/reroute and degraded state in the app
Pilot 0 v0.5  external-navigation adapter and capability/failure model
Pilot 0 gate  physical-device/manual accessibility checklist
Auto readiness Car App Library POI spike and local DHU evidence
Pilot 1       foreground location, bounded recorder, external navigation, POI car surface
```

## Maintainer preparation

No new paid course is required. Use existing Manning/Pluralsight access and official
Android training. Before the automotive readiness spike:

- install/update Android Studio Android SDK tools;
- install the Desktop Head Unit package;
- have one Android phone suitable for Android Auto/DHU connection;
- review the current Android for Cars App Library and quality checklist;
- keep the production mobile package free of car declarations until the spike is
  review-ready.
