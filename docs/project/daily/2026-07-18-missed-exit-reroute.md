# Report di sviluppo — 18 luglio 2026 — Missed exit e reroute deterministico

## Stato del report

`pre-final-review complete`

Il ledger autorevole di CI finale, review e merge è la timeline della
[PR #22](https://github.com/kinderp/tdna/pull/22). Il report viene chiuso prima
dello SHA finale per non creare commit auto-invalidanti dopo i due round.

## Obiettivo

Costruire una state machine didattica e provider-neutral:

```text
normalized evidence
-> suspicion / recovery / confirmation
-> one correlated reroute command
-> provider executor
-> validated atomic route replacement
```

## Tracciabilità

- issue: [#21](https://github.com/kinderp/tdna/issues/21);
- PR: [#22](https://github.com/kinderp/tdna/pull/22);
- branch: `agent/missed-exit-reroute`;
- base verificata: `95cf2f0d900b039efb86ba0a570e3ca3f8d8cef5`;
- capitolo: `docs/it/49-off-route-missed-exit-e-reroute.md`;
- scenario: `docs/it/lab/scenarios/navigation-missed-exit-reroute.md`.

## Codice introdotto

### `shared/off-route-contracts`

- evidence `OnRoute`, `Suspicious`, `Indeterminate`;
- motivi sospetti bounded enum;
- `OffRouteObservation` con route/sample identity;
- policy count+duration bounded;
- episode/attempt ID positivi;
- states `OnRoute`, `Suspected`, `Confirmed`;
- decisioni accepted/rejected e transition esplicite;
- command/outcome correlati.

### `shared/off-route-state-machine`

- `inspect` non mutante;
- `accept` con commit solo accepted;
- false-alarm recovery;
- indeterminate hold;
- confirmation count+duration;
- state confirmed sticky;
- wrong route/stale sequence/time rejection;
- reset esplicito;
- state bounded senza event history.

### `shared/reroute-coordinator`

- `Ready` e `InFlight`;
- un solo attempt;
- request canonica dalla posizione corrente alla destinazione;
- old-route retention;
- stale outcome rejection;
- cancellation cleanup;
- exception-to-failure mapping;
- provenance verification;
- canonical request/route postconditions;
- new route ID;
- atomic replacement.

### `labs/missed-exit-cli`

- due episodi, un recovery e un confirmation;
- duplicate begin e stale outcome;
- fake planner e replacement;
- nuovo progress tracker;
- report JSON esatto;
- benchmark state-machine-only.

## Decisioni architetturali

1. Evidence normalization resta upstream dalla state machine.
2. `Indeterminate` non conta e non recupera.
3. Conferma richiede count e durata.
4. `Confirmed` è sticky finché il runtime installa/resetta una route.
5. Tracker e coordinator non eseguono I/O.
6. Il planner viene chiamato da un executor separato.
7. La vecchia route resta autorevole durante in-flight/failure.
8. Attempt e source route ID correlano ogni outcome.
9. Cancellazione propaga dopo cleanup.
10. La route nuova viene applicata soltanto dopo provenance e postcondizioni.
11. Route ID deve cambiare.
12. Matcher/progress/map state devono essere ricreati per la route nuova.

## Finding e correzioni pre-review

### 1 — stati pubblici permissivi

`Suspected` e `Confirmed` potevano essere costruiti manualmente con evidence non
sospetta o durata incoerente.

Correzione:

- evidence iniziale/finale obbligatoriamente suspicious;
- count 1 implica prima==ultima osservazione;
- count >1 implica sequence/time crescenti;
- duration confirmed uguale all’elapsed monotono reale.

### 2 — catch troppo ampio

L’executor catturava `Throwable`, rischiando di mascherare errori fatali.

Correzione:

```text
CancellationException -> cleanup e rethrow
Exception ordinaria   -> Internal failure
Error/Throwable fatale -> non mascherato
```

### 3 — provenance non verificata dall’executor

Una route poteva essere attribuita a un provider diverso dal planner scelto.

Correzione: provider ID della route deve coincidere col descriptor del planner.

### 4 — benchmark non valido per ogni conteggio ammesso

Il pattern poteva terminare in `Suspected` per alcuni `sampleCount`, mentre il
post-check richiedeva sempre `OnRoute`.

Correzione: ultimo campione forzato `OnRoute`; test con conteggi 2 e 3.

## Test

- bounds policy/ID/diagnostics;
- public-state invariants;
- false alarm;
- count+duration;
- indeterminate;
- sticky confirmation;
- rejection non mutante;
- inspect/reset;
- one in-flight;
- stale outcome;
- failure/retry;
- invalid replacement;
- provenance;
- cancellation/exception cleanup;
- valid executor pipeline;
- Lab ground truth;
- benchmark args e piccoli conteggi;
- common/JVM/Linux;
- architecture boundaries.

## Lab

```bash
sh tools/tdna lab missed-exit
```

Output atteso:

```json
{"scenario":"reference-missed-exit-v0","episodes":2,"recoveries":1,"indeterminate":1,"confirmations":1,"attempts":1,"duplicate_begin":"AlreadyInFlight","stale_outcome_ignored":true,"old_route_held":true,"replaced":true,"new_route_id":"reference-rerouted-route-v0","new_tracker_empty":true}
```

## Benchmark

```bash
sh tools/tdna bench off-route 10000 7
```

Misura soltanto validazione e transizioni in-memory del tracker. Il risultato
finale sarà registrato nel ledger PR dopo la CI sul substantive head.

Non misura evidence normalization, matcher, provider, rete, replacement,
dispositivo, batteria o strada.

## Documentazione prodotta

- capitolo 49;
- scenario promoted to executable;
- fixture metadata;
- code/state map e tracepoint;
- reading path;
- feature/documentation/commenting status;
- milestone e development status;
- tooling;
- questo report e indice permanente.

## Review plan

### Round 1

- state invariants e transition precedence;
- count/duration;
- non-mutating rejection;
- command/outcome correlation;
- old route retention;
- replacement postconditions;
- cancellation/exception tests.

### Round 2

- dependency direction;
- hot-path shape;
- privacy;
- benchmark claims;
- documentazione;
- CI e scope.

## Decisioni richieste

Nessuna per questa slice. Le soglie sono esplicitamente didattiche e non vengono
promosse a valori di prodotto.

## Debito e non-obiettivi

- nessuna normalizzazione reale da matcher/GPS;
- nessun traffic-aware reroute;
- nessun backoff;
- nessun adapter mobile;
- nessuna installazione atomica completa di map/voice;
- nessun field test;
- Gradle Wrapper ancora assente.

## Prossimo passo

Completare indici e architettura, ottenere CI verde sul final head, due review
pulite, expected-head merge e riallineamento `main`. Dopo il merge si valuterà
la chiusura della milestone Foundations o il primo adapter mobile controllato.
