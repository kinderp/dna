# Daily development report — 2026-07-19 — Android emulator smoke

## Goal

Advance Android Pilot 0 from a CI-built APK to runtime evidence on an Android
emulator, while keeping the app permission-free, navigation bounded and CI
artifacts reviewable.

## Tracking

- issue: [#27](https://github.com/kinderp/tdna/issues/27);
- pull request: [#28](https://github.com/kinderp/tdna/pull/28);
- branch: `agent/android-pilot0-emulator-smoke`;
- verified base: `cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f`;
- risk: `R2`;
- authoritative final ledger: PR #28.

## Repository housekeeping

The stale aggregate Foundation issues #1 and #2 were closed as completed after
verifying that Foundations v0 and the Android shell are already on `main`.

## Implemented

### Bounded navigation

- explicit stable routes `home`, `pilots`, `demo`, `study`;
- total `fromSavedRoute` decoder with Home fallback;
- route persisted through `rememberSaveable`;
- semantic tag for every navigation item and screen root;
- no `valueOf` restoration path.

### Tests

- JVM round-trip test for all routes;
- null/unknown route fallback test;
- semantic-tag uniqueness test;
- instrumentation Home identity test;
- traversal of all four destinations;
- Activity recreation test preserving Demo.

### Emulator tooling

- `tools/android/run_emulator_smoke.sh`;
- command `sh tools/tdna check-android-emulator`;
- official `sdkmanager`, `avdmanager`, `emulator` and `adb` only;
- API 35 default x86_64 image and Pixel 2 profile;
- KVM access where available;
- disposable AVD home outside artifact output;
- explicit path plus discovery preflight;
- bounded SDK install, AVD creation, ADB registration and boot;
- clean AVD without snapshots;
- connected instrumentation execution;
- explicit APK install, Activity start and package-path assertions;
- screenshot, APK checksum and exact-head JSON report;
- bounded diagnostics and cleanup.

### CI

The workflow has two serial jobs:

```text
foundation
-> android-emulator
```

The emulator job runs only after the complete documentation/Java/Rust/KMP/Android
build job succeeds. No new third-party GitHub Action was introduced. Upload paths
are allowlisted and do not recursively include AVD disks.

### Documentation

- chapter 60;
- executable emulator scenario;
- updated repository and project indexes;
- updated milestone/development status;
- this report and permanent index entry.

## Finding 1 — run #240 timeout

The Foundation job was green on head
`cc66a9568d8edb17cc6dc04305d01ecdbee0762f`; the emulator job reached its
35-minute timeout.

Diagnostic artifact:

```text
Unknown AVD name [tdna_pilot0_api35]
HOME is defined but there is no file tdna_pilot0_api35.ini
```

Root cause:

```text
AVD not visible in emulator search path
-> emulator exited immediately
-> unbounded adb wait-for-device kept waiting
-> external job timeout cancelled the step
```

Correction:

1. explicit AVD home/path;
2. discovery preflight;
3. removal of `adb wait-for-device`;
4. shared registration/boot deadline;
5. PID checks and bounded diagnostics/cleanup.

## Finding 2 — run #246 artifact size

Run #246 was green for both build and emulator on head
`a980e8852383fca52d8445cc11950c86776965ea`. It proved that the corrected AVD
was discovered, booted and able to execute instrumentation.

The emulator artifact, however, measured approximately 512 MB because the AVD
home had been placed under the recursively uploaded evidence directory. The AVD
disks are reproducible execution state, not useful review evidence.

Correction:

1. AVD home moved to `RUNNER_TEMP` in CI and a separate build directory locally;
2. fail-fast if `TDNA_AVD_HOME` is inside `build/android-emulator`;
3. AVD home deleted during cleanup;
4. artifact upload restricted to top-level JSON/TXT/LOG/PNG plus test reports;
5. report SHA supplied explicitly from the checked-out substantive head;
6. install/start/package assertions added before success report generation.

Run #246 is valid functional development evidence but not the final artifact-policy
evidence. Both findings and fixes are substantive; clean reviews remain `0 / 2`.

## Expected final evidence

```text
build/android-emulator/emulator-smoke.json
build/android-emulator/pilot0-screen.png
build/android-emulator/activity-start.txt
build/android-emulator/package-path.txt
build/android-emulator/app-apk-sha256.txt
connected Android Test reports
```

Required report invariants:

```text
head_sha = exact substantive head
app_installed=true
activity_started=true
package_visible=true
instrumentation_task=connectedDebugAndroidTest
sensitive_permissions_requested=false
road_evidence=false
```

## Operational gate

The exact final SHA, CI run, bounded artifact digest, review IDs and merge commit
are recorded in PR #28. No repository commit is made after the two clean review
rounds.

## Non-goals

- physical-device install;
- GPS or permissions;
- process-death persistence;
- foreground service;
- external navigator intents;
- maps or providers;
- battery, road or production claims.

## Next step

Obtain one exact-head green run with a bounded emulator artifact, inspect the
report and test results, run two clean review rounds and merge with expected-head
verification.
