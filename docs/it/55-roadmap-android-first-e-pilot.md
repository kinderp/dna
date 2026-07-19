# Android-first: roadmap dei pilot e sequenza APK

## Stato

`implementation-backed in PR #26 — decisione accettata da ADR-0010`

Questo capitolo trasforma la scelta Android-first in una sequenza verificabile.
Distingue una APK didattica installabile da un pilot stradale e da una beta
chiusa, così una build che si avvia non viene confusa con un prodotto sicuro da
usare in viaggio.

## Obiettivi

Al termine dovresti saper spiegare:

1. perché Android viene prima di iOS;
2. che cosa differenzia Pilot 0, Pilot 1 e Pilot 2;
3. quali tecnologie entrano nella prima APK e quali restano fuori;
4. perché `apps/android` è separato dai moduli Kotlin Multiplatform;
5. quale evidenza serve prima di usare l'app su strada;
6. quali account, dispositivi e acquisti servono davvero;
7. come la sequenza APK riduce il rischio senza bloccare l'apprendimento.

## Decisione Android-first

La decisione formale è in
[ADR-0010](../adr/0010-android-first-pilot-sequence.md).

Motivazioni principali:

- riuso immediato di Kotlin, Java e dei moduli shared esistenti;
- build e CI iniziali possibili su Linux;
- una sola piattaforma mobile da capire durante la validazione del prodotto;
- percorso naturale verso Android Auto, ma senza anticiparlo;
- iOS resta una seconda piattaforma, non un non-obiettivo definitivo.

## Lessico dei rilasci interni

### Build

Un artifact generato dal sistema di build. Può non essere installabile o
sufficientemente verificato.

### APK debug

Pacchetto Android installabile per sviluppo. Non è una release pubblica, può
contenere diagnostica e usa una firma debug.

### APK instrumentation

Pacchetto di test installato insieme all'app su emulatore o dispositivo. Non è
un'app per tester finali.

### App Bundle

Formato destinato soprattutto alla distribuzione tramite Google Play. Non serve
per il primo Pilot 0 distribuito direttamente.

### Pilot

Esperimento controllato con pubblico, obiettivi, criteri di uscita, non-obiettivi
e protocollo di raccolta evidenze.

## Stack iniziale

| Area | Decisione corrente | Perché |
| --- | --- | --- |
| Build | Gradle Wrapper 9.5.1 | È già verificato e comune a locale/CI. |
| Android plugin | AGP 9.3.0 | Supporta API 37 ed è compatibile con Gradle 9.5.x. |
| SDK | `compileSdk=37`, `targetSdk=37` | La UI Compose corrente richiede toolchain aggiornata. |
| Compatibilità | `minSdk=26` | Riduce il carico legacy del pilot mantenendo una base ampia. |
| Kotlin app | built-in Kotlin di AGP | Evita il plugin `kotlin-android` nel modulo app. |
| Kotlin shared | Kotlin Multiplatform 2.4.0 | Conserva i contratti esistenti JVM/Linux. |
| Compose compiler | plugin Kotlin 2.4.0 | Versione allineata al compilatore Kotlin del repository. |
| UI | Jetpack Compose + Material 3 | UI dichiarativa, stato esplicito e test semantici. |
| Versioni Compose | BOM stabile 2026.06.00 | Allinea le librerie Compose compatibili. |
| Activity | `activity-compose` 1.13.0 | Entry point single-activity corrente. |
| Java | runtime/toolchain 21, bytecode Android 17 | Il repository usa Java 21; Android mantiene target compatibile. |
| Architettura | single activity + composable state hoisted | Riduce componenti prima del lifecycle reale. |
| DI | nessun framework nel Pilot 0 | Composition root manuale finché il bisogno non è dimostrato. |
| Persistenza | nessuna nel Pilot 0 | Evita Room/DataStore prima di un caso d'uso durable. |
| Rete | nessuna nel Pilot 0 | La shell deve funzionare senza backend o account. |

Il modulo applicazione usa:

```text
apps/android
-> Android framework / AndroidX
-> shared contracts selezionati
```

È vietata la dipendenza inversa:

```text
shared/* -> apps/android
shared/* -> android.*
```

## Perché il modulo app è separato

AGP 9 abilita Kotlin integrato nei moduli Android. I moduli KMP continuano a usare
il plugin multiplatform. Unire applicazione Android e plugin KMP nello stesso
subproject renderebbe ambiguo il confine e non è supportato dalla toolchain
corrente.

La separazione rende visibili tre responsabilità:

```text
shared contracts      semantica provider-neutral
Android adapters      conversione API piattaforma
apps/android          composition root, lifecycle e UI
```

## Pilot 0 — shell didattica installabile

### Finestra target

**10–21 agosto 2026**.

La finestra assume CI disponibile e nessun blocco prolungato della toolchain. È
una pianificazione, non una promessa di release pubblica.

### Pubblico

- maintainer;
- studenti;
- emulatore;
- uno–tre dispositivi fisici.

### Flusso

```text
installazione APK debug
-> Home
-> roadmap Pilot 0/1/2
-> snapshot dei contratti condivisi
-> percorso di studio
-> diagnostica build/versione
```

### Cosa dimostra

- AGP, Wrapper, SDK e Compose funzionano nel monorepo;
- `apps/android` consuma contratti TDNA esistenti;
- una UI Android può restare priva di permessi e rete;
- unit test e instrumentation test compilano;
- CI produce APK e checksum;
- uno studente può collegare una schermata ai modelli spiegati nei capitoli.

### Cosa non dimostra

- GPS;
- comportamento background;
- mappa reale;
- navigazione esterna;
- database;
- batteria su viaggio;
- resilienza del processo;
- sicurezza durante la guida.

### Criteri di uscita

- APK debug installabile su emulatore;
- installazione su almeno un telefono;
- nessun permesso sensibile nel manifest;
- schermate leggibili in modalità chiara e scura;
- rotazione/configurazione senza perdita dello screen selezionato;
- unit test verdi;
- APK di test compilata;
- artifact CI con SHA-256;
- walkthrough per studenti.

## Pilot 1 — companion stradale controllato

### Finestra target

**21 settembre–9 ottobre 2026**.

### Pubblico

- cinque–dieci tester invitati;
- percorsi dichiarati e brevi;
- almeno una persona non alla guida incaricata delle osservazioni interattive.

### Flusso candidato

```text
crea/seleziona viaggio
-> spiega e richiede posizione foreground
-> avvia sessione
-> notifica persistente
-> handoff a navigatore esterno
-> recorder locale bounded
-> ritorno a Travel DNA
-> timeline minima
-> pausa/fine
-> export diagnostico redatto
```

### Policy posizione

Il primo pilot stradale non richiede `ACCESS_BACKGROUND_LOCATION`. La posizione
viene usata mentre l'attività è visibile o durante un foreground service
esplicitamente avviato dall'utente, con notifica persistente.

L'utente deve poter:

- capire perché la posizione serve;
- negare il permesso senza crash;
- terminare la sessione;
- revocare il permesso;
- cancellare i dati locali;
- verificare che nessuna posizione precisa venga caricata di default.

### Criteri di uscita

- start/pausa/ripresa/fine coerenti;
- processo ricreato senza viaggio fantasma;
- notifica corretta durante la sessione;
- handoff e ritorno verificati sui provider dichiarati;
- spazio locale bounded e pulibile;
- export diagnostico senza token o identificatori personali;
- almeno tre modelli Android o una motivazione della matrice più piccola;
- osservazione batteria documentata;
- accessibilità di base verificata;
- nessuna interazione visuale obbligatoria per il conducente.

## Pilot 2 — piccola beta chiusa

### Finestra più precoce credibile

**2 novembre–11 dicembre 2026**, da ristimare dopo Pilot 1.

### Candidati

- MapLibre e superficie OSM reale;
- recupero viaggio più robusto;
- account/backend minimo;
- conversazione o domanda stradale limitata;
- quindici–trenta tester;
- matrice dispositivi/batteria più ampia;
- distribuzione interna tramite canale scelto.

Pilot 2 non entra automaticamente nella roadmap solo perché Pilot 1 si installa.
Serve evidenza su lifecycle, privacy, batteria, utilità del viaggio e qualità dei
feedback.

## Sequenza delle APK interne

| APK | Contenuto | Gate principale |
| --- | --- | --- |
| `0.1` | Modulo app, MainActivity, Compose, Home | Build e installazione. |
| `0.2` | Design system, navigazione bounded, roadmap/studio | Stato e accessibilità UI. |
| `0.3` | Replay e route progress sintetici | Contratti shared dentro l'app. |
| `0.4` | Missed-exit/reroute didattico e diagnostica | Coerenza state machine. |
| `0.5` | Adapter navigatori esterni | Intent, capability e failure. |
| `0.6` | Posizione foreground e permission UX | Privacy e lifecycle. |
| `0.7` | Foreground trip service e recorder bounded | Notifica, restart e batteria. |
| `0.8` | Candidato Pilot 1 | Device matrix e guida tester. |

Una APK può essere saltata o accorpata soltanto se il diff resta una vertical
slice revisionabile. Le etichette non sono ancora versioni pubbliche semantiche.

## Cosa serve al maintainer

### Subito

- account GitHub funzionante per Actions;
- Java 21;
- Android Studio compatibile con AGP 9.3;
- Android SDK 37 e Build Tools 36.0.0;
- emulatore;
- tempo per eseguire una checklist di installazione.

### Prima del Pilot 1

- almeno un telefono Android fisico compatibile con `minSdk 26`;
- cavo USB affidabile oppure wireless debugging configurato;
- supporto auto stabile;
- alimentazione in auto;
- passeggero/osservatore durante i test interattivi;
- due o tre tragitti brevi e ripetibili;
- disponibilità a condividere solo report redatti, non l'intera vita digitale del
  telefono.

### Più avanti

- Play Console solo se scegliamo testing interno tramite Play;
- dispositivo aggiuntivo se la matrice attuale non copre versioni o produttori
  utili;
- Mac/iPhone soltanto quando parte la milestone iOS.

## Acquisti consigliati

### Ora

**Nessun corso o libro aggiuntivo obbligatorio.** Gli account Manning e
Pluralsight, insieme ai corsi ufficiali Android gratuiti, coprono Kotlin, Compose,
coroutines, test e architettura necessari al Pilot 0.

### Se manca hardware

L'acquisto prioritario è un telefono Android reale, non un altro corso. Un
modello recente di fascia media già disponibile è sufficiente per iniziare; un
secondo dispositivo ricondizionato diventa utile solo per allargare la matrice.

### Opzionali per field test

- supporto auto stabile;
- cavo e alimentatore affidabili;
- power bank per test non collegati all'auto;
- etichette o foglio di test per identificare dispositivo/build.

### Da non comprare ancora

- licenze Mapbox o altri SDK cartografici commerciali;
- hardware LoRa;
- unità Android Auto da laboratorio;
- servizi backend premium;
- abbonamenti analytics;
- Mac per lo sviluppo Android;
- corso generico “mobile cross-platform” che sposta il progetto su Flutter o
  React Native.

## Roadmap delle vertical slice

1. **Toolchain e shell installabile** — PR #26.
2. **Design system e navigazione applicativa**.
3. **Replay/progress/missed-exit interattivi**.
4. **Adapter per navigatori esterni**.
5. **Posizione foreground e permission UX**.
6. **Foreground trip service e recorder locale**.
7. **Packaging, device matrix e Pilot 1**.

Ogni slice parte dal `main` verificato dopo il merge della precedente.

## Rischi principali

| Rischio | Contromisura |
| --- | --- |
| Android types nei contratti shared | Adapter e architecture check. |
| Troppe dipendenze nel bootstrap | Nessun Hilt/Room/Firebase/map SDK in Pilot 0. |
| “APK verde” confusa con affidabilità | Definizioni e criteri separati per ogni pilot. |
| Operazioni durante la guida | Passenger-observer e stop conditions. |
| Dati precisi nei log | Fixture sintetiche e diagnostica redatta. |
| Battery drain | Misure su dispositivo nel Pilot 1, non benchmark JVM. |
| Fragmentation | Matrice progressiva basata su evidenze. |
| Date percepite come promesse | Finestre ristimate a ogni gate. |

## Collegamenti

- [ADR-0010](../adr/0010-android-first-pilot-sequence.md)
- [Percorso di studio Android-first](56-percorso-studio-android-first.md)
- [Protocollo Pilot 1 su strada](57-protocollo-pilot-stradale-android.md)
- [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
- [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
- [Privacy, sicurezza e guida](33-privacy-security-driving-safety.md)
- [Regole operative](00-regole-operative.md)
