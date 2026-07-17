# Project tooling

`tools/tdna` è l'entry point comune per build, test, Lab e benchmark della
fondazione. È un orchestratore POSIX sottile che invoca Java, Cargo, Gradle e
Python.

```bash
sh tools/tdna COMMAND
```

## Comandi

| Comando | Scopo |
| --- | --- |
| `doctor` | Mostra Java, Python, Rust e Gradle. |
| `check-docs` | Verifica link e code fence. |
| `check-architecture` | Controlla import Kotlin shared. |
| `check-java` | Compila/testa Java 21. |
| `check-rust` | Esegue fmt e test Rust. |
| `check-contract` | Confronta report Java/Rust. |
| `check-kotlin` | Esegue test KMP, Lab e benchmark diagnostici. |
| `check` | Esegue l'intera Foundation CI localmente. |
| `lab reference-routing [dijkstra\|astar]` | Lab algoritmo. |
| `lab routing-contracts` | Lab provider-neutral. |
| `lab map-scene` | Lab scena/renderer. |
| `lab location-replay` | Lab sample/replay. |
| `lab route-progress` | Lab matched route progress. |
| `bench location-replay [n] [run-dispari]` | Benchmark replay. |
| `bench route-progress [n] [run-dispari]` | Benchmark progress. |
| `clean` | Rimuove `build/`. |

## Ambiente CI

```text
Ubuntu 24.04
Java 21
Gradle 9.5.1
Kotlin 2.4.0
Rust stable
```

Il Gradle Wrapper committato resta un task della fondazione; fino ad allora la CI
è l'ambiente riproducibile di riferimento.

## Output generati

```text
build/java/reference-routing/
build/rust/
build/contract/
build/kotlin/routing-contracts-lab.json
build/kotlin/map-scene-lab.json
build/kotlin/location-replay-lab.json
build/kotlin/location-replay-benchmark.json
build/kotlin/route-progress-lab.json
build/kotlin/route-progress-benchmark.json
```

La CI conserva gli output Kotlin come artifact `foundation-kotlin-observations`
per 14 giorni. I benchmark non sono gate.

## Benchmark route progress

```bash
sh tools/tdna bench route-progress 10000 7
```

Vincoli:

- 2–100.000 campioni;
- 1–25 iterazioni, obbligatoriamente dispari;
- tre warm-up;
- fino a 100 leg e una manovra per leg più `Arrive`;
- route, posizioni, tracker e cursori costruiti fuori dal timer;
- reset fuori dal timer;
- finestra misurata: `accept`, binary search, snapshot e commit.

Non misura map matching, projector/renderer, GPS, rete, database, batteria o
dispositivo mobile.

## Architecture checker

`tools/check_architecture.py` verifica gli import ammessi nei moduli shared e
cerca token provider/piattaforma nel codice eseguibile dopo aver rimosso commenti
e literal. È un primo guardrail e non sostituisce un futuro controllo del grafo
Gradle.

## Regole

- locale e CI usano lo stesso entry point;
- tool mancanti causano un errore esplicito;
- output generati restano in `build/`;
- un comando nuovo richiede test, documentazione e CI nella stessa PR;
- un benchmark diagnostico non diventa SLA.
