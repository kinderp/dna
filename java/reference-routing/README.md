# Java reference routing

This module is the readable JVM reference implementation for the first Travel
DNA Lab scenario. It uses only the Java 21 standard library.

It deliberately owns a small educational model rather than the future mobile
canonical API. Its purpose is to make Dijkstra, A*, fixture parsing and route
reconstruction easy to inspect and compare with the Rust implementation.

Run from the repository root:

```bash
sh tools/tdna check-java
sh tools/tdna lab reference-routing
```

The module performs no network, database, map rendering or provider work.
