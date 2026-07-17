# Commenting status

This file tracks current review status, not the chronological history of every
comment change. Git and pull requests preserve history.

## Status values

- `Pending`: no focused documentation review yet.
- `Contract documented`: public responsibilities and errors are documented.
- `Hot-path reviewed`: concurrency and performance invariants are documented.
- `Teaching-ready`: an Italian guided explanation and Lab link exist.
- `Needs refresh`: code changed after the last review.

## Current status

| Area | Status | Notes |
| --- | --- | --- |
| Documentation contracts | Teaching-ready | Foundation, reading paths and project rules exist. |
| Java reference routing | Teaching-ready | Public types, invariants and errors are documented; chapter 43 and executable Lab exist. |
| Rust reference routing | Teaching-ready | Crate/module docs, error contracts and executable Lab path exist; CI remains the toolchain gate. |
| Route fixture v0 | Contract documented | Format, provenance, invariants and ground truth are documented. |
| Canonical production geo models | Pending | Reference-routing types are explicitly fixture-scoped. |
| Plugin SDK | Pending | Contracts described, code not started. |
| Navigation runtime | Pending | Architecture and future scenarios documented. |
| Android UI | Pending | No code yet. |
| iOS UI | Pending | No code yet. |
| Backend | Pending | Architecture proposed, implementation not started. |
| Journey diary | Pending | Domain and privacy rules documented. |
| Conversation runtime | Pending | Driver/passenger modes documented. |

Update this table whenever a module is introduced or structurally changed.
