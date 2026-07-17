# Scenario: reference routing in Java and Rust

id: `lab.routing.reference-java-rust.v0`

status: `executable`

scenario kind: `routing-foundation`

## Learning goal

Seguire la stessa richiesta di percorso attraverso fixture, parser, grafo,
Dijkstra/A*, ricostruzione e report in due implementazioni indipendenti.

## Prerequisites

- [Glossario](../../02-glossario.md)
- [Routing e navigazione](../../24-routing-e-navigazione.md)
- [Capitolo del laboratorio](../../43-reference-routing-java-rust.md)

## User story

Uno studente esegue il grafo sintetico e verifica che Java e Rust producano lo
stesso percorso ottimo da `A` a `E`.

## Platforms

- Java 21 CLI;
- Rust stable CLI;
- Linux CI;
- qualsiasi sistema locale con le due toolchain.

## Fixture

- [`reference-network-v0.tdna`](../../../../fixtures/routes/reference-network-v0.tdna)
- [`reference-network-v0.meta.yaml`](../../../../fixtures/routes/reference-network-v0.meta.yaml)

La fixture è sintetica e non contiene posizioni personali.

## Trigger

```bash
sh tools/tdna check
```

Esecuzione interattiva:

```bash
sh tools/tdna lab reference-routing dijkstra
sh tools/tdna lab reference-routing astar
```

## Expected evidence

- il parser carica cinque nodi;
- ogni `road` produce due adjacency entry;
- la distanza diretta non supera il costo della strada;
- Dijkstra trova `A,C,D,E` con costo `3600`;
- A* trova lo stesso risultato;
- Java e Rust emettono report byte-identici;
- fixture malformata e grafo disconnesso vengono rifiutati;
- il link checker documentale passa.

## Expected domain events

Il Lab non introduce eventi runtime di produzione. Le evidenze logiche sono:

```text
REFERENCE_FIXTURE_PARSED
REFERENCE_ROUTE_REQUESTED
REFERENCE_ROUTE_COMPUTED
REFERENCE_REPORT_EMITTED
REFERENCE_REPORTS_MATCHED
```

## Logical tracepoints

I nomi restano `stable-doc` e non generano tracing nel percorso caldo:

```text
REFERENCE_FIXTURE_PARSED
REFERENCE_FRONTIER_NODE_SELECTED
REFERENCE_EDGE_RELAXED
REFERENCE_ROUTE_RECONSTRUCTED
REFERENCE_REPORT_EMITTED
REFERENCE_REPORTS_MATCHED
```

## Function and module path

### Java

```text
ReferenceRoutingCli.main()
-> ReferenceFixtureParser.parse()
-> RoadGraph.addNode()/addBidirectionalRoad()
-> AbstractBestFirstRouter.route()
-> reconstructPath()
-> RouteReport.canonicalLine()
```

### Rust

```text
main()
-> parse_fixture()
-> RoadGraph::add_node()/add_bidirectional_road()
-> route()
-> reconstruct_path()
-> canonical_report()
```

### Contract

```text
tests/contract/test_reference_routing.sh
-> tools/tdna check-contract
-> Java report
-> Rust report
-> diff -u
```

## State changes

| Stato | Proprietario | Durata |
| --- | --- | --- |
| grafo caricato | parser/CLI | una esecuzione |
| `best_cost` | algoritmo | una query |
| `previous` | algoritmo | una query |
| frontier | algoritmo | una query |
| route result | CLI/test | fino all'emissione |
| report | build artifact | diagnostica locale/CI |

Nessuno stato viene inviato in rete o salvato come dato utente.

## Expected output

Dijkstra:

```json
{"scenario":"reference-network-v0","algorithm":"dijkstra","origin":"A","destination":"E","path":["A","C","D","E"],"total_cost_m":3600}
```

A* differisce soltanto nel campo `algorithm`.

## Performance properties

- nessun I/O durante la ricerca dopo il caricamento;
- nessuna rete;
- costi interi con overflow controllato;
- priority queue, non scansione completa per selezionare il minimo;
- nessun benchmark di produzione dichiarato in questa slice.

## Privacy and safety properties

- coordinate sintetiche;
- nessuna traccia GPS;
- nessun dato personale;
- nessun uso durante la guida;
- nessuna raccomandazione di percorso reale.

## Existing tests

- `ReferenceRoutingTestSuite` Java;
- test `#[cfg(test)]` Rust;
- `tests/contract/test_reference_routing.sh`;
- `tools/check_docs.py`;
- workflow `Foundation CI`.

## Missing tests

- grafo con costi molto grandi vicino all'overflow;
- più query nella stessa fixture;
- strade direzionali;
- parser property-based;
- benchmark su grafi crescenti.

## Future tests

- confronto Dijkstra/A* su grafo generato;
- import di un estratto OSM trasformato;
- adapter verso i futuri `RouteRequest` e `RoutePlan` canonici;
- replay con clock virtuale;
- benchmark Java/Rust con ambiente dichiarato.

## Common failures

- Rust non installato localmente: `check-java` funziona, `check` deve fallire;
- costo strada inferiore alla distanza diretta: fixture rifiutata;
- modifica della ground truth solo in un file;
- output JSON con ordine campi diverso;
- algoritmo non deterministico in caso di parità;
- tentativo di usare questo report come API mobile.

## Non-goals

- OpenStreetMap;
- MapLibre;
- Valhalla;
- Ferrostar;
- GPS;
- turn-by-turn;
- traffico;
- app Android/iOS;
- benchmark prestazionale conclusivo;
- scelta definitiva Java contro Rust.

## Related docs

- [Capitolo didattico](../../43-reference-routing-java-rust.md)
- [Strategia di test](../../30-strategia-test.md)
- [Tracepoint Model v0](../../41-tracepoint-model-v0.md)
- [Registro milestone](../../50-registro-milestone.md)
