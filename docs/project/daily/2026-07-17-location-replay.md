# Report di sviluppo — 17 luglio 2026 — LocationSample e replay deterministico

## Stato del report

`pre-review — il benchmark intermedio è in acquisizione`

Il ledger autorevole dei round finali sarà la timeline della
[PR #16](https://github.com/kinderp/tdna/pull/16). Questo file viene completato
prima dello SHA finale; gli esiti dei round non verranno copiati con un commit
successivo che invaliderebbe le review.

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
- capitolo: `docs/it/46-location-sample-e-replay-deterministico.md`;
- scenario: `docs/it/lab/scenarios/location-replay-deterministico.md`.

## Codice introdotto

### `shared/geo-contracts`

- `GeoPoint` WGS84 condiviso;
- normalizzazione dello zero firmato;
- uguaglianza e hash semantici;
- compatibilità sorgente temporanea in `routing-contracts`.

### `shared/location-contracts`

- `MonotonicInstant`;
- `LocationSequence`;
- `LocationSampleOrigin`;
- `LocationSample` bounded;
- `LocationSampleGate`;
- decisioni accepted/rejected con motivo esplicito.

### `shared/location-replay`

- `PlaybackRate` razionale normalizzato;
- `LocationReplayScenario` immutabile e bounded;
- `VirtualReplayClock`;
- `ReplayDelayScaler` con resto;
- `DeterministicReplayRunner`;
- `ReplayEvent`, `ReplayState` e `ReplaySummary`.

### `labs/location-replay-cli`

- parser JVM della fixture;
- report canonico;
- benchmark diagnostico;
- test parser/ground truth.

### `fixtures/gps`

- `TDNA_LOCATION_REPLAY_V0`;
- sei campioni sintetici;
- una sequence duplicata;
- un timestamp monotono non crescente;
- metadati di provenance e privacy.

## Decisioni architetturali

1. `GeoPoint` appartiene a un modulo geografico cross-domain, non al routing.
2. Il tempo del runtime è monotono; il tempo civile resta per diario/UI.
3. Sequence e timestamp devono crescere entrambi.
4. Il replay conserva l'ordine della fixture e non corregge input stantii.
5. Il primo accepted stabilisce la baseline e produce delay zero.
6. La velocità è una frazione intera, non un `Double`.
7. Il resto di scala viene conservato tra campioni.
8. Parser e file I/O restano fuori dal modulo common e dal futuro hot path.
9. Il runner possiede stato bounded e non trattiene la cronologia degli eventi.
10. Fixture e report sono didattici, non API pubbliche o telemetria di produzione.

## Finding e correzioni

### Finding 1 — contatori pubblici non bounded

La prima versione limitava lo scenario a 100.000 campioni, ma il costruttore
pubblico di `ReplaySummary` accettava ancora contatori fino a `Int.MAX_VALUE` e
sommava alcuni valori come `Int`.

Correzione:

- tutti i contatori bounded a `LocationReplayScenario.MaxSamples`;
- somme accepted/rejected e reason counts eseguite in `Long`;
- reason counts positive e bounded;
- stati `Ready` e `Completed` coerenti;
- delay obbligatoriamente zero senza accepted;
- regression test con input `Int.MAX_VALUE`.

### Finding 2 — aspettative fixture non bounded

Le aspettative del parser potevano superare il massimo dello scenario e i reason
count venivano convertiti direttamente in `Int`.

Correzione:

- accepted/rejected/reason bounded a 100.000;
- somma in `Long`;
- aspettative complessive non superiori allo scenario;
- test per quantità oltre limite e combinazioni impossibili.

### Finding 3 — benchmark non conservato come evidenza

Il comando benchmark esisteva ma il suo risultato viveva soltanto nello stdout
del runner CI.

Correzione:

- `check-kotlin` salva `location-replay-benchmark.json`;
- Foundation CI pubblica Lab e benchmark come artifact per 14 giorni;
- nessuna soglia di performance viene introdotta.

Ogni finding ha azzerato il contatore delle review pulite.

## Test e verifiche

Copertura principale:

- WGS84 e zero firmato;
- accuratezza, velocità e bearing invalidi;
- duplicate sequence e tempo non crescente;
- rifiuti che non muovono clock o rate remainder;
- baseline con timestamp non zero;
- rate normalizzato e resto conservato;
- pause, step, resume e cancel;
- snapshot difensive;
- summary e aspettative bounded;
- parser rigoroso e report esatto;
- test common, JVM e Linux x64;
- architecture boundary check.

## Benchmark diagnostico

Scenario:

```text
10.000 campioni validi
3 warm-up
7 iterazioni
Ubuntu 24.04 GitHub-hosted runner
Java 21
Gradle 9.5.1
Kotlin 2.4.0
```

Risultato: **in acquisizione tramite artifact CI**.

Il dato non misura GPS, parser, rete, database, MapLibre, map matching, batteria
o prestazioni mobile. Non viene usato come threshold.

## Documentazione

- capitolo 46 implementation-backed;
- scenario Lab eseguibile;
- indice didattico e Lab;
- stato funzionalità e documentazione;
- guida di lettura, milestone e stato sviluppo da allineare nello SHA finale;
- indice permanente dei report da allineare nello SHA finale.

## Review plan

### Round 1

Focus:

- correttezza dei contratti;
- invarianti temporali;
- bounds e overflow;
- state machine;
- ownership;
- fixture parser;
- test di regressione.

### Round 2

Focus:

- architettura e dipendenze;
- forma del futuro hot path;
- privacy/provenance;
- affermazioni del benchmark;
- documentazione e percorsi studenti;
- CI e scope milestone.

Servono due round consecutivi sullo stesso substantive head finale.

## Decisioni richieste

Nessuna decisione di prodotto o architetturale. Il maintainer ha autorizzato il
merge autonomo soltanto dopo tutti i gate.

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

Completare indici e milestone, acquisire il benchmark artifact, fissare lo SHA
finale, ottenere CI verde e due round puliti, quindi mergiare con expected-head
guard e verificare il nuovo `main`.
