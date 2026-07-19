# Migrazione di TDNA nel monorepo DNA

## Stato

Fase A attiva: `domains/travel` è un riferimento Git al repository `kinderp/tdna`, fissato al commit:

```text
85c73ab78dd56506c5595673098adf514765de9c
```

Questa scelta non è l'architettura finale. È un ponte conservativo che rende l'intero contenuto TDNA disponibile da un checkout DNA senza copiarlo in modo parziale o perdere la tracciabilità.

## Guida operativa del submodule

I comandi completi per studenti e contributori sono documentati in:

- [Git submodule in DNA: guida operativa e didattica](../governance/02-git-submodules.md)

La guida copre:

- primo clone e repository già clonato;
- aggiornamento di `main` e cambio branch;
- detached HEAD;
- verifica del gitlink;
- lavoro sul repository Travel;
- aggiornamento intenzionale del puntatore;
- recupero da checkout errato o conflitto;
- fork, Windows e comportamento CI.

## Checkout minimo

```bash
git clone --recurse-submodules https://github.com/kinderp/dna.git
cd dna
git submodule update --init --recursive
sh tools/dna check-travel-revision
```

Non usare nel flusso normale:

```bash
git submodule update --remote
```

DNA deve scegliere esplicitamente un commit TDNA revisionato.

## Fonti autorevoli durante la fase A

| Tema | Fonte autorevole |
|---|---|
| governance comune | `AGENTS.md`, `docs/governance/` in DNA |
| architettura piattaforma | `docs/it/` e ADR di DNA |
| contratti Travel implementati | `domains/travel/shared`, `apps/android`, test e fixture TDNA |
| documentazione didattica Travel | `domains/travel/docs` |
| roadmap cross-domain | DNA |
| roadmap tecnica Travel corrente | TDNA, salvo decisione trasversale successiva |
| Alfred | repository autonomo `kinderp/alfred` e bridge documentato in DNA |

Una decisione trasversale nuova non deve essere aggiunta soltanto alla documentazione TDNA.

## Verifica della revisione

```bash
sh tools/dna doctor
sh tools/dna check-travel-revision
```

La verifica confronta:

1. revisione attesa dalla fase di migrazione;
2. gitlink registrato dal commit DNA corrente;
3. commit effettivamente aperto in `domains/travel`.

I tre valori devono coincidere.

## Perché non copiare subito uno snapshot

Una copia manuale perderebbe blame, collegamenti a commit e certezza sulla completezza. Il riferimento Git conserva il commit esatto mentre prepariamo l'import definitivo.

## Fase B: appiattimento definitivo

Il passaggio finale userà un import history-aware, preferibilmente:

```bash
git remote add tdna https://github.com/kinderp/tdna.git
git fetch tdna main
git subtree add --prefix=domains/travel tdna main
```

Prima dell'esecuzione verrà verificato se usare subtree completo o history rewrite con prefisso. Dopo l'import:

1. rimuovere `.gitmodules` e il gitlink;
2. mantenere nel commit di migrazione il riferimento al commit sorgente;
3. riallineare script e Gradle dalla root;
4. spostare le decisioni comuni in DNA;
5. correggere link e path;
6. eseguire CI, emulator smoke e review;
7. lasciare TDNA accessibile finché la nuova `main` non è stabile;
8. archiviare TDNA con una PR/operazione separata.

## Non-obiettivi della fase A

- rinominare package Kotlin;
- fondere subito i due sistemi Gradle;
- dichiarare TDNA deprecato;
- modificare la roadmap Android;
- eseguire automaticamente l'emulatore su ogni PR;
- perdere issue o cronologia del repository sorgente.
