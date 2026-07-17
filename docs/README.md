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
- [`commenting-status.md`](commenting-status.md): current comment/documentation review state.

## Recommended starting points

- New reader: [`it/01-visione-prodotto.md`](it/01-visione-prodotto.md)
- Guided reading paths: [`it/03-guida-lettura-documentazione.md`](it/03-guida-lettura-documentazione.md)
- Contributor: [`it/00-regole-operative.md`](it/00-regole-operative.md)
- Reviewer/maintainer: [`it/06-review-e-merge.md`](it/06-review-e-merge.md)
- Current development state: [`project/development-status.md`](project/development-status.md)
- Software architecture: [`it/20-architettura-generale.md`](it/20-architettura-generale.md)
- Technology decisions: [`it/52-matrice-tecnologie-decisioni.md`](it/52-matrice-tecnologie-decisioni.md)
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
   monotonic location samples, ordering gate, virtual clock and replay.

Run all foundation checks from the repository root:

```bash
sh tools/tdna check
```

Run individual Labs:

```bash
sh tools/tdna lab reference-routing dijkstra
sh tools/tdna lab routing-contracts
sh tools/tdna lab map-scene
sh tools/tdna lab location-replay
```

Hand-written teaching documentation remains the primary explanation. Generated
references, graphs, test reports and benchmark observations must stay in
recognizable generated paths and must never replace the narrative source.
