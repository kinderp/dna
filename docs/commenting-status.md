# Commenting status

This file tracks current review state, not the chronological history of every
comment change. Git and pull requests preserve history.

## Status values

- `Pending`: no focused documentation review yet.
- `Contract documented`: public responsibilities and errors are documented.
- `Hot-path reviewed`: ownership, state, failure and performance invariants are documented.
- `Teaching-ready`: an Italian guided chapter and executable/planned Lab path exist.
- `Needs refresh`: code changed after the last focused review.

## Current status

| Area | Status | Notes |
| --- | --- | --- |
| Documentation/governance contracts | Teaching-ready | Rules, serial PR workflow, reading paths and indexed reports exist. |
| Java reference routing | Teaching-ready | Chapter 43 and executable Lab. |
| Rust reference routing | Teaching-ready | Crate docs, tests and Java/Rust contract comparison. |
| Route fixture v0 | Contract documented | Format, provenance, invariants and ground truth. |
| Plugin SDK | Teaching-ready | Descriptor, capability, runtime platform and chapter 44. |
| Canonical geo/routing contracts | Teaching-ready | KMP contracts, invariants, provenance and testkit. |
| Fake route planner | Teaching-ready | Deterministic behavior and reusable conformance probe. |
| MapScene/map contracts | Hot-path reviewed | Static scene versus bounded deltas, privacy semantics and chapter 45. |
| Fake map renderer | Teaching-ready | Semantic state, snapshots, capability checks and executable Lab. |
| Location contracts | Hot-path reviewed | Monotonic time, sequence, sensor bounds and non-mutating inspection. |
| Deterministic location replay | Teaching-ready | Atomic state commits, fixture, clock, rate, benchmark and chapter 46. |
| GPS fixture v0 | Contract documented | Synthetic, versioned, bounded and accompanied by ground truth metadata. |
| Production navigation runtime | Pending | Map matching, progress, maneuvers and off-route still planned. |
| Android UI/adapters | Pending | No Android application code yet. |
| iOS UI/adapters | Pending | No iOS application code yet. |
| Backend | Pending | Architecture proposed, implementation not started. |
| Journey diary | Pending | Domain and privacy rules documented, code not started. |
| Conversation runtime | Pending | Driver/passenger behavior documented, code not started. |

Update this table whenever a module is introduced or structurally changed.
