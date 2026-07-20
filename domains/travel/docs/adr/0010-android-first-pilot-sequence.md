# ADR-0010 — Android-first pilot sequence

- Status: accepted
- Date: 2026-07-18
- Decision owner: maintainer
- Related issue: [#25](https://github.com/kinderp/tdna/issues/25)
- Supersedes: none

## Context

Travel DNA has completed its provider-neutral Foundations v0. The repository now
contains executable contracts, deterministic fakes, replay, map-scene models,
route progress, map-matching boundaries and missed-exit/reroute coordination, but
it has no mobile application.

The next step must turn those foundations into something installable without
prematurely claiming production navigation, background reliability or road
safety. Starting Android and iOS simultaneously would introduce two build
systems, two UI frameworks, two lifecycle models and two device-test programs
before the product flow has been validated on either platform.

The maintainer selected Android-first on 2026-07-18.

## Decision

Travel DNA will build its first installable product surface on Android.

The first Android application will live in a separate module:

```text
apps/android
```

It will consume provider-neutral shared modules and will not combine the Android
application plugin and Kotlin Multiplatform plugin in the same Gradle subproject.

The delivery order is:

```text
Pilot 0 — installable teaching shell
-> Pilot 1 — controlled foreground road companion
-> Pilot 2 — small closed beta
```

Each delivery is decomposed into serial vertical slices. The repository keeps at
most one pull request open, and every merge requires exact-head CI plus two
consecutive clean review rounds on the same substantive SHA.

## Pilot definitions

### Pilot 0 — installable teaching shell

Target window: 2026-08-10 to 2026-08-21.

Audience:

- maintainer;
- students;
- emulator;
- one to three physical Android devices.

Capabilities:

- installable debug APK;
- single-activity Jetpack Compose shell;
- Android composition root;
- deterministic fake trip and replay;
- route-progress and missed-exit teaching screens;
- local bounded UI/session state;
- diagnostics and links to the corresponding repository chapters;
- unit tests and instrumentation smoke test;
- reproducible CI artifact.

Non-goals:

- location permission;
- real GPS;
- foreground/background trip service;
- real map or routing provider;
- chat/backend;
- road-reliability claims;
- public Play release.

### Pilot 1 — controlled road companion

Target window: 2026-09-21 to 2026-10-09.

Audience:

- five to ten invited testers;
- declared routes;
- explicit feedback and incident protocol.

Candidate capabilities:

- foreground location permission flow;
- foreground service with persistent notification only while a trip is active;
- start, pause, resume and end trip;
- bounded local location/event recording;
- external-navigation handoff and return flow;
- canonical route or shadow-route context where available;
- local timeline and first daily-page draft;
- redacted diagnostic export;
- lifecycle, restart, offline, battery and accessibility observations;
- signed internal APK or App Bundle.

The first road pilot will not request background-location permission. It will not
upload precise location by default and will not require reading or typing while
driving.

### Pilot 2 — small closed beta

Earliest credible window: 2026-11-02 to 2026-12-11.

Candidate capabilities, subject to Pilot 1 evidence:

- MapLibre adapter and real OpenStreetMap-based map surface;
- stronger trip/session recovery;
- account/backend seed;
- constrained conversation or road-question prototype;
- fifteen to thirty invited testers;
- broader device, battery and accessibility matrix.

Pilot 2 scope and dates are re-estimated after Pilot 1 rather than treated as a
fixed commitment.

## Initial Android stack

The first implementation will validate this candidate stack in CI:

```text
Android Gradle Plugin 9.3.0
Gradle Wrapper 9.5.1
Android SDK compile/target 37
minimum SDK 26
AGP built-in Kotlin for the Android app module
Kotlin 2.4.0 Compose compiler plugin
Jetpack Compose BOM 2026.06.00
Material 3
single activity
```

The first shell deliberately avoids adding dependency injection, persistence,
networking or map SDKs. A dependency is introduced only when a vertical slice has
a use case, an adapter boundary, tests, documentation and a supply-chain review.

## APK sequence

```text
APK 0.1 — empty installable shell and build smoke
APK 0.2 — design system, bounded navigation and pilot/study screens
APK 0.3 — deterministic replay and route-progress demonstration
APK 0.4 — missed-exit/reroute teaching flow and diagnostics
APK 0.5 — external-navigation adapter prototype
APK 0.6 — foreground location adapter and permission UX
APK 0.7 — foreground trip service and bounded recorder
APK 0.8 — Pilot 1 candidate, device matrix and tester guide
```

Version numbers are internal milestone labels until release/versioning policy is
formalized.

## Consequences

### Positive

- Kotlin/Java knowledge and the existing shared modules are reused immediately.
- Linux CI can build the first Android artifacts.
- The product flow is validated on one lifecycle model before adding iOS.
- Android Auto preparation follows the same platform path later.
- Students get a concrete application while the architecture remains
  provider-neutral.

### Negative

- iOS parity is delayed.
- Android lifecycle, permissions and device fragmentation become immediate work.
- A later iOS shell still requires Swift, Xcode, Apple signing and its own field
  evidence.
- The first pilot must resist Android-specific types leaking into shared
  contracts.

## Alternatives considered

### iOS-first

Rejected for the first pilot because it would require macOS/Xcode/Swift and Apple
signing before the existing Kotlin/JVM build can produce an installable product.
It remains a planned second platform.

### Android and iOS in parallel

Rejected because it violates the current serial-delivery strategy and doubles
platform uncertainty before product validation.

### Continue with shared modules only

Rejected because the next major risk is no longer purely algorithmic: it is the
mobile composition root, UI, lifecycle, packaging and device behavior.

### Build the full navigator first

Rejected because Pilot 0 and Pilot 1 can validate Travel DNA as a companion while
using an external navigator. A production navigation engine is not required to
learn whether the diary, journey context and social value are useful.

## Guardrails

1. `apps/android` may depend on shared contracts and selected Android adapters;
   shared modules must not depend on the application module.
2. Android framework types are converted at the adapter boundary.
3. The first shell requests no sensitive permission.
4. Real location is introduced in a dedicated PR with privacy and lifecycle
   review.
5. MapLibre, routing providers, Room, Hilt, Firebase and analytics are not bundled
   into the shell bootstrap.
6. APK artifacts are internal until signing, distribution and privacy rules are
   documented.
7. Every road test uses a written safety protocol and a non-driving operator for
   interactive observations.
8. Pilot dates are evidence-based planning windows, not promises of production
   readiness.

## Revisit conditions

Revisit this ADR if:

- the Android build cannot consume the shared contracts without redesign;
- Pilot 0 reveals a product flow that is fundamentally iOS-dependent;
- device availability blocks field verification;
- a partnership imposes an iOS-first requirement;
- a major toolchain incompatibility invalidates the selected stack.

## References

- [Android Gradle plugin 9.3 release notes](https://developer.android.com/build/releases/agp-9-3-0-release-notes)
- [Migrate to AGP built-in Kotlin](https://developer.android.com/build/migrate-to-built-in-kotlin)
- [Jetpack Compose setup](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler)
- [Compose BOM](https://developer.android.com/develop/ui/compose/bom)
- [Android location permissions](https://developer.android.com/develop/sensors-and-location/location/permissions)
