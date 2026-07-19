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
- exact persisted route and semantic-tag contract test;
- semantic-tag uniqueness test;
- instrumentation Home identity test;
- traversal of all four destinations;
- Activity recreation test preserving Demo.

### Emulator tooling

- `tools/android/run_emulator_smoke.sh`;
- command `sh tools/tdna check-android-emulator`;
- official `sdkmanager`, `avdmanager`, `emulator` and `adb` only;
- API 35 default x86_64 image and Pixel 2 profile;
- KVM access prepared outside the public runner;
- unique disposable AVD directory below a configured root;
- explicit path plus discovery preflight;
- one declared serial, `emulator-5554`;
- fail-fast when another Android device is online;
- bounded SDK install, AVD creation, ADB registration and boot;
- clean AVD without snapshots;
- connected instrumentation execution through `ANDROID_SERIAL`;
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

1. AVD state moved outside `build/android-emulator`;
2. AVD state deleted during cleanup;
3. artifact upload restricted to top-level JSON/TXT/LOG/PNG plus test reports;
4. report SHA supplied explicitly from the checked-out substantive head;
5. install/start/package assertions added before success report generation.

Run #246 is valid functional development evidence but not the final artifact-policy
evidence.

## Finding 3 — round 1 local-run safety

The first final review on head
`7b537d7603524dcc829a80a0110b37d72c4f1629` found three risks that did not
appear on the isolated hosted runner:

1. `TDNA_AVD_HOME` could point directly to a directory deleted with `rm -rf`;
2. unscoped ADB commands and `connectedDebugAndroidTest` could observe a phone or
   another emulator already attached to a developer machine;
3. the public runner changed `/dev/kvm` permissions through interactive `sudo`.

Correction:

1. `TDNA_AVD_ROOT` selects only a parent directory;
2. `mktemp` creates a unique `tdna-avd-*` child and cleanup removes only that child;
3. the emulator is bound to port 5554 and serial `emulator-5554`;
4. target operations use `adb -s` and Gradle receives `ANDROID_SERIAL`;
5. the runner rejects other online devices and verifies the final evidence serial;
6. KVM permission changes remain in the reviewed CI setup step; local execution
   only validates accessibility and fails with an actionable message.

## Finding 4 — persisted identifiers were not pinned by tests

After the runner fixes, the pre-clean review found that route round-trip and tag
uniqueness tests would still remain green if the persisted route strings or the
semantic identifiers changed together. That would weaken the stated stability
contract and could leave documentation or restored state behind.

Correction:

- JVM tests now assert the exact ordered route values;
- navigation tags and screen tags are asserted explicitly;
- uniqueness remains a separate invariant.

All four findings produced substantive commits, so clean reviews remain `0 / 2`
until a new exact-head CI is green and two new rounds complete.

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
serial = emulator-5554
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

Obtain one exact-head green run after the review fixes, inspect the bounded
artifacts, run two clean review rounds and merge with expected-head verification.
