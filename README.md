# Travel DNA

Travel DNA è un progetto didattico e di prodotto per costruire una guida-diario
sociale dei viaggiatori, capace di funzionare con navigatori esterni e, in futuro,
con una navigazione integrata.

Il prodotto unisce:

- diario di viaggio utile anche senza altri utenti;
- guida turistica e itinerari;
- incontri, saluti, consigli e Cartoline DNA;
- privacy by default e condivisione esplicita;
- una codebase studiabile da studenti e contributori.

## Stato

**Foundations and Travel DNA Lab v0 è completata.** Il rapporto è in
[docs/project/foundation-v0-closure.md](docs/project/foundation-v0-closure.md).

La milestone attiva è **Android-first Pilot 0**:

```text
contratti shared già testati
-> apps/android composition root
-> Jetpack Compose
-> shell permission-free
-> debug APK e test APK
-> installazione su emulatore/telefono
```

La shell non contiene ancora GPS, mappe reali, backend, chat o diario operativo.

## Guida unica per studio, pilot e acquisti

Il riferimento principale per prepararsi e pianificare le prove è:

- **[Materiali didattici con link diretti e roadmap acquisti dei pilot Android](docs/it/59-materiali-didattici-e-acquisti-pilot-android.md)**

Il documento raccoglie in un solo posto:

- libri Manning con link diretto;
- corsi Pluralsight con link diretto;
- corsi e documentazione ufficiale gratuita;
- ordine di studio collegato alle APK `0.1`–`0.8`;
- esercizi da svolgere nel repository;
- hardware, account e acquisti per Pilot 0, Pilot 1 e Pilot 2;
- cose da non comprare o studiare troppo presto.

## Roadmap pilot

| Pilot | Finestra di pianificazione | Risultato |
| --- | --- | --- |
| **Pilot 0** | 10–21 agosto 2026 | APK didattica installabile per maintainer e studenti. |
| **Pilot 1** | 21 settembre–9 ottobre 2026 | Companion stradale foreground per 5–10 tester controllati. |
| **Pilot 2** | 2 novembre–11 dicembre 2026, da ristimare | Piccola beta chiusa dopo le evidenze del Pilot 1. |

Approfondimenti:

- [Materiali didattici e acquisti per fase](docs/it/59-materiali-didattici-e-acquisti-pilot-android.md)
- [Roadmap Android-first, stack e sequenza APK](docs/it/55-roadmap-android-first-e-pilot.md)
- [Percorso di studio Manning/Pluralsight/ufficiale](docs/it/56-percorso-studio-android-first.md)
- [Protocollo del futuro pilot su strada](docs/it/57-protocollo-pilot-stradale-android.md)
- [Come è costruita la shell Android](docs/it/58-shell-android-pilot0.md)
- [ADR-0010 Android-first](docs/adr/0010-android-first-pilot-sequence.md)

## Stack Android Pilot 0

```text
Android Gradle Plugin 9.3.0
Gradle Wrapper 9.5.1
compileSdk / targetSdk 37
minSdk 26
AGP built-in Kotlin
Kotlin / Compose compiler 2.4.0
Jetpack Compose BOM 2026.06.00
Material 3
single activity
```

`apps/android` è separato dai moduli Kotlin Multiplatform. I moduli shared non
importano API Android.

## Build

Prerequisito della fondazione:

```text
Java 21
```

Per Android servono anche SDK Platform 37 e Build Tools 36.0.0.

```bash
sh tools/tdna doctor
sh tools/tdna check
sh tools/tdna check-android
```

Artifact Android attesi:

```text
build/android/tdna-pilot0-debug.apk
build/android/tdna-pilot0-debug-androidTest.apk
build/android/sha256.txt
```

La APK instrumentation compilata non equivale a un test eseguito su dispositivo.

## Percorso didattico implementation-backed

```text
37  build riproducibile e supply chain
43  routing Java/Rust, Dijkstra e A*
44  routing contracts e fake provider
45  MapScene e fake renderer
46  LocationSample e replay deterministico
47  matched position e route progress
48  porta di map matching
49  missed exit e reroute
55  roadmap Android-first
56  percorso di studio
57  protocollo stradale
58  shell Android Pilot 0
59  materiali linkati e acquisti per fase
```

Indice completo: [docs/README.md](docs/README.md).

## Cosa studiare adesso

Con Manning e Pluralsight non serve acquistare un altro corso generalista.
L'ordine minimo consigliato è:

```text
Kotlin idiomatico
-> Android fundamentals/lifecycle
-> Jetpack Compose e state
-> coroutines/cancellation/Flow
-> test Android
-> Intents
-> permessi e foreground service solo prima del Pilot 1
```

Link, priorità, esercizi e acquisti sono nella
**[guida didattica e roadmap acquisti](docs/it/59-materiali-didattici-e-acquisti-pilot-android.md)**.

Il primo eventuale acquisto utile è un telefono Android reale, non un altro
corso.

## Laboratori della fondazione

```bash
sh tools/tdna lab build-bootstrap
sh tools/tdna lab reference-routing astar
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
sh tools/tdna lab location-replay
sh tools/tdna lab route-progress
sh tools/tdna lab map-matching
sh tools/tdna lab missed-exit
```

I Lab usano dati sintetici e benchmark diagnostici senza threshold. Non sono
prove di affidabilità su strada.

## Da dove iniziare

- [Guida didattica Android e acquisti](docs/it/59-materiali-didattici-e-acquisti-pilot-android.md)
- [Guida alla lettura](docs/it/03-guida-lettura-documentazione.md)
- [Regole operative](docs/it/00-regole-operative.md)
- [Review e merge](docs/it/06-review-e-merge.md)
- [Stato dello sviluppo](docs/project/development-status.md)
- [Report giornalieri](docs/project/daily/README.md)
- [Travel DNA Lab](docs/it/lab/README.md)
- [ADR](docs/adr/README.md)

## Principi

```text
Travel DNA dipende dai propri contratti, non dai provider.
Il navigatore è una capacità, non l'intero prodotto.
Il diario è privato per impostazione predefinita.
Ogni astrazione e linguaggio devono pagare il proprio costo.
Una funzione corretta ma lenta, insicura o non documentata non è conclusa.
```

## Licenza

Il modello di licenza non è ancora deciso. Prima di accettare contributi esterni
di codice deve essere chiuso
[ADR-0009](docs/adr/0009-project-licensing-model.md) e aggiunto `LICENSE`.
