# Project tooling

`tools/tdna` è il punto di ingresso comune per build, test, Lab e benchmark della
fondazione Travel DNA. È un orchestratore POSIX sottile: invoca Java, Cargo,
Gradle e Python senza reimplementarne il comportamento.

Eseguirlo tramite `sh` mantiene il comando utilizzabile anche quando un archivio
ZIP o un'operazione sui contenuti GitHub non conserva il bit eseguibile:

```bash
sh tools/tdna COMMAND
```

## Comandi correnti

| Comando | Scopo |
| --- | --- |
| `doctor` | Mostra disponibilità/versioni di Java, Python, Rust e Gradle. |
| `check-docs` | Verifica link Markdown locali e code fence bilanciate. |
| `check-architecture` | Controlla i confini di import dei moduli Kotlin shared. |
| `check-java` | Compila Java 21 con warning-as-error ed esegue i test. |
| `check-rust` | Esegue `cargo fmt --check` e i test Rust. |
| `check-contract` | Confronta byte-per-byte i report routing Java/Rust. |
| `check-kotlin` | Esegue test KMP, tutti i Lab Kotlin e il benchmark replay diagnostico. |
| `check` | Esegue l'intera Foundation CI localmente. |
| `lab reference-routing [dijkstra\|astar]` | Esegue il Lab Java/Rust. |
| `lab routing-contracts` | Esegue il Lab routing provider-neutral. |
| `lab map-scene` | Esegue il Lab MapScene/fake renderer. |
| `lab location-replay` | Esegue il Lab LocationSample/replay deterministico. |
| `bench location-replay [n] [run-dispari]` | Esegue il microbenchmark diagnostico replay. |
| `clean` | Rimuove `build/`. |

Esempi:

```bash
sh tools/tdna doctor
sh tools/tdna check-architecture
sh tools/tdna check-kotlin
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
sh tools/tdna lab location-replay
sh tools/tdna bench location-replay 10000 7
sh tools/tdna check
```

## Prerequisiti

Il percorso Java richiede:

- Java/Javac 21;
- Python 3;
- shell POSIX.

Il percorso Java/Rust richiede inoltre Rust stable, Cargo e rustfmt. Il percorso
Kotlin richiede una versione Gradle compatibile. La GitHub Actions Foundation CI
installa esplicitamente:

```text
Ubuntu 24.04
Java 21
Gradle 9.5.1
Kotlin 2.4.0
Rust stable
```

Il Gradle Wrapper committato è ancora un task dichiarato della fondazione. Fino
alla sua introduzione, la CI è l'ambiente di riferimento riproducibile e la
versione Gradle locale resta responsabilità del chiamante.

## Output generati

```text
build/java/reference-routing/
build/rust/
build/contract/
build/kotlin/routing-contracts-lab.json
build/kotlin/map-scene-lab.json
build/kotlin/location-replay-lab.json
build/kotlin/location-replay-benchmark.json
```

La CI conserva per 14 giorni i due output location replay come artifact
`foundation-kotlin-observations`. Il benchmark non è un gate: l'artifact serve a
ricostruire scenario e ordine di grandezza osservato.

## Benchmark location replay

```bash
sh tools/tdna bench location-replay 10000 7
```

Vincoli:

- campioni fra 1 e 100.000;
- iterazioni fra 1 e 25;
- iterazioni obbligatoriamente dispari;
- tre warm-up;
- minimo, mediana unica e massimo;
- nessuna soglia CI.

Il benchmark misura soltanto il runner in-memory con campioni sintetici. Non
misura parser, GPS, rete, database, map matching, MapLibre, batteria o dispositivo
mobile.

## Architecture checker

`tools/check_architecture.py` verifica gli import ammessi nei moduli Kotlin
shared. Analizza inoltre il codice eseguibile alla ricerca di token provider o
piattaforma vietati dopo aver rimosso commenti e literal.

La distinzione consente a un commento di spiegare il confine MapLibre o
CoreLocation senza trasformarlo in una dipendenza. Il checker è intenzionalmente
leggero e non sostituisce un futuro gate sul dependency graph Gradle.

## Regole di design

- locale e CI usano lo stesso entry point;
- un tool obbligatorio mancante fa fallire il comando interessato;
- `doctor` osserva e non installa software privilegiato;
- gli artifact generati restano in `build/` o nei target configurati;
- il wrapper deve restare leggibile integralmente da uno studente;
- i comandi nativi restano documentati e utilizzabili per il debug;
- un nuovo comando richiede implementazione, test, documentazione e CI nella
  stessa PR;
- un benchmark diagnostico non diventa una promessa prestazionale.

## Comandi futuri

```text
sh tools/tdna build
sh tools/tdna test shared
sh tools/tdna test android
sh tools/tdna test ios
sh tools/tdna replay <scenario-id>
sh tools/tdna bench <benchmark-family>
sh tools/tdna docs
```
