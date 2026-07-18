# Report di sviluppo — 18 luglio 2026 — Missed exit e reroute deterministico

## Stato del report

`pre-final-review complete — finding corretti, nuovo final head richiesto`

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

- `Ready` e `InFlight` con invarianti pubbliche;
- un solo attempt;
- request canonica dalla posizione corrente alla destinazione;
- old-route retention;
- stale outcome rejection;
- cancellation cleanup;
- exception-to-failure mapping;
- capability `routing.plan` verificata prima della chiamata;
- capability manovre verificata sul risultato;
- provenance verification;
- canonical request/route postconditions;
- new route ID;
- atomic replacement.

### `labs/missed-exit-cli`

- due episodi, un recovery e un confirmation;
- duplicate begin e stale outcome;
- fake planner e replacement con manovre coerenti col descriptor;
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
10. La route nuova viene applicata soltanto dopo capability, provenance e postcondizioni.
11. Route ID deve cambiare.
12. Matcher/progress/map state devono essere ricreati per la route nuova.

## Finding e correzioni pre-review

### 1 — stati `Suspected` e `Confirmed` permissivi

I costruttori potevano rappresentare evidence o durata incoerenti.

Correzione: evidence sospetta, route uguale, count bounded, sequence/tempo
crescenti e durata uguale all'elapsed monotono reale.

### 2 — catch troppo ampio

L'executor catturava `Throwable`.

Correzione:

```text
CancellationException -> cleanup e rethrow
Exception ordinaria   -> Internal failure
Error/Throwable fatale -> non mascherato
```

### 3 — provenance non verificata

La route deve essere attribuita al provider rappresentato dal descriptor del
planner.

### 4 — benchmark non valido per ogni conteggio

Il pattern poteva terminare in `Suspected`. L'ultimo campione è ora `OnRoute` e
sono testati i conteggi minimi 2 e 3.

## Finding emersi nel primo audit del final head verde

### 5 — `InFlight` costruibile con identità incoerenti

Il modello pubblico accettava una command per una source route diversa dalla
route attiva o una request con destinazione diversa.

Correzione: init guard su source route e destination, con test di regressione.

### 6 — capability `routing.plan` non verificata

Un oggetto `RoutePlannerPort` poteva essere invocato anche se il descriptor non
dichiarava la capability.

Correzione: failure non retryable prima della chiamata e test che verifica zero
invocazioni.

### 7 — capability manovre contraddetta dal risultato

Un provider poteva dichiarare `routing.maneuvers` e restituire legs vuote.

Correzione: provider-contract failure non retryable; il Lab produce `Depart` e
`Arrive` coerenti col fake planner.

### 8 — nome ambiguo dell'ultima osservazione

`lastObservation` era in realtà l'ultima osservazione sospetta; un
`Indeterminate` aggiorna la baseline accepted ma non quel campo.

Correzione: `lastSuspiciousObservation` in contratto, tracker, test e
spiegazione.

### 9 — integrazione documentale incompleta

Capitolo/scenario/report esistevano, ma mancavano entry point, indici, status,
code map, tracepoint e milestone.

Correzione: tutti i percorsi didattici e i registri sono stati allineati prima
del nuovo final gate.

Ogni finding o commit sostanziale ha azzerato il contatore delle review pulite.

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
- public `InFlight` identity invariants;
- missing `routing.plan` senza invocazione;
- declared maneuvers con leg vuota;
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
finale viene registrato nel ledger PR dopo la CI sul substantive head.

Non misura evidence normalization, matcher, provider, rete, replacement,
dispositivo, batteria o strada.

## Documentazione prodotta

- capitolo 49;
- scenario promoted to executable;
- fixture metadata;
- root/docs/Italian/Lab indexes;
- repository structure e reading path;
- code/state map e tracepoint;
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
- capability e provenance;
- old-route retention;
- replacement postconditions;
- cancellation/exception tests.

### Round 2

- dependency direction;
- hot-path shape;
- privacy;
- benchmark claims;
- documentazione;
- CI e scope.

Servono due round consecutivi sullo stesso nuovo substantive head.

## Decisioni richieste

Nessuna per questa slice. Le soglie sono didattiche e non vengono promosse a
valori di prodotto.

## Debito e non-obiettivi

- nessuna normalizzazione reale da matcher/GPS;
- nessun traffic-aware reroute;
- nessun backoff;
- nessun adapter mobile;
- nessuna installazione atomica completa di map/voice;
- nessun field test;
- Gradle Wrapper ancora assente.

## Prossimo passo

Fissare il nuovo SHA finale, ottenere CI verde, eseguire due review pulite,
mergiare con expected-head e riallineare `main`. Dopo il merge la fondazione può
chiudere il debito Gradle Wrapper e produrre il rapporto finale di milestone.
