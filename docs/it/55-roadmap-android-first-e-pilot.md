# Android-first: roadmap dei pilot, sequenza APK e percorso automotive

## Stato

`implementation-backed for v0.1/v0.2; planning-backed by ADR-0010 and ADR-0011`

Questo capitolo trasforma la scelta Android-first in una sequenza verificabile.
Distingue una APK didattica, un pilot stradale, una car experience Android Auto e
una futura navigazione turn-by-turn, così una build verde non viene confusa con un
prodotto sicuro da usare in viaggio.

Le policy Android for Cars citate sono state ricontrollate il 19 luglio 2026 e
devono essere verificate nuovamente prima di ogni spike o pubblicazione.

## Obiettivi

Al termine dovresti saper spiegare:

1. perché Android viene prima di iOS;
2. che cosa differenzia Pilot 0, Pilot 1 e Pilot 2;
3. quali tecnologie entrano in ogni APK;
4. perché `apps/android` resta separato dai moduli Kotlin Multiplatform;
5. quale evidenza serve prima di usare l'app su strada;
6. perché Android Auto non è la UI Compose proiettata sulla head unit;
7. perché Pilot 1 è POI-first e la categoria Navigation arriva più tardi;
8. come replay e route progress preparano un futuro `AUTO_DRIVE` senza anticipare
   la navigazione reale.

## Decisioni correlate

- [ADR-0010 — Android-first](../adr/0010-android-first-pilot-sequence.md)
- [ADR-0011 — Android Auto POI-first](../adr/0011-android-auto-poi-first-and-car-surfaces.md)

Motivazioni principali:

- riuso immediato di Kotlin, Java e dei moduli shared;
- build e CI iniziali possibili su Linux;
- una sola piattaforma mobile durante la prima validazione;
- percorso automotive progressivo e verificabile;
- iOS resta una seconda piattaforma, non un non-obiettivo definitivo.

## Lessico delle evidenze

### Build

Artifact generato dal sistema di build. Può non essere installabile o verificato.

### APK debug

Pacchetto Android installabile per sviluppo, firmato con chiave debug.

### APK instrumentation

Pacchetto di test installato insieme all'app. Non è un'app per tester finali.

### Emulatore Android

Verifica l'app mobile, Compose e instrumentation. Non mostra Android Auto.

### Desktop Head Unit

Simula una head unit Android Auto. Verifica template, input e lifecycle car, ma non
sostituisce un veicolo reale.

### Android Automotive OS emulator

Esegue un'app installata nel sistema del veicolo. È distinto dal DHU e richiede
packaging automotive.

### Pilot

Esperimento controllato con pubblico, obiettivi, criteri di uscita, non-obiettivi e
protocollo di evidenza.

```text
APK verde
!= emulatore mobile verde
!= DHU verde
!= veicolo compatibile
!= affidabilità su strada
```

## Stack iniziale

| Area | Decisione corrente | Perché |
| --- | --- | --- |
| Build | Gradle Wrapper 9.5.1 | Comune a locale e CI. |
| Android plugin | AGP 9.3.0 | Toolchain corrente del repository. |
| SDK | `compileSdk=37`, `targetSdk=37` | Baseline Android corrente. |
| Compatibilità | `minSdk=26` | Riduce carico legacy mantenendo una base ampia. |
| Kotlin app | built-in Kotlin di AGP | Evita `kotlin-android` nel modulo app. |
| Kotlin shared | Kotlin Multiplatform 2.4.0 | Contratti JVM/Linux condivisi. |
| UI mobile | Jetpack Compose + Material 3 | Stato esplicito e test semantici. |
| UI car futura | Android for Cars App Library | Template e limiti driver-safe. |
| DI | nessun framework nel Pilot 0 | Composition root manuale. |
| Persistenza | nessuna nel Pilot 0 | Nessun caso d'uso durable ancora richiesto. |
| Rete | nessuna nel Pilot 0 | Shell e simulazione offline. |

Il confine resta:

```text
shared contracts
    nessun tipo Android/Compose/Car App Library

apps/android
    mobile composition root e Compose

future Android Auto adapter
    Car App Library mapper e lifecycle

future Automotive OS app
    artifact e packaging separati
```

## Pilot 0 — APK didattica installabile

### Finestra target

**10–21 agosto 2026**.

È una pianificazione, non una promessa di release pubblica.

### Pubblico

- maintainer;
- studenti;
- emulatore;
- uno–tre dispositivi fisici.

### Risultato progressivo

```text
0.1 shell/build/APK
-> 0.2 navigation/runtime emulator
-> 0.3 replay e route progress interattivi
-> 0.4 missed-exit/reroute didattici
-> 0.5 navigatori esterni
-> gate fisico/manuale
```

### Cosa dimostra

- toolchain e Compose funzionano nel monorepo;
- `apps/android` consuma contratti TDNA;
- la UI resta priva di permessi e rete nelle prime slice;
- replay, matching e progress possono attraversare il core e arrivare alla UI;
- CI produce APK, checksum e runtime evidence;
- uno studente può collegare la schermata ai capitoli teorici.

### Cosa non dimostra

- GPS reale;
- comportamento foreground/background;
- mappa reale;
- navigazione esterna prima della relativa slice;
- Android Auto;
- batteria o resilienza su viaggio;
- sicurezza durante la guida.

### Criteri di uscita

- APK installabile su emulatore e almeno un telefono;
- manifest senza permessi sensibili non necessari;
- modalità chiara/scura e font scaling verificati;
- rotazione/configurazione con stato coerente;
- unit test e instrumentation verdi;
- artifact legati allo SHA esatto;
- replay/progress deterministici;
- walkthrough didattico;
- checklist manuale accessibilità.

## Pilot 0 v0.3 — replay, progress e snapshot driver-safe

La schermata Demo diventa una simulazione interattiva:

```text
fixture sintetica
-> start / pausa / step / reset / velocità
-> LocationSample accettato o rifiutato
-> map matching
-> route progress
-> timeline bounded
-> DriverJourneySnapshot
-> Compose renderer
```

`DriverJourneySnapshot` è un nome provvisorio per uno stato immutabile e
provider-neutral che potrà alimentare in futuro anche Android Auto. Non importa
`androidx.car.app`, Compose, Android Location o provider cartografici.

Questa slice non implementa:

- GPS;
- permessi;
- Car App Library;
- template auto;
- `AUTO_DRIVE` runtime;
- strada reale.

## Pilot 0 v0.4 — missed exit e degraded state

```text
traccia sintetica deviata
-> evidence off-route
-> conferma missed exit
-> reroute deterministico
-> loading / degraded / error state
-> snapshot driver-safe aggiornato
```

La UI mostra messaggi brevi e stati bounded riutilizzabili da futuri renderer, ma
non offre ancora guidance reale.

## Pilot 0 v0.5 — navigatori esterni

Obiettivi:

- capability model per provider;
- handoff di destinazione;
- fallback app non installata;
- ritorno a Travel DNA;
- nessun feed di route inventato;
- navigatore esterno come autorità delle manovre.

## Automotive readiness spike

Slice separata dopo v0.3/v0.4 e prima di promettere Android Auto nel Pilot 1.

```text
DriverJourneySnapshot stabile
-> modulo/adattatore Android dedicato
-> Car App Library
-> CarAppService + Session minimi
-> categoria POI nel prototipo
-> template con dati sintetici
-> Desktop Head Unit
-> day/night, touch, rotary e screenshot
```

Non include:

- categoria Navigation;
- guida vocale;
- Google Play submission;
- Android Automotive OS production package;
- strada reale.

## Pilot 1 — companion stradale controllato e POI Android Auto

### Finestra target

**21 settembre–9 ottobre 2026**, da ristimare dopo v0.3/v0.4 e lo spike POI.

### Pubblico

- cinque–dieci tester invitati;
- percorsi dichiarati e brevi;
- passeggero/osservatore durante le prove interattive.

### Flusso telefono

```text
crea/seleziona viaggio
-> spiega e richiede posizione foreground
-> avvia sessione e notifica persistente
-> recorder locale bounded
-> handoff a navigatore esterno
-> timeline minima
-> pausa/fine
-> export diagnostico redatto
```

### Flusso Android Auto candidato

```text
viaggio configurato sul telefono
-> apre TDNA POI companion
-> mostra lista breve di tappe/POI
-> seleziona destinazione
-> handoff al navigatore esterno
-> azioni essenziali pause/stop quando applicabili
```

TDNA non dichiara turn-by-turn authority nel Pilot 1.

### Policy posizione

Nessun `ACCESS_BACKGROUND_LOCATION` nel primo pilot stradale. La posizione viene
usata con app visibile o foreground service esplicitamente avviato, con notifica.

L'utente deve poter:

- capire perché la posizione serve;
- negare il permesso senza crash;
- terminare la sessione;
- revocare il permesso;
- cancellare dati locali;
- evitare upload di posizione precisa per default.

### Criteri di uscita

- lifecycle start/pausa/ripresa/fine coerente;
- nessun viaggio fantasma dopo process recreation;
- notifica corretta;
- handoff e ritorno verificati;
- storage bounded e pulibile;
- export senza token o identificatori personali;
- matrice dispositivi motivata;
- batteria osservata;
- accessibilità di base;
- nessuna interazione visuale obbligatoria per il conducente;
- se incluso, POI Android Auto verificato su DHU e dispositivi dichiarati;
- nessuna pretesa di Navigation category.

## Pilot 2 — piccola beta chiusa

### Finestra più precoce credibile

**2 novembre–11 dicembre 2026**, da ristimare dopo Pilot 1.

Candidati:

- MapLibre e superficie OSM reale;
- recupero viaggio più robusto;
- account/backend minimo;
- conversazione stradale limitata e driver-safe;
- quindici–trenta tester;
- matrice dispositivi/batteria più ampia;
- distribuzione interna.

Pilot 2 non entra automaticamente nella roadmap perché Pilot 1 si installa. Serve
evidenza su lifecycle, privacy, batteria, utilità e feedback.

## Internal Navigation Beta — categoria Navigation

Milestone successiva, non inclusa automaticamente in Pilot 1 o Pilot 2.

Gate minimi:

- route e guidance runtime;
- posizione e map matching reali;
- manovra, distanza, durata ed ETA;
- missed exit e reroute;
- `NavigationManager` start/stop/focus;
- `Trip`, `Step`, `Destination` e travel estimates;
- navigation intents;
- audio guidance con focus corretto;
- `onAutoDriveEnabled()` alimentato da replay deterministico;
- template correnti e cluster quando richiesto;
- DHU matrix;
- quality checklist e review Google Play;
- vehicle/field evidence separata.

## Sequenza APK e gate interni

| Versione | Contenuto | Gate principale |
| --- | --- | --- |
| `0.1` | Modulo app, MainActivity, Compose, Home | Build e installazione. |
| `0.2` | Navigazione bounded e runtime emulator | Stato, semantics e lifecycle. |
| `0.3` | Replay, route progress e driver snapshot | Core shared dentro l'app. |
| `0.4` | Missed-exit/reroute e degraded state | Coerenza state machine. |
| `0.5` | Adapter navigatori esterni | Intent, capability e failure. |
| `0.6` | Posizione foreground e permission UX | Privacy e lifecycle. |
| `0.7` | Foreground trip service e recorder bounded | Notifica, restart e batteria. |
| `0.8` | Candidato Pilot 1 | Device matrix e guida tester. |
| `auto-spike` | POI Car App Library + DHU | Categoria, template e driver-safe scope. |
| `nav-beta` | Navigation category completa | Guidance, AUTO_DRIVE e car quality. |

Le etichette non sono versioni pubbliche semantiche. Lo spike automotive non viene
accorpato silenziosamente alla v0.3 o alla v0.4.

## Cosa serve al maintainer

### Subito

- GitHub Actions;
- Java 21;
- Android Studio compatibile con la toolchain;
- Android SDK 37 e Build Tools 36.0.0;
- emulatore Android;
- checklist di installazione.

### Prima del Pilot 1

- almeno un telefono Android fisico compatibile;
- cavo USB o wireless debugging;
- supporto e alimentazione auto;
- passeggero/osservatore;
- tragitti brevi e ripetibili;
- condivisione di report redatti, non dati personali completi.

### Prima dello spike automotive

- policy Android for Cars ricontrollata;
- Desktop Head Unit installato;
- telefono compatibile con il DHU;
- versione Car App Library scelta e documentata;
- use case POI ridotto approvato;
- nessuna dichiarazione car nel package production prima del gate.

## Acquisti

### Ora

Nessun corso o libro aggiuntivo obbligatorio. Manning, Pluralsight e formazione
ufficiale Android coprono il Pilot 0.

### Hardware prioritario

Un telefono Android reale è più utile di un altro corso. Un secondo dispositivo
ricondizionato serve solo per ampliare la matrice.

### Da non comprare ancora

- head unit Android Auto da laboratorio: usare prima il DHU;
- licenze cartografiche commerciali;
- hardware LoRa;
- backend premium;
- analytics a pagamento;
- Mac per sviluppo Android;
- corsi cross-platform che spostano il progetto su Flutter o React Native.

## Roadmap delle vertical slice

1. Toolchain e shell installabile — merged PR #26.
2. Navigazione e runtime emulatore — merged PR #28.
3. Replay/progress e driver snapshot.
4. Missed-exit/reroute e degraded state.
5. Navigatori esterni.
6. Posizione foreground e permission UX.
7. Foreground service e recorder.
8. Automotive readiness POI/DHU spike.
9. Packaging, device matrix e Pilot 1.
10. Internal Navigation Beta separata.

Ogni slice parte dal `main` verificato dopo il merge della precedente.

## Rischi principali

| Rischio | Contromisura |
| --- | --- |
| Android types nei contratti shared | Adapter e architecture check. |
| UI mobile proiettata sulla car | Renderer Car App Library separato. |
| Categoria Navigation anticipata | POI-first e gate espliciti. |
| Feature distraenti sul display auto | Allowlist driver-safe. |
| APK verde confusa con affidabilità | Evidenze separate per livello. |
| Operazioni durante la guida | Passenger observer e stop conditions. |
| Dati precisi nei log | Fixture sintetiche e diagnostica redatta. |
| Battery drain | Misure su dispositivo nel Pilot 1. |
| Policy Google cambia | Recheck datato prima di ogni slice/release. |
| Review car blocca release mobile | Prototipo/flavor/package separato da decidere. |
| Date percepite come promesse | Ristima a ogni gate. |

## Collegamenti

- [Android Auto compliance](61-android-auto-compliance-e-roadmap-automotive.md)
- [ADR-0010](../adr/0010-android-first-pilot-sequence.md)
- [ADR-0011](../adr/0011-android-auto-poi-first-and-car-surfaces.md)
- [Percorso di studio Android-first](56-percorso-studio-android-first.md)
- [Protocollo Pilot 1](57-protocollo-pilot-stradale-android.md)
- [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
- [Privacy e sicurezza durante la guida](33-privacy-security-driving-safety.md)
- [Regole operative](00-regole-operative.md)
