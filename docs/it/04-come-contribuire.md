# Come contribuire a Travel DNA

## Modello di contributo

```text
repository upstream
-> fork personale
-> branch di lavoro
-> issue o sub-issue
-> draft pull request
-> CI
-> review e fix
-> due review round consecutivi senza finding
-> ready
-> merge
-> chiusura issue
```

Il branch `main` deve restare stabile e studiabile. I nuovi contributori non
lavorano direttamente su `main`.

La procedura normativa di review è descritta in
[06-review-e-merge.md](06-review-e-merge.md).

## Prima di iniziare

1. leggere `AGENTS.md` e le regole operative;
2. scegliere un'issue piccola e definita;
3. verificare che appartenga alla milestone corrente;
4. leggere il documento del componente;
5. identificare i test e gli scenari Lab;
6. eseguire la baseline disponibile;
7. creare un branch descrittivo.

Nomi indicativi:

```text
docs-explain-route-progress
feat-add-fake-route-planner
test-missed-exit-replay
perf-measure-navigation-snapshot
fix-presence-expiry
```

## Good first issue

Una issue è adatta a un nuovo contributore solo se:

- non richiede una decisione architetturale aperta;
- ha criterio di accettazione chiaro;
- indica i documenti da leggere;
- indica i test da eseguire;
- non tratta dati reali sensibili;
- non cambia direttamente un percorso caldo senza tutoraggio.

Esempi futuri:

- aggiungere una fixture sintetica;
- documentare una capability;
- implementare un fake provider;
- aggiungere un property test geometrico;
- migliorare uno scenario Lab;
- costruire un piccolo algoritmo Java di riferimento.

## Issue madre e micro-step

Una milestone usa una issue madre con:

- goal;
- primary roadmap;
- non-obiettivi;
- dipendenze;
- rischi;
- checklist;
- tabella di tracciabilità;
- issue figlie;
- PR collegate.

Le issue figlie rappresentano vertical slice o prove specifiche.

## Prima di modificare il codice

Compilare la scheda del task definita nelle regole operative. In particolare:

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

La CI deve chiamare gli stessi comandi o gli stessi task sottostanti. Non deve
esistere una procedura segreta disponibile soltanto al server.

Con la crescita del monorepo verranno aggiunti comandi mirati per Android, iOS,
replay, benchmark e documentazione.

## Aggiungere un test

Scegliere la famiglia in base al contratto:

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

La PR deve spiegare:

- comportamento utente;
- bounded context e piattaforme;
- livello di rischio;
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

I finding importanti vanno lasciati inline o descritti chiaramente nella PR. Il
fix deve essere collegato al finding e accompagnato da un test quando possibile.

### Due round consecutivi obbligatori

Ogni PR, compresa una PR `R0` di sola documentazione, richiede due review round
consecutivi senza nuovi finding prima del passaggio a ready o del merge.

Ogni round registra:

```text
head SHA
focus
file e contratti controllati
CI e test osservati
finding oppure no new findings
clean round count
```

Se un round trova un problema:

```text
fix
-> test
-> nuovo substantive head
-> CI
-> clean round count = 0
```

Dopo il fix servono due nuovi round puliti. I round precedenti al fix non contano.

### Commit che invalidano la review

Invalidano i round puliti le modifiche a:

- codice;
- test;
- fixture;
- contratti;
- build e workflow;
- documentazione stabile;
- report tecnico della slice.

Non li invalidano da soli:

- aggiornamento della descrizione della PR;
- commento di review;
- label o milestone;
- rerun CI sullo stesso SHA.

I due round puliti devono quindi riferirsi allo stesso substantive head.

### Focus consigliati

Per evitare due passaggi identici:

```text
Round 1
correttezza, invarianti, ownership, error model, test

Round 2
architettura, concorrenza, performance, privacy, documentazione, CI e scope
```

Per `R3` entrambi i round devono includere una verifica esplicita di threat model,
abuso, dati, retention e sicurezza durante la guida.

## Gate di merge

Prima del merge verificare:

- [ ] CI verde sul substantive head corrente;
- [ ] nessun finding aperto;
- [ ] review round pulito 1 registrato;
- [ ] review round pulito 2 registrato;
- [ ] nessun commit sostanziale successivo ai round;
- [ ] PR body aggiornato;
- [ ] report giornaliero aggiornato;
- [ ] documentazione e milestone coerenti;
- [ ] issue pronta a chiudersi con il merge.

Il merge resta una decisione del maintainer quando il repository lo prevede.
L'issue non viene chiusa soltanto perché il codice è pronto: si chiude con il
merge o subito dopo.

## Commit

Commit in inglese, monoscopo e leggibili. Formato consigliato:

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

Una fixture pubblica deve essere sintetica o anonimizzata e deve documentare la
provenienza.
