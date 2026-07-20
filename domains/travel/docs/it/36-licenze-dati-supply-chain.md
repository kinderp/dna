# Licenze, dati e supply chain

## Scopo

Travel DNA integra dati e software con licenze diverse. Una dipendenza tecnica
può essere ottima ma incompatibile con distribuzione, offline o modello
commerciale.

## Inventario obbligatorio

Per ogni dipendenza:

```text
name
version
source
license
runtime/build/dev
platform
network calls
telemetry
data processed
update policy
known vulnerabilities
replacement adapter
```

## Licenza di Travel DNA

La licenza del progetto non è ancora decisa. Pubblicare il repository non equivale
a concedere automaticamente il diritto di riuso. Prima di accettare contributi
esterni di codice bisogna chiudere [ADR-0009](../adr/0009-project-licensing-model.md),
aggiungere i file di licenza e allineare la procedura contributiva.

È possibile che applicazione, librerie generiche, documentazione e fixture usino
licenze diverse, ma la separazione deve essere esplicita e comprensibile.

## OpenStreetMap

I dati OSM sono ODbL e richiedono attribuzione nell'uso pubblico. Il modo in cui
si combinano database derivati, contenuti editoriali e user content va progettato
con attenzione.

L'app deve mostrare attribuzione adeguata per mappa, geocoding e routing basati su
OSM.

## Server OSMF

Tile e Nominatim pubblici hanno policy di capacità e non sono infrastruttura di
produzione garantita. La licenza dei dati non implica diritto di usare senza
limiti i server pubblici.

## Librerie iniziali

### MapLibre Native

BSD 2-Clause. Verificare anche attribuzioni di dipendenze, font, sprite e tile.

### Valhalla

MIT. I dati di input conservano proprie licenze.

### Ferrostar

BSD. Stato beta/API e dipendenze vanno monitorati.

### Provider commerciali

Google/Sygic/altri hanno termini, billing, display e caching specifici. Non
mescolare dati o output in modo vietato.

## Contenuti Touring

Il nome e l'ispirazione non autorizzano uso di:

- marchio;
- testi;
- fotografie;
- selezioni;
- classificazioni;
- database;
- stile protetto.

Serve accordo. Il prototipo usa contenuti originali o fonti aperte.

## Wikimedia

Wikidata è strutturato con licenza propria; immagini Commons hanno licenza per
file. Il downloader deve conservare attribution e licence metadata.

## Fotografie utenti

L'utente mantiene diritti. Termini e UI devono spiegare licenza necessaria per
mostrare/condividere, durata e revoca. Non assumere licenza ampia dal semplice
upload.

## SBOM

Produrre Software Bill of Materials per release. Formati candidati:

- CycloneDX;
- SPDX.

## Dependency policy

Una nuova dipendenza deve giustificare:

- problema;
- alternative;
- maintenance;
- size;
- license;
- security;
- privacy;
- platform support;
- adapter boundary.

## Pinning e aggiornamenti

- lockfile;
- renovate/dependabot controllato;
- changelog review;
- test matrix;
- no auto-merge di SDK sensibili;
- emergency update path.

Per il build bootstrap valgono regole aggiuntive:

```text
versione esplicita
checksum da fonte revisionata
file generati riconoscibili
policy committata
clean-checkout smoke
CI e due review sullo stesso SHA
```

## Gradle Wrapper

TDNA committa:

```text
gradlew
gradlew.bat
gradle-wrapper.jar
gradle-wrapper.properties
```

Il Wrapper è una dipendenza di build eseguibile. Non viene escluso dalla review
solo perché è generato o binario.

La policy:

```text
gradle/wrapper/tdna-wrapper-policy.json
```

registra versione, URL, checksum distribuzione, checksum JAR e hash dei launcher.
`tools/check_gradle_wrapper.py` verifica il drift prima del build.

Il checksum della distribuzione protegge il file scaricato rispetto al valore
revisionato. Il checksum del JAR protegge il bootstrap committato. Nessuno dei due
è descritto come firma dell'editore o root of trust esterna.

Approfondimento:
[Build riproducibile e Gradle Wrapper](37-build-riproducibile-gradle-wrapper.md).

## GitHub Actions

I workflow eseguono codice esterno con privilegi della CI. Un riferimento come:

```yaml
uses: owner/action@v4
```

può essere spostato dal proprietario del repository dell'action.

TDNA richiede:

```yaml
uses: owner/action@COMMIT_SHA_COMPLETO # versione leggibile
```

`tools/check_ci_actions.py` applica una allowlist con SHA revisionati. Una nuova
action non entra semplicemente perché “nota” o popolare: richiede aggiornamento
della policy e review.

Il workflow normale usa permessi minimi:

```yaml
permissions:
  contents: read
```

Permessi di scrittura temporanei usati per un bootstrap devono essere rimossi
prima del final head.

## Livelli di fiducia

Separare:

### Integrità

```text
byte scaricati == checksum atteso
```

### Drift repository

```text
file corrente == file revisionato
```

### Provenance/autenticità

```text
chi ha prodotto l'artifact e come lo dimostriamo esternamente?
```

### Riproducibilità

```text
stesso input/versione -> risultato equivalente osservabile
```

### Ermeticità

```text
tutti gli input sono dichiarati e nessun accesso esterno non controllato serve
```

Foundations v0 migliora integrità, drift e riproducibilità del bootstrap. Non
dichiara ancora build ermetico o provenance firmata end-to-end.

## Secrets

- nessuna chiave nel repo;
- environment/secret manager;
- restriction per bundle ID/package/signature;
- rotation;
- separate dev/staging/prod;
- scan pre-commit/CI.

## Offline packages

Ogni package dichiara:

- data sources;
- attribution;
- licence;
- version;
- expiry;
- redistribution rights;
- encryption/signature;
- size.

## Dataset fixture

Dati sintetici generati dal progetto possono essere CC0 o altra licenza decisa.
Dati estratti da OSM mantengono requisiti ODbL e attribution.

## Legal review gates

Prima di:

- produzione;
- offline distribution;
- partnership editoriale;
- monetizzazione;
- advertising;
- export pubblico;
- uso dati utenti per training;

eseguire review legale specialistica.

## Supply-chain security

Baseline corrente:

- protected review process;
- una sola PR;
- due round puliti;
- expected-head merge;
- Wrapper committato e verificato;
- checksum distribuzione;
- Action SHA allowlist;
- least-privilege CI;
- fixture sintetiche;
- plugin/provider boundaries.

Lavoro futuro:

- dependency verification Gradle/Maven;
- dependency locking completo;
- SBOM;
- provenance build;
- signed artifacts e attestazioni;
- dependency scanning;
- mirror controllato quando giustificato;
- checksum/firme offline packages;
- verifica periodica degli SHA allowlisted.
