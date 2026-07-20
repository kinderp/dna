# Travel DNA code commenting style

This document defines how comments and API documentation are written across the
Travel DNA codebase.

## Language

All code comments and public API documentation are written in English. Teaching
material is written in Italian under `docs/it`.

## Purpose

Comments explain information that cannot be recovered cheaply from syntax:

- design intent;
- responsibility and non-responsibility;
- invariants;
- ownership and lifetime;
- threading, actor or coroutine confinement;
- cancellation and cleanup;
- error and degraded-mode behavior;
- privacy boundaries;
- hot-path constraints;
- provider integration boundaries;
- temporary migration seams.

Comments must not translate code line by line.

## File and module documentation

Important modules start with a short responsibility statement. It should also
state what the module deliberately does not own.

Kotlin example:

```kotlin
/**
 * Canonical route-planning contracts used by the application core.
 *
 * This module owns provider-neutral requests, results and errors. It does not
 * perform HTTP calls, render maps or expose Valhalla response models.
 */
package org.traveldna.navigation.contracts
```

Rust example:

```rust
//! Deterministic route-progress and guidance state transitions.
//!
//! This crate owns location-to-snapshot computation. It performs no file,
//! network, database or UI I/O in the steady-state advance path.
```

Swift example:

```swift
/// Hosts the persistent MapLibre view used by the iOS navigation screen.
///
/// The host translates canonical map-scene updates into MapLibre operations.
/// It does not decide route semantics or fetch social data.
final class NavigationMapHost { }
```

## Public contracts

Document public interfaces, structs, enums and functions when callers need to
know more than the type system expresses.

A contract comment should cover, when relevant:

- accepted input and validation;
- ordering requirements;
- ownership and copying;
- cancellation;
- idempotency;
- timeout and error translation;
- thread or dispatcher requirements;
- performance characteristics;
- privacy or retention effects;
- capability limitations.

```kotlin
/**
 * Plans one or more canonical routes for [request].
 *
 * Implementations must be cancellable and must not block the main thread.
 * Provider-specific failures are translated into [RoutePlanningError]. The
 * returned route geometry is immutable and contains no vendor model objects.
 */
suspend fun plan(request: RouteRequest): RoutePlanningResult
```

## Invariant comments

Use comments for rules whose violation would produce subtle bugs.

```rust
/// Samples must be processed in monotonic timestamp order. A late sample is
/// rejected rather than replayed into an already published navigation state.
pub fn advance(...) -> Result<NavigationSnapshot, GuidanceError>
```

## Hot-path comments

Any function on a documented hot path must state prohibited work when that rule
is not obvious.

```kotlin
/**
 * Applies a compact progress delta to the existing map scene.
 *
 * This method must not rebuild GeoJSON, query the database, perform network I/O
 * or resolve plugins. Route geometry is installed separately and reused.
 */
fun applyProgress(delta: RouteProgressDelta)
```

## Privacy comments

Explain precision, retention and visibility when processing location, media or
identity data.

```kotlin
/**
 * Produces a road-presence cell suitable for nearby discovery.
 *
 * Exact coordinates remain local. The published cell is short-lived, delayed
 * and scoped to the active trip direction.
 */
fun approximatePresence(sample: LocationSample): PresenceCell
```

## Test comments

Tests should explain the scenario, contract and reason for non-obvious setup.
Avoid narrating each assertion.

For complex tests, start with a block such as:

```text
Scenario contract:
- a missed motorway exit moves confidence from HIGH to LOW;
- precise time-to-POI suggestions are suspended;
- exactly one reroute request is active;
- the previous route remains usable until replacement succeeds.
```

## TODO comments

TODOs require a category and should describe the intended direction.

```text
TODO(nav-core): replace the temporary linear segment scan with the indexed
candidate search described in issue #123 before enabling country-scale routes.
```

Allowed categories include:

- `TODO(nav-core)`
- `TODO(provider-migration)`
- `TODO(performance)`
- `TODO(battery)`
- `TODO(privacy)`
- `TODO(offline)`
- `TODO(android-auto)`
- `TODO(carplay)`
- `TODO(test)`
- `TODO(cleanup)`

Repository-wide or architectural work belongs in an issue, not only in a TODO.

## Language-specific tools

- Kotlin/Java: KDoc/Javadoc and Dokka where useful.
- Rust: rustdoc, examples and `# Errors`/`# Panics` sections when applicable.
- Swift: DocC-compatible comments.
- Schemas: field comments beside Protobuf, JSON Schema or SQL definitions.

Formatting tools may enforce layout, but they must not generate meaningless
comments to satisfy a quota.
