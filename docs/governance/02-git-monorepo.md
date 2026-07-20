# Git nel monorepo DNA

## Stato corrente

`domains/travel` è una directory ordinaria del repository DNA. La storia
del repository TDNA è stata importata e il commit sorgente
`85c73ab78dd56506c5595673098adf514765de9c` è raggiungibile dalla cronologia corrente.

Non servono più `--recurse-submodules`, `git submodule update` o checkout
separati per modificare Travel.

## Primo clone

```bash
git clone https://github.com/kinderp/dna.git
cd dna
sh tools/dna doctor
sh tools/dna check-travel-history
```

## Aggiornare main

```bash
git status --short
git switch main
git pull --ff-only
sh tools/dna doctor
```

## Creare una slice

Prima verificare issue, SHA di `main` e assenza di PR aperte:

```bash
git switch main
git pull --ff-only
git switch -c agent/NOME-SLICE
```

I file Travel si modificano direttamente:

```bash
$EDITOR domains/travel/PERCORSO
git status --short
git add domains/travel/PERCORSO
git commit -m "Descrizione coerente della slice"
git push -u origin agent/NOME-SLICE
```

## Verifiche

```bash
sh tools/dna check-core-contracts
sh tools/dna check-core-reference
sh tools/dna check-travel-history
sh tools/dna check-travel
```

L'emulatore resta un controllo intenzionale:

```bash
sh tools/dna check-travel-emulator
```

## Consultare la storia TDNA

Verificare la provenienza:

```bash
git cat-file -t 85c73ab78dd56506c5595673098adf514765de9c
git merge-base --is-ancestor 85c73ab78dd56506c5595673098adf514765de9c HEAD
git log --graph --oneline --all --decorate
```

Il subtree merge mantiene la cronologia TDNA raggiungibile come secondo
ramo del commit di import. I vecchi path TDNA erano alla root del loro
repository; i path correnti vivono sotto `domains/travel`.

## Regole

- non aggiungere nuovi submodule per i domini applicativi iniziali;
- non modificare direttamente storage o internals di un altro dominio;
- aggiornare contratti, test e documentazione nella stessa PR;
- usare una sola PR aperta e due review pulite sullo stesso SHA;
- non archiviare `kinderp/tdna` finché la fase di chiusura non è stata
  revisionata separatamente.

## Documento storico

La precedente procedura submodule è conservata in
[`docs/migration/phase-a-git-submodules.md`](../migration/phase-a-git-submodules.md).
