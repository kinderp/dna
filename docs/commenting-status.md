# Commenting status

This file tracks current review state, not the chronological history of every
comment change. Git and pull requests preserve history.

## Status values

- `Pending`: no focused documentation review yet.
- `Contract documented`: public responsibilities and errors are documented.
- `Hot-path reviewed`: ownership, state, failure and performance invariants are documented.
- `Teaching-ready`: an Italian guided chapter and Lab path exist.
- `Needs refresh`: code changed after the last focused review.

## Current status

| Area | Status | Notes |
| --- | --- | --- |
| Documentation/governance | Teaching-ready | Rules, serial PR workflow, reading paths and indexed reports. |
| Java/Rust reference routing | Teaching-ready | Chapter 43, tests and byte-level contract comparison. |
| Route fixture v0 | Contract documented | Format, provenance, invariants and ground truth. |
| Plugin SDK and routing contracts | Teaching-ready | Capability, provider-neutral models, fake and testkit. |
| MapScene/map contracts | Hot-path reviewed | Static scene, bounded deltas and renderer contract. |
| Location contracts | Hot-path reviewed | Monotonic time, sequence and sensor bounds. |
| Deterministic location replay | Teaching-ready | Atomic state, fixture, clock, benchmark and chapter 46. |
| GPS fixture v0 | Contract documented | Synthetic, versioned and accompanied by ground truth. |
| Matched-position contracts | Hot-path reviewed | Route coordinate, confidence, lateral distance and rejection model. |
| Route progress tracker | Hot-path reviewed | Bounded state, binary searches, arrival tie-break and chapter 47. |
| Route-progress map binding/projector | Hot-path reviewed | Full geometry verified at install; O(1) compact updates. |
| Production map matching/runtime | Pending | Filtering, matching, distance, ETA, off-route and rerouting remain planned. |
| Android UI/adapters | Pending | No Android application code yet. |
| iOS UI/adapters | Pending | No iOS application code yet. |
| Backend | Pending | Architecture proposed, implementation not started. |
| Journey diary | Pending | Domain/privacy rules documented, code not started. |
| Conversation runtime | Pending | Driver/passenger behavior documented, code not started. |

Update this table whenever a module is introduced or structurally changed.
