# Scenario: porta di map matching e fake provider

id: `lab.navigation.map-matching-fake.v0`

status: `executable`

scenario kind: `navigation-matching-foundation`

## Learning goal

Seguire campioni sintetici attraverso una porta provider-neutral, distinguere
`Matched`, `Unmatched` e `Failure`, validare i risultati e inviare soltanto i
matched al route progress.

## Prerequisites

- [Capitolo 46 — LocationSample e replay](../../46-location-sample-e-replay-deterministico.md)
- [Capitolo 47 — Route progress](../../47-posizione-matched-e-route-progress.md)
- [Capitolo 48 — Porta map matching](../../48-porta-map-matching-e-fake-deterministico.md)

## User story

Uno studente lega una route canonica a un fake matcher. Sei `LocationSample`
producono quattro match, un unmatched e una failure del provider. Soltanto i
quattro match validati entrano nel tracker e raggiungono l'arrivo.

## Platforms

- Kotlin common per contratti, testkit e fake;
- test JVM e Linux x64;
- CLI JVM;
- nessun SDK mobile o provider reale.

## Fixture

- [`reference-map-matching-v0.meta.yaml`](../../../../fixtures/navigation/reference-map-matching-v0.meta.yaml)
- route e campioni in `FakeMapMatchFixtures`;
- catalogo esatto in `FakeMapMatcher`.

Tutti i dati sono sintetici.

## Trigger

```bash
sh tools/tdna lab map-matching
```

Verifica completa:

```bash
sh tools/tdna check
```

Benchmark:

```bash
sh tools/tdna bench map-matching 10000 7
```

## Input

```text
seq 0, t=0     -> Matched index 0
seq 1, t=1000  -> Matched index 1
seq 2, t=2000  -> Unmatched NoCandidate
seq 3, t=3000  -> Matched index 2
seq 4, t=4000  -> Matched index 3 / Arrive
seq 5, t=5000  -> Failure ProviderUnavailable
```

## Expected evidence

- capability `navigation.map-match` dichiarata;
- route legata senza modifica;
- risultato matched canonico;
- determinismo verificato da una nuova sessione;
- unmatched distinto da failure;
- failure code `ProviderUnavailable`;
- quattro matched accettati dal route progress;
- ultimo snapshot all'indice 3 e `arrived = true`;
- sei chiamate registrate dal fake;
- diagnostica bounded.

## Logical tracepoints

```text
MAP_MATCH_ROUTE_BOUND
MAP_MATCH_SAMPLE_RECEIVED
MAP_MATCH_POSITION_PRODUCED
MAP_MATCH_UNMATCHED_PRODUCED
MAP_MATCH_PROVIDER_FAILURE
MAP_MATCH_RESULT_VALIDATED
MAP_MATCH_RESULT_FORWARDED_TO_PROGRESS
MAP_MATCH_REPORT_EMITTED
```

## Function path

```text
Main.runLab
-> MapMatcherContractProbe.verify
-> FakeMapMatcher.bind
-> Session.match
   -> exact Key(routeId, sample) lookup
   -> ArrayDeque bounded record
-> Matched.requireMatches
-> RouteProgressTracker.accept
-> JSON report
```

## State ownership

| Stato | Owner | Durata |
| --- | --- | --- |
| canonical route | `RoutePlan` / session | sessione |
| exact catalog | `FakeMapMatcher` | vita fake |
| recent calls | bounded `ArrayDeque` | vita fake/reset |
| total call count | fake diagnostic | vita fake/reset |
| one result | caller | una chiamata |
| accepted progress | `RouteProgressTracker` | sessione |
| report counters | CLI | una esecuzione |

## Expected output

```json
{"scenario":"reference-map-matching-v0","provider":"org.traveldna.fake-map-matcher","contract_checks":6,"matched":4,"unmatched":1,"failed":1,"unmatched_reason":"NoCandidate","failure_code":"ProviderUnavailable","progress_accepted":4,"final_index":3,"arrived":true,"calls":6}
```

## Performance properties

- route binding fuori dal loop;
- exact lookup medio `O(1)`;
- call-window update ammortizzato `O(1)`;
- nessuna copia della route per campione;
- stato diagnostico bounded;
- nessun file, rete, database o renderer nel common path;
- benchmark diagnostico senza threshold.

## Privacy and safety

- coordinate sintetiche;
- nessun viaggio reale;
- nessuna pubblicazione in rete;
- provider diagnostic code bounded;
- nessun payload personale nei report;
- unmatched/confidence non diventano automaticamente off-route;
- nessuna guida reale o decisione di sicurezza.

## Existing tests

- `MapMatchingContractsTest`;
- `MapMatcherContractProbeTest`;
- `FakeMapMatcherTest`;
- `MapMatchingCliArgumentsTest`;
- architecture checker;
- JVM/Linux x64;
- Foundation CI.

## Missing tests

- provider realmente sospendibile;
- cancellazione coroutine;
- sessione stateful con più campioni correlati;
- property test su cataloghi generati;
- due provider in shadow mode;
- memoria su cataloghi al limite;
- adapter Android/iOS;
- accuratezza geografica.

## Future tests

- location replay -> matcher fake -> progress;
- filtro -> matcher reale;
- confidence policy;
- unmatched accumulation;
- off-route state machine;
- missed exit e reroute;
- benchmark mobile;
- field replay anonimizzato.

## Common failures

- confondere fake e algoritmo;
- usare la stessa sessione per il repeat deterministico;
- trattare unmatched come eccezione;
- trattare failure come unmatched;
- perdere identity del sample;
- accettare indice fuori route;
- ricostruire route/index nel loop;
- conservare chiamate illimitate;
- interpretare il benchmark come accuratezza su strada.

## Non-goals

- spatial search;
- road graph;
- HMM/Viterbi;
- snapping automatico;
- GPS filtering;
- off-route/reroute;
- Android/iOS;
- MapLibre;
- produzione.

## Questions for students

1. Perché `bind` precede `match`?
2. Perché `Unmatched` non è un `Failure`?
3. Quali identità conserva `requireMatches`?
4. Perché il determinismo usa una sessione nuova?
5. Che cosa accade al route progress dopo un unmatched?
6. Quale stato del fake è bounded?
7. Che cosa non misura il benchmark?
8. Dove entrerebbe un adapter Valhalla o Rust futuro?

## Related docs

- [Capitolo 48](../../48-porta-map-matching-e-fake-deterministico.md)
- [Code/state map](../../40-mappa-codice-e-stati.md)
- [Tracepoint Model](../../41-tracepoint-model-v0.md)
- [Missed exit scenario](navigation-missed-exit-reroute.md)
