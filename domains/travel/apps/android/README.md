# Travel DNA Android

`apps/android` is the Android application composition root. It owns Android
framework integration and Jetpack Compose UI; it consumes shared TDNA contracts
without exporting Android types back into them.

## Pilot 0 scope

The first shell is intentionally permission-free and deterministic. It shows the
pilot roadmap, a shared-contract snapshot and the study path. It performs no
network, storage, location, service or map work.

## Build

Prerequisites:

```text
Java 21
Android SDK platform 37
Android SDK Build Tools 36.0.0
```

Commands:

```bash
sh tools/tdna check-android
./gradlew :apps:android:assembleDebug
```

Generated APKs are copied to `build/android/` by the project command.

## Boundaries

```text
apps/android
-> shared contracts
-> AndroidX / Android framework

shared contracts
-X-> apps/android
-X-> android.*
```

Pilot 0 does not add Hilt, Room, Firebase, MapLibre, a routing SDK or location
permissions. Each belongs to a later use-case-driven adapter slice.
