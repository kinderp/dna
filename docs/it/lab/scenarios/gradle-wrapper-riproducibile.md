# Scenario: Gradle Wrapper riproducibile

id: `lab.engineering.build-bootstrap.v0`

status: `executable`

scenario kind: `repository-engineering`

## Learning goal

Seguire il bootstrap dal clone pulito al build Gradle senza dipendere da una
installazione globale del tool e distinguere le prove di integrità dalle prove di
autenticità dell'editore.

## Prerequisiti

- [Regole operative](../../00-regole-operative.md)
- [Licenze e supply chain](../../36-licenze-dati-supply-chain.md)
- [Capitolo sul Gradle Wrapper](../../37-build-riproducibile-gradle-wrapper.md)

## User story

Uno studente clona TDNA su una macchina con Java 21 e nessun requisito di Gradle
globale. Verifica i file committati, avvia Gradle 9.5.1 tramite `./gradlew` ed
esegue lo stesso entry point usato dalla CI.

## Platforms

- Linux/POSIX con `gradlew`;
- Windows con `gradlew.bat`;
- GitHub Actions Linux;
- Java 21.

## Inputs

```text
gradlew
gradlew.bat
gradle-wrapper.jar
gradle-wrapper.properties
tdna-wrapper-policy.json
foundation-ci.yml
```

I file wrapper sono stati generati dal task ufficiale Gradle 9.5.1 e verificati
prima del commit.

## Trigger

```bash
sh tools/tdna lab build-bootstrap
```

Build completo:

```bash
sh tools/tdna check
```

## Expected evidence

- tutti i file richiesti esistono;
- `gradlew` è eseguibile su POSIX;
- URL e versione sono quelli revisionati;
- checksum distribuzione coincide;
- checksum JAR, launcher e properties coincidono;
- manifest JAR contiene la main class attesa;
- non esistono path pericolosi nel JAR;
- esiste un solo Wrapper JAR;
- tutte le action esterne sono pin a SHA completo e allowlisted;
- `./gradlew --version` mostra Gradle 9.5.1;
- il global `gradle` non è requisito del progetto;
- il report JSON coincide con il ground truth.

## Expected output

```json
{"scenario":"build-bootstrap-v0","gradle_version":"9.5.1","distribution_sha256":"bafc141b619ad6350fd975fc903156dd5c151998cc8b058e8c1044ab5f7b031f","wrapper_jar_sha256":"497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7","wrapper_files":4,"actions_pinned":5,"global_gradle_required":false,"java_required":true}
```

## Logical tracepoints

```text
BUILD_BOOTSTRAP_POLICY_LOADED
GRADLE_WRAPPER_FILES_VERIFIED
GRADLE_DISTRIBUTION_POLICY_VERIFIED
GITHUB_ACTION_REFERENCES_VERIFIED
GRADLE_WRAPPER_STARTED
BUILD_BOOTSTRAP_REPORT_EMITTED
```

Sono tracepoint documentali. Il checker non invia telemetria.

## Function and module path

```text
sh tools/tdna lab build-bootstrap
-> tools/gradle_wrapper_report.py
   -> check_gradle_wrapper.validate_wrapper
      -> properties parsing
      -> SHA-256 verification
      -> JAR structure/manifest verification
   -> check_ci_actions.validate_workflows
      -> action SHA/allowlist verification
-> canonical JSON report
```

Il build completo aggiunge:

```text
./gradlew
-> GradleWrapperMain
-> distribution cache/download
-> distribution SHA verification
-> Gradle 9.5.1
-> project tasks
```

## State ownership

| Stato | Proprietario | Durata |
| --- | --- | --- |
| wrapper policy | repository | versionata |
| distribution cache | Gradle user home | locale/CI cache |
| downloaded ZIP | Wrapper/Gradle cache | fino a cleanup |
| build state | Gradle process | una esecuzione |
| Lab report | CLI/artifact CI | diagnostico |

Il repository non possiede credenziali di download né modifica la home utente.

## Security properties

- checksum verificati prima di affidarsi ai byte;
- URL HTTPS e host Gradle atteso;
- action esterne a SHA completo;
- workflow normale con `contents: read`;
- checker fail-closed su drift;
- JAR ZIP privo di path traversal;
- nessun segreto nel report.

## Privacy properties

- nessun dato utente;
- nessuna posizione;
- nessun viaggio;
- nessun identificatore personale;
- artifact limitati a report e diagnostica del build.

## Existing tests/checks

- `tools/check_gradle_wrapper.py`;
- `tools/check_ci_actions.py`;
- `tools/gradle_wrapper_report.py`;
- Foundation CI su clone GitHub pulita;
- `./gradlew --version` con verifica testuale della versione;
- build multi-language completa.

## Missing/future checks

- verifica dipendenze Gradle/Maven artifact per artifact;
- dependency locking completo;
- SBOM;
- attestazioni firmate;
- mirror controllato;
- build completamente offline/ermetico;
- smoke Windows in CI;
- verifica firma o provenance esterna del Wrapper.

## Common failures

- `gradlew` non eseguibile;
- URL versione e policy divergenti;
- properties modificate senza aggiornare hash;
- JAR sostituito e policy non aggiornata;
- action referenziata con tag mobile;
- action nuova non allowlisted;
- launcher generati da versioni diverse;
- build locale ancora basato su `gradle` globale;
- checksum descritto impropriamente come firma.

## Non-goals

- dimostrare identità assoluta dell'editore;
- vendorizzare Gradle;
- garantire rete disponibile;
- rendere tutte le dipendenze offline;
- introdurre Android/iOS;
- aggiornare Gradle oltre 9.5.1.

## Related docs

- [Capitolo 37](../../37-build-riproducibile-gradle-wrapper.md)
- [Supply chain](../../36-licenze-dati-supply-chain.md)
- [Tooling](../../../README.md)
- [Rapporto di chiusura Foundations](../../../project/foundation-v0-closure.md)
