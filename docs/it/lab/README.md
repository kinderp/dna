# Travel DNA Lab

Il Lab collega un'azione comprensibile a uno studente con dati, stati, moduli,
tracepoint logici, test, prestazioni e proprietà di sicurezza.

## Come leggere uno scenario

1. leggere [Tracepoint Model v0](../41-tracepoint-model-v0.md);
2. aprire lo scenario;
3. eseguire o ispezionare fixture/fake;
4. seguire funzioni e ownership;
5. confrontare output e test;
6. rispondere alle domande;
7. provare una variante senza rompere il contratto.

## Scenari eseguibili

| Scenario | Cosa insegna | Comando |
| --- | --- | --- |
| [Reference routing Java/Rust](scenarios/reference-routing-java-rust.md) | Grafo, Dijkstra, A* e contract diff. | `sh tools/tdna lab reference-routing astar` |
| [Routing contracts](scenarios/routing-contracts-fake-provider.md) | Porte, capability, provenance e fake. | `sh tools/tdna lab routing-contracts` |
| [MapScene](scenarios/map-scene-fake-renderer.md) | Scena, delta, marker e renderer conformance. | `sh tools/tdna lab map-scene` |
| [Location replay](scenarios/location-replay-deterministico.md) | Tempo monotono, ordering gate, clock e rate. | `sh tools/tdna lab location-replay` |
| [Route progress](scenarios/route-progress-tracker.md) | Matched position, leg, manovra, arrival e map binding. | `sh tools/tdna lab route-progress` |
| [Map matching boundary](scenarios/map-matching-fake-provider.md) | Sessione route-bound, Matched/Unmatched/Failure, fake e progress. | `sh tools/tdna lab map-matching` |
| [Missed exit e reroute](scenarios/navigation-missed-exit-reroute.md) | Evidenza, falso allarme, conferma, correlazione e route replacement. | `sh tools/tdna lab missed-exit` |

Benchmark diagnostici:

```bash
sh tools/tdna bench location-replay 10000 7
sh tools/tdna bench route-progress 10000 7
sh tools/tdna bench map-matching 10000 7
sh tools/tdna bench off-route 10000 7
```

## Scenari pianificati

| Scenario | Stato | Cosa insegna |
| --- | --- | --- |
| [Route canonica](scenarios/render-canonical-route.md) | stable-doc | Adapter grafico reale e verifica fake/reale. |
| [Chat con navigatore esterno](scenarios/chat-with-external-navigation.md) | stable-doc | Background, push e superficie sicura. |
| [Pagina del giorno](scenarios/daily-page-photos-thoughts.md) | stable-doc | Eventi, media, pensieri e privacy. |
| [Scambio DNA](scenarios/dna-exchange-privacy.md) | stable-doc | Consenso, minimizzazione e revoca. |

`executable` significa che comando, dati/fake e test esistono; non significa
capacità di prodotto o affidabilità su strada.
