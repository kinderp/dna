# Come contribuire a Travel DNA

## Modello di contributo

```text
repository upstream
-> fork personale
-> branch di lavoro
-> issue o sub-issue
-> draft pull request
-> CI
-> review
-> merge
```

Il branch `main` deve restare stabile e studiabile. I nuovi contributori non
lavorano direttamente su `main`.

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

I comandi definitivi arriveranno con il bootstrap del monorepo. L'obiettivo è
fornire un wrapper coerente:

```text
./tdna doctor
./tdna format
./tdna lint
./tdna build
./tdna test shared
./tdna test rust
./tdna test android
./tdna test ios
./tdna replay <scenario>
./tdna bench <family>
./tdna docs
```

La CI deve chiamare gli stessi comandi o gli stessi task sottostanti. Non deve
esistere una procedura segreta disponibile soltanto al server.

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
- contratti modificati;
- provider coinvolti;
- impatto su prestazioni e batteria;
- privacy, permessi e guida;
- test eseguiti;
- documentazione aggiornata;
- punti di attenzione per il reviewer.

Restare in draft finché la slice non è verificabile.

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

I finding importanti vanno lasciati inline. Il fix deve essere collegato al
finding e accompagnato da un test quando possibile.

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
