# DNA

**DNA** è una piattaforma modulare per mettere in relazione persone, luoghi, intenzioni e servizi attraverso frammenti di profilo condivisibili, matching contestuale, mappe, comunità e diversi canali di comunicazione.

Travel è il primo dominio applicativo della piattaforma. Durante la migrazione, l'intero repository TDNA è disponibile sotto `domains/travel` come riferimento Git fissato a un commit verificabile.

## Principi

- DNA privato per impostazione predefinita.
- Bounded context separati in un solo monorepo.
- Mappa come superficie territoriale comune.
- Comunicazione transport-agnostic.
- Un ecosistema e più esperienze specializzate.
- Alfred come piano asincrono di osservazione e correlazione.
- Coordinamento umano tramite GeoRoom, topic e gruppi.
- Privacy, sicurezza, prove e documentazione by design.

## Componenti

- DNA Identity
- DNA Exchange
- DNA Discovery
- DNA Commons
- Geo & Navigation Core
- Event Backbone
- Trust & Moderation
- DNA–Alfred Bridge
- domini Travel, Shopping, Social ed Economy
- superfici mobile, Android Auto e Android Automotive OS

## Checkout completo

```bash
git clone --recurse-submodules https://github.com/kinderp/dna.git
cd dna
git submodule update --init --recursive
sh tools/dna check-travel-revision
```

Per clone esistenti, cambio branch, aggiornamento del puntatore, detached HEAD e recupero errori leggere la [guida Git submodule](docs/governance/02-git-submodules.md).

## Controlli

```bash
python3 -m pip install -r requirements-dev.txt
sh tools/dna check-core-contracts
sh tools/dna check-core-reference
sh tools/dna check-travel-revision
sh tools/dna check-travel
```

Il test emulatore Travel è separato e intenzionale:

```bash
sh tools/dna check-travel-emulator
```

## Migrazione TDNA

- [Manifest e strategia](docs/migration/tdna-import.md)
- [Comandi Git submodule per studenti e contributori](docs/governance/02-git-submodules.md)
- sorgente fissata: `kinderp/tdna@85c73ab78dd56506c5595673098adf514765de9c`
- `kinderp/tdna` non viene archiviato finché l'import history-aware e i test del monorepo non sono completati.

## Risorse

- [Documentazione italiana](docs/it/README.md)
- [Governance](docs/governance/00-operational-rules.md)
- [Review e merge](docs/governance/01-review-and-merge.md)
- [Guida Git submodule](docs/governance/02-git-submodules.md)
- [Schemi JSON v0.1](schemas/v0.1/README.md)
- [Implementazione Kotlin di riferimento](reference/kotlin/README.md)
- [Issue di migrazione](https://github.com/kinderp/dna/issues/3)

## Licenza

Da definire prima della pubblicazione del primo codice riutilizzabile.
