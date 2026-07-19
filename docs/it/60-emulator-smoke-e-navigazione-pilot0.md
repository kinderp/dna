# Emulator smoke e navigazione verificata del Pilot 0

## Stato

`implementation-backed in PR #28`

Questo capitolo descrive la seconda slice Android del Pilot 0. La prima shell
compilava unit test, lint, APK debug e APK instrumentation; questa slice aggiunge
la prova che l'app viene realmente installata ed eseguita su un runtime Android
in CI.

La distinzione è importante:

```text
APK compilata
!= APK installata
!= test eseguito su Android
!= prova su telefono fisico
!= affidabilità su strada
```

## Obiettivi didattici

Al termine dovresti saper spiegare:

1. perché un test JVM non sostituisce un test instrumentation;
2. come `rememberSaveable` interagisce con la ricreazione dell'Activity;
3. perché le destinazioni devono avere identificatori stabili;
4. perché i test Compose usano semantics e non coordinate pixel;
5. come viene avviato e fermato un emulatore headless;
6. quali evidenze produce la CI e quali restano assenti;
7. perché un emulatore verde non autorizza ancora un pilot su strada.

## Slice

- issue [#27](https://github.com/kinderp/tdna/issues/27);
- PR [#28](https://github.com/kinderp/tdna/pull/28);
- branch `agent/android-pilot0-emulator-smoke`;
- base `cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f`;
- rischio `R2`.

## Problema affrontato

La shell iniziale possedeva quattro destinazioni e un test instrumentation
compilato, ma la CI non avviava un dispositivo Android. Non era quindi provato
che:

- l'Activity partisse su Android;
- ogni voce della navigation bar fosse raggiungibile;
- i nodi Compose fossero individuabili semanticamente;
- la destinazione selezionata sopravvivesse alla ricreazione dell'Activity;
- l'APK potesse essere installata dall'Android Debug Bridge;
- i test instrumentation venissero realmente eseguiti.

## Navigazione bounded

Le destinazioni del Pilot 0 sono un insieme chiuso:

```text
home
pilots
demo
study
```

Ogni `PilotScreen` possiede:

```text
route          identificatore persistito
label          testo per l'utente
navigationTag  identità semantica della voce di navigazione
contentTag     identità semantica della superficie
```

Il ripristino usa una funzione totale:

```kotlin
PilotScreen.fromSavedRoute(route)
```

Una route nota restituisce la destinazione corrispondente. `null` o una stringa
sconosciuta tornano a `Home`. Il processo non usa `valueOf`, quindi un valore
obsoleto o corrotto non deve causare un'eccezione durante la composizione.

## Perché la route non coincide con il nome dell'enum

Il nome Kotlin è una scelta di implementazione. La route è il dato persistito.
Separarli permette in futuro di rinominare una costante senza trasformare
involontariamente una refactor in una migrazione dello stato salvato.

Per questa slice le route non sono deep link pubblici e non costituiscono ancora
un contratto di navigazione esterno.

## Semantics e test tag

Le voci e le superfici espongono tag stabili:

```text
pilot-navigation-home
pilot-navigation-pilots
pilot-navigation-demo
pilot-navigation-study

pilot-screen-home
pilot-screen-pilots
pilot-screen-demo
pilot-screen-study
```

I test eseguono:

```text
find navigation node by semantic tag
-> click
-> find destination content by semantic tag
-> assert displayed
```

Non usano coordinate dello schermo, dimensioni del dispositivo o colori. Un test
basato sui pixel sarebbe fragile rispetto a font scaling, densità, layout e
accessibilità.

I tag sono strumenti di test e diagnostica. Non sostituiscono label e contenuti
accessibili agli utenti.

## Ricreazione dell'Activity

Il test seleziona `Demo`, verifica la superficie, invoca la ricreazione
controllata dell'Activity e verifica nuovamente la stessa superficie.

```text
Demo selected
-> rememberSaveable stores "demo"
-> Activity recreated
-> saved state restored
-> fromSavedRoute("demo")
-> Demo displayed
```

Questo prova la ricreazione dell'Activity nel processo del test. Non prova ancora:

- process death reale;
- restore dopo reboot;
- stato di una sessione viaggio;
- storage durevole;
- comportamento di un foreground service.

## Strategia dei test

### Unit test JVM

`PilotScreenTest` controlla:

- round-trip di tutte le route;
- fallback per valore nullo o sconosciuto;
- unicità dei tag semantici.

Queste proprietà non richiedono Android.

### Instrumentation test

`MainActivitySmokeTest` controlla su Android:

- identità della Home;
- raggiungibilità delle quattro destinazioni;
- persistenza della destinazione dopo `ActivityScenario.recreate()`.

### Manifest guard

Il checker già presente continua a verificare sorgente e manifest fuso. Il job
emulatore non introduce permessi.

## Emulator runner

Il comando pubblico è:

```bash
sh tools/tdna check-android-emulator
```

Il runner usa soltanto strumenti dell'Android SDK:

```text
sdkmanager
avdmanager
emulator
adb
Gradle Wrapper
```

Non viene introdotta una GitHub Action di terze parti per l'emulatore.

## Device dichiarato

Baseline della slice:

```text
API Android: 35
image: default x86_64
profilo: Pixel 2
UI: headless
GPU: SwiftShader indirect
snapshot: disabilitati
boot data: pulito a ogni run
```

L'app mantiene `compileSdk` e `targetSdk` 37. Il test su API 35 è una prova di
compatibilità runtime, non sostituisce una matrice di versioni e produttori.

## Sequenza del runner

```text
verifica ANDROID_HOME e tool
-> installa/aggiorna emulator e system image dichiarata
-> crea AVD da zero
-> abilita KVM quando disponibile
-> avvia emulator headless
-> adb wait-for-device
-> attende sys.boot_completed con timeout
-> disabilita animazioni
-> assembleDebug + assembleDebugAndroidTest
-> connectedDebugAndroidTest
-> installa esplicitamente APK debug
-> avvia MainActivity
-> acquisisce screenshot
-> verifica package path
-> produce report JSON e checksum
-> arresta emulatore
```

## Timeout e stato bounded

Il boot ha una scadenza configurabile, pari a 360 secondi nella CI iniziale. Il
runner non attende indefinitamente. Ogni esecuzione elimina e ricrea la directory
di output:

```text
build/android-emulator
```

L'AVD viene creato con dati puliti e senza snapshot persistenti.

## Diagnostica in caso di errore

Un errore attiva automaticamente la raccolta di:

```text
adb-devices.txt
device-properties.txt
emulator.log
logcat.txt
failure-screen.png, quando disponibile
```

Il processo emulatore viene arrestato anche quando Gradle o un test falliscono.
La diagnostica non deve contenere dati personali perché la shell usa esclusivamente
dati sintetici e non possiede account o rete.

## Evidenza di successo

Il report canonico è:

```text
build/android-emulator/emulator-smoke.json
```

Campi principali:

```text
scenario
head_sha
requested_api
device_api
abi
model
serial
instrumentation_task
app_installed
sensitive_permissions_requested
road_evidence
```

`road_evidence` rimane esplicitamente `false`.

Altri output:

```text
pilot0-screen.png
activity-start.txt
package-path.txt
adb-install.txt
app-apk-sha256.txt
```

I report HTML/XML di Android Test vengono caricati nello stesso artifact della CI.

## Confini di interpretazione

Una CI verde dimostra:

- build su checkout pulito;
- boot del device dichiarato;
- installazione dell'APK;
- esecuzione dei test instrumentation;
- navigazione semantica delle quattro superfici;
- restore durante Activity recreation;
- assenza dei permessi vietati già controllati dal build job.

Non dimostra:

- installazione su un telefono del maintainer;
- comportamento di OEM differenti;
- process death completo;
- accessibilità manuale con TalkBack;
- batteria o termiche;
- GPS;
- rete assente durante un viaggio;
- sicurezza di interazione alla guida;
- affidabilità su strada.

## Failure mode da studiare

### System image non disponibile

`sdkmanager` fallisce prima della creazione dell'AVD. Il package dichiarato deve
essere aggiornato in una PR revisionata, non scelto dinamicamente.

### Emulatore termina durante il boot

Il PID viene controllato durante l'attesa. Il log dell'emulatore viene conservato.

### Boot non completa

Il timeout chiude il run e raccoglie diagnosi.

### Test non trova un nodo

Il report instrumentation identifica il test e l'asserzione. Prima di cambiare il
test bisogna verificare se è cambiato un contratto semantico o solo il testo.

### Stato non ripristinato

Il test di recreation fallisce sulla superficie `Demo`. La correzione deve
preservare ownership e serializzabilità dello stato, non aggiungere storage globale.

## Esercizi

1. Aggiungere una destinazione fittizia in una branch e aggiornare test e tag.
2. Dimostrare che una route sconosciuta non causa crash.
3. Rompere volontariamente un tag e leggere il report instrumentation.
4. Ridurre il timeout di boot e osservare gli artifact diagnostici.
5. Spiegare la differenza tra Activity recreation e process death.
6. Proporre una matrice fisica minima senza dichiarare equivalenza con l'emulatore.

## Passo successivo

Dopo il merge di questa slice il Pilot 0 possiede evidenza automatizzata di
runtime. Restano separati:

```text
installazione su telefono fisico
-> verifica manuale tema/font/TalkBack
-> replay e route progress interattivi
-> eventuale candidate APK interna
```

GPS, servizi foreground e navigatori esterni appartengono alle slice successive e
non devono entrare per comodità nel test emulatore.

## Riferimenti interni

- [Roadmap Android-first](55-roadmap-android-first-e-pilot.md)
- [Percorso di studio](56-percorso-studio-android-first.md)
- [Protocollo stradale](57-protocollo-pilot-stradale-android.md)
- [Shell Android Pilot 0](58-shell-android-pilot0.md)
- [Materiali e acquisti](59-materiali-didattici-e-acquisti-pilot-android.md)
- [Strategia test](30-strategia-test.md)
- [Debugging e strumenti](34-debugging-e-strumenti.md)
- [Scenario emulator smoke](lab/scenarios/android-pilot0-emulator-smoke.md)
