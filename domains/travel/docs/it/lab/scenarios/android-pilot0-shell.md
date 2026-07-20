# Scenario: Android Pilot 0 teaching shell

id: `lab.android.pilot0-shell.v0`

status: `implementation-backed — APK build in PR #26`

## Learning goal

Seguire il passaggio dai contratti condivisi a una applicazione Android
installabile, distinguendo composition root, UI, artifact build e capacità di
prodotto.

## Prerequisites

- [Build riproducibile](../../37-build-riproducibile-gradle-wrapper.md)
- [Roadmap Android-first](../../55-roadmap-android-first-e-pilot.md)
- [Shell Android Pilot 0](../../58-shell-android-pilot0.md)

## User story

Un docente o studente installa la debug APK, naviga fra roadmap, demo e percorso
di studio, e verifica che la demo costruisca modelli TDNA shared reali senza
richiedere posizione, rete o account.

## Platforms

- Android API 26+;
- CI Linux per build;
- emulatore/dispositivo per esecuzione manuale.

## Trigger

```bash
sh tools/tdna check-android
```

## Expected evidence

```text
unit test green
lintDebug green
debug APK exists
instrumentation APK exists
SHA-256 manifest exists
manifest has no sensitive permission
Home/Pilot/Demo/Studio source paths exist
```

## Module path

```text
settings.gradle.kts
-> :apps:android
-> MainActivity
-> TravelDnaTheme
-> TravelDnaApp
-> PilotCatalog
-> shared contracts
```

## State ownership

| State | Owner | Lifetime |
| --- | --- | --- |
| selected screen | `TravelDnaApp` / saved Compose state | activity recreation |
| pilot milestones | immutable `PilotCatalog` | process/code version |
| demo snapshot | remembered immutable value | composition |
| real trip/location | absent | not applicable |

## Expected UI

```text
Travel DNA
Pilot 0 · shell Android didattica
Home | Pilot | Demo | Studio
```

## Privacy and safety

- no location permission;
- no network permission;
- no account;
- no user content;
- synthetic coordinate only;
- not intended for moving-vehicle interaction.

## Existing tests

- `PilotCatalogTest`;
- instrumentation Compose smoke source;
- Android lint;
- APK assembly;
- repository architecture/documentation checks.

## Manual tests still required

- emulator launch;
- physical-device installation;
- rotation;
- light/dark theme;
- font scaling;
- TalkBack traversal;
- no-permission observation.

## Common failures

- Android SDK absent;
- app module accidentally included in foundation-only commands;
- Android types imported in shared contracts;
- state lost on configuration change;
- APK compiled but not uploaded;
- instrumentation APK confused with executed device test;
- Pilot 0 described as road-ready.

## Non-goals

- location and foreground service;
- external-navigation intents;
- MapLibre;
- database or backend;
- signing for public release;
- road test.

## Related docs

- [Chapter 58](../../58-shell-android-pilot0.md)
- [Study path](../../56-percorso-studio-android-first.md)
- [Road protocol](../../57-protocollo-pilot-stradale-android.md)
- [Daily report](../../../project/daily/2026-07-18-android-first-pilot-shell.md)
