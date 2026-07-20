# Report di sviluppo — 18 luglio 2026 — Gradle Wrapper e chiusura Foundations v0

## Stato del report

`pre-final-review complete`

Il ledger autorevole di CI finale, due review e merge è la timeline della
[PR #24](https://github.com/kinderp/tdna/pull/24). Questo file viene chiuso prima
dello SHA finale per non introdurre un commit dopo le review.

## Obiettivo

Eliminare l'ultimo requisito ambientale non versionato della fondazione:

```text
prima:
Java + Gradle globale -> build

dopo:
Java + Wrapper committato/verificato -> Gradle 9.5.1 -> build
```

Contestualmente:

- verificare la catena bootstrap;
- bloccare le GitHub Actions a commit SHA;
- documentare limiti e modello di minaccia;
- pubblicare il rapporto di chiusura Foundations v0.

## Tracciabilità

- issue: [#23](https://github.com/kinderp/tdna/issues/23);
- PR: [#24](https://github.com/kinderp/tdna/pull/24);
- branch: `agent/gradle-wrapper-foundation-closure`;
- base verificata: `8570eb466b43384756f2678da2303929295e087a`;
- capitolo: `docs/it/37-build-riproducibile-gradle-wrapper.md`;
- scenario: `docs/it/lab/scenarios/gradle-wrapper-riproducibile.md`;
- closure report: `docs/project/foundation-v0-closure.md`.

## Bootstrap controllato

Il repository non possedeva i file wrapper e il connettore GitHub non poteva
trasferire direttamente un binario locale. È stata usata una procedura temporanea
ed esplicita:

1. Foundation CI ha configurato Gradle 9.5.1 già usato dalla baseline;
2. il task ufficiale `gradle wrapper` ha generato i quattro file;
3. `distributionSha256Sum` è stato impostato durante la generazione;
4. il JAR è stato confrontato con il checksum revisionato;
5. i file sono stati pubblicati come artifact di un giorno;
6. un job interno con permesso `contents: write` li ha committati sulla sola branch
   della PR;
7. il workflow temporaneo è stato rimosso;
8. il workflow finale è tornato a `contents: read`.

Il primo tentativo di self-commit è fallito perché `actions/checkout` aveva
selezionato il merge ref detached della PR. Il push non poteva essere fast-forward
sulla branch sorgente. Il secondo tentativo ha checkoutato esplicitamente la head
branch e ha committato correttamente i file.

Questa procedura di bootstrap non resta in `main`.

## Artifact e checksum

Artifact bootstrap iniziale:

```text
artifact: 8426303461
name: gradle-wrapper-bootstrap
digest: sha256:ea066e17c8029af6ef89484b3e56a8861ed4c10f423538340161f81f59b84ce4
retention: 1 giorno
```

Checksum revisionati:

```text
Gradle 9.5.1 bin distribution
bafc141b619ad6350fd975fc903156dd5c151998cc8b058e8c1044ab5f7b031f

Gradle 9.5.1 Wrapper JAR
497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7

gradlew
ab5c0cad16305af2e619c159c1f58dd68d07fab9c11e36701e109c0277407f7a

gradlew.bat
475c4f08cd57cf2faa819e7f36d72aa93f0ad646ea23a8f7fa3ef54dee1cbc52

gradle-wrapper.properties
9caeb142fade370957e5e9cd95a83441bbe41f73a1863398dd5467695853332e
```

## Codice e policy introdotti

```text
gradlew
gradlew.bat
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
gradle/wrapper/tdna-wrapper-policy.json
tools/check_gradle_wrapper.py
tools/check_ci_actions.py
tools/gradle_wrapper_report.py
```

### Wrapper checker

Verifica:

- file richiesti;
- schema policy;
- proprietà esatte;
- URL/versione/host;
- checksum distribuzione;
- checksum JAR/launcher/properties;
- bit eseguibile;
- singolo Wrapper JAR;
- struttura ZIP sicura;
- main class e SPDX manifest.

### Action checker

Verifica:

- full SHA di 40 caratteri;
- allowlist revisionata;
- SHA atteso per ogni action;
- commento versione umano;
- nessun tag mobile come `@v4` nel workflow finale.

### Wrapper Lab

```bash
sh tools/tdna lab build-bootstrap
```

Output atteso:

```json
{"scenario":"build-bootstrap-v0","gradle_version":"9.5.1","distribution_sha256":"bafc141b619ad6350fd975fc903156dd5c151998cc8b058e8c1044ab5f7b031f","wrapper_jar_sha256":"497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7","wrapper_files":4,"actions_pinned":5,"global_gradle_required":false,"java_required":true}
```

## Workflow finale

Il workflow finale:

- usa `contents: read`;
- blocca checkout, setup-java, setup-gradle e upload-artifact a SHA completi;
- configura la cache Gradle senza installare una versione globale;
- esegue i checker prima del build;
- esegue `./gradlew --version` e verifica `Gradle 9.5.1`;
- usa `sh tools/tdna check` per l'intera fondazione;
- conserva il build-bootstrap report insieme agli altri artifact.

`tools/tdna` usa soltanto `./gradlew` per task, Lab e benchmark Kotlin.

## Modello di fiducia

Le verifiche introdotte dimostrano:

- uguaglianza con byte revisionati;
- assenza di drift;
- integrità del download rispetto al checksum committato;
- immutabilità delle revisioni Action scelte.

Non dimostrano da sole:

- identità assoluta dell'editore;
- assenza di compromissione simultanea di codice, policy e review;
- build ermetico;
- integrità di ogni dipendenza Maven/Gradle;
- presenza di firme o attestazioni esterne.

Questi limiti sono dichiarati nel capitolo e nel closure report.

## Documentazione prodotta

- capitolo 37;
- scenario build-bootstrap;
- rapporto Foundations v0;
- aggiornamento supply-chain e tooling;
- repository, reading path e Lab indexes;
- stato funzionalità/documentazione/commenti;
- milestone e development status;
- questo report e indice permanente.

## Decisioni prese autonomamente

1. Conservare Gradle 9.5.1, già baseline CI, senza upgrade laterale.
2. Usare distribuzione `bin`, non `all`.
3. Committare il JAR dopo verifica, non scaricarlo a ogni CI con script custom.
4. Verificare anche launcher e properties.
5. Fail-closed su action non allowlisted.
6. Rendere Gradle globale opzionale.
7. Separare bootstrap temporaneo e workflow finale.
8. Non chiamare checksum una firma.

Sono decisioni reversibili e non cambiano il prodotto.

## Debito residuo

- dependency verification Gradle/Maven;
- dependency locking completo;
- SBOM;
- attestazioni firmate;
- build Windows CI;
- mirror controllato;
- build ermetico/offline;
- Android/iOS.

## Review plan

### Round 1

- file e hash;
- parser/proprietà;
- JAR safety;
- wrapper entry point;
- clean-checkout smoke;
- failure modes dei checker.

### Round 2

- workflow permissions;
- action pinning;
- trust claims;
- documentazione;
- closure criteria;
- CI e scope.

## Decisione successiva

Dopo merge e verifica di `main`, la prima shell mobile richiede una decisione di
prodotto/architettura. La raccomandazione corrente è Android-first, ma non viene
aperta automaticamente una PR mobile senza registrare la scelta.
