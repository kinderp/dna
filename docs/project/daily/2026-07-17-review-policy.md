# Report di sviluppo — 17 luglio 2026 — review policy e hardening

## Obiettivo

Applicare una nuova regola normativa a tutto il progetto:

> Una pull request può essere marcata ready, chiusa come completata o mergiata
> soltanto dopo due review round consecutivi senza nuovi finding sullo stesso
> substantive head.

La sessione è iniziata dopo il merge della PR
[#6](https://github.com/kinderp/tdna/pull/6), che ha introdotto contratti routing
provider-neutral, plugin SDK e fake planner Kotlin Multiplatform.

## Tracciabilità

- decisione del maintainer: due round puliti obbligatori;
- issue: [#8](https://github.com/kinderp/tdna/issues/8);
- pull request draft: [#9](https://github.com/kinderp/tdna/pull/9);
- branch: `agent/two-clean-review-policy`;
- base: merge commit PR #6 `2a28d1654988cef4188986342f76fd7d19be358f`.

## Perché la regola è necessaria

La sequenza precedente poteva terminare così:

```text
review
-> finding
-> fix
-> CI verde
-> merge
```

Questa sequenza non controlla il risultato completo dopo il fix. La modifica può:

- introdurre una regressione;
- invalidare una spiegazione;
- spostare il problema in un altro modulo;
- cambiare ownership o invarianti;
- rendere obsoleta la review precedente.

La sequenza normativa diventa:

```text
review con finding
-> fix
-> test/regressione
-> CI sul nuovo head
-> clean review 1
-> clean review 2
-> ready
-> merge
-> chiusura issue
```

## Documenti aggiornati

### Costituzione operativa

`docs/it/00-regole-operative.md` ora definisce:

- review round;
- finding;
- substantive head;
- reset del clean-review counter;
- commit che invalidano i round;
- gate di ready, merge e chiusura issue;
- estensione della Definition of Done.

### Capitolo didattico dedicato

Creato:

```text
docs/it/06-review-e-merge.md
```

Il capitolo contiene:

- vocabolario;
- sequenza normativa;
- profondità per rischio `R0`–`R3`;
- esempi con finding multipli;
- rapporto fra CI e review;
- checklist di merge;
- regole per agenti autonomi;
- rapporto con i report giornalieri.

### Contributori e agenti

Allineati:

```text
CONTRIBUTING.md
docs/it/04-come-contribuire.md
AGENTS.md
```

Tutti dichiarano che:

- il requisito vale per ogni PR;
- un finding resetta il conteggio;
- una modifica sostanziale resetta il conteggio;
- i due round devono riferirsi allo stesso SHA;
- CI e review sono entrambe obbligatorie;
- il merge resta del maintainer quando previsto.

### Template delle pull request

`.github/pull_request_template.md` ora contiene campi per:

```text
Review round 1
Review round 2
Additional review rounds
Ready and merge gate
```

Ogni round registra:

- substantive head SHA;
- focus;
- file e contratti controllati;
- prove CI/test;
- finding;
- esito;
- conteggio pulito consecutivo.

## Finding emerso applicando la nuova regola

Durante la prima review della PR #6 già mergiata è emerso un limite non ancora
modellato nei contratti canonici:

```kotlin
RouteManeuver.roadName
RouteManeuver.exitNumber
```

Le proprietà erano verificate come `null` oppure non vuote, ma un adapter poteva
fornire stringhe di dimensione arbitraria.

### Rischio

I valori arrivano da provider o dataset esterni e possono attraversare:

- memoria del modello;
- log diagnostici;
- banner di navigazione;
- TTS;
- sincronizzazione o cache future.

Senza limite, il confine canonico non era completamente bounded.

### Correzione

Aggiunti:

```text
RouteManeuver.MaxInstructionLength = 512
RouteManeuver.MaxRoadNameLength = 256
RouteManeuver.MaxExitNumberLength = 64
```

Il costruttore rifiuta valori oltre i limiti.

### Regression test

`RouteContractsTest` ora verifica il rifiuto di:

- `roadName` più lungo di 256 caratteri;
- `exitNumber` più lungo di 64 caratteri.

Il finding ha correttamente riportato il clean-review counter a zero.

## Semantica del substantive head

Sono considerate sostanziali le modifiche a:

```text
codice
test
contratti
fixture
workflow/build
documentazione stabile
report tecnico della slice
```

Non resettano da sole:

```text
PR body
commenti di review
label/milestone
rerun CI sullo stesso commit
```

Questa distinzione evita due estremi:

- considerare valida una review su codice ormai cambiato;
- resettare il processo per una semplice annotazione nella conversazione PR.

## Rapporto con i livelli di rischio

Il numero minimo resta due per ogni PR. Cambia il contenuto:

| Rischio | Primo round | Secondo round |
| --- | --- | --- |
| `R0` | testo, link, scope | percorso del lettore e CI docs |
| `R1` | comportamento, invarianti e test | architettura, regressioni e documentazione |
| `R2` | provider, ownership, cancellazione | performance, failure mode e CI |
| `R3` | privacy, sicurezza, abuso | review trasversale completa e threat model |

La PR #9 è classificata `R1`.

## Indici aggiornati

Aggiornati:

- `docs/it/README.md`;
- `docs/it/documentation-status.md`;
- `docs/project/daily/README.md`;
- `docs/project/README.md` quando applicabile.

Il presente file permette di conservare separatamente la sessione di governance
dalla lunga sessione KMP dello stesso giorno.

## Verifica richiesta

La PR deve superare sul substantive head finale:

```text
check documentation
check architecture
check Java reference routing
check Rust reference routing
check Java/Rust contract
check Kotlin Multiplatform
```

Il run esatto e il substantive head finale vengono registrati nella PR #9 dopo
il commit di questo report, perché GitHub assegna il run soltanto dopo il push.

## Review round richiesti per questa PR

La PR applica a se stessa la nuova regola.

### Audit iniziale

Esito: finding sul limite di `roadName` e `exitNumber`.

```text
clean count = 0
```

### Round pulito 1

Da eseguire sul substantive head finale con focus:

- correttezza della policy;
- coerenza delle definizioni;
- limiti del contratto;
- regressioni Kotlin;
- accuratezza dei link e degli indici.

### Round pulito 2

Da eseguire sullo stesso head con focus:

- applicabilità per `R0`–`R3`;
- possibilità di aggirare il reset;
- coerenza fra agenti, contributori e template;
- relazione CI/review/merge;
- documentazione e tracciabilità.

Gli esiti finali vengono registrati nella descrizione della PR senza modificare
nuovamente il repository dopo l'inizio dei due round.

## Decisioni prese autonomamente

1. applicare il gate a tutte le PR, incluse quelle `R0`;
2. variare la profondità ma non il numero dei round;
3. richiedere lo stesso substantive head SHA;
4. resettare dopo ogni commit tecnico o documentale sostanziale;
5. non contare la CI come review;
6. non contare la review come sostituto della CI;
7. chiudere l'issue con il merge, non con la sola prontezza tecnica;
8. conservare la storia dei round invece di sovrascriverla;
9. separare questo report dal report KMP già presente per la stessa data;
10. includere l'hardening delle etichette di manovra nella PR di governance,
    perché è il finding concreto che ha motivato il primo reset.

## Decisioni richieste al maintainer

Nessuna decisione di prodotto o architetturale.

Il maintainer dovrà soltanto decidere il merge della PR #9 dopo che la PR avrà:

- CI verde;
- due round consecutivi puliti;
- evidenza completa nella descrizione.

## Non-obiettivi

- branch protection GitHub configurata via API;
- auto-merge;
- modifica retroattiva della cronologia della PR #6;
- MapScene o fake renderer;
- cambiamenti a GPS, chat, diario o UI;
- benchmark prestazionali.

## Prossimo passo

Dopo il merge della PR #9, la milestone prosegue con la issue
[#7](https://github.com/kinderp/tdna/issues/7): `MapScene`, delta e
`FakeMapRenderer` provider-neutral.
