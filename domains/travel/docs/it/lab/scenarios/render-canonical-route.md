# Scenario: render a canonical route through MapLibre

id: `lab.map.render-canonical-route.v0`
status: `stable-doc`
scenario kind: `map-adapter`

## Learning goal

Dimostrare che la UI e il dominio non dipendono da tipi MapLibre.

## Trigger

`MapScene` con route, camera e tre POI viene installata; poi arrivano dieci
`MapSceneDelta` di progresso.

## Expected evidence

- base scene installed once;
- route geometry converted once;
- progress updates do not rebuild source geometry;
- POI interaction returns canonical `MapItemId`;
- renderer can be replaced by fake in tests.

## Tracepoints

```text
MAP_BASE_SCENE_INSTALLED
MAP_PROGRESS_DELTA_APPLIED
```

## Module path target

```text
ActiveTripPresentation
-> MapSceneRendererPort
-> MapLibreMapSceneAdapter
-> native MapView
```

## Performance properties

- zero plugin registry lookup per delta;
- zero route JSON encoding per delta;
- stable memory;
- frame budget respected.

## Missing tests

- adapter contract;
- lifecycle restore;
- style reload;
- route replacement;
- low-memory;
- Android and iOS parity.

## Non-goals

- tile hosting;
- routing;
- UI design final;
- 3D terrain.
