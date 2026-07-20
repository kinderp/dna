# Build riproducibile, Gradle Wrapper e catena di fiducia

## Stato del capitolo

`implementation-backed — Foundations and Travel DNA Lab v0`

Questo capitolo documenta il bootstrap di build usato dal repository TDNA:

```text
clone pulita
-> Java 21
-> file Gradle Wrapper committati
-> distribuzione Gradle 9.5.1 verificata
-> build multi-modulo
-> Lab e test
```

Il codice e le policy si trovano in:

```text
gradlew
gradlew.bat
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
gradle/wrapper/tdna-wrapper-policy.json
tools/check_gradle_wrapper.py
tools/check_ci_actions.py
tools/gradle_wrapper_report.py
tools/tdna
.github/workflows/foundation-ci.yml
```

## Cosa imparerai

Al termine dovresti saper spiegare:

1. perché `gradle` e `./gradlew` non sono lo stesso entry point;
2. quali file costituiscono il Gradle Wrapper;
3. perché il Wrapper JAR viene committato, anche se è un binario;
4. che cosa protegge `distributionSha256Sum`;
5. perché serve anche un checksum revisionato del Wrapper JAR;
6. la differenza fra rilevare corruzione, rilevare drift e dimostrare l'identità dell'editore;
7. perché un tag come `@v4` è meno immutabile di un commit SHA completo;
8. come TDNA impedisce che CI e sviluppo locale usino build tool diversi;
9. perché la prima esecuzione del Wrapper può usare la rete senza introdurre rete nei test di dominio;
10. come aggiornare Gradle senza sostituire file alla cieca;
11. quali prove servono prima di dichiarare una build riproducibile;
12. che cosa resta ancora fuori dalla supply-chain foundation.

## Il problema: “funziona sul mio computer”

Senza un bootstrap versionato, due sviluppatori possono eseguire:

```bash
gradle check
```

ma ottenere comportamenti differenti perché possiedono:

- versioni Gradle diverse;
- JVM diverse;
- plugin risolti in momenti diversi;
- cache differenti;
- launcher o opzioni differenti.

La documentazione che dice soltanto “installare Gradle” trasferisce una decisione
architetturale sul computer di ogni studente. Questo è fragile e poco didattico:
non è evidente quale versione sia la fonte di verità.

TDNA usa invece:

```bash
./gradlew check
```

Il repository sceglie la versione; il computer fornisce Java e un sistema capace
di eseguire il launcher.

## Che cos'è il Gradle Wrapper

Il Wrapper non è un singolo file. È un piccolo bootstrap composto da quattro
artifact:

```text
gradlew
```

Launcher POSIX per Linux e macOS.

```text
gradlew.bat
```

Launcher Windows.

```text
gradle/wrapper/gradle-wrapper.jar
```

Codice Java del bootstrap. Legge la configurazione, trova o scarica la
distribuzione e avvia Gradle.

```text
gradle/wrapper/gradle-wrapper.properties
```

Configurazione della distribuzione scelta.

La configurazione TDNA contiene:

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-9.5.1-bin.zip
distributionSha256Sum=bafc141b619ad6350fd975fc903156dd5c151998cc8b058e8c1044ab5f7b031f
validateDistributionUrl=true
networkTimeout=10000
retries=0
retryBackOffMs=500
```

## Perché il JAR viene committato

Una domanda comune è:

> Perché mettere un file binario in Git invece di rigenerarlo ogni volta?

Perché il Wrapper è il programma che deve poter avviare il build prima che il
build esista. Se il repository contenesse soltanto uno script che richiede una
installazione globale di Gradle per rigenerare il JAR, il bootstrap dipenderebbe
ancora dall'ambiente locale.

Committendo il JAR:

```text
clone
-> Java esegue un bootstrap noto
-> bootstrap ottiene la distribuzione dichiarata
-> distribuzione esegue il build
```

Il fatto che il file sia committato non significa che debba essere accettato
senza verifica. In TDNA il suo SHA-256 revisionato è:

```text
497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7
```

## Due checksum con responsabilità diverse

### Checksum della distribuzione

`distributionSha256Sum` protegge il file ZIP scaricato dal Wrapper:

```text
URL dichiarato
-> byte scaricati
-> SHA-256
-> confronto con valore committato
-> estrazione soltanto se coincide
```

Protegge da:

- download corrotto;
- cache modificata;
- sostituzione casuale o malevola dei byte, se il checksum nel repository resta
  affidabile;
- drift involontario della versione scaricata.

### Checksum del Wrapper JAR

Il checksum del JAR protegge il programma che effettua il download e l'avvio.
Senza questa seconda verifica potremmo controllare la distribuzione usando un
bootstrap modificato.

TDNA verifica anche gli hash di:

```text
gradlew
gradlew.bat
gradle-wrapper.properties
```

Questi valori sono conservati in:

```text
gradle/wrapper/tdna-wrapper-policy.json
```

## Integrità non significa automaticamente autenticità

È essenziale non sovrastimare il modello.

Un checksum committato risponde bene alla domanda:

> I byte correnti sono gli stessi byte che abbiamo revisionato?

Non risponde da solo alla domanda:

> Chi ha pubblicato per primo quei byte e possiamo dimostrarne crittograficamente
> l'identità tramite una root of trust esterna?

Se un attaccante potesse modificare contemporaneamente:

```text
binario
checksum
checker
workflow
```

un confronto interno non basterebbe.

Per questo TDNA combina più livelli:

```text
review Git
branch/PR history
checksum distribuzione
checksum JAR e launcher
checker indipendente nel repository
CI su clone pulita
Actions bloccate a commit SHA
regola dei due round puliti
```

Questa combinazione aumenta la resistenza a drift e modifiche non osservate, ma
non viene chiamata “prova assoluta dell'editore”. Una futura supply-chain
milestone potrà aggiungere firme, attestazioni, mirror controllati e provenance
esterna.

## `tools/check_gradle_wrapper.py`

Il checker verifica:

1. presenza dei cinque file richiesti, inclusa la policy TDNA;
2. schema della policy;
3. insieme esatto delle proprietà ammesse;
4. URL HTTPS verso `services.gradle.org`;
5. versione `9.5.1` coerente nell'URL;
6. checksum della distribuzione;
7. timeout, retry e validazione URL;
8. SHA-256 di JAR, launcher e properties;
9. bit eseguibile del launcher POSIX;
10. presenza di un solo `gradle-wrapper.jar` nel repository;
11. struttura ZIP del JAR senza path traversal;
12. main class e identificatore di licenza nel manifest.

Comando:

```bash
sh tools/tdna check-gradle-wrapper
```

Un aggiornamento parziale fallisce. Per esempio, modificare soltanto:

```properties
distributionUrl=...gradle-9.6-bin.zip
```

non è sufficiente: versione, checksum, JAR, launcher, policy, documentazione e CI
devono essere revisionati insieme.

## GitHub Actions e riferimenti immutabili

Una action scritta così:

```yaml
uses: actions/checkout@v4
```

punta a un riferimento che il proprietario del repository può spostare.

TDNA usa un commit SHA completo:

```yaml
uses: actions/checkout@34e114876b0b11c390a56381ad16ebd13914f8d5 # v4
```

Il commento mantiene leggibile la famiglia di versione; lo SHA rende immutabile
la revisione eseguita.

`tools/check_ci_actions.py`:

- scansiona i workflow;
- consente action locali e immagini Docker esplicite;
- richiede SHA esadecimale di 40 caratteri per action esterne;
- confronta ogni action con una allowlist revisionata;
- richiede un commento di versione umano;
- rifiuta action sconosciute finché non vengono aggiunte con una review.

Comando:

```bash
sh tools/tdna check-ci-actions
```

## Stesso entry point in locale e CI

La pipeline non esegue una versione nascosta del progetto. Usa:

```bash
sh tools/tdna check
```

Lo script usa a sua volta:

```bash
./gradlew --no-daemon ...
```

Non cerca più un comando globale `gradle`.

Il global Gradle può essere presente per manutenzione o sperimentazione, ma è
opzionale e non è la fonte di verità.

## Primo avvio e accesso alla rete

Alla prima esecuzione, se la distribuzione non è in cache:

```text
./gradlew
-> legge distributionUrl
-> scarica Gradle 9.5.1
-> verifica distributionSha256Sum
-> estrae nella Gradle user home
-> esegue il build
```

Questo accesso alla rete appartiene al bootstrap del tool, non ai moduli di
dominio.

I test Java/Rust/Kotlin e i core deterministici non ottengono il permesso di usare
la rete solo perché il launcher può scaricare Gradle. In CI la cache può evitare
il download; una cache hit non cambia la verifica dei file committati.

## Clean-checkout smoke test

La prova significativa non è eseguire il Wrapper in una working tree già
preparata manualmente. È:

```text
checkout Git pulito
-> Java 21
-> nessun requisito di Gradle globale
-> check dei file committati
-> ./gradlew --version
-> Gradle 9.5.1
-> build e test
```

Il workflow esegue esplicitamente:

```bash
sh tools/tdna check-gradle-wrapper
sh tools/tdna check-ci-actions
./gradlew --no-daemon --version
sh tools/tdna check
```

La riga `Gradle 9.5.1` nell'output è verificata prima dei test Kotlin.

## Build-bootstrap Lab

Comando:

```bash
sh tools/tdna lab build-bootstrap
```

Output canonico:

```json
{"scenario":"build-bootstrap-v0","gradle_version":"9.5.1","distribution_sha256":"bafc141b619ad6350fd975fc903156dd5c151998cc8b058e8c1044ab5f7b031f","wrapper_jar_sha256":"497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7","wrapper_files":4,"actions_pinned":5,"global_gradle_required":false,"java_required":true}
```

Il report dimostra la policy del repository. Non prova che il computer sia privo
di malware né che Internet sia sempre disponibile.

## Procedura di aggiornamento

Un aggiornamento Gradle corretto è una vertical slice di infrastruttura:

1. aprire issue con versione, motivazione e compatibilità;
2. verificare release notes e requisiti JVM;
3. generare il Wrapper con una versione Gradle esplicita;
4. ottenere checksum ufficiali da una fonte indipendente dal file scaricato;
5. verificare JAR e distribuzione;
6. aggiornare policy e checker;
7. eseguire build e test da clone pulita;
8. controllare warning e deprecazioni;
9. aggiornare capitolo, stato e report;
10. ottenere CI verde e due review pulite sullo stesso SHA;
11. fare merge con expected-head guard.

Non aggiornare copiando soltanto un nuovo JAR da una risposta web o da una cache
locale non tracciata.

## Alternative considerate

### Richiedere Gradle globale

**Pro:** nessun JAR nel repository.  
**Contro:** ambiente non riproducibile, onboarding peggiore, CI e locale possono
divergere.

Scartata come entry point supportato.

### Scaricare il Wrapper JAR con uno script custom

**Pro:** il binario non è committato.  
**Contro:** serve un altro downloader da rendere affidabile; il bootstrap si
sposta invece di scomparire; offline onboarding peggiore.

Non scelta.

### Committare il Wrapper senza policy

**Pro:** pratica comune e semplice.  
**Contro:** drift del JAR o dei launcher meno visibile.

TDNA aggiunge una policy esplicita perché il repository è anche materiale
didattico.

### Usare un container di build

**Pro:** sistema operativo e tool più controllati.  
**Contro:** non sostituisce il Wrapper, aggiunge immagini e registry da verificare,
complica mobile e IDE.

Possibile livello futuro, non sostituto della baseline corrente.

## Errori comuni

- usare `gradle` in un comando e `./gradlew` in un altro;
- aggiornare l'URL senza aggiornare checksum e policy;
- accettare un JAR perché “proviene da una PR automatica” senza verificarlo;
- considerare un tag di action immutabile;
- dare `contents: write` al workflow normale senza necessità;
- lasciare un workflow bootstrap temporaneo in `main`;
- rigenerare il Wrapper durante ogni CI, nascondendo il drift committato;
- chiamare un checksum “firma”;
- confondere cache riproducibile con build ermetica;
- dichiarare offline l'intero build quando dipendenze/plugin non sono ancora
  vendorizzati.

## Esercizi

### Esercizio 1 — drift del launcher

Modificare una riga non funzionale in una copia di `gradlew` ed eseguire il
checker. Spiegare perché anche un commento modifica l'hash revisionato.

### Esercizio 2 — tag mobile

Sostituire in una branch di prova lo SHA di `actions/checkout` con `@v4`. Verificare
che `check-ci-actions` fallisca.

### Esercizio 3 — modello di minaccia

Descrivere quali attacchi vengono fermati se:

- cambia soltanto la distribuzione;
- cambia soltanto il JAR;
- cambia soltanto la policy;
- un attaccante controlla repository e review.

### Esercizio 4 — upgrade plan

Preparare, senza applicarla, una scheda di upgrade Gradle con file, fonti,
compatibilità, test, rollback e finding attesi.

### Esercizio 5 — cache vuota

Eseguire il build con una Gradle user home temporanea e osservare quale parte usa
la rete e quale parte resta deterministica.

## Non-obiettivi

Questa slice non rende ancora il build:

- completamente ermetico;
- indipendente da Maven Central o Gradle Plugin Portal;
- firmato con una root of trust TDNA;
- accompagnato da SBOM completo;
- protetto da dependency locking/verifica di ogni artifact;
- pronto per Android o iOS;
- indipendente da Java 21.

Costruisce una base riproducibile e verificabile, non la conclusione della
sicurezza supply-chain.

## Collegamenti

- [Licenze, dati e supply chain](36-licenze-dati-supply-chain.md)
- [Debugging e strumenti](34-debugging-e-strumenti.md)
- [Regole operative](00-regole-operative.md)
- [Review e merge](06-review-e-merge.md)
- [Struttura del repository](21-struttura-repository.md)
- [Scenario Build Bootstrap](lab/scenarios/gradle-wrapper-riproducibile.md)
- [Rapporto di chiusura Foundations v0](../project/foundation-v0-closure.md)
