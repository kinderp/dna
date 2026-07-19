# Materiali didattici e roadmap acquisti per i pilot Android

## Stato e scopo

`curated learning and pilot-readiness guide — Android-first milestone`

Questo documento è il punto di accesso unico per:

- materiali Manning già disponibili con l'abbonamento;
- corsi Pluralsight con link diretto;
- corsi e documentazione ufficiale gratuita;
- ordine consigliato di studio;
- collegamento fra studio e vertical slice di Travel DNA;
- roadmap delle APK interne;
- hardware, account e acquisti necessari nelle diverse fasi;
- cose che non conviene acquistare o studiare troppo presto.

I collegamenti esterni sono stati verificati il **19 luglio 2026**. I cataloghi a
pagamento possono rinominare o sostituire i corsi: in caso di redirect, cercare il
titolo esatto indicato qui e verificare la data di aggiornamento.

## Regola di studio

Non studiare tutto prima di contribuire. Usare questo ciclo:

```text
20–60 minuti di teoria mirata
-> leggere il capitolo TDNA collegato
-> eseguire test o Lab
-> modificare un caso piccolo
-> riesaminare la PR reale
```

Impegno sostenibile consigliato:

```text
4–6 ore a settimana
2 ore materiale esterno
2 ore repository TDNA
1–2 ore esercizi, review o dispositivo
```

## Percorso minimo per seguire subito la PR Android

1. [Kotlin Fundamentals — Pluralsight](https://www.pluralsight.com/courses/kotlin-fundamentals1)
2. [Android with Kotlin: Fundamentals — Pluralsight](https://www.pluralsight.com/courses/android-kotlin-fundamentals)
3. [Android Basics with Compose — Android Developers](https://developer.android.com/courses/android-basics-compose/course)
4. [Android UI with Jetpack Compose — Pluralsight](https://www.pluralsight.com/courses/android-ui-jetpack-compose)
5. [Guida ufficiale all'architettura Android](https://developer.android.com/topic/architecture)
6. [Kotlin Coroutines — Pluralsight](https://www.pluralsight.com/courses/kotlin-coroutines)
7. [Android Unit Testing with JUnit and Mockito Using Kotlin — Pluralsight](https://www.pluralsight.com/courses/android-unit-testing-junit-mockito-using-kotlin)
8. [Capitolo TDNA 55 — roadmap Android-first](55-roadmap-android-first-e-pilot.md)
9. [Capitolo TDNA 58 — shell Android Pilot 0](58-shell-android-pilot0.md)

Questo insieme è sufficiente per comprendere build, Activity, Compose, stato,
contratti condivisi e test della prima APK.

# Parte I — Manning

L'abbonamento Manning già posseduto copre le letture principali. Non è necessario
acquistare separatamente gli eBook, salvo preferenza per una copia permanente o
cartacea.

## Priorità A — leggere durante Pilot 0

### Kotlin in Action, Second Edition

Link diretto:

- [Kotlin in Action, Second Edition](https://www.manning.com/books/kotlin-in-action-second-edition)

Usare come riferimento principale per:

- funzioni e classi;
- null safety;
- data/value class;
- sealed class e sealed interface;
- collections e lambdas;
- generics e variance quando compaiono nei contratti;
- coroutines, structured concurrency e Flow.

Applicazione TDNA:

```text
GeoPoint
LocationSample
RouteCoordinate
MapMatchOutcome
OffRouteState
RerouteExecutor
```

Non leggere subito reflection e DSL avanzate.

### Grokking Software Architecture

Link diretto:

- [Grokking Software Architecture](https://www.manning.com/books/grokking-software-architecture)

Usare per:

- trade-off;
- coesione e accoppiamento;
- dependency inversion;
- architettura esagonale;
- composition root;
- decisioni difendibili e ADR.

Applicazione TDNA:

```text
shared contracts
-> ports
-> Android adapters
-> apps/android composition root
```

È una lettura architetturale generale, non una fonte normativa per API Android.

## Priorità B — leggere durante Pilot 0 e Pilot 1

### Effective Software Testing

Link diretto:

- [Effective Software Testing](https://www.manning.com/books/effective-software-testing)

Utile per:

- boundary testing;
- precondizioni, postcondizioni e invarianti;
- property-based thinking;
- scegliere fra unit, integration e system test;
- progettare test che trovino davvero difetti.

Applicazione TDNA:

```text
LocationSample bounds
RouteProgress rejection precedence
off-route state transitions
manifest permission contract
APK/device smoke tests
```

### Unit Testing Principles, Practices, and Patterns

Link diretto:

- [Unit Testing Principles, Practices, and Patterns](https://www.manning.com/books/unit-testing)

Utile per:

- valore e costo dei test;
- test fragili;
- fake, mock e test double;
- separare comportamento osservabile e dettagli d'implementazione.

Gli esempi sono in C#, ma i principi si applicano ai test Kotlin/JVM/Android.

### Good Code, Bad Code

Link diretto:

- [Good Code, Bad Code](https://www.manning.com/books/good-code-bad-code)

Utile per:

- API difficili da usare male;
- codice leggibile;
- failure mode espliciti;
- funzioni piccole e responsabilità chiare;
- review del codice quotidiano.

## Priorità C — approfondimenti facoltativi

### The Joy of Kotlin

- [The Joy of Kotlin](https://www.manning.com/books/the-joy-of-kotlin)

Da leggere dopo aver acquisito Kotlin idiomatico, per approfondire funzioni,
immutabilità, error handling e gestione dello stato.

### Safe Programming with Kotlin — gratuito su Manning

- [Safe Programming with Kotlin](https://www.manning.com/books/safe-programming-with-kotlin)

Estratto breve utile per ragionare su programmi più difficili da usare male.

### Functional Programming in Kotlin

- [Functional Programming in Kotlin](https://www.manning.com/books/functional-programming-in-kotlin)

Facoltativo e avanzato. Non è richiesto per Pilot 0 o Pilot 1.

# Parte II — Pluralsight

## Fase 1 — Kotlin

### Percorso Kotlin completo

- [Kotlin learning path](https://www.pluralsight.com/paths/kotlin)

Usarlo come indice, non come obbligo di completare ogni corso.

### Corso principale aggiornato

- [Kotlin Fundamentals](https://www.pluralsight.com/courses/kotlin-fundamentals1)

Corso iniziale consigliato. Dopo ogni modulo aprire un contratto TDNA equivalente.

### Alternativa più estesa

- [Kotlin Fundamentals — percorso esteso](https://www.pluralsight.com/courses/fundamentals-kotlin)

Usarlo soltanto per colmare lacune, non in parallelo al corso principale.

### Coroutines

- [Kotlin Coroutines](https://www.pluralsight.com/courses/kotlin-coroutines)

Concentrarsi su:

- coroutine builders;
- suspending functions;
- structured concurrency;
- eccezioni;
- cancellazione.

Applicazione TDNA: replay executor, map matcher session e reroute executor.

### Flow e channel — approfondimento

- [Kotlin Coroutine Channels and Flows](https://www.pluralsight.com/courses/kotlin-coroutine-channels-flows)

Studiare prima dell'adapter di posizione e del recorder, non per forza durante la
prima shell.

## Fase 2 — Android e lifecycle

### Percorso Android con Kotlin

- [Android Development with Kotlin learning path](https://www.pluralsight.com/paths/android-development-with-kotlin2)

### Corso principale

- [Android with Kotlin: Fundamentals](https://www.pluralsight.com/courses/android-kotlin-fundamentals)

Mostra il ciclo completo, ma Room, Hilt e networking presenti nel corso non sono
requisiti automatici per TDNA.

### Prima app e lifecycle

- [Developing Android Applications with Kotlin: Getting Started](https://www.pluralsight.com/courses/android-apps-kotlin-build-first-app)
- [Android Fundamentals: Activities](https://www.pluralsight.com/courses/android-fundamentals-activities)

Obiettivo TDNA:

- distinguere Activity e processo;
- capire rotazione e recreation;
- installare APK su emulatore e telefono;
- non conservare stato di sessione dentro l'Activity.

## Fase 3 — Jetpack Compose

### Introduzione rapida

- [Building UIs in Kotlin with Jetpack Compose](https://www.pluralsight.com/courses/kotlin-jetpack-compose-building-uis)

### Corso principale

- [Android UI with Jetpack Compose](https://www.pluralsight.com/courses/android-ui-jetpack-compose)

Concentrarsi su:

- Row, Column e LazyColumn;
- Material 3;
- state hoisting;
- navigation;
- effetti e lifetime;
- semantics per accessibilità e test.

## Fase 4 — test

- [Android Unit Testing with JUnit and Mockito Using Kotlin](https://www.pluralsight.com/courses/android-unit-testing-junit-mockito-using-kotlin)
- [Android Apps with Kotlin: Tools and Testing](https://www.pluralsight.com/courses/android-apps-kotlin-tools-testing)

Strategia TDNA:

```text
common tests          invarianti e state machine
JVM tests             parser e application logic pura
Android unit tests    catalogo e stato senza dispositivo
instrumentation       Activity, Compose e manifest su emulator/device
field audit           lifecycle, batteria e guida reale
```

## Fase 5 — Intent e navigatori esterni

Da studiare per APK `0.5`, non per la prima shell:

- [Android Fundamentals: Intents](https://www.pluralsight.com/courses/android-fundamentals-intents)
- [Android Fundamentals: Common Actions Using Intents](https://www.pluralsight.com/courses/android-fundamentals-common-actions-intents)

Il corso più vecchio [Android Programming with Intents](https://www.pluralsight.com/courses/android-intents)
può essere usato come approfondimento, ma le policy e le API correnti vanno sempre
verificate nella documentazione Android ufficiale.

# Parte III — materiale ufficiale gratuito

Le policy Android cambiano più rapidamente dei libri. Per lifecycle, permessi,
foreground service, target SDK e distribuzione, la fonte primaria deve essere la
documentazione ufficiale corrente.

## Corso completo iniziale

- [Android Basics with Compose](https://developer.android.com/courses/android-basics-compose/course)
- [Android Basics with Compose — pagina introduttiva](https://developer.android.com/kotlin/androidbasics)

Unità prioritarie:

```text
Unit 1  prima app e Android Studio
Unit 2  Kotlin, state e test
Unit 3  liste e Material
Unit 4  lifecycle, architecture, ViewModel, navigation e adaptive UI
Unit 5  coroutines e data layer, selettivamente
```

Le unità Firebase non sono necessarie per Pilot 0.

## Compose

- [Portale Jetpack Compose](https://developer.android.com/compose)
- [Compose essentials](https://developer.android.com/courses/pathways/jetpack-compose-for-android-developers-1)
- [State in Compose](https://developer.android.com/develop/ui/compose/state)
- [Side effects in Compose](https://developer.android.com/develop/ui/compose/side-effects)
- [Testing Compose layouts](https://developer.android.com/develop/ui/compose/testing)
- [Accessibility in Compose](https://developer.android.com/develop/ui/compose/accessibility)

## Architettura

- [Guide to app architecture](https://developer.android.com/topic/architecture)
- [Recommendations for Android architecture](https://developer.android.com/topic/architecture/recommendations)
- [UI layer](https://developer.android.com/topic/architecture/ui-layer)
- [Domain layer opzionale](https://developer.android.com/topic/architecture/domain-layer)

Leggere le raccomandazioni come linee guida da adattare, non come motivo per
introdurre automaticamente repository, Hilt o use case vuoti.

## Kotlin, coroutines e Flow

- [Kotlin language documentation](https://kotlinlang.org/docs/home.html)
- [Coroutines guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Kotlin Flow](https://kotlinlang.org/docs/coroutines-flow.html)
- [Android coroutines best practices](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)

## Test e qualità

- [Test apps on Android](https://developer.android.com/training/testing)
- [Fundamentals of testing Android apps](https://developer.android.com/training/testing/fundamentals)
- [Test doubles and fakes](https://developer.android.com/training/testing/fundamentals/test-doubles)
- [Core app quality](https://developer.android.com/docs/quality-guidelines/core-app-quality)
- [Accessibility overview](https://developer.android.com/guide/topics/ui/accessibility)

## Emulatori e dispositivi

- [Build and run your app](https://developer.android.com/studio/run/)
- [Run apps on a hardware device](https://developer.android.com/studio/run/device)
- [Android Emulator](https://developer.android.com/studio/run/emulator)
- [Emulator from command line](https://developer.android.com/studio/run/emulator-commandline)
- [ADB](https://developer.android.com/tools/adb)

## Intent e integrazione con altre app

- [Intents and intent filters](https://developer.android.com/guide/components/intents-filters)
- [Common intents](https://developer.android.com/guide/components/intents-common)
- [Package visibility](https://developer.android.com/training/package-visibility)

## Posizione e Pilot 1

Studiare soltanto prima delle relative vertical slice:

- [Request location permissions](https://developer.android.com/develop/sensors-and-location/location/permissions)
- [Approximate location](https://developer.android.com/develop/sensors-and-location/location/permissions/runtime)
- [Background location](https://developer.android.com/develop/sensors-and-location/location/permissions/background)
- [Foreground services overview](https://developer.android.com/develop/background-work/services/fgs)
- [Foreground service types](https://developer.android.com/develop/background-work/services/fgs/service-types)
- [Location foreground service type](https://developer.android.com/develop/background-work/services/fgs/service-types#location)
- [Restrictions on starting foreground services](https://developer.android.com/develop/background-work/services/fgs/restrictions-bg-start)

La prima prova stradale TDNA usa posizione foreground e un servizio esplicitamente
avviato, evitando `ACCESS_BACKGROUND_LOCATION` finché non esiste una necessità
dimostrata e revisionata.

## Campioni ufficiali utili

- [Now in Android](https://github.com/android/nowinandroid)
- [Android architecture samples](https://github.com/android/architecture-samples)
- [Compose samples](https://github.com/android/compose-samples)

Usare i sample per confrontare decisioni e test, non per copiare intere
architetture dentro TDNA.

# Parte IV — roadmap di studio collegata alle APK

| Versione interna | Funzione principale | Studio prima/durante | Prova personale |
| --- | --- | --- | --- |
| `0.1` | modulo, Activity, Compose, Home | Android Basics Unit 1; Android Fundamentals | build e installazione |
| `0.2` | design system e navigazione bounded | Compose essentials; state; accessibility | aggiungere screen e test semantico |
| `0.3` | replay e route progress sintetici | Kotlin in Action; coroutines; capitoli TDNA 46–47 | seguire sample → snapshot |
| `0.4` | missed-exit/reroute didattico | capitoli TDNA 48–49; Effective Software Testing | testare failure e recovery |
| `0.5` | adapter navigatori esterni | Pluralsight Intents; common intents ufficiali | capability e failure matrix |
| `0.6` | posizione foreground | permission docs; lifecycle; coroutines/Flow | rationale, deny e revoke |
| `0.7` | foreground service e recorder bounded | FGS docs; process death; test doubles | restart e retention test |
| `0.8` | candidato Pilot 1 | testing, accessibility, field protocol | device matrix e field audit |

# Parte V — acquisti, account e attrezzatura per fase

I prezzi non vengono fissati in questo documento perché cambiano. Ogni acquisto va
riesaminato al momento della relativa milestone.

## Fase attuale — sviluppo APK 0.1–0.4 / Pilot 0

### Già sufficiente

- account GitHub con Actions funzionanti;
- account Manning;
- account Pluralsight;
- Android Studio e Android SDK gratuiti;
- computer capace di eseguire emulatori;
- Java 21;
- repository e Gradle Wrapper.

### Acquisti obbligatori

**Nessuno**, se esiste già almeno un computer adeguato.

### Acquisti utili ma rinviabili

- un telefono Android reale recente di fascia media;
- un cavo USB dati affidabile;
- eventualmente un hub USB alimentato se il computer ha poche porte.

### Non comprare

- Play Console;
- licenze Mapbox/Mapbox Navigation;
- SDK analytics a pagamento;
- backend premium;
- hardware LoRa;
- unità Android Auto;
- Mac per Android;
- corsi Flutter/React Native.

## APK 0.5 — navigatore esterno

### Serve

- almeno un telefono Android reale;
- installazione dei navigatori che dichiariamo supportati nel test;
- account di quei provider soltanto quando l'app lo richiede;
- cavo o wireless debugging.

### Non serve ancora

- licenza cartografica commerciale;
- Android Auto;
- posizione background;
- Play Store.

## APK 0.6 — posizione foreground

### Serve

- telefono reale con GPS;
- secondo dispositivo o emulatore per confrontare deny/approximate/precise;
- cavo dati;
- power bank opzionale;
- checklist privacy e permessi.

### Acquisto consigliato

Un telefono Android fisico è il primo acquisto realmente prioritario se non ne
possiedi uno compatibile. Meglio un modello diffuso e aggiornato che un flagship
costoso.

## APK 0.7 — foreground service e recorder

### Serve

- telefono reale;
- supporto auto stabile;
- alimentatore da auto e cavo affidabile;
- power bank per confrontare test alimentati/non alimentati;
- passeggero-osservatore durante ogni interazione;
- due o tre percorsi brevi ripetibili.

### Acquisti facoltativi

- secondo telefono ricondizionato di produttore/versione Android differente;
- misuratore USB per osservazioni energetiche grossolane, senza chiamarlo misura
  scientifica della batteria.

## APK 0.8 — candidato Pilot 1

### Account/canale di distribuzione

Scegliere uno dei due:

1. distribuzione diretta di APK firmata internamente;
2. Google Play Console con testing interno/chiuso.

La Play Console diventa utile solo se semplifica davvero distribuzione,
aggiornamenti e tester. Non è necessaria per la prima installazione diretta.

### Device matrix minima candidata

- dispositivo principale recente;
- secondo produttore o versione Android;
- emulatore con schermo/densità differente;
- eventuale dispositivo più vecchio vicino a `minSdk`, se disponibile.

### Materiale fisico

- supporto auto;
- alimentazione;
- passeggero-osservatore;
- scheda test stampata o digitale;
- procedura di emergenza e stop immediato.

## Pilot 2 — beta chiusa

Valutare soltanto dopo i report del Pilot 1:

- Play Console se non già attivata;
- dominio e hosting/backend minimo;
- secondo/terzo dispositivo;
- eventuale servizio di test device cloud;
- budget per storage/log diagnostici redatti;
- eventuale hardware di test cartografico.

MapLibre è open source: non comprare automaticamente una licenza Mapbox. La scelta
di tile, geocoding, routing e hosting è una decisione separata con costi, licenze e
policy specifici.

# Parte VI — cosa non studiare o comprare ancora

Rimandare finché una issue non li rende necessari:

- Flutter e React Native;
- Firebase completo;
- Hilt avanzato;
- Room tuning;
- Mapbox SDK commerciale;
- Android Auto template API;
- CarPlay;
- NDK e FFI Android;
- machine learning per recommendation;
- microservizi backend;
- Kubernetes;
- LoRa hardware;
- monetizzazione e advertising;
- pubblicazione pubblica sul Play Store.

# Piano di studio operativo di sei settimane

| Settimana | Materiale | Esercizio TDNA | Output |
| --- | --- | --- | --- |
| 1 | Kotlin Fundamentals + Kotlin in Action | leggere quattro contratti shared | note su invarianti |
| 2 | Android Basics + Activity/lifecycle | installare APK su emulatore | checklist build/install |
| 3 | Compose Pluralsight + Compose essentials | modificare una card e testarla | PR/esercizio locale |
| 4 | architettura Android + Grokking Architecture | disegnare composition root/port/adapter | diagramma e trade-off |
| 5 | Kotlin Coroutines + guide ufficiali | review di replay/reroute cancellation | finding o spiegazione |
| 6 | test Android + Intents | smoke test e design adapter esterno | test matrix e contratto |

# Gate personale per dichiararsi pronti al Pilot 1

Devi saper:

- compilare e installare APK e instrumentation APK;
- leggere manifest sorgente e manifest fuso;
- distinguere Activity recreation e process death;
- spiegare state hoisting e unidirectional data flow;
- leggere coroutine scope e propagazione della cancellazione;
- distinguere unit, integration, instrumentation e field test;
- progettare un Intent adapter con failure espliciti;
- spiegare approximate/precise e foreground/background location;
- interrompere un field test quando la sicurezza non è garantita;
- leggere una PR TDNA e verificare CI più due review sullo stesso SHA.

# Collegamenti TDNA

- [Roadmap Android-first e versioni APK](55-roadmap-android-first-e-pilot.md)
- [Percorso di studio narrativo](56-percorso-studio-android-first.md)
- [Protocollo del pilot stradale](57-protocollo-pilot-stradale-android.md)
- [Shell Android Pilot 0](58-shell-android-pilot0.md)
- [ADR-0010 Android-first](../adr/0010-android-first-pilot-sequence.md)
- [Regole operative](00-regole-operative.md)
- [Strategia test](30-strategia-test.md)
- [Privacy e sicurezza durante la guida](33-privacy-security-driving-safety.md)
