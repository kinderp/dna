# Emulator smoke e navigazione verificata del Pilot 0

## Stato

`implementation-backed in PR #28`

Questo capitolo descrive la seconda slice Android del Pilot 0. La shell precedente
compilava unit test, lint, APK debug e APK instrumentation; questa slice aggiunge
la prova che l'app viene installata ed eseguita su un runtime Android in CI.

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
5. come viene creato, scoperto, avviato e fermato un AVD headless;
6. perché registrazione ADB e boot devono avere un deadline;
7. quali evidenze produce la CI e quali restano assenti;
8. perché un emulatore verde non autorizza ancora un pilot su strada.

## Slice

- issue [#27](https://github.com/kinderp/tdna/issues/27);
- PR [#28](https://github.com/kinderp/tdna/pull/28);
- branch `agent/android-pilot0-emulator-smoke`;
- base `cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f`;
- rischio `R2`.

## Problema affrontato

La prima shell possedeva quattro destinazioni e un test instrumentation compilato,
ma la CI non avviava un dispositivo Android. Non era quindi provato che:

- l'Activity partisse su Android;
- ogni voce della navigation bar fosse raggiungibile;
- i nodi Compose fossero individuabili semanticamente;
- la destinazione selezionata sopravvivesse alla ricreazione dell'Activity;
- l'APK potesse essere installata con ADB;
- i test instrumentation venissero realmente eseguiti.

## Navigazione bounded

Le destinazioni costituiscono un insieme chiuso:

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
sconosciuta tornano a `Home`. Il processo non usa `valueOf`, quindi un dato
obsoleto o corrotto non causa un'eccezione durante la composizione.

### Route e nome dell'enum

Il nome Kotlin è una scelta di implementazione; la route è il dato persistito.
Separarli permette di rinominare una costante senza trasformare una refactor in
una migrazione involontaria dello stato salvato. Queste route non sono ancora deep
link pubblici.

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

Il percorso di test è:

```text
find navigation node by semantic tag
-> click
-> find destination content by semantic tag
-> assert displayed
```

Non usa coordinate, densità o colori. I tag sono strumenti di test e diagnostica;
non sostituiscono label e contenuti accessibili agli utenti.

## Ricreazione dell'Activity

Il test seleziona `Demo`, verifica la superficie, ricrea l'Activity e verifica
nuovamente la stessa destinazione:

```text
Demo selected
-> rememberSaveable stores "demo"
-> Activity recreated
-> saved state restored
-> fromSavedRoute("demo")
-> Demo displayed
```

Questo prova la ricreazione dell'Activity nel processo del test. Non prova process
death reale, restore dopo reboot, storage durevole o stato di una sessione viaggio.

## Strategia dei test

### Unit test JVM

`PilotScreenTest` controlla:

- round-trip di tutte le route;
- fallback per valore nullo o sconosciuto;
- unicità dei tag semantici.

### Instrumentation test

`MainActivitySmokeTest` controlla su Android:

- identità della Home;
- raggiungibilità delle quattro destinazioni;
- persistenza di `Demo` dopo `ActivityScenario.recreate()`.

### Manifest guard

Il checker esistente continua a verificare manifest sorgente e fuso. Il job
emulatore non introduce permessi.

## Emulator runner

Il comando pubblico è:

```bash
sh tools/tdna check-android-emulator
```

Il runner usa soltanto strumenti ufficiali dell'Android SDK:

```text
sdkmanager
avdmanager
emulator
adb
Gradle Wrapper
```

Non viene introdotta una GitHub Action di terze parti per l'emulatore.

## Device dichiarato

```text
API Android: 35
image: default x86_64
profilo: Pixel 2
UI: headless
GPU: SwiftShader indirect
snapshot: disabilitati
boot data: pulito a ogni run
```

L'app mantiene `compileSdk` e `targetSdk` 37. API 35 è una singola prova di
compatibilità runtime, non una matrice di versioni o produttori.

## Sequenza del runner

```text
verifica ANDROID_HOME, tool e command timeout
-> installa/aggiorna emulator e system image dichiarata
-> imposta ANDROID_AVD_HOME dentro build/android-emulator
-> crea AVD con path esplicito
-> verifica il nome con emulator -list-avds
-> abilita KVM quando disponibile
-> avvia emulator headless
-> attende registrazione adb con deadline e controllo PID
-> attende sys.boot_completed sullo stesso deadline
-> disabilita animazioni
-> assembleDebug + assembleDebugAndroidTest
-> connectedDebugAndroidTest
-> installa esplicitamente APK debug
-> avvia MainActivity
-> acquisisce screenshot e package path
-> produce report JSON e checksum
-> arresta emulatore
```

## Timeout e stato bounded

I limiti iniziali sono:

```text
sdkmanager install       900 secondi
avdmanager create         60 secondi
registrazione + boot     360 secondi complessivi
singolo comando diagnostico 10 secondi
job GitHub Actions        35 minuti
```

Il runner non usa più `adb wait-for-device`, perché quell'operazione può attendere
indefinitamente quando il processo emulatore è già terminato. Un loop controlla
invece insieme:

```text
adb get-state
PID del processo emulator
deadline monotona del runner
```

Ogni esecuzione elimina e ricrea:

```text
build/android-emulator
```

L'AVD vive in una `ANDROID_AVD_HOME` esplicita sotto tale directory, con dati
puliti e senza snapshot persistenti.

## Finding del primo run emulatore

La run di sviluppo #240 ha prodotto questo log:

```text
Unknown AVD name [tdna_pilot0_api35]
HOME is defined but there is no file tdna_pilot0_api35.ini
```

`avdmanager` aveva terminato senza rendere l'AVD visibile nel percorso cercato da
`emulator`. Il processo emulatore è quindi uscito immediatamente. Subito dopo lo
script era entrato in `adb wait-for-device`, che non controllava né PID né deadline;
il job è stato cancellato soltanto dal timeout esterno.

La correzione è strutturale:

1. `ANDROID_AVD_HOME` è esplicita e contenuta negli artifact;
2. `avdmanager --path` crea l'AVD nel percorso dichiarato;
3. `emulator -list-avds` verifica l'identità prima dell'avvio;
4. la registrazione ADB è un loop bounded;
5. l'uscita prematura del processo fallisce immediatamente;
6. diagnostica e cleanup hanno a loro volta timeout.

Questo finding è sostanziale e azzera qualunque conteggio di review precedente.

## Diagnostica in caso di errore

Il runner tenta di salvare:

```text
avdmanager-create.log
avd-list.txt
adb-devices.txt
device-properties.txt
emulator.log
logcat.txt
failure-screen.png, quando disponibile
```

Il processo viene arrestato anche quando Gradle o un test falliscono. Ogni comando
ADB diagnostico è bounded, così la raccolta delle prove non può diventare un
secondo blocco infinito.

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

I report HTML/XML di Android Test vengono caricati nello stesso artifact.

## Confini di interpretazione

Una CI verde dimostra:

- checkout pulito;
- creazione, discovery e boot del device dichiarato;
- installazione dell'APK;
- esecuzione dei test instrumentation;
- navigazione semantica delle quattro superfici;
- restore durante Activity recreation;
- assenza dei permessi vietati verificata dal build job.

Non dimostra:

- installazione su un telefono del maintainer;
- comportamento di OEM differenti;
- process death completo;
- accessibilità manuale con TalkBack;
- batteria o termiche;
- GPS, rete o guida;
- affidabilità su strada.

## Failure mode da studiare

### System image non disponibile

`sdkmanager` fallisce o raggiunge il timeout prima della creazione dell'AVD.

### AVD non visibile

La lista AVD non contiene il nome dichiarato; il runner fallisce prima di avviare
un processo destinato a terminare.

### Emulatore termina prima di ADB

Il PID viene controllato durante la registrazione e il runner fallisce subito.

### Boot non completa

La deadline condivisa chiude il run e raccoglie diagnosi.

### Test non trova un nodo

Il report instrumentation identifica test e asserzione; bisogna verificare il
contratto semantico prima di cambiare il test.

### Stato non ripristinato

Il test di recreation fallisce sulla superficie `Demo`; la correzione deve
preservare ownership e serializzabilità senza aggiungere storage globale.

## Esercizi

1. Aggiungere una destinazione fittizia e aggiornare test e tag.
2. Dimostrare che una route sconosciuta non causa crash.
3. Rompere volontariamente un tag e leggere il report instrumentation.
4. Usare un nome AVD non valido e verificare il fail-fast.
5. Ridurre il timeout di boot e osservare gli artifact diagnostici.
6. Spiegare Activity recreation, process death e reboot.
7. Proporre una matrice fisica minima senza equipararla all'emulatore.

## Passo successivo

Dopo il merge della slice restano separati:

```text
installazione su telefono fisico
-> verifica manuale tema/font/TalkBack
-> replay e route progress interattivi
-> eventuale candidate APK interna
```

GPS, servizi foreground e navigatori esterni appartengono alle slice successive.

## Riferimenti interni

- [Roadmap Android-first](55-roadmap-android-first-e-pilot.md)
- [Percorso di studio](56-percorso-studio-android-first.md)
- [Protocollo stradale](57-protocollo-pilot-stradale-android.md)
- [Shell Android Pilot 0](58-shell-android-pilot0.md)
- [Materiali e acquisti](59-materiali-didattici-e-acquisti-pilot-android.md)
- [Strategia test](30-strategia-test.md)
- [Debugging e strumenti](34-debugging-e-strumenti.md)
- [Scenario emulator smoke](lab/scenarios/android-pilot0-emulator-smoke.md)
