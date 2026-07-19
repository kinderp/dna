# Project tooling

Entry point:

```bash
sh tools/tdna COMMAND
```

## Core commands

| Command | Purpose |
| --- | --- |
| `doctor` | Java/Python/Rust/Wrapper/Android SDK/global Gradle status. |
| `check-gradle-wrapper` | Wrapper version and checksum policy. |
| `check-ci-actions` | Immutable reviewed GitHub Action SHAs. |
| `check-android-build-policy` | Mixed Android/KMP plugin IDs, version refs and root classpath contract. |
| `check-docs` | Markdown links and fences. |
| `check-architecture` | Shared Kotlin source boundaries. |
| `check-java` | Java routing. |
| `check-rust` | Rust routing. |
| `check-contract` | Java/Rust byte report equality. |
| `check-kotlin` | Foundation KMP tests/Labs without Android SDK. |
| `check-android` | Android policy, unit tests, lint, manifest guard and APK builds. |
| `check-android-emulator` | Boot declared AVD, run instrumentation and collect runtime evidence. |
| `check` | Complete non-Android foundation verification. |

## Android build

Prerequisites:

```text
Java 21
Android SDK Platform 37
Build Tools 36.0.0
```

```bash
sh tools/tdna check-android-build-policy
sh tools/tdna check-android
```

The policy check runs before Gradle and requires the Android application,
Compose compiler, Kotlin Multiplatform and Kotlin JVM plugin families to remain
preloaded together in the root `plugins` block. This is a tested classloader
contract of the mixed Android/KMP build, not cosmetic centralization.

Build outputs:

```text
build/android/tdna-pilot0-debug.apk
build/android/tdna-pilot0-debug-androidTest.apk
build/android/manifest-report.json
build/android/sha256.txt
```

## Android emulator smoke

Additional requirements:

```text
Android SDK command-line tools
emulator package
system-images;android-35;default;x86_64
KVM when available
bash, adb, avdmanager and sdkmanager
```

Run:

```bash
sh tools/tdna check-android-emulator
```

Optional environment overrides:

```text
TDNA_EMULATOR_API_LEVEL
TDNA_EMULATOR_ABI
TDNA_EMULATOR_DEVICE_PROFILE
TDNA_EMULATOR_AVD_NAME
TDNA_EMULATOR_BOOT_TIMEOUT_SECONDS
```

Default CI device:

```text
API 35
x86_64 default system image
Pixel 2 profile
360-second boot timeout
headless, clean data, no snapshots
```

Success evidence:

```text
build/android-emulator/emulator-smoke.json
build/android-emulator/pilot0-screen.png
build/android-emulator/activity-start.txt
build/android-emulator/package-path.txt
build/android-emulator/app-apk-sha256.txt
```

Failure diagnostics attempt to preserve:

```text
build/android-emulator/adb-devices.txt
build/android-emulator/device-properties.txt
build/android-emulator/emulator.log
build/android-emulator/logcat.txt
build/android-emulator/failure-screen.png
```

The runner uses official Android SDK tools and does not hide emulator lifecycle
inside a third-party GitHub Action. It always attempts cleanup through `adb emu
kill` and the process PID.

A green emulator proves installation and declared instrumentation behavior on one
virtual device. It is not physical-device, battery, accessibility or road
evidence.

## Foundation-only Gradle graph

`check-kotlin`, Labs and benchmarks pass:

```text
-Ptdna.includeAndroid=false
```

so deterministic shared work remains accessible without installing the Android
SDK. Android commands include the app by default. The Android/Compose plugin
markers remain declared `apply false` at the root so they share a compatible
classloader with the Kotlin plugin families; excluding the Android subproject
still prevents Android tasks from entering foundation commands.

## Generated outputs

All generated artifacts stay under `build/` or module build directories. Never
commit APKs, traces, Gradle caches or real user locations.

## Rules

- local and CI use the same project commands;
- missing required tools fail explicitly;
- new commands require documentation and CI in the same PR;
- emulator boot is timeout-bounded and diagnostics are retained;
- benchmarks are observations, not SLA;
- emulator, physical-device and field evidence remain separate.
