# Scenario — Android Pilot 0 emulator smoke

## Metadata

```text
id: android-pilot0-emulator-smoke-v0
status: executable in PR #28
platform: Android emulator / Linux CI
issue: #27
pull request: #28
privacy: synthetic data only
road evidence: false
```

## Learning goal

Seguire una APK dal checkout fino all'esecuzione su Android e distinguere build,
installazione, instrumentation, Activity recreation e prova fisica.

## Prerequisiti

- Java 21;
- Gradle Wrapper verificato;
- Android SDK con command-line tools;
- KVM consigliato;
- capitoli 58 e 60.

## Trigger

```bash
sh tools/tdna check-android-emulator
```

## Percorso

```text
PilotScreen route/tag contract
-> Compose NavigationBar semantics
-> MainActivitySmokeTest
-> official SDK system image
-> AVD headless
-> connectedDebugAndroidTest
-> explicit APK install/start
-> screenshot + package evidence
-> emulator-smoke.json
```

## Expected evidence

```text
build/android-emulator/emulator-smoke.json
build/android-emulator/pilot0-screen.png
build/android-emulator/activity-start.txt
build/android-emulator/package-path.txt
build/android-emulator/app-apk-sha256.txt
Android connected-test XML/HTML reports
```

Il report deve dichiarare:

```json
{"app_installed":true,"instrumentation_task":"connectedDebugAndroidTest","road_evidence":false,"scenario":"android-pilot0-emulator-smoke-v0","sensitive_permissions_requested":false}
```

Gli altri campi dipendono dal runner e dal commit esatto.

## State ownership

- `PilotScreen`: insieme chiuso delle destinazioni;
- `selectedRoute`: stato visuale salvabile posseduto da `TravelDnaApp`;
- Activity/Compose: restore durante recreation;
- script emulatore: lifecycle del processo AVD e della diagnostica;
- Gradle Android Test: installazione ed esecuzione dei test.

## Existing tests

- route note e fallback;
- unicità dei semantic tag;
- Home visibile;
- tutte le destinazioni raggiungibili;
- destinazione Demo conservata dopo recreation;
- manifest sorgente/fuso privo di permessi vietati.

## Failure evidence

In caso di errore il runner tenta di salvare:

```text
adb-devices.txt
logcat.txt
device-properties.txt
emulator.log
failure-screen.png
```

## Non-goals

- telefono fisico;
- process death completo;
- GPS;
- batteria;
- TalkBack manuale;
- mappa o navigatore esterno;
- affidabilità su strada.

## Questions

1. Perché una APK instrumentation compilata non è un test eseguito?
2. Quale stato viene provato da `ActivityScenario.recreate()`?
3. Perché il test usa tag semantici e non coordinate?
4. Quali failure possono avvenire prima dell'avvio dell'app?
5. Perché il report mantiene `road_evidence=false`?

## Related docs

- [Capitolo 60](../../60-emulator-smoke-e-navigazione-pilot0.md)
- [Shell Android](../../58-shell-android-pilot0.md)
- [Strategia test](../../30-strategia-test.md)
- [Roadmap Pilot 0](../../55-roadmap-android-first-e-pilot.md)
