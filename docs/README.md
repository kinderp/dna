# Travel DNA documentation

This directory contains the stable product, architecture, engineering and
teaching documentation for Travel DNA.

## Main areas

- [`it/`](it/README.md): Italian book-like product and engineering chapters.
- [`it/lab/`](it/lab/README.md): executable and planned teaching scenarios.
- [`adr/`](adr/README.md): Architecture Decision Records.
- [`project/`](project/README.md): live development state and foundation records.
- [`project/daily/`](project/daily/README.md): permanent index of daily reports.
- [`commenting-style.md`](commenting-style.md): source-comment rules.
- [`commenting-status.md`](commenting-status.md): current review state.

## Recommended starting points

- New reader: [`it/01-visione-prodotto.md`](it/01-visione-prodotto.md)
- Guided paths: [`it/03-guida-lettura-documentazione.md`](it/03-guida-lettura-documentazione.md)
- Contributor: [`it/00-regole-operative.md`](it/00-regole-operative.md)
- Reviewer/maintainer: [`it/06-review-e-merge.md`](it/06-review-e-merge.md)
- Current development state: [`project/development-status.md`](project/development-status.md)
- Architecture: [`it/20-architettura-generale.md`](it/20-architettura-generale.md)
- Navigation fundamentals: [`it/24-routing-e-navigazione.md`](it/24-routing-e-navigazione.md)
- Teaching scenarios: [`it/lab/README.md`](it/lab/README.md)

## Implementation-backed learning sequence

1. [`43-reference-routing-java-rust.md`](it/43-reference-routing-java-rust.md):
   graph, Dijkstra, A* and independent Java/Rust reports.
2. [`44-contratti-routing-e-fake-provider.md`](it/44-contratti-routing-e-fake-provider.md):
   provider-neutral routing contracts and deterministic fake planner.
3. [`45-map-scene-e-fake-renderer.md`](it/45-map-scene-e-fake-renderer.md):
   declarative map scene, bounded deltas and semantic fake renderer.
4. [`46-location-sample-e-replay-deterministico.md`](it/46-location-sample-e-replay-deterministico.md):
   monotonic samples, ordering gate, virtual clock and replay.
5. [`47-posizione-matched-e-route-progress.md`](it/47-posizione-matched-e-route-progress.md):
   already-matched positions, route progress, maneuver/arrival policy and map delta.
6. [`48-porta-map-matching-e-fake-deterministico.md`](it/48-porta-map-matching-e-fake-deterministico.md):
   route-bound matching port, explicit outcomes, deterministic fake and progress pipeline.
7. [`49-off-route-missed-exit-e-reroute.md`](it/49-off-route-missed-exit-e-reroute.md):
   normalized evidence, bounded confirmation, correlated reroute and atomic route replacement.

Run all foundation checks:

```bash
sh tools/tdna check
```

Run individual Labs:

```bash
sh tools/tdna lab reference-routing dijkstra
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
sh tools/tdna lab location-replay
sh tools/tdna lab route-progress
sh tools/tdna lab map-matching
sh tools/tdna lab missed-exit
```

Hand-written documentation remains the primary explanation. Generated reports,
benchmarks and graphs stay in recognizable generated paths and never replace the
narrative source.
