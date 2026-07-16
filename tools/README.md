# Tooling roadmap

La codebase avrà più build system. Per studenti e CI è previsto un entry point
comune, senza nascondere i comandi nativi.

Interfaccia target:

```text
./tdna doctor
./tdna build
./tdna check
./tdna test shared
./tdna test rust
./tdna test android
./tdna test ios
./tdna replay <scenario-id>
./tdna bench <benchmark-family>
./tdna docs
```

Il comando `tdna` sarà un orchestratore sottile di Gradle, Cargo, xcodebuild e
strumenti documentali. Non deve reimplementare la loro logica né produrre una CI
misteriosa diversa dalla procedura locale.

Il primo script verrà introdotto soltanto quando esistono almeno due toolchain da
orchestrare e test che ne fissano il comportamento.
