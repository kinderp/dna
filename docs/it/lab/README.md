# Travel DNA Lab

Il Lab collega un'azione comprensibile a uno studente con dati, stati, moduli,
tracepoint logici, test, prestazioni e proprietà di sicurezza.

## Come leggere uno scenario

1. leggere [Tracepoint Model v0](../41-tracepoint-model-v0.md);
2. aprire lo scenario;
3. eseguire o ispezionare la fixture indicata;
4. seguire il percorso logico e, quando esiste, quello delle funzioni reali;
5. confrontare output e test;
6. rispondere alle domande di ripasso;
7. provare una variante senza modificare il contratto stabile.

## Scenari eseguibili

| Scenario | Stato | Cosa insegna |
| --- | --- | --- |
| [Reference routing Java/Rust](scenarios/reference-routing-java-rust.md) | executable | Grafo, Dijkstra, A*, fixture, determinismo e contract test cross-language. |
| [Routing contracts e fake provider](scenarios/routing-contracts-fake-provider.md) | executable | Porte, modelli canonici, capability, provenance, invarianti e provider conformance. |
| [MapScene e fake renderer](scenarios/map-scene-fake-renderer.md) | executable | Scena statica, delta, route progress, marker semantici, privacy e renderer conformance. |

Comandi minimi:

```bash
sh tools/tdna lab reference-routing astar
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
```

## Scenari documentali pianificati

| Scenario | Stato | Cosa insegna |
| --- | --- | --- |
| [Route canonica](scenarios/render-canonical-route.md) | stable-doc | Futuro adapter grafico e verifica fake/reale. |
| [Uscita mancata e ricalcolo](scenarios/navigation-missed-exit-reroute.md) | stable-doc | GPS, map matching, progress, off-route e reroute. |
| [Chat con navigatore esterno](scenarios/chat-with-external-navigation.md) | stable-doc | Background, push e superficie sicura. |
| [Pagina del giorno](scenarios/daily-page-photos-thoughts.md) | stable-doc | Eventi viaggio, media, pensieri e privacy. |
| [Scambio DNA](scenarios/dna-exchange-privacy.md) | stable-doc | Minimizzazione, consenso e proiezione condivisa. |

## Regola sugli stati

- `stable-doc`: il percorso didattico è abbastanza chiaro da guidare una futura
  implementazione, ma il codice può non esistere;
- `executable`: fixture, comando e test esistono nel repository;
- `public-output`: eventuale contratto macchina versionato, non ancora presente.

Ogni scenario deve dichiarare test `existing`, `missing` e `future` senza
fingere copertura.
