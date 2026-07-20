# Pubblicazione iniziale del repository GitHub

Questo documento descrive la pubblicazione della fondazione come repository
`kinderp/tdna`.

## Visibilità iniziale

Il repository viene mantenuto **privato durante la fondazione**. Questa scelta
permette di definire licenza, governance, dati di test e procedure di sicurezza
prima di aprire il progetto a contributi esterni.

La visibilità potrà essere cambiata in seguito dalle impostazioni GitHub. Prima
di renderlo pubblico occorre almeno:

1. chiudere [ADR-0009](../adr/0009-project-licensing-model.md);
2. aggiungere il file `LICENSE` e gli eventuali notice;
3. verificare che fixture, cronologia e documentazione non contengano dati
   personali, segreti o materiali senza diritto di redistribuzione;
4. definire security policy, code of conduct e modello contributivo;
5. rieseguire link check e controlli documentali.

Rendere privato un repository dopo una fase pubblica non ritira eventuali cloni
o copie già effettuate. Per questo la prima apertura pubblica deve essere una
decisione esplicita.

## Creazione con GitHub CLI

Prerequisiti:

```bash
gh --version
gh auth status
```

Dalla root del clone locale, per un nuovo repository privato:

```bash
gh repo create kinderp/tdna \
  --private \
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

1. mantenere il repository privato finché licenza e governance non sono chiuse;
2. abilitare Discussions quando inizia il lavoro progettuale pubblico o interno;
3. proteggere `main` quando esiste la prima CI;
4. creare le famiglie di label documentate nelle regole operative;
5. creare la milestone `Foundations and Travel DNA Lab v0`;
6. non attivare merge automatici finché test e review policy non sono stabili;
7. rendere pubblico il repository solo dopo il gate descritto sopra.
