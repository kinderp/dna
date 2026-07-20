# Daily development report — 2026-07-18 — Android-first Pilot 0

## Goal

Start the first mobile product milestone after Foundations v0: record the
Android-first decision, publish the pilot/study/field-test roadmap and build a
permission-free installable Jetpack Compose teaching shell.

## Tracking

- issue: [#25](https://github.com/kinderp/tdna/issues/25);
- pull request: [#26](https://github.com/kinderp/tdna/pull/26);
- branch: `agent/android-first-pilot-shell`;
- verified base: `7090882b40e747a85d812decb0b3567a1276d701`;
- risk: `R2`;
- final review/merge ledger: PR #26.

## Decision

Android-first was selected by the maintainer on 2026-07-18 and recorded in
`ADR-0010`.

## Implemented

### Build and module boundary

- Android application subproject `apps/android`;
- Android Gradle Plugin 9.3.0;
- Gradle Wrapper 9.5.1;
- compile/target SDK 37, min SDK 26;
- AGP built-in Kotlin for the app;
- Kotlin 2.4.0 Compose compiler plugin;
- Compose BOM 2026.06.00 and Material 3;
- Google Maven repository;
- conditional app include so foundation Labs run without Android SDK;
- hosted Android SDK 37 revision-family verification;
- shared common-code guard against `android.*` and `androidx.*`.

### Application

- single `MainActivity`;
- light/dark Compose theme;
- bounded four-screen navigation state;
- Home, pilot roadmap, deterministic demo and study screens;
- real construction of `LocationSample`, `RouteCoordinate` and `OffRoutePolicy`;
- no permission, network, storage, service or map SDK;
- unit tests for pilot ordering, non-goals and demo ground truth;
- instrumentation Compose smoke source.

### Tooling and evidence

- `sh tools/tdna check-android`;
- unit test, lint, debug APK and instrumentation APK build;
- source and merged-manifest policy checker;
- explicit rejection of location, foreground-service, notification, network,
  Bluetooth and nearby-device permissions;
- debug/test APK copied to `build/android`;
- SHA-256 artifact manifest;
- machine-readable manifest report;
- CI upload of APKs and observations.

### Documentation

- ADR-0010;
- chapters 55–58;
- Android Pilot 0 scenario;
- study path using official Android, Pluralsight and Manning material;
- future road-pilot protocol;
- repository, milestone, status, Lab and report indexes;
- Android module README.

## Pilot windows

```text
Pilot 0 teaching shell     2026-08-10 .. 2026-08-21
Pilot 1 road companion     2026-09-21 .. 2026-10-09
Pilot 2 closed beta        2026-11-02 .. 2026-12-11, re-estimated after Pilot 1
```

These are planning windows, not production commitments.

## Purchases

No additional course or book is required. Manning and Pluralsight subscriptions
plus official Android training cover the initial learning path. A physical
Android phone is the first useful purchase only if the maintainer has no suitable
device before Pilot 1.

## Development CI evidence

Foundation CI run `#202` passed on head
`ba24af50092c37e2125d61c4de5b8797e3f5fcc3` before the manifest-policy hardening.
It proved:

```text
foundation documentation/architecture/Java/Rust/KMP    success
Android SDK verification                                success
Android unit test and lint                              success
debug APK and instrumentation APK                       success
artifact upload                                          success
```

Artifact `8429618558`, digest
`sha256:582c348ffbb2f4d6e12ef060f1c0a93bd06ba7c9e386f3c2e29a2a3b4ef995b2`,
contained:

```text
tdna-pilot0-debug.apk                 11,649,284 bytes
tdna-pilot0-debug-androidTest.apk      1,131,167 bytes
```

Recorded APK hashes:

```text
0488d6d8a33eee066885ed58bdbdd3fe4efbb2c8d363db65af673348af7ff3cf  app
aca8022d8f27654500023705e5379853e7f6ec10a25bc8b1ad74b6ef1f71ef3c  instrumentation
```

These hashes identify that CI output. The project does not claim that separately
built debug APKs are bit-for-bit reproducible.

## Findings corrected

1. CI attempted to reinstall Android packages already present on the hosted image.
2. SDK verification assumed one platform directory name despite revisioned API 37 packages.
3. shared common-code checks covered only `android.location`; they now reject general Android/AndroidX references.
4. “reproducible APK” wording overstated current evidence; artifact identity is now expressed as commit plus SHA-256.
5. the permission-free property was source-only; the build now validates source and merged manifests and publishes a report.

Every substantive fix resets the clean-review counter.

## Evidence still required before merge

- green CI on the manifest-hardened final substantive head;
- Android manifest report in the artifact;
- zero unresolved threads;
- two consecutive clean review rounds on the same SHA;
- expected-head merge and post-merge verification.

## Pilot 0 manual evidence still required after merge

- emulator installation and launch;
- physical-device installation;
- rotation and process recreation observations;
- light/dark theme and font scaling;
- TalkBack traversal;
- no-permission observation.

Compilation of the instrumentation APK is not execution of the test on a device.

## Non-goals

- real GPS;
- foreground service;
- external-navigation Intent;
- MapLibre or routing provider;
- Room/Hilt/Firebase;
- signed/public distribution;
- road reliability.

## Next executable step

Obtain a green exact-head CI with the manifest report, complete the two final
review rounds, merge PR #26 and then perform the Pilot 0 emulator/device
installation checklist as the next serial slice.
