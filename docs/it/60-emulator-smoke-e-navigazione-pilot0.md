# Emulator smoke e navigazione verificata del Pilot 0

## Stato

`implementation-backed in PR #28; operational closure in the PR ledger`

Questo capitolo descrive la seconda slice Android del Pilot 0. La shell precedente
compilava unit test, lint, APK debug e APK instrumentation; questa slice aggiunge
la prova che l'app viene installata ed eseguita su un runtime Android isolato in
CI.

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
3. perché route persistite e semantic tag devono avere valori stabili e testati;
4. perché i test Compose usano semantics e non coordinate pixel;
5. come viene creato, scoperto, avviato e fermato un AVD headless;
6. perché tutti i comandi ADB devono riferirsi a un solo seriale dichiarato;
7. perché registrazione ADB e boot devono avere un deadline;
8. perché il runner pubblico non deve modificare i privilegi KVM dell'host;
9. perché i dischi AVD non sono artifact di review;
10. quali evidenze produce la CI e quali restano assenti.

## Slice

- issue [#27](https://github.com/kinderp/tdna/issues/27);
- PR [#28](https://github.com/kinderp/tdna/pull/28);
- branch `agent/android-pilot0-emulator-smoke`;
- base `cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f`;
- rischio `R2`.

Il ledger esatto di SHA, CI, artifact, review e merge è nella PR. Il capitolo resta
valido sia durante la review sia dopo il merge.

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
sconosciuta tornano a `Home`. Non viene usato `valueOf`, quindi un dato obsoleto o
corrotto non causa un'eccezione durante la composizione.

Il nome Kotlin dell'enum è una scelta di implementazione; la route è il dato
persistito. I test JVM bloccano esplicitamente i quattro valori, così una refactor
non può diventare accidentalmente una migrazione dello stato salvato.

## Semantics e contratti di test

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

I test JVM verificano separatamente:

- valori esatti e ordine delle route persistite;
- valori esatti dei tag di navigazione;
- valori esatti dei tag delle superfici;
- round-trip route → destinazione;
- fallback per route nulla o sconosciuta;
- unicità dei tag.

Il percorso instrumentation è:

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

`PilotScreenTest` controlla contratti puri che non richiedono Android.

### Instrumentation test

`MainActivitySmokeTest` controlla su Android:

- identità della Home;
- raggiungibilità delle quattro destinazioni;
- persistenza di `Demo` dopo `ActivityScenario.recreate()`.

### Manifest guard

Il checker esistente continua a verificare manifest sorgente e fuso. Il job
emulatore non introduce permessi sensibili.

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

## Device e seriale dichiarati

```text
API Android: 35
image: default x86_64
profilo: Pixel 2
seriale: emulator-5554
porta: 5554
UI: headless
GPU: SwiftShader indirect
snapshot: disabilitati
boot data: pulito a ogni run
```

L'app mantiene `compileSdk` e `targetSdk` 37. API 35 è una singola prova di
compatibilità runtime, non una matrice di versioni o produttori.

Ogni comando destinato al device usa `adb -s emulator-5554`. Il runner esporta
anche `ANDROID_SERIAL`, così `connectedDebugAndroidTest` è vincolato allo stesso
emulatore. Se un telefono o un altro emulatore risulta online, il comando fallisce
prima dei test: una prova isolata non deve dipendere dal device collegato per caso.

## Stato AVD con ownership sicura

`TDNA_AVD_ROOT` sceglie soltanto la directory padre. Il runner crea al suo interno
una directory unica:

```text
tdna-avd-<nome>.<suffisso-casuale>
```

mediante `mktemp`. Il cleanup elimina esclusivamente quel figlio creato dal run.
Un valore di configurazione non viene mai trattato direttamente come directory da
cancellare.

La root AVD non può ricadere dentro `build/android-emulator`, che contiene solo le
evidenze revisionabili.

## KVM e privilegi dell'host

Il runner pubblico non usa `sudo`, non esegue `chmod` e non modifica la macchina.
Se `/dev/kvm` esiste ma non è leggibile e scrivibile, fallisce con un messaggio
esplicito.

La CI prepara KVM in uno step separato e revisionato del workflow. Su una macchina
locale i permessi devono essere configurati dall'amministratore prima di eseguire
il comando.

## Sequenza del runner

```text
verifica ANDROID_HOME e tool richiesti
-> installa/aggiorna emulator e system image dichiarata con timeout
-> normalizza TDNA_AVD_ROOT
-> crea un figlio AVD unico con mktemp
-> crea AVD con path esplicito
-> verifica il nome con emulator -list-avds
-> verifica accessibilità KVM senza cambiare privilegi
-> avvia ADB server
-> rifiuta altri device online o seriale già occupato
-> avvia emulator headless su porta 5554
-> attende registrazione adb -s emulator-5554 con deadline e controllo PID
-> attende sys.boot_completed sullo stesso deadline
-> disabilita animazioni sul seriale dichiarato
-> assembleDebug + assembleDebugAndroidTest
-> connectedDebugAndroidTest con ANDROID_SERIAL
-> installa APK debug con adb -s e verifica Success
-> avvia MainActivity e verifica Status: ok
-> verifica package path
-> acquisisce screenshot e checksum
-> verifica che il seriale osservato sia emulator-5554
-> produce report JSON legato allo SHA sostanziale
-> arresta emulatore e cancella soltanto il figlio AVD temporaneo
```

## Timeout e stato bounded

I limiti iniziali sono:

```text
sdkmanager install          900 secondi
avdmanager create            60 secondi
registrazione + boot        360 secondi complessivi
singolo comando diagnostico  10 secondi
job GitHub Actions           35 minuti
```

Il runner non usa `adb wait-for-device`, perché può attendere indefinitamente
quando il processo emulatore è già terminato. Un loop controlla insieme stato ADB,
PID e deadline.

Le aree hanno ownership differenti:

```text
RUNNER_TEMP/tdna-avd-* oppure <TDNA_AVD_ROOT>/tdna-avd-*
    stato pesante, univoco e usa-e-getta del dispositivo virtuale

build/android-emulator
    report, log, testo e screenshot bounded da revisionare
```

L'upload CI consente soltanto file top-level `.json`, `.txt`, `.log` e `.png`,
oltre ai report connected-test dichiarati.

## Finding risolti durante lo sviluppo

### AVD non scoperto e attesa infinita

Una prima run creava l'AVD fuori dal percorso cercato da `emulator`; il processo
terminava, mentre `adb wait-for-device` restava bloccato. Sono stati aggiunti path
esplicito, discovery preflight, deadline e controllo PID.

### Artifact da circa 512 MB

Una run verde aveva incluso i dischi AVD nell'artifact. Lo stato virtuale è stato
spostato fuori dall'area evidenze e l'upload è stato trasformato in allowlist.

### Directory configurabile eliminata direttamente

Una versione del runner accettava `TDNA_AVD_HOME` e la cancellava con `rm -rf`.
Ora `TDNA_AVD_ROOT` seleziona solo un padre e `mktemp` crea il figlio esatto di cui
il processo possiede il lifecycle.

### Device non isolato

Comandi ADB non qualificati potevano osservare un telefono o un altro emulatore.
Ora seriale, porta, `adb -s`, `ANDROID_SERIAL`, preflight e report finale sono
correlati.

### Privilegi KVM nel comando pubblico

Il runner invocava `sudo chmod`. La mutazione è stata rimossa: la CI prepara KVM
nel workflow, mentre il comando locale verifica soltanto i prerequisiti.

### Identificatori dichiarati stabili ma non bloccati

Round-trip e unicità non proteggevano i valori esatti. I test ora fissano route e
tag persistiti, separando stabilità da unicità.

Ogni finding ha prodotto un commit sostanziale e ha azzerato le review pulite.

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

Ogni comando ADB diagnostico è bounded e indirizzato al seriale dichiarato quando
possibile.

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
activity_started
package_visible
sensitive_permissions_requested
road_evidence
```

`head_sha` arriva dal checkout sostanziale della CI, non dal merge ref implicito.
`serial` deve essere `emulator-5554`. `road_evidence` rimane `false`.

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

- checkout pulito e identità dello SHA;
- creazione, discovery e boot del device dichiarato;
- isolamento sul seriale dichiarato;
- installazione dell'APK e avvio Activity;
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

### Root AVD dentro l'area artifact

Il runner rifiuta la configurazione prima di creare il device.

### Altro device online

Il preflight rifiuta la prova per evitare evidenze provenienti da un target
ambiguo.

### Seriale 5554 già occupato

Il runner non riusa un emulatore precedente: chiede di arrestarlo.

### KVM non accessibile

Il comando fallisce senza tentare escalation di privilegi.

### Emulatore termina prima di ADB

Il PID viene controllato durante la registrazione e il runner fallisce subito.

### Boot non completa

La deadline condivisa chiude il run e raccoglie diagnosi.

### Test o asserzione runtime fallisce

I report e i file `adb-install.txt`, `activity-start.txt` e `package-path.txt`
mostrano quale contratto non è stato soddisfatto.

## Esercizi

1. Aggiungere una destinazione fittizia e aggiornare route, tag e test esatti.
2. Dimostrare che una route sconosciuta non causa crash.
3. Rompere volontariamente un tag e leggere il report instrumentation.
4. Collegare un secondo device e osservare il fail-fast di isolamento.
5. Impostare `TDNA_AVD_ROOT` nell'area evidence e verificare il rifiuto.
6. Ridurre il timeout di boot e osservare gli artifact diagnostici.
7. Spiegare Activity recreation, process death e reboot.

## Passo successivo

Dopo la chiusura operativa della slice restano separati:

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
