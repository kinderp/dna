# Report di sviluppo — 17 luglio 2026 — LocationSample e replay deterministico

## Stato del report

`pre-review complete`

Il ledger autorevole dei round finali è la timeline della
[PR #16](https://github.com/kinderp/tdna/pull/16). Questo report viene chiuso
prima dello SHA finale: gli esiti dei round, la CI finale e il merge saranno
registrati nel ledger senza creare un commit auto-invalidante.

## Obiettivo

Costruire la quarta vertical slice eseguibile di **Foundations and Travel DNA
Lab v0**:

```text
fixture sintetica
-> GeoPoint condiviso
-> LocationSample
-> ordering gate
-> clock virtuale
-> rate razionale
-> replay state machine
-> summary bounded
-> report deterministico
```

## Tracciabilità

- issue: [#11](https://github.com/kinderp/tdna/issues/11);
- PR: [#16](https://github.com/kinderp/tdna/pull/16);
- branch: `agent/location-sample-replay`;
- base verificata: `76680433089842db5805d28eb50416a23c7d0a88`;
- technical head benchmarkato: `2b27a731e98c0456e2532ef3ebfc850523ab0ef4`;
- capitolo: `docs/it/46-location-sample-e-replay-deterministico.md`;
- scenario: `docs/it/lab/scenarios/location-replay-deterministico.md`.

Il substantive head finale sarà il commit che contiene questo report e gli
ultimi indici; il suo SHA esatto sarà registrato nel ledger PR.

## Codice introdotto

### `shared/geo-contracts`

- `GeoPoint` WGS84 cross-domain;
- normalizzazione dello zero firmato;
- uguaglianza e hash semantici;
- compatibilità sorgente temporanea in `routing-contracts`.

### `shared/location-contracts`

- `MonotonicInstant`;
- `LocationSequence`;
- `LocationSampleOrigin`;
- `LocationSample` bounded;
- `LocationSampleGate` con ispezione non mutante;
- decisioni accepted/rejected con motivo esplicito.

### `shared/location-replay`

- `PlaybackRate` razionale normalizzato;
- `LocationReplayScenario` immutabile e bounded;
- `VirtualReplayClock`;
- `ReplayDelayScaler` con preview/commit e resto;
- `DeterministicReplayRunner` con transizione atomica;
- `ReplayEvent`, `ReplayState` e `ReplaySummary`.

### `labs/location-replay-cli`

- parser JVM rigoroso;
- report canonico;
- benchmark diagnostico con numero dispari di run;
- test parser, ground truth e argomenti benchmark.

### `fixtures/gps`

- `TDNA_LOCATION_REPLAY_V0`;
- sei campioni sintetici;
- una sequence duplicata;
- un timestamp monotono non crescente;
- metadati di provenance, privacy e non-obiettivi.

## Decisioni architetturali

1. `GeoPoint` appartiene a un modulo geografico cross-domain, non al routing.
2. Il runtime usa tempo monotono; il tempo civile resta a diario e UI.
3. Sequence e timestamp devono crescere entrambi.
4. Il replay conserva l'ordine dichiarato e non corregge input stantii.
5. Il primo accepted stabilisce la baseline con delay zero.
6. La velocità è una frazione intera, non un `Double`.
7. Il resto della divisione viene conservato tra campioni.
8. Parser e file I/O restano fuori dal modulo common e dal futuro hot path.
9. Il runner conserva stato bounded, non la cronologia degli eventi.
10. Una transizione accepted viene preparata completamente prima di mutare stato.
11. Fixture e report sono didattici, non API pubbliche o telemetria.
12. Il benchmark è osservazione diagnostica e non un gate prestazionale.

## Finding e correzioni

### Finding 1 — contatori pubblici non bounded

`ReplaySummary` accettava contatori fino a `Int.MAX_VALUE` benché lo scenario
fosse limitato a 100.000 campioni.

Correzione:

- contatori bounded a `LocationReplayScenario.MaxSamples`;
- somme accepted/rejected e reason counts in `Long`;
- reason counts positivi e bounded;
- stati `Ready` e `Completed` coerenti;
- delay zero quando non esiste un accepted;
- regression test con `Int.MAX_VALUE`.

### Finding 2 — aspettative fixture non bounded

Accepted, rejected e reason count del parser potevano superare lo scenario.

Correzione:

- valori bounded a 100.000;
- somme in `Long`;
- accepted + rejected uguale al numero dei campioni;
- reason count uguale ai rifiuti;
- test per quantità oltre limite e combinazioni impossibili.

### Finding 3 — benchmark non conservato

Il risultato viveva soltanto nello stdout della CI.

Correzione:

- `check-kotlin` salva Lab e benchmark JSON;
- Foundation CI pubblica `foundation-kotlin-observations` per 14 giorni;
- nessuna soglia di pass/fail.

### Finding 4 — mediana ambigua con run pari

Il benchmark accettava un numero pari di iterazioni ma chiamava mediana
l'elemento superiore centrale.

Correzione:

- iterazioni obbligatoriamente dispari;
- intervallo bounded `[1, 25]`;
- test per zero, pari e fuori limite;
- uso documentato di sette run.

### Finding 5 — summary processato senza accepted

Un `ReplaySummary` costruito manualmente poteva dichiarare campioni processati ma
nessuna baseline accettata, stato impossibile per uno stream strutturalmente
valido.

Correzione:

```text
processed > 0 -> accepted > 0
```

con test di rifiuto.

### Finding 6 — transizione accepted non atomica

Con timestamp estremo e rate lento, un overflow del delay poteva avvenire dopo
che gate, clock e indice erano già avanzati.

Correzione:

```text
gate.inspect
-> clock.deltaTo
-> delayScaler.preview
-> total-overflow check
-> commit gate
-> commit clock
-> commit rate remainder
-> increment counters/index
```

Il test provoca ripetutamente l'overflow e verifica che:

- `processedSamples` resti 1;
- clock e last sequence restino sulla prima baseline;
- delay resti zero;
- lo stato resti `Running`;
- lo stesso campione possa fallire di nuovo senza corruzione aggiuntiva.

### Finding 7 — entry point e stato commenti obsoleti

README radice, indice `docs` e commenting status dichiaravano ancora due Lab e
moduli non implementati.

Correzione:

- quattro Lab descritti e collegati;
- sequenza didattica 43–46;
- plugin, map e location modules classificati teaching-ready/hot-path reviewed;
- comandi e artifact replay documentati.

Ogni finding ha azzerato il contatore delle review pulite. Nessun round finale è
stato dichiarato prima della chiusura di questa history.

## Test e verifiche

Copertura principale:

- WGS84 e zero firmato;
- accuratezza, velocità e bearing invalidi;
- sequence duplicata e tempo non crescente;
- inspect non mutante e baseline accepted;
- rifiuti che non muovono clock o rate remainder;
- timestamp iniziale non zero;
- rate normalizzato e resto conservato;
- pause, step, resume e cancel;
- summary e aspettative bounded;
- transizione atomica in caso di overflow;
- parser rigoroso e report esatto;
- benchmark arguments bounded/dispari;
- common, JVM e Linux x64;
- architecture boundary check.

## Benchmark diagnostico osservato

Evidenza:

- Foundation CI run
  [#97](https://github.com/kinderp/tdna/actions/runs/29577113153);
- technical head `2b27a731e98c0456e2532ef3ebfc850523ab0ef4`;
- artifact ID `8405564857`;
- artifact digest
  `sha256:f9fa90fdbce6631c5f1f473f9d26768c16d737ccabcc01249b4a0390071bda80`;
- scadenza artifact: 31 luglio 2026.

Ambiente:

```text
GitHub-hosted runner
Ubuntu 24.04.4
Java 21
Gradle 9.5.1
Kotlin 2.4.0
10.000 campioni validi
3 warm-up
7 iterazioni
```

Output:

```json
{"benchmark":"location-replay-v0","samples":10000,"warmups":3,"iterations":7,"min_elapsed_ns":400325,"median_elapsed_ns":2973838,"max_elapsed_ns":11811276,"median_ns_per_sample":297.38}
```

Interpretazione limitata:

- mediana osservata: circa `2,97 ms` per 10.000 campioni;
- mediana osservata: `297,38 ns/campione`;
- forte variabilità tra minimo e massimo;
- nessuna soglia o promessa pubblica;
- il dato serve come baseline diagnostica del core in-memory.

Non misura:

- parsing fixture;
- GPS o sensori;
- rete/database;
- map matching;
- route progress;
- MapLibre/GPU;
- Android/iOS;
- batteria;
- viaggio su strada.

La CI finale sul substantive head rieseguirà benchmark e Lab; il ledger PR
registrerà il run finale senza modificare questo report.

## Documentazione prodotta

- capitolo 46 implementation-backed;
- scenario Lab eseguibile;
- reading paths e Lab roadmap;
- feature/documentation/commenting status;
- tracepoint model;
- tooling guide;
- milestone e development status;
- README radice e indice `docs`;
- questo report e indice permanente.

## Review plan

### Round 1

Focus:

- correttezza dei contratti;
- invarianti temporali;
- bounds e overflow;
- atomicità delle transizioni;
- state machine e ownership;
- fixture parser;
- test di regressione.

### Round 2

Focus:

- dipendenze e provider isolation;
- forma del futuro hot path;
- privacy e provenance;
- correttezza delle affermazioni benchmark;
- documentazione e percorsi studenti;
- CI, scope e non-obiettivi.

Servono due round consecutivi sullo stesso substantive head finale.

## Decisioni richieste

Nessuna. Il maintainer ha autorizzato il merge autonomo soltanto dopo CI verde,
due round puliti sullo stesso SHA, assenza di thread e expected-head guard.

## Debito e non-obiettivi

- nessun adapter Android/iOS;
- nessun GPS reale;
- nessun map matching;
- nessun route progress;
- nessun off-route/reroute;
- nessuna coroutine cancellation concorrente;
- nessuna misura batteria;
- nessuna affidabilità stradale;
- Gradle Wrapper ancora assente.

## Prossimo passo

Fissare il substantive head con questo report, ottenere Foundation CI verde,
eseguire due round puliti, mergiare PR #16 e verificare issue e nuovo `main`.
Dopo il merge, la slice successiva partirà dal nuovo `main` con una sola PR.
