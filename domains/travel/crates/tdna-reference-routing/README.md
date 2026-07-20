# Rust reference routing

This crate implements the same deterministic fixture contract as
`java/reference-routing` using only the Rust standard library.

It is an educational comparison target, not yet the production Travel DNA
navigation core. The first objective is to make ownership, parsing, priority
queues, route reconstruction and deterministic output visible to students.

Run from the repository root:

```bash
sh tools/tdna check-rust
sh tools/tdna check-contract
```
