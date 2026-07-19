# Git submodule in DNA: guida operativa e didattica

## 1. Perché esiste il submodule Travel

Durante la fase A della migrazione, `domains/travel` non è ancora una normale cartella del monorepo. È un **Git submodule** che punta a un commit preciso del repository storico `kinderp/tdna`.

Il repository padre DNA registra quindi:

```text
domains/travel -> commit TDNA 85c73ab78dd56506c5595673098adf514765de9c
```

Git non copia i file Travel dentro la cronologia di DNA. Registra un riferimento, chiamato anche **gitlink**, al commit del repository figlio.

Questa soluzione è temporanea e serve a:

- rendere disponibile tutto TDNA senza copie parziali;
- mantenere la cronologia verificabile;
- centralizzare governance e documentazione comune;
- preparare l'import history-aware definitivo sotto `domains/travel`.

Il submodule non è l'architettura finale del monorepo.

## 2. Regola fondamentale

Il commit Travel usato da DNA è quello registrato dal repository padre, non il più recente disponibile su `tdna/main`.

Non usare:

```bash
git submodule update --remote
```

Quel comando segue un riferimento remoto mobile e può spostare Travel senza una decisione revisionata nel repository DNA.

Usare invece:

```bash
git submodule update --init --recursive
```

Questo porta ogni submodule esattamente al commit registrato dal checkout DNA corrente.

## 3. Primo clone consigliato

### Linux, macOS e Git Bash su Windows

```bash
git clone --recurse-submodules https://github.com/kinderp/dna.git
cd dna
git submodule status --recursive
sh tools/dna check-travel-revision
```

Il comando `--recurse-submodules` clona DNA e inizializza anche `domains/travel`.

L'output di `git submodule status` deve mostrare il commit registrato. Il primo carattere ha significato:

- spazio: submodule inizializzato al commit atteso;
- `-`: submodule non inizializzato;
- `+`: checkout del submodule diverso dal commit registrato;
- `U`: conflitto non risolto sul gitlink.

## 4. Repository già clonato senza submodule

Se `domains/travel` è vuoto o incompleto:

```bash
cd dna
git submodule sync --recursive
git submodule update --init --recursive
sh tools/dna check-travel-revision
```

`sync` riallinea gli URL locali a `.gitmodules`; `update` inizializza e porta il submodule al commit registrato.

## 5. Aggiornare il proprio checkout di `main`

Prima verificare di non avere lavoro locale non salvato:

```bash
git status --short
git -C domains/travel status --short
```

Poi:

```bash
git switch main
git pull --ff-only
git submodule sync --recursive
git submodule update --init --recursive
sh tools/dna doctor
sh tools/dna check-travel-revision
```

`--ff-only` impedisce a `git pull` di creare automaticamente un merge locale inatteso.

## 6. Cambiare branch DNA

Quando si passa da un branch DNA a un altro, anche il commit Travel registrato può cambiare.

Usare:

```bash
git switch NOME_BRANCH
git submodule sync --recursive
git submodule update --init --recursive
sh tools/dna check-travel-revision
```

Non assumere che il submodule venga aggiornato automaticamente dal solo `git switch`.

## 7. Controllare stato e differenze

### Stato sintetico

```bash
git submodule status --recursive
```

### Stato del repository padre e figlio

```bash
git status
git -C domains/travel status
```

### Commit Travel attualmente aperto

```bash
git -C domains/travel rev-parse HEAD
```

### Commit Travel registrato nel commit DNA corrente

```bash
git ls-tree HEAD domains/travel
```

### Commit Travel registrato nell'indice Git corrente

```bash
git ls-files --stage -- domains/travel
```

L'indice è il valore che sarà incluso nel prossimo commit. Durante un aggiornamento intenzionale del puntatore può essere diverso da `HEAD`.

### Differenza del puntatore tra due commit DNA

```bash
git diff --submodule=log BASE..HEAD -- domains/travel
```

### Configurazione utile per visualizzare i submodule

```bash
git config status.submoduleSummary 1
git config diff.submodule log
```

Per applicarla globalmente a tutti i repository:

```bash
git config --global status.submoduleSummary 1
git config --global diff.submodule log
```

## 8. Perché Travel appare in detached HEAD

Dopo `git submodule update`, `domains/travel` è normalmente in **detached HEAD**.

È corretto: il repository padre richiede un commit preciso, non un branch mobile.

Controllare con:

```bash
git -C domains/travel status
```

Per consultare, compilare e testare Travel non serve cambiare branch.

Non eseguire dentro il submodule:

```bash
git switch main
git pull
```

salvo che si stia intenzionalmente iniziando un lavoro nel repository TDNA. Farlo cambia il checkout locale del submodule e DNA mostrerà `domains/travel` come modificato.

## 9. Eseguire i controlli Travel dalla root DNA

```bash
sh tools/dna check-travel-revision
sh tools/dna check-travel
```

Controllo emulatore, separato e intenzionale:

```bash
sh tools/dna check-travel-emulator
```

Comandi diagnostici:

```bash
sh tools/dna doctor
```

Il comando `check-travel-revision` verifica che:

- il submodule sia inizializzato;
- il commit aperto in `domains/travel` coincida con il gitlink nell'indice DNA;
- il gitlink coincida con la revisione Travel approvata per questa fase.

## 10. Lavorare sul codice Travel durante la fase A

Travel conserva ancora la propria cronologia nel repository `kinderp/tdna`.

La modalità più chiara è usare un clone separato:

```bash
git clone https://github.com/kinderp/tdna.git
cd tdna
git switch main
git pull --ff-only
git switch -c agent/NOME-SLICE
```

Dopo modifiche e verifiche:

```bash
git status --short
git add PERCORSI_DELLA_SLICE
git commit -m "Descrizione coerente della slice"
git push -u origin agent/NOME-SLICE
```

Aprire la PR nel repository TDNA solo se la roadmap di migrazione autorizza ancora quella slice e non esiste un'altra PR attiva secondo le regole del progetto.

### Lavorare direttamente dentro `domains/travel`

È tecnicamente possibile, ma richiede attenzione perché il checkout nasce detached:

```bash
git -C domains/travel switch -c agent/NOME-SLICE
```

Dopo il lavoro:

```bash
git -C domains/travel status --short
git -C domains/travel add PERCORSI_DELLA_SLICE
git -C domains/travel commit -m "Descrizione coerente della slice"
git -C domains/travel push -u origin agent/NOME-SLICE
```

Il commit creato appartiene a TDNA, non a DNA. Un commit nel repository padre non include automaticamente i file modificati del repository figlio.

## 11. Aggiornare intenzionalmente il puntatore Travel in DNA

Questa operazione si esegue soltanto dopo che il nuovo commit TDNA è stato revisionato e scelto esplicitamente.

Esempio:

```bash
TDNA_SHA=INSERIRE_SHA_COMPLETO_REVISIONATO

git switch main
git pull --ff-only
git switch -c agent/update-travel-source

git submodule sync --recursive
git submodule update --init --recursive

git -C domains/travel fetch origin main
git -C domains/travel checkout --detach "$TDNA_SHA"

git add domains/travel
git diff --cached --submodule=log -- domains/travel
```

Nella stessa PR aggiornare anche:

- `docs/migration/tdna-import.md`;
- la costante `EXPECTED_TRAVEL_SHA` in `tools/dna`;
- eventuali riferimenti al commit sorgente in ADR, README o report della slice.

Poi:

```bash
sh tools/dna check-travel-revision
sh tools/dna check-travel

git add docs/migration/tdna-import.md tools/dna
git commit -m "Update Travel source to reviewed TDNA revision"
git push -u origin agent/update-travel-source
```

La PR DNA deve mostrare chiaramente il passaggio:

```text
Subproject commit VECCHIO_SHA
Subproject commit NUOVO_SHA
```

## 12. Comandi da non usare nel normale flusso

### Non seguire automaticamente il remoto

```bash
git submodule update --remote
```

### Non committare il repository padre con lavoro Travel non pubblicato

Prima del commit DNA:

```bash
git -C domains/travel status --short
```

Se esistono modifiche, devono essere:

- committate e pubblicate in TDNA;
- oppure salvate su un branch locale;
- oppure eliminate consapevolmente.

### Non cancellare manualmente solo la cartella

```bash
rm -rf domains/travel
```

non è un metodo di aggiornamento o riparazione. Usare i comandi Git descritti sotto.

### Non modificare `branch = ...` in `.gitmodules`

La fase A usa un commit fissato. Il branch remoto non è la fonte del checkout riproducibile.

## 13. Risolvere problemi comuni

### `domains/travel` non contiene i file

```bash
git submodule sync --recursive
git submodule update --init --recursive
```

### L'URL del submodule è cambiato

```bash
git submodule sync --recursive
git submodule update --init --recursive
```

### `git submodule status` mostra `+`

Il submodule è aperto su un commit diverso da quello registrato.

Prima controllare il lavoro locale:

```bash
git -C domains/travel status --short
```

Se non esiste lavoro da conservare:

```bash
git submodule update --init --recursive
sh tools/dna check-travel-revision
```

### Ripristino forzato senza lavoro da conservare

**Attenzione:** il comando seguente può eliminare modifiche non committate dentro il submodule.

```bash
git -C domains/travel status --short
git submodule update --init --recursive --force
```

Usarlo solo dopo avere verificato che l'output di `status --short` sia vuoto o che le modifiche siano sacrificabili.

### Conservare lavoro locale prima del ripristino

```bash
git -C domains/travel switch -c rescue/lavoro-locale
git -C domains/travel add -A
git -C domains/travel commit -m "Preserve local Travel work before submodule reset"
```

Poi tornare al commit registrato:

```bash
git submodule update --init --recursive --force
```

### Conflitto sul gitlink durante merge o rebase

Ispezionare i commit candidati:

```bash
git ls-files -u domains/travel
```

Dopo avere scelto lo SHA revisionato:

```bash
git -C domains/travel fetch origin
git -C domains/travel checkout --detach SHA_SCELTO
git add domains/travel
git status
```

La risoluzione non consiste nel fondere automaticamente i file interni: consiste nello scegliere quale commit TDNA debba essere registrato dal repository DNA.

### Rimuovere una directory submodule rimasta dopo un cambio branch

Prima provare:

```bash
git submodule update --init --recursive
```

Se il branch corrente non contiene più il submodule e la directory contiene soltanto dati già pubblicati, consultare un maintainer prima della pulizia. Non usare comandi distruttivi alla cieca.

## 14. Aggiornare un fork

Clone iniziale del proprio fork:

```bash
git clone --recurse-submodules https://github.com/PROPRIO-UTENTE/dna.git
cd dna
git remote add upstream https://github.com/kinderp/dna.git
```

Aggiornamento:

```bash
git fetch upstream
git switch main
git merge --ff-only upstream/main
git submodule sync --recursive
git submodule update --init --recursive
sh tools/dna check-travel-revision
git push origin main
```

Il submodule continua normalmente a usare l'URL pubblico di `kinderp/tdna` dichiarato in `.gitmodules`.

## 15. Windows

I comandi Git funzionano anche in PowerShell. Gli script del progetto sono shell script e vanno eseguiti preferibilmente con:

- Git Bash;
- WSL;
- un ambiente CI Linux equivalente.

Esempio in Git Bash:

```bash
sh tools/dna doctor
sh tools/dna check-travel-revision
```

## 16. CI

I workflow che richiedono Travel usano:

```yaml
- uses: actions/checkout@SHA_REVISIONATO
  with:
    submodules: recursive
```

La CI core può usare:

```yaml
submodules: false
```

quando non necessita del codice Travel. Questo riduce tempo, rete e costo dei job.

Il workflow emulatore Travel resta manuale durante la migrazione.

## 17. Fine della fase submodule

Dopo l'import history-aware definitivo:

- `domains/travel` diventerà una normale directory del monorepo;
- `.gitmodules` verrà rimosso;
- non servirà più `git submodule update`;
- questa guida resterà come documento storico della migrazione o verrà sostituita da una guida Git del monorepo appiattito.

Fino ad allora il flusso canonico è:

```text
clone/update DNA
-> sync submodule
-> checkout del commit registrato
-> verifica revisione
-> esecuzione controlli
```
