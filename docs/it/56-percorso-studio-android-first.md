# Percorso di studio Android-first per maintainer e studenti

## Scopo

Questo percorso prepara a leggere, revisionare e provare il codice Android di
Travel DNA senza trasformare lo studio in un secondo progetto parallelo.

Il maintainer dispone già di Manning e Pluralsight. La strategia è quindi:

```text
materiale ufficiale corrente
+ corsi già inclusi negli abbonamenti
+ esercizi sul repository TDNA
-> nessun acquisto didattico obbligatorio
```

## Principio

Non serve diventare esperti di tutto Android prima di contribuire. Serve sapere
abbastanza per prendere decisioni, leggere una PR, eseguire una build, capire i
failure mode e verificare un dispositivo.

Ogni fase produce una prova nel repository.

## Impegno suggerito

```text
4–6 ore alla settimana
2 ore teoria
2 ore codice TDNA
1–2 ore esercizi/review
```

Con più tempo, aumentare gli esercizi e non il numero di corsi guardati in
parallelo.

## Fase 0 — orientamento al repository

### Leggere

1. [Visione del prodotto](01-visione-prodotto.md)
2. [Regole operative](00-regole-operative.md)
3. [Review e merge](06-review-e-merge.md)
4. [Architettura generale](20-architettura-generale.md)
5. [ADR-0010 Android-first](../adr/0010-android-first-pilot-sequence.md)
6. [Roadmap pilot](55-roadmap-android-first-e-pilot.md)

### Eseguire

```bash
sh tools/tdna doctor
sh tools/tdna check
sh tools/tdna check-android
```

### Risultato atteso

Saper distinguere:

```text
shared contract
Android adapter
application composition root
Lab sintetico
Pilot su dispositivo
```

## Fase 1 — Kotlin essenziale

### Pluralsight

- **Kotlin Fundamentals** di Kevin Jones, aggiornato a luglio 2026.
- Se alcune parti restano poco chiare, usare il corso più lungo **Kotlin
  Fundamentals** del 2024 come approfondimento, non in parallelo dall'inizio.

### Manning

- **Kotlin in Action, Second Edition**.

Ordine di lettura selettivo:

```text
funzioni e classi
null safety
collections
lambdas
sealed class/interface
value/data class
coroutines e Flow
```

Non è necessario leggere subito reflection e DSL avanzate.

### Esercizio TDNA

- aprire `GeoPoint`, `LocationSample`, `RouteCoordinate` e `OffRoutePolicy`;
- identificare invarianti e signed-zero normalization;
- aggiungere un caso limite in una branch di esercizio;
- spiegare perché il costruttore fallisce prima che il dato entri nel runtime.

### Gate personale

- leggere una `sealed interface` senza tradurla mentalmente in una gerarchia Java
  verbosa;
- capire `data class`, value class, nullable e collection immutable/read-only;
- sapere distinguere eccezione ordinaria e cancellazione coroutine.

## Fase 2 — fondamenti Android

### Corso ufficiale gratuito

- **Android Basics with Compose**.

Seguire almeno:

```text
prima app
UI di base
liste e Material Design
navigation/app architecture
```

Le unità Firebase non sono necessarie per Pilot 0.

### Pluralsight

- **Android with Kotlin: Fundamentals**.

Usarlo per vedere il ciclo completo build–APK–test, ma non copiare
automaticamente Room, Hilt o networking nella shell TDNA: sono esempi del corso,
non requisiti correnti del progetto.

### Esercizio TDNA

- aprire `MainActivity`;
- trovare manifest, namespace, application ID, min/target/compile SDK;
- installare l'APK su emulatore;
- ruotare il dispositivo e verificare la schermata selezionata;
- leggere l'output di `adb logcat` senza inserire dati personali.

### Gate personale

Saper spiegare:

- Activity e processo non sono la stessa cosa;
- rotazione e process recreation sono eventi diversi;
- manifest, Gradle e codice Kotlin hanno ruoli diversi;
- una debug APK non è una release Play.

## Fase 3 — Jetpack Compose

### Pluralsight

Percorso consigliato:

1. **Building UIs in Kotlin with Jetpack Compose** — introduzione rapida;
2. **Android UI with Jetpack Compose** — layout, state, navigation, effect e
   Material Design.

### Materiale ufficiale

- corso Compose di Android Developers;
- documentazione state e side effects;
- Compose testing.

### Concetti da padroneggiare

```text
@Composable
recomposition
state hoisting
remember / rememberSaveable
unidirectional data flow
stable keys
side effects e lifecycle
semantics per test/accessibilità
```

### Esercizio TDNA

- aggiungere una card alla shell passando dati come parametro;
- evitare singleton mutabile;
- scrivere un test semantico sul testo visibile;
- spiegare perché una composable non deve chiamare direttamente un provider di
  routing.

### Gate personale

Saper individuare:

- stato locale puramente visuale;
- stato di screen;
- stato di sessione applicativa;
- effetto che deve essere cancellato quando la schermata scompare.

## Fase 4 — architettura Android

### Materiale ufficiale

Studiare le raccomandazioni Android su:

- UI layer;
- data layer;
- domain layer opzionale;
- single source of truth;
- unidirectional data flow;
- lifecycle-aware state collection;
- testability.

### Manning

- **Grokking Software Architecture** come lettura leggera e progressiva.

È già incluso nell'abbonamento Manning; non serve acquistarlo separatamente. È
utile per vocabolario, trade-off e architectural thinking, non come fonte di
pattern Android specifici.

### Esercizio TDNA

Disegnare:

```text
Composable
-> screen state
-> application use case
-> port
-> Android adapter
```

Poi indicare dove non devono comparire:

```text
android.location.Location
Intent
Context
MapLibre class
```

## Fase 5 — coroutines, cancellation e Flow

### Pluralsight

1. **Kotlin Coroutines** — structured concurrency, eccezioni e cancellazione;
2. **Kotlin Coroutines Deep Dive** — Flow e channel, dopo il primo corso.

### Manning

Usare i capitoli coroutines/Flow di **Kotlin in Action, Second Edition** come
riferimento scritto.

### Esercizio TDNA

- rileggere `RerouteExecutor`;
- spiegare perché `CancellationException` viene propagata;
- confrontare clock virtuale del replay e wall clock;
- progettare, senza implementare, uno stream `Flow<LocationSample>` con owner
  lifecycle esplicito.

### Gate personale

- non usare `GlobalScope`;
- distinguere suspending function, thread e coroutine;
- capire parent/child cancellation;
- sapere quando `StateFlow` descrive stato e quando un evento one-shot richiede
  altro.

## Fase 6 — test Android

### Pluralsight

- **Android Unit Testing with JUnit and Mockito Using Kotlin**;
- **Unit Testing with JUnit 5 and Kotlin** per i moduli JVM, tenendo presente che
  il modulo Android usa inizialmente JUnit 4 per compatibilità con il runner;
- il percorso **Android: Testing** quando disponibile nel proprio catalogo.

### Strategia TDNA

```text
common/unit tests        invarianti e state machine
JVM tests                parser, fixture e pure application code
Android unit tests       catalogo/composition logic senza device
instrumentation compile  Activity/Compose smoke source
emulator/device tests    lifecycle, permissions e UI reali
field audit              strada e batteria
```

### Esercizio TDNA

- estendere `PilotCatalogTest`;
- aggiungere un'asserzione Compose basata su semantics, non coordinate pixel;
- descrivere quale bug richiede un dispositivo e non può essere provato da un
  unit test JVM.

## Fase 7 — Intents e navigatori esterni

Questa fase serve per APK `0.5`, non per la prima shell.

### Pluralsight

- **Android Fundamentals: Intents**;
- **Android Fundamentals: Common Actions Using Intents**.

Preferire questi corsi recenti al vecchio corso completo sugli Intents del 2019,
che può essere usato solo come approfondimento storico.

### Esercizio TDNA

Progettare il contratto:

```text
ExternalNavigationRequest
-> capability check
-> Android Intent adapter
-> LaunchResult
```

Elencare failure:

- app provider assente;
- URI non supportato;
- destinazione invalida;
- Activity non risolvibile;
- ritorno nell'app non garantito.

## Fase 8 — posizione e foreground service

Questa fase inizia soltanto dopo Pilot 0.

### Materiale primario

Usare prima la documentazione ufficiale Android su:

- foreground location;
- runtime permissions;
- approximate/precise location;
- foreground services;
- restrizioni di avvio in background;
- notifica e tipi di servizio;
- lifecycle e process death.

I corsi invecchiano più rapidamente delle policy Android: per permessi e servizi
la documentazione ufficiale corrente è la fonte normativa.

### Esercizio TDNA

Prima del codice compilare:

- data inventory;
- permission rationale;
- degraded mode senza permesso;
- stop condition;
- retention locale;
- log redaction;
- test matrix;
- piano batteria.

## Fase 9 — mappe e routing reali

Studiare soltanto quando esiste la relativa issue:

- OpenStreetMap data model e licenza;
- MapLibre Native Android;
- Valhalla API e costing;
- map matching reale;
- performance rendering e batteria.

I capitoli TDNA 23, 24, 32 e 36 vengono prima dei tutorial SDK.

## Cosa non studiare ancora

Non investire tempo adesso in:

- Flutter o React Native;
- Firebase completo;
- Hilt avanzato;
- Room tuning;
- Android Auto template API;
- CarPlay;
- NDK/FFI Android;
- Mapbox SDK commerciale;
- machine learning per recommendation;
- LoRa hardware;
- pubblicazione Play e monetizzazione;
- microservizi backend.

Non sono argomenti inutili: semplicemente non sbloccano la prossima vertical
slice.

## Piano di sei settimane

| Settimana | Studio | Prova TDNA |
| --- | --- | --- |
| 1 | Kotlin fundamentals | Leggere e testare quattro contratti shared. |
| 2 | Android basics e build | Installare Pilot 0 su emulatore. |
| 3 | Compose state/layout | Modificare una screen con test. |
| 4 | Architettura/lifecycle | Disegnare ownership e recreation. |
| 5 | Coroutines/cancellation | Review di replay e reroute. |
| 6 | Test e Intents | Smoke test e design handoff. |

Il piano può seguire le PR: ciò che si studia viene subito usato in review.

## Acquisti

### Non necessari

Gli abbonamenti già posseduti includono le risorse principali. Non consiglio di
comprare un altro corso generalista Android o Kotlin.

### Facoltativi

- copia cartacea di **Kotlin in Action, Second Edition** solo se preferisci
  annotare un libro fisico;
- secondo telefono ricondizionato dopo Pilot 0, se serve una matrice diversa;
- supporto e alimentazione auto prima dei field test.

### Priorità economica

```text
telefono reale e tempo di test
> strumenti di montaggio/alimentazione
> eventuale canale Play interno
> nuovi libri/corsi
> SDK commerciali
```

## Checklist “sono pronto a seguire la PR”

- [ ] So eseguire Wrapper, test e APK build.
- [ ] So trovare manifest e build file del modulo app.
- [ ] Capisco perché app Android e KMP sono subproject separati.
- [ ] So leggere una composable e identificare lo stato.
- [ ] So distinguere unit, instrumentation e field test.
- [ ] Capisco cancellazione coroutine e monotonic time.
- [ ] So leggere i non-obiettivi della PR prima del diff.
- [ ] Non considero una CI verde prova di sicurezza stradale.

## Risorse consigliate

### Android Developers

- Android Basics with Compose
- Jetpack Compose course
- Guide to app architecture
- Testing on Android
- Location permissions
- Foreground services

### Pluralsight

- Kotlin Fundamentals
- Android with Kotlin: Fundamentals
- Building UIs in Kotlin with Jetpack Compose
- Android UI with Jetpack Compose
- Kotlin Coroutines
- Kotlin Coroutines Deep Dive
- Android Unit Testing with JUnit and Mockito Using Kotlin
- Android Fundamentals: Intents
- Android Fundamentals: Common Actions Using Intents

### Manning

- Kotlin in Action, Second Edition
- Grokking Software Architecture

## Collegamenti TDNA

- [Roadmap Android-first](55-roadmap-android-first-e-pilot.md)
- [Protocollo stradale](57-protocollo-pilot-stradale-android.md)
- [Architettura generale](20-architettura-generale.md)
- [Stack](28-stack-linguaggi-e-gui.md)
- [Test](30-strategia-test.md)
- [Privacy e guida](33-privacy-security-driving-safety.md)
