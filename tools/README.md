# Project tooling

`tools/tdna` is the common entry point for the first Travel DNA foundation
checks. It is a thin POSIX shell orchestrator: it calls native tools and does not
reimplement Java, Cargo or Python behavior.

Invoke it through `sh` so the same command works even when a ZIP extraction or a
GitHub content operation does not preserve the executable bit:

```bash
sh tools/tdna COMMAND
```

## Current commands

| Command | Purpose |
| --- | --- |
| `doctor` | Show Java, Python and Rust toolchain availability. |
| `check-docs` | Validate local Markdown links and balanced code fences. |
| `check-java` | Compile Java 21 sources with warnings-as-errors and run tests. |
| `check-rust` | Run `cargo fmt --check` and Rust tests. |
| `check-contract` | Compare Java and Rust route reports byte-for-byte. |
| `check` | Run the complete foundation verification. |
| `lab reference-routing [dijkstra\|astar]` | Execute the first Lab scenario. |
| `clean` | Remove generated `build/` output. |

Examples:

```bash
sh tools/tdna doctor
sh tools/tdna check-java
sh tools/tdna lab reference-routing astar
sh tools/tdna check
```

## Current prerequisites

The Java-only path requires:

- Java/Javac 21;
- Python 3;
- a POSIX shell.

The full path additionally requires stable Rust with Cargo and rustfmt. The
GitHub Actions workflow installs Java 21 and stable Rust before invoking exactly
the same project commands.

## Design rules

- local and CI verification use the same entry point;
- missing mandatory tools make the relevant command fail explicitly;
- `doctor` reports availability but never installs privileged system software;
- generated files stay under `build/` or Cargo's configured target directory;
- the wrapper must remain small enough that students can read it completely;
- native commands remain documented and may be run directly while debugging.

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

Adding a command requires implementation, documentation and CI alignment in the
same pull request.
