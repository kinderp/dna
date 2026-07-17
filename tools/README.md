# Project tooling

`tools/tdna` is the common entry point for Travel DNA foundation checks. It is a
thin POSIX shell orchestrator: it calls native tools and does not reimplement
Java, Cargo, Gradle or Python behavior.

Invoke it through `sh` so the same command works even when a ZIP extraction or a
GitHub content operation does not preserve the executable bit:

```bash
sh tools/tdna COMMAND
```

## Current commands

| Command | Purpose |
| --- | --- |
| `doctor` | Show Java, Python, Rust and Gradle toolchain availability. |
| `check-docs` | Validate local Markdown links and balanced code fences. |
| `check-architecture` | Enforce source-level dependency boundaries for shared Kotlin modules. |
| `check-java` | Compile Java 21 sources with warnings-as-errors and run tests. |
| `check-rust` | Run `cargo fmt --check` and Rust tests. |
| `check-contract` | Compare Java and Rust route reports byte-for-byte. |
| `check-kotlin` | Run KMP contract/fake-provider tests and emit the routing-contracts Lab report. |
| `check` | Run the complete foundation verification. |
| `lab reference-routing [dijkstra\|astar]` | Execute the Java/Rust Lab. |
| `lab routing-contracts` | Execute the KMP provider-neutral contracts Lab. |
| `clean` | Remove generated `build/` output. |

Examples:

```bash
sh tools/tdna doctor
sh tools/tdna check-java
sh tools/tdna check-architecture
sh tools/tdna check-kotlin
sh tools/tdna lab routing-contracts
sh tools/tdna check
```

## Current prerequisites

The Java-only path requires:

- Java/Javac 21;
- Python 3;
- a POSIX shell.

The Java/Rust path additionally requires stable Rust with Cargo and rustfmt.
The Kotlin path additionally requires a compatible Gradle installation. The
GitHub Actions workflow provisions Gradle 9.5.1 explicitly and resolves Kotlin
2.4.0 through the version catalog.

A committed Gradle Wrapper is still a declared foundation task. Until it exists,
local Gradle version management is the caller's responsibility; CI remains the
reproducible reference environment.

## Generated outputs

Current commands write only under generated directories:

```text
build/java/reference-routing/
build/rust/
build/contract/
build/kotlin/
```

Gradle and Cargo may also use their standard caches outside the repository.

## Design rules

- local and CI verification use the same project entry point;
- missing mandatory tools make the relevant command fail explicitly;
- `doctor` reports availability but never installs privileged system software;
- generated project files stay under `build/` or configured tool target paths;
- the wrapper must remain small enough that students can read it completely;
- native commands remain documented and may be run directly while debugging;
- adding a command requires implementation, documentation and CI alignment in
  the same pull request.

## Future commands

The interface will grow only when real modules exist. Candidate commands are:

```text
sh tools/tdna build
sh tools/tdna test shared
sh tools/tdna test android
sh tools/tdna test ios
sh tools/tdna replay <scenario-id>
sh tools/tdna bench <benchmark-family>
sh tools/tdna docs
```
