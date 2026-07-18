# Project tooling

`tools/tdna` è l'entry point comune per build, test, Lab e benchmark. È un
orchestratore POSIX sottile che invoca Java, Cargo, il Gradle Wrapper e Python.

```bash
sh tools/tdna COMMAND
```

## Bootstrap supportato

```text
Java 21
Python 3
Rust stable per i task Rust
./gradlew committato per i task Kotlin
```

Un comando globale `gradle` è opzionale e non viene usato come fonte di verità.

## Comandi

| Comando | Scopo |
| --- | --- |
| `doctor` | Mostra tool richiesti/opzionali e presenza del Wrapper. |
| `check-gradle-wrapper` | Verifica file, proprietà, JAR, permessi e hash. |
| `check-ci-actions` | Verifica allowlist e SHA immutabili delle Actions. |
| `check-docs` | Verifica link e code fence. |
| `check-architecture` | Controlla import Kotlin shared. |
| `check-java` | Compila/testa Java 21. |
| `check-rust` | Esegue fmt e test Rust. |
| `check-contract` | Confronta report Java/Rust. |
| `check-kotlin` | Esegue test KMP, Lab e benchmark tramite `./gradlew`. |
| `check` | Esegue l'intera Foundation CI localmente. |
| `lab build-bootstrap` | Lab Wrapper, hash e Action pinning. |
| `lab reference-routing [dijkstra\|astar]` | Lab algoritmo. |
| `lab routing-contracts` | Lab provider-neutral routing. |
| `lab map-scene` | Lab scena/renderer. |
| `lab location-replay` | Lab sample/replay. |
| `lab route-progress` | Lab matched route progress. |
| `lab map-matching` | Lab porta matcher/fake/progress. |
| `lab missed-exit` | Lab evidenza off-route e reroute correlato. |
| `bench location-replay [n] [run-dispari]` | Benchmark replay. |
| `bench route-progress [n] [run-dispari]` | Benchmark progress. |
| `bench map-matching [n] [run-dispari]` | Benchmark matching fake + progress. |
| `bench off-route [n] [run-dispari]` | Benchmark state machine off-route. |
| `clean` | Rimuove `build/`. |

## Ambiente CI

```text
Ubuntu 24.04
Java 21
Gradle Wrapper 9.5.1
Kotlin 2.4.0
Rust stable
```

La CI configura la cache Gradle ma non installa una versione globale del tool.
Esegue `./gradlew` dalla clone pulita.

## Output generati

```text
build/java/reference-routing/
build/rust/
build/contract/
build/kotlin/build-bootstrap-lab.json
build/kotlin/routing-contracts-lab.json
build/kotlin/map-scene-lab.json
build/kotlin/location-replay-lab.json
build/kotlin/location-replay-benchmark.json
build/kotlin/route-progress-lab.json
build/kotlin/route-progress-benchmark.json
build/kotlin/map-matching-lab.json
build/kotlin/map-matching-benchmark.json
build/kotlin/missed-exit-lab.json
build/kotlin/off-route-benchmark.json
```

La CI conserva gli output Kotlin come artifact `foundation-kotlin-observations`
per 14 giorni. I benchmark non sono gate.

## Build-bootstrap checks

```bash
sh tools/tdna check-gradle-wrapper
sh tools/tdna check-ci-actions
sh tools/tdna lab build-bootstrap
```

Il primo checker confronta i byte con
`gradle/wrapper/tdna-wrapper-policy.json`. Il secondo rifiuta action esterne non
allowlisted o non bloccate a full SHA. Il Lab emette un report JSON canonico.

I checksum rilevano drift/corruzione rispetto alla policy revisionata; non sono
presentati come firma o prova indipendente dell'identità dell'editore.

## Benchmark off-route

```bash
sh tools/tdna bench off-route 10000 7
```

Misura validazione, transizione e commit in-memory del tracker; non provider,
rete, route replacement, dispositivo o strada.

## Benchmark map matching boundary

```bash
sh tools/tdna bench map-matching 10000 7
```

Misura exact fake lookup, diagnostica bounded e progress accept; non ricerca
geografica o accuratezza.

## Architecture checker

`tools/check_architecture.py` verifica import ammessi e token provider/piattaforma
nei moduli shared. È un guardrail sorgente, non un sostituto del grafo Gradle.

## Regole

- locale e CI usano lo stesso entry point;
- il Wrapper è l'unico Gradle supportato;
- tool mancanti causano un errore esplicito;
- output generati restano in `build/`;
- un comando nuovo richiede test, documentazione e CI nella stessa PR;
- un benchmark diagnostico non diventa SLA.
