# Pubblicazione iniziale del repository GitHub

Questo documento descrive il passaggio meccanico per pubblicare la fondazione
locale come repository `kinderp/tdna`.

## Scelta iniziale

Il repository è pensato come pubblico, perché il progetto ha finalità didattiche
e prevede contributi e future librerie open source. Il codice non deve però
accettare contributi esterni finché non viene chiuso
[ADR-0009](../adr/0009-project-licensing-model.md) e aggiunto un file `LICENSE`.

## Pubblicazione con GitHub CLI

Prerequisiti:

```bash
gh --version
gh auth status
```

Dalla root del clone locale:

```bash
gh repo create kinderp/tdna \
  --public \
  --description "Travel DNA: guida-diario sociale e laboratorio didattico per navigazione mobile" \
  --source . \
  --remote origin \
  --push
```

## Pubblicazione dopo creazione dal sito GitHub

Se il repository vuoto è stato creato dal sito:

```bash
git remote add origin https://github.com/kinderp/tdna.git
git push -u origin main
```

Non inizializzare il repository remoto con README, `.gitignore` o licenza,
perché questi file sono già presenti o deliberatamente rimandati nella
fondazione locale.

## Verifica

Dopo il push controllare:

```bash
git remote -v
git status -sb
git log -1 --oneline
```

Sul repository GitHub devono comparire almeno:

```text
README.md
AGENTS.md
CONTRIBUTING.md
docs/
fixtures/
tools/
.github/
```

## Configurazione GitHub successiva

Dopo la pubblicazione:

1. abilitare Discussions;
2. proteggere `main` quando esiste la prima CI;
3. creare le famiglie di label documentate nelle regole operative;
4. creare la prima milestone `Foundations and Travel DNA Lab v0`;
5. non attivare merge automatici finché test e review policy non sono stabili;
6. chiudere la decisione sulla licenza prima di accettare codice esterno.
