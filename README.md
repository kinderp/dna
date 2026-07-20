# DNA

**DNA** è una piattaforma modulare per mettere in relazione persone,
luoghi, intenzioni e servizi attraverso profili condivisibili, matching
contestuale, mappe, comunità e diversi canali di comunicazione.

Travel è il primo dominio applicativo ed è ora incluso direttamente in
`domains/travel`. La cronologia del precedente repository TDNA è stata
importata nel monorepo.

## Principi

- DNA privato per impostazione predefinita.
- Bounded context separati in un solo monorepo.
- Mappa come superficie territoriale comune.
- Comunicazione transport-agnostic.
- Un ecosistema e più esperienze specializzate.
- Alfred come piano asincrono di osservazione e correlazione.
- Privacy, sicurezza, prove e documentazione by design.

## Checkout

```bash
git clone https://github.com/kinderp/dna.git
cd dna
sh tools/dna doctor
```

Non sono più necessari Git submodule.

## Controlli

```bash
python3 -m pip install -r requirements-dev.txt
sh tools/dna check-core-contracts
sh tools/dna check-core-reference
sh tools/dna check-travel-history
sh tools/dna check-travel
```

Il test emulatore Travel è separato e intenzionale:

```bash
sh tools/dna check-travel-emulator
```

## Provenienza Travel

- commit TDNA importato: `85c73ab78dd56506c5595673098adf514765de9c`;
- dominio corrente: `domains/travel`;
- [manifest della migrazione](docs/migration/tdna-import.md);
- [guida Git del monorepo](docs/governance/02-git-monorepo.md);
- [guida storica della fase submodule](docs/migration/phase-a-git-submodules.md).

Il repository `kinderp/tdna` resta disponibile finché una successiva
operazione revisionata non ne stabilirà la modalità read-only e
l'archiviazione.

## Risorse

- [Documentazione italiana](docs/it/README.md)
- [Governance](docs/governance/00-operational-rules.md)
- [Review e merge](docs/governance/01-review-and-merge.md)
- [Guida Git del monorepo](docs/governance/02-git-monorepo.md)
- [Schemi JSON v0.1](schemas/v0.1/README.md)
- [Implementazione Kotlin di riferimento](reference/kotlin/README.md)

## Licenza

Da definire prima della pubblicazione del primo codice riutilizzabile.
