# Scenario: MapScene provider-neutral e fake renderer

id: `lab.map.scene-fake-renderer.v0`

status: `executable`

scenario kind: `map-rendering-foundation`

## Learning goal

Seguire una route canonica dalla proiezione in `RouteOverlay` fino a una scena,
applicare delta bounded e osservare lo stato risultante senza MapLibre o UI
mobile.

## Prerequisites

- [Contratti routing e fake provider](../../44-contratti-routing-e-fake-provider.md)
- [MapScene e fake renderer](../../45-map-scene-e-fake-renderer.md)
- [Plugin e provider](../../22-architettura-plugin-provider.md)
- [Performance budget](../../32-performance-budget.md)

## User story

Un'applicazione installa una route primaria, mostra un luogo e un Compagno di
strada approssimato, aggiorna il progresso, aggiunge una traccia DNA e la
seleziona. Lo stesso flusso dovrà funzionare con un futuro adapter MapLibre.

## Platforms

- Kotlin common;
- JVM Lab;
- JVM e Linux x64 test in CI;
- nessuna UI nativa.

## Trigger

```bash
sh tools/tdna lab map-scene
```

Verifica completa:

```bash
sh tools/tdna check
```

## Input

- `FakeRouteFixtures.ReferenceRoute`;
- route overlay ID `route.primary`;
- scena `scene.reference-map-v0`;
- luogo pubblico;
- companion con semantica `ApproximateArea`;
- delta progresso `(index=2, fraction=0.5)`;
- nuova traccia DNA approssimata.

## Expected evidence

- la `RoutePlan` viene proiettata senza tipi provider;
- la scena installa una sola copia della geometria;
- il progresso modifica solo lo stato `RouteOverlayProgress`;
- luogo, companion e DNA usano ID unici;
- il companion non può usare `PublicPlace`;
- il marker DNA può essere aggiunto e selezionato;
- la scena rifiuta delta con ID stantio;
- il fake conserva una call history;
- il report finale è deterministico.

## Expected logical events

```text
ROUTE_OVERLAY_PROJECTED
MAP_SCENE_INSTALLED
MAP_ROUTE_PROGRESS_APPLIED
MAP_MARKERS_UPSERTED
MAP_ITEM_SELECTED
MAP_SNAPSHOT_OBSERVED
```

Questi sono tracepoint documentali, non telemetria runtime.

## Function and module path

```text
FakeRouteFixtures.ReferenceRoute
-> RouteMapProjector.project()
-> MapScene(...)
-> FakeMapRenderer.install()
-> FakeMapRenderer.apply(UpdateRouteProgress)
-> FakeMapRenderer.apply(UpsertMarkers)
-> FakeMapRenderer.apply(SelectItem)
-> FakeMapRenderer.snapshot
-> labs/map-scene-cli MainKt
```

## State ownership

| Stato | Owner | Durata |
| --- | --- | --- |
| route geometry | scena/fake renderer | installazione scena |
| route progress | fake renderer | delta successivi |
| marker catalog | fake renderer | fino a remove/clear |
| selection | fake renderer | fino a nuova selezione/remove/clear |
| call history | fake renderer | vita del fake |
| provider capability | descriptor | vita del plugin |

## Expected output

Forma indicativa:

```json
{"provider":"org.traveldna.fake-map-renderer","capabilities":["map.apply-delta","map.camera","map.install-scene","map.markers","map.route-progress","map.selection"],"scene_id":"scene.reference-map-v0","routes":1,"markers":3,"completed_index":2,"progress_fraction":0.5,"selected":"dna.scenic-stop","operations":4}
```

L'ordine dei campi è stabile per il Lab, ma non costituisce ancora uno schema
pubblico.

## Performance properties

- nessuna rete;
- nessun database;
- nessuna GPU;
- route geometry installata una volta;
- progress delta contiene ID, indice e frazione;
- marker delta massimo 100 operazioni;
- scena massimo 500 marker e tre route;
- nessuna affermazione FPS.

## Privacy and safety properties

- dati sintetici;
- nessuna posizione reale;
- companion obbligatoriamente marcato `ApproximateArea`;
- la semantica non sostituisce il privacy filter;
- nessuna interazione durante guida reale.

## Existing tests

- `MapContractsTest`;
- `FakeMapRendererTest`;
- `RouteMapProjectorTest`;
- `MapRendererContractProbe` eseguito dal fake;
- architecture checker;
- Foundation CI.

## Missing tests

- property test per sequenze casuali di delta;
- concorrenza e ordinamento asincrono;
- più scene installate in rapida successione;
- capability probe ridotti;
- benchmark con geometrie grandi;
- adapter MapLibre.

## Future tests

- replay GPS -> progress delta;
- confronto snapshot fake/adattatore MapLibre;
- screenshot test Android/iOS;
- frame timing;
- clustering marker;
- memory soak su viaggio lungo;
- privacy filter con corridoio reale anonimizzato.

## Common failures

- usare `fractionToNext = 1.0`;
- ricreare la route a ogni progress update;
- inviare delta per una scena precedente;
- usare lo stesso ID per route e marker;
- trattare una persona come luogo pubblico;
- credere che il fake misuri la GPU;
- far usare al probe capability non dichiarate;
- inserire tipi MapLibre nel contratto.

## Non-goals

- renderer grafico;
- UI mobile;
- navigazione attiva;
- GPS;
- tile e stile;
- traffico;
- esatte posizioni sociali;
- benchmark prestazionale conclusivo.

## Related docs

- [Capitolo 45](../../45-map-scene-e-fake-renderer.md)
- [Scenario route canonica precedente](render-canonical-route.md)
- [Registro milestone](../../50-registro-milestone.md)
