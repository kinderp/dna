# Migrazione di TDNA nel monorepo DNA

## Stato

**Fase B implementata nella branch di migrazione, in attesa di review e
merge.** Il submodule transitorio è stato rimosso e TDNA è stato importato
sotto `domains/travel` con storia raggiungibile.

Commit sorgente fissato:

```text
85c73ab78dd56506c5595673098adf514765de9c
```

## Verifica

```bash
test ! -e .gitmodules
sh tools/dna check-travel-history
git cat-file -e 85c73ab78dd56506c5595673098adf514765de9c^{commit}
git merge-base --is-ancestor 85c73ab78dd56506c5595673098adf514765de9c HEAD
```

## Fonti autorevoli

| Tema | Fonte autorevole |
|---|---|
| governance comune | `AGENTS.md`, `docs/governance/` |
| architettura piattaforma | `docs/it/` e ADR di DNA |
| contratti e codice Travel | `domains/travel/` |
| documentazione didattica Travel | `domains/travel/docs/` |
| roadmap cross-domain | DNA |
| Alfred | `kinderp/alfred` e bridge documentato in DNA |

## Metodo usato

La migrazione ha seguito una subtree merge history-aware:

```text
verifica commit TDNA
-> rimozione del gitlink transitorio
-> fetch della storia TDNA
-> import sotto domains/travel
-> riallineamento di tooling, CI e documentazione
-> test core, Travel ed emulatore
```

La guida submodule della fase A è conservata come documento storico in
[`phase-a-git-submodules.md`](phase-a-git-submodules.md).

## Lavoro residuo

- review e merge della branch di flattening;
- verifica di `main` dopo il merge;
- eventuale riconciliazione di issue e link storici;
- operazione separata per rendere `kinderp/tdna` read-only;
- archiviazione soltanto dopo un periodo di stabilità del monorepo.
