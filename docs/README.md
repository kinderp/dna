# Travel DNA documentation

This directory contains the stable documentation of the Travel DNA project.
The root `README.md` is the repository entry point; this page explains how the
technical, architectural and teaching material is organized.

## Main areas

- [`it/`](it/README.md): Italian teaching, product and architecture documentation.
- [`adr/`](adr/README.md): Architecture Decision Records.
- [`project/`](project/README.md): development status, daily reports and
  repository-level foundation records.
- [`commenting-style.md`](commenting-style.md): language-aware source comment rules.
- [`commenting-status.md`](commenting-status.md): current comment review status.

## Recommended starting points

- New reader or student: [`it/03-guida-lettura-documentazione.md`](it/03-guida-lettura-documentazione.md)
- Contributor: [`it/00-regole-operative.md`](it/00-regole-operative.md)
- Current development state: [`project/development-status.md`](project/development-status.md)
- Software architecture: [`it/20-architettura-generale.md`](it/20-architettura-generale.md)
- Technology decisions: [`it/52-matrice-tecnologie-decisioni.md`](it/52-matrice-tecnologie-decisioni.md)
- Navigation fundamentals: [`it/24-routing-e-navigazione.md`](it/24-routing-e-navigazione.md)
- First implementation-backed chapter: [`it/43-reference-routing-java-rust.md`](it/43-reference-routing-java-rust.md)
- Teaching scenarios: [`it/lab/README.md`](it/lab/README.md)

## Executable documentation

The first executable Lab uses a synthetic graph and independent Java/Rust
implementations. Run from the repository root:

```bash
sh tools/tdna check-java
sh tools/tdna lab reference-routing dijkstra
```

With stable Rust installed:

```bash
sh tools/tdna check
```

The hand-written teaching documentation remains the primary explanation.
Generated API references, graphs and reports must live in recognizable generated
paths and must never replace the narrative source.
