# Report di sviluppo — 18 luglio 2026 — Porta map matching e fake deterministico

## Stato del report

`pre-final-review complete`

Il ledger autorevole di CI finale, due review e merge è la timeline della
[PR #20](https://github.com/kinderp/tdna/pull/20). Il report viene scritto prima
dello SHA finale per evitare un commit auto-invalidante dopo le review.

## Obiettivo

Completare il confine deterministico fra la slice LocationSample/replay e la
slice route progress:

```text
LocationSample
-> MapMatcherPort / MapMatchSession
-> Matched | Unmatched | Failure
-> postcondition
-> RouteProgressTracker
```

## Tracciabilità

- issue: [#19](https://github.com/kinderp/tdna/issues/19);
- PR: [#20](https://github.com/kinderp/tdna/pull/20);
- branch: `agent/map-matcher-port`;
- base verificata: `9921fbcc1da1000e6434bdae49646122cae8f0e0`;
- capitolo: `docs/it/48-porta-map-matching-e-fake-deterministico.md`;
- scenario: `docs/it/lab/scenarios/map-matching-fake-provider.md`.

## Codice introdotto

### `shared/map-matching-contracts`

- capability `navigation.map-match`, `deterministic` e `offline`;
- `MapMatcherPort` e route-bound `MapMatchSession`;
- `MapMatchResult.Matched`;
- `MapMatchResult.Unmatched` con reason esplicita;
- `MapMatchResult.Failure` con error model bounded;
- provenance provider-neutral;
- postcondizioni per route, sequence, tempo, geometry index, final fraction e
  provider identity.

### `shared/map-matching-testkit`

- conformance probe riutilizzabile;
- determinismo confrontato su sessioni fresche;
- prova separata di matched, unmatched e provider failure;
- report con snapshot difensivo dei check.

### `shared/fake-map-matcher`

- catalogo esatto route/sample -> outcome;
- lookup medio `O(1)` senza hash dell'intera route nel loop;
- stessa route ID non riutilizzabile con snapshot diverso;
- validazione dei matched all'avvio;
- catalog miss come `Unmatched(NoCandidate)`;
- finestra chiamate bounded con `ArrayDeque`;
- counter e reset diagnostico;
- fixture con quattro match, un unmatched e una failure.

### `labs/map-matching-cli`

- Lab end-to-end fake matcher -> route progress;
- report JSON con tutti e tre gli esiti;
- benchmark diagnostico della pipeline fake;
- pass di correttezza fuori dal timer;
- argomenti bounded e numero dispari di run.

## Decisione: sessione legata alla route

La issue iniziale citava un possibile `MapMatchRequest(route, sample)`. La slice
usa invece:

```text
matcher.bind(route) -> session.match(sample)
```

Motivazione:

- validare e preparare la route una volta;
- non ripetere/copiare un modello grande per campione;
- permettere stato matcher bounded e strutture indicizzate;
- mantenere più piccolo il futuro hot path.

La decisione è documentata nel capitolo 48 e sostituisce il deliverable
originariamente formulato come request per campione.

## Finding e correzioni

### 1 — dipendenze del testkit non dichiarate

La CI architetturale ha rilevato che la firma pubblica del probe usa direttamente
`LocationSample` e `RoutePlan`, mentre build e checker dichiaravano soltanto
`map-matching-contracts`.

Correzione:

- dipendenze `api` dirette a location/routing contracts;
- namespace consentiti nel checker;
- divieti provider/piattaforma invariati.

Foundation CI #122 è passata sul relativo head tecnico.

### 2 — determinismo testato nella stessa sessione

Ripetere lo stesso sample nella stessa sessione avrebbe imposto idempotenza a un
provider potenzialmente stateful.

Correzione:

```text
session A + sample S
session B fresca + sample S
-> stesso risultato
```

Un test usa un matcher che rifiuta il duplicato nella stessa sessione e dimostra
che il probe adotta la semantica corretta.

### 3 — finestra diagnostica con rimozione lineare

La prima versione usava `MutableList.removeAt(0)`, costo `O(n)` quando la finestra
era piena.

Correzione:

- `ArrayDeque`;
- `removeFirst` / `addLast` ammortizzati `O(1)`;
- finestra massima 10.000;
- nessuna history illimitata.

### 4 — Failure non dimostrata dal Lab

Il contratto distingueva `Failure`, ma fixture, probe e report mostravano soltanto
matched/unmatched.

Correzione:

- sesto sample con `ProviderUnavailable` retryable;
- check dedicato nel testkit;
- contatori e codice failure nell'output Lab;
- test che impedisce la conversione in unmatched.

### 5 — report del probe non defensively copied

Una lista mutabile passata al report poteva modificarne retroattivamente il
contenuto.

Correzione: snapshot con `toList()` e test di regressione.

### 6 — benchmark poteva verificare soltanto lo snapshot finale

Una regressione intermedia avrebbe potuto restare invisibile se l'ultimo sample
fosse stato comunque accepted.

Correzione:

- un pass completo untimed verifica ogni `Matched` e ogni decisione progress;
- warm-up e run misurati restano privi di assert per campione;
- verifica finale resta fuori dal timer.

## Test e verifiche

Copertura:

- bounds di errori, diagnostics e provenance;
- route ID, sequence, monotonic time e geometry index;
- final fraction;
- provider identity;
- determinismo su fresh session;
- matched/unmatched/failure distinti;
- catalog miss;
- chiavi duplicate e route ID riusato;
- call window bounded/resettable;
- report immutabile;
- Lab pipeline verso route progress;
- benchmark bounds;
- common/JVM/Linux x64;
- architecture checker.

## Lab

```bash
sh tools/tdna lab map-matching
```

Output atteso:

```json
{"scenario":"reference-map-matching-v0","provider":"org.traveldna.fake-map-matcher","contract_checks":6,"matched":4,"unmatched":1,"failed":1,"unmatched_reason":"NoCandidate","failure_code":"ProviderUnavailable","progress_accepted":4,"final_index":3,"arrived":true,"calls":6}
```

## Benchmark

```bash
sh tools/tdna bench map-matching 10000 7
```

Il risultato finale sarà acquisito dalla CI sul substantive head candidato e
registrato nel ledger PR senza modificare questo report.

Misura:

- lookup del catalogo esatto;
- diagnostica bounded;
- passaggio del matched al route progress;
- costruzione snapshot e commit.

Non misura algoritmo, indice stradale, GPS, rete, dispositivo, batteria o
accuratezza.

## Documentazione prodotta

- capitolo 48;
- scenario Lab eseguibile;
- metadata fixture;
- code/state map e tracepoint;
- reading path;
- feature/documentation/commenting status;
- milestone e development status;
- tooling e comandi;
- questo report e indice permanente.

## Review plan

### Round 1

Focus:

- outcome contracts;
- postcondizioni;
- fresh-session determinism;
- catalog/indexing;
- bounded state;
- test e benchmark correctness.

### Round 2

Focus:

- dependency direction;
- hot-path shape;
- privacy/provenance;
- benchmark claims;
- documentazione e percorsi studenti;
- CI, scope e non-obiettivi.

Servono due round consecutivi sullo stesso substantive head.

## Decisioni richieste

Nessuna per la chiusura della slice. Le scelte sono reversibili e coerenti con la
milestone Navigation Runtime Replay v0.

## Debito e non-obiettivi

- nessun algoritmo geometrico;
- nessun road graph o OSM;
- nessun HMM/Viterbi;
- nessun adapter mobile;
- nessuna cancellation concorrente reale;
- nessun off-route/reroute;
- nessuna misura mobile/stradale;
- Gradle Wrapper ancora assente.

## Prossimo passo

Fissare lo SHA documentale finale, ottenere CI verde, eseguire due review pulite,
mergiare con expected-head e riallineare `main`. La milestone successiva potrà
poi introdurre evidenza off-route e missed-exit senza riaprire il confine matcher.
