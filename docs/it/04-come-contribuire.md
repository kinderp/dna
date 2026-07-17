# Come contribuire a Travel DNA

## Modello di contributo seriale

```text
repository upstream
-> verifica main e PR aperte
-> fork personale o branch autorizzato
-> branch di lavoro dal main corrente
-> issue o sub-issue
-> una draft pull request
-> CI
-> review e fix
-> clean review round 1
-> clean review round 2 sullo stesso SHA
-> ready
-> merge autorizzato
-> verifica issue e nuovo main
-> soltanto allora branch e PR successivi
```

Il branch `main` deve restare stabile e studiabile. I nuovi contributori non
lavorano direttamente su `main`.

La procedura normativa di review e merge è descritta in
[06-review-e-merge.md](06-review-e-merge.md).

## Prima di iniziare

1. leggere `AGENTS.md` e le regole operative;
2. scegliere un'issue piccola e definita;
3. verificare che appartenga alla milestone corrente;
4. leggere il documento del componente;
5. identificare test e scenari Lab;
6. eseguire la baseline disponibile;
7. controllare la lista delle PR aperte;
8. leggere lo SHA corrente di `main`;
9. creare un branch descrittivo da quel `main`.

Nomi indicativi:

```text
docs-explain-route-progress
feat-add-fake-route-planner
test-missed-exit-replay
perf-measure-navigation-snapshot
fix-presence-expiry
```

## Una sola PR alla volta

Nel flusso ordinario del progetto esiste una sola PR aperta.

Mentre una PR è attiva si possono:

- preparare issue future;
- discutere alternative;
- scrivere note non committate;
- eseguire ricerca e spike separati;
- conservare un branch non pubblicato come PR.

Non si possono aprire:

- PR stacked dipendenti dalla PR corrente;
- PR placeholder o vuote;
- PR `noop` usate come segnaposto;
- PR basate su una versione di `main` precedente all'ultimo merge.

Un'eccezione richiede autorizzazione esplicita del maintainer e tracciabilità in
ogni PR coinvolta.

### Chiusura senza merge

Una PR accidentale, duplicata, stacked o abbandonata può essere chiusa
amministrativamente senza due review pulite, perché non distribuisce codice. Il
commento di chiusura deve spiegare:

- perché non è una review unit valida;
- che non è stata mergiata;
- se il branch viene conservato;
- che prima di un nuovo uso dovrà essere riallineato al `main` futuro.

## Good first issue

Una issue è adatta a un nuovo contributore solo se:

- non richiede una decisione architetturale aperta;
- ha criteri di accettazione chiari;
- indica i documenti da leggere;
- indica i test da eseguire;
- non tratta dati reali sensibili;
- non cambia un percorso caldo senza tutoraggio.

Esempi:

- aggiungere una fixture sintetica;
- documentare una capability;
- implementare un fake provider;
- aggiungere un property test geometrico;
- migliorare uno scenario Lab;
- costruire un piccolo algoritmo Java di riferimento.

## Issue madre e micro-step

Una milestone usa una issue madre con:

- goal;
- roadmap primaria;
- non-obiettivi;
- dipendenze;
- rischi;
- checklist;
- tracciabilità;
- issue figlie;
- PR collegate.

Le issue figlie rappresentano vertical slice o prove specifiche. Creare una issue
non equivale ad aprire la sua PR.

## Scheda del task

Prima di modificare il codice registrare:

```text
use case
bounded context
piattaforme
contratti
provider
hot path
dati e permessi
test
benchmark
documentazione
scope
base main SHA
inventario PR aperte
```

## Test locali

Il wrapper corrente è:

```text
sh tools/tdna doctor
sh tools/tdna check-docs
sh tools/tdna check-architecture
sh tools/tdna check-java
sh tools/tdna check-rust
sh tools/tdna check-contract
sh tools/tdna check-kotlin
sh tools/tdna check
```

La CI deve usare gli stessi comandi o task sottostanti. Non deve esistere una
procedura segreta disponibile soltanto al server.

## Scegliere il test

| Contratto | Test |
| --- | --- |
| Regola di dominio | unit test |
| Invariante geometrica | property-based test |
| Sessione e transizione | state-machine test |
| Provider sostituibile | contract test |
| Flusso GPS | replay test |
| DB/rete/piattaforma | integration test |
| Schermata | UI test |
| Percorso caldo | benchmark |
| Viaggio lungo | soak test |
| Condizione reale | field audit |

Ogni nuovo scenario importante aggiorna `30-strategia-test.md` o uno scenario
Lab.

## Pull request

La PR spiega:

- comportamento utente;
- bounded context e piattaforme;
- livello di rischio;
- base `main` usata;
- inventario delle PR aperte;
- contratti modificati;
- provider coinvolti;
- impatto su prestazioni e batteria;
- privacy, permessi e guida;
- test eseguiti;
- documentazione aggiornata;
- non-obiettivi;
- punti di attenzione per il reviewer.

Restare in draft finché la slice non è verificabile e non sono completati i due
round puliti richiesti.

## Review

Il reviewer controlla almeno:

- correttezza;
- chiarezza del contratto;
- isolamento del provider;
- copertura test;
- semplicità;
- concorrenza e cancellazione;
- performance e memoria;
- batteria e background;
- privacy e sicurezza;
- degraded mode;
- coerenza Android/iOS;
- documentazione e tracciabilità;
- licenza delle dipendenze.

I finding importanti vanno lasciati inline o descritti nel ledger della PR. Il
fix deve essere collegato al finding e accompagnato da un test quando possibile.

### Due round consecutivi obbligatori

Ogni PR che può essere mergiata, compresa una PR `R0` di sola documentazione,
richiede due review round consecutivi senza nuovi finding prima del passaggio a
ready o del merge.

Ogni round registra:

```text
head SHA
rischio e focus
file e contratti controllati
CI e test osservati
finding oppure no new findings
clean-round count
```

Se un round trova un problema:

```text
fix
-> test
-> nuovo substantive head
-> CI
-> clean-round count = 0
```

Dopo il fix servono due nuovi round puliti. I round precedenti non contano.

### Commit che invalidano la review

Invalidano i round puliti modifiche a:

- codice;
- test;
- fixture;
- contratti;
- build e workflow;
- documentazione stabile;
- report tecnico della slice.

Non li invalidano da soli:

- aggiornamento della descrizione PR;
- review submission o commento;
- label o milestone;
- rerun CI sullo stesso SHA.

### Focus consigliati

```text
Round 1
correttezza, invarianti, ownership, error model, test

Round 2
architettura, concorrenza, performance, privacy, documentazione, CI e scope
```

Per `R3` entrambi includono threat model, abuso, dati, retention e sicurezza
durante la guida.

## Ledger PR e report

Il ledger autorevole per il merge è la timeline delle review e la descrizione PR.
Può essere aggiornato dopo i round senza cambiare il commit revisionato.

Il report Markdown committato prima dei round finali contiene:

- contesto;
- finding e fix;
- substantive head previsto;
- review plan;
- link alla PR.

Non viene modificato dopo i round soltanto per duplicarne l'esito. Dopo il merge,
una successiva PR documentale può riconciliare il report storico con CI finale,
due round puliti e merge commit.

## Gate di merge

Prima del merge verificare:

- [ ] questa è l'unica PR ordinaria aperta;
- [ ] CI verde sul substantive head corrente;
- [ ] nessun finding o thread aperto;
- [ ] review round pulito 1 registrato;
- [ ] review round pulito 2 registrato sullo stesso SHA;
- [ ] nessun commit sostanziale successivo;
- [ ] PR body aggiornato senza cambiare il head;
- [ ] report presente e indicizzato;
- [ ] documentazione e milestone coerenti con lo stato pre-merge;
- [ ] issue pronta a chiudersi con il merge;
- [ ] autorità di merge esplicita;
- [ ] expected-head guard pronto.

Un agente può mergiare soltanto con autorizzazione esplicita o permanente del
maintainer. Altrimenti lascia la PR ready.

## Dopo il merge

Prima di aprire la PR successiva:

1. verificare che la PR sia `merged`;
2. verificare l'issue collegata;
3. registrare il nuovo SHA di `main`;
4. aggiornare o pianificare la riconciliazione storica;
5. creare il branch successivo dal nuovo `main`;
6. riallineare eventuali branch conservati;
7. verificare che non esistano altre PR aperte.

## Commit

Formato consigliato:

```text
<type>(<scope>): <imperative subject>

Explain what changed and why.
Explain contract, hot-path or ownership effects when relevant.

Modified files:
- path/one
- path/two
```

Tipi indicativi:

```text
feat fix docs test refactor perf build chore style
```

## Dati e fixture

Non committare:

- tracce GPS personali;
- coordinate domestiche;
- foto private;
- conversazioni reali;
- token o chiavi API;
- dump di database;
- log con identificativi.

Una fixture pubblica deve essere sintetica o anonimizzata e documentare la
provenienza.
