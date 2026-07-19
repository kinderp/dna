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

In CI il workflow passa anche:

```text
TDNA_SUBSTANTIVE_SHA = pull_request.head.sha oppure github.sha su push
```

Il report non deve derivare lo SHA dal merge ref implicito di un evento PR.

## Percorso

```text
PilotScreen route/tag contract
-> Compose NavigationBar semantics
-> MainActivitySmokeTest
-> official SDK system image
-> AVD home/path espliciti
-> AVD discovery preflight
-> ADB registration + boot bounded
-> connectedDebugAndroidTest
-> explicit APK install/start/package checks
-> screenshot + checksum
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

Il report deve dichiarare almeno:

```json
{"activity_started":true,"app_installed":true,"instrumentation_task":"connectedDebugAndroidTest","package_visible":true,"road_evidence":false,"scenario":"android-pilot0-emulator-smoke-v0","sensitive_permissions_requested":false}
```

`head_sha` deve coincidere con lo SHA sostanziale del checkout e dell'intera CI.
Gli altri campi dichiarano API richiesta/reale, ABI, modello e seriale emulatore.

## State ownership

- `PilotScreen`: insieme chiuso delle destinazioni;
- `selectedRoute`: stato visuale salvabile posseduto da `TravelDnaApp`;
- Activity/Compose: restore durante recreation;
- workflow: identità dello SHA sostanziale;
- script emulatore: lifecycle AVD, deadline e diagnostica;
- Gradle Android Test: installazione ed esecuzione dei test.

## Existing tests

- route note e fallback;
- unicità dei semantic tag;
- Home visibile;
- tutte le destinazioni raggiungibili;
- destinazione Demo conservata dopo recreation;
- manifest sorgente/fuso privo di permessi vietati.

## Runtime assertions

Dopo i test instrumentation il runner verifica esplicitamente:

```text
adb install output == Success
am start -W contiene Status: ok
pm path restituisce package:
```

Solo dopo queste asserzioni scrive `app_installed`, `activity_started` e
`package_visible` come `true`.

## Failure evidence

In caso di errore il runner tenta di salvare:

```text
avdmanager-create.log
avd-list.txt
adb-devices.txt
logcat.txt
device-properties.txt
emulator.log
failure-screen.png
```

Ogni comando diagnostico è bounded. Un processo emulatore terminato prima della
registrazione ADB deve produrre un fallimento immediato, non attendere il timeout
del job.

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
4. Perché `adb wait-for-device` non è sufficiente come deadline?
5. Perché il report riceve lo SHA sostanziale dal workflow?
6. Perché `road_evidence` rimane `false`?

## Related docs

- [Capitolo 60](../../60-emulator-smoke-e-navigazione-pilot0.md)
- [Shell Android](../../58-shell-android-pilot0.md)
- [Strategia test](../../30-strategia-test.md)
- [Roadmap Pilot 0](../../55-roadmap-android-first-e-pilot.md)
