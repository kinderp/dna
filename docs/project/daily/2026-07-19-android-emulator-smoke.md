# Daily development report — 2026-07-19 — Android emulator smoke

## Goal

Advance Android Pilot 0 from a CI-built APK to runtime evidence on an Android
emulator, while keeping the app permission-free and the navigation state bounded.

## Tracking

- issue: [#27](https://github.com/kinderp/tdna/issues/27);
- pull request: [#28](https://github.com/kinderp/tdna/pull/28);
- branch: `agent/android-pilot0-emulator-smoke`;
- verified base: `cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f`;
- risk: `R2`;
- final review/merge ledger: PR #28.

## Repository housekeeping

The stale aggregate Foundation issues #1 and #2 were closed as completed after
verifying that Foundations v0 and the Android shell are already on `main`.

## Implemented

### Bounded navigation

- explicit stable routes `home`, `pilots`, `demo`, `study`;
- total `fromSavedRoute` decoder with Home fallback;
- route persisted through `rememberSaveable`;
- semantic tag for every navigation item;
- semantic tag for every screen root;
- no `valueOf` restoration path.

### Tests

- JVM round-trip test for all routes;
- null/unknown route fallback test;
- semantic-tag uniqueness test;
- instrumentation Home identity test;
- instrumentation traversal of all four destinations;
- Activity recreation test preserving Demo.

### Emulator tooling

- `tools/android/run_emulator_smoke.sh`;
- command `sh tools/tdna check-android-emulator`;
- official `sdkmanager`, `avdmanager`, `emulator` and `adb` only;
- API 35 default x86_64 image and Pixel 2 profile;
- KVM access where available;
- bounded 360-second boot timeout in CI;
- clean AVD without snapshots;
- connected instrumentation execution;
- explicit APK installation and Activity launch;
- success screenshot, package path, start output, APK checksum and JSON report;
- logcat, properties and screenshot diagnostics on failure.

### CI

The workflow now has two serial jobs:

```text
foundation
-> android-emulator
```

The emulator job runs only after the complete documentation/Java/Rust/KMP/Android
build job succeeds. No new third-party GitHub Action was introduced.

### Documentation

- chapter 60;
- executable emulator scenario;
- updated repository documentation indexes;
- updated milestone and development status;
- this report and permanent index entry.

## Expected evidence

```text
build/android-emulator/emulator-smoke.json
build/android-emulator/pilot0-screen.png
build/android-emulator/activity-start.txt
build/android-emulator/package-path.txt
build/android-emulator/app-apk-sha256.txt
connected Android Test reports
```

Expected report invariants:

```text
app_installed=true
instrumentation_task=connectedDebugAndroidTest
sensitive_permissions_requested=false
road_evidence=false
```

## Development gate

At report creation:

- final substantive SHA is not fixed;
- build and emulator CI must be observed after the documentation commits;
- any finding or substantive change resets clean reviews;
- clean-review count is `0 / 2`;
- merge remains forbidden until exact-head CI and reviews are complete.

## Non-goals

- physical-device install;
- GPS or permissions;
- process-death persistence;
- foreground service;
- external navigator intents;
- maps or providers;
- battery, road or production claims.

## Next executable step

Obtain exact-head build and emulator evidence, inspect the artifact, fix any
finding, freeze the substantive head, run two clean review rounds and merge with
expected-head verification.
