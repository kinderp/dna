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

- provenance build;
- signed artifacts;
- protected branches;
- least-privilege CI;
- dependency scanning;
- reproducibility progressiva;
- plugin allowlist;
- checksum offline packages.
