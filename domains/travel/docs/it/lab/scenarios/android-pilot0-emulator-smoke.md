# Scenario — Android Pilot 0 emulator smoke

## Metadata

```text
id: android-pilot0-emulator-smoke-v0
status: implementation-backed in PR #28
platform: Android emulator / Linux CI
issue: #27
pull request: #28
privacy: synthetic data only
road evidence: false
```

## Learning goal

Seguire una APK dal checkout fino all'esecuzione su un solo runtime Android e
distinguere build, installazione, instrumentation, Activity recreation, prova su
telefono e prova su strada.

## Prerequisiti

- Java 21;
- Gradle Wrapper verificato;
- Android SDK con command-line tools;
- KVM configurato dall'host quando disponibile;
- nessun telefono o altro emulatore online;
- capitoli 58 e 60.

Il runner non modifica i privilegi KVM e non invoca `sudo`.

## Trigger

```bash
sh tools/tdna check-android-emulator
```

In CI il workflow passa:

```text
TDNA_SUBSTANTIVE_SHA = pull_request.head.sha oppure github.sha su push
```

Il report non deriva lo SHA dal merge ref implicito di un evento PR.

## Configurazione bounded

Valori di default:

```text
API 35
default x86_64
Pixel 2
porta 5554
seriale emulator-5554
boot deadline 360 secondi
```

`TDNA_AVD_ROOT` può selezionare la directory padre dello stato temporaneo. Il
runner crea con `mktemp` un figlio unico `tdna-avd-*` e cancella soltanto quel
figlio. La root non può trovarsi dentro `build/android-emulator`.

## Percorso

```text
PilotScreen route/tag exact contract
-> Compose NavigationBar semantics
-> MainActivitySmokeTest
-> official SDK system image
-> unique temporary AVD child
-> AVD discovery preflight
-> reject other online Android devices
-> start emulator-5554
-> ADB registration + boot bounded on that serial
-> connectedDebugAndroidTest through ANDROID_SERIAL
-> explicit APK install/start/package checks with adb -s
-> verify observed serial == emulator-5554
-> screenshot + checksum
-> exact-head emulator-smoke.json
-> cleanup only owned temporary AVD child
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
{"activity_started":true,"app_installed":true,"instrumentation_task":"connectedDebugAndroidTest","package_visible":true,"road_evidence":false,"scenario":"android-pilot0-emulator-smoke-v0","sensitive_permissions_requested":false,"serial":"emulator-5554"}
```

`head_sha` deve coincidere con lo SHA sostanziale del checkout e dell'intera CI.
Gli altri campi dichiarano API richiesta/reale, ABI e modello.

## State ownership

- `PilotScreen`: insieme chiuso delle destinazioni e valori persistiti esatti;
- `selectedRoute`: stato visuale salvabile posseduto da `TravelDnaApp`;
- Activity/Compose: restore durante recreation;
- workflow: identità dello SHA sostanziale e preparazione KVM;
- `TDNA_AVD_ROOT`: solo parent configurabile;
- script emulatore: figlio AVD temporaneo, seriale, deadline e diagnostica;
- Gradle Android Test: installazione ed esecuzione sul seriale dichiarato.

## Existing tests

- valori esatti delle route `home`, `pilots`, `demo`, `study`;
- valori esatti dei navigation tag e content tag;
- round-trip e fallback delle route;
- unicità dei semantic tag;
- Home visibile;
- tutte le destinazioni raggiungibili;
- destinazione Demo conservata dopo recreation;
- manifest sorgente/fuso privo di permessi vietati.

## Runtime assertions

Dopo i test instrumentation il runner verifica:

```text
adb -s emulator-5554 install output == Success
am start -W contiene Status: ok
pm path restituisce package:
get-serialno == emulator-5554
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
registrazione ADB produce un fallimento immediato.

## Failure mode principali

```text
root AVD dentro area artifact      -> fail-fast
altro device online                -> fail-fast
seriale emulator-5554 già occupato -> fail-fast
KVM non accessibile                -> errore senza sudo
AVD non scoperto                   -> errore prima del launch
registrazione/boot oltre deadline  -> diagnostica + cleanup
strumentation o asserzione ADB      -> report di test e file evidence
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
3. Perché route e tag esatti sono un contratto distinto dalla loro unicità?
4. Perché tutti i comandi ADB devono usare un seriale?
5. Perché il runner accetta una root ma crea autonomamente il figlio da eliminare?
6. Perché KVM viene preparato fuori dal comando pubblico?
7. Perché `road_evidence` rimane `false`?

## Related docs

- [Capitolo 60](../../60-emulator-smoke-e-navigazione-pilot0.md)
- [Shell Android](../../58-shell-android-pilot0.md)
- [Strategia test](../../30-strategia-test.md)
- [Roadmap Pilot 0](../../55-roadmap-android-first-e-pilot.md)
