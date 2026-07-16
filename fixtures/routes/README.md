# Route fixtures

This directory stores synthetic, versioned routing fixtures. It follows the
[general fixture policy](../README.md): no personal routes, no silently copied
provider data and explicit provenance for every file.

## First fixture

`reference-network-v0.tdna` is the graph used by the first executable Java/Rust
Lab. Its companion metadata file is `reference-network-v0.meta.yaml`.

The format is intentionally line-oriented:

```text
TDNA_REFERENCE_GRAPH_V0
scenario SCENARIO_ID
node NODE_ID LATITUDE LONGITUDE
road FROM TO COST_METRES
query ORIGIN DESTINATION
expect TOTAL_COST_METRES NODE_1,NODE_2,...
```

Properties:

- coordinates use WGS84;
- roads are bidirectional in v0;
- costs are positive integer metres;
- each road cost must be no shorter than the direct WGS84 distance, preserving
  the admissibility of the A* heuristic used in the Lab;
- `expect` is ground truth and turns the fixture into a reproducible test;
- identifiers are restricted so the small canonical JSON report needs no
  general-purpose escaping library.

Run:

```bash
sh tools/tdna lab reference-routing dijkstra
sh tools/tdna check-java
```

The fixture is educational. It does not represent OpenStreetMap, turn
restrictions, traffic, map matching or production route costing.
