# Navigation fixtures

This directory documents synthetic navigation-runtime fixtures that are not raw
GPS traces and are not imported from a routing provider.

## Reference route-progress fixture

`reference-route-progress-v0.meta.yaml` describes the code-defined fixture used
by `labs/route-progress-cli` and the common route-progress tests.

It contains:

- one canonical route with four geometry points;
- two contiguous legs sharing geometry index `2`;
- a previous-leg and next-leg maneuver at the shared boundary;
- six ordered matched positions;
- five accepted decisions;
- one deliberate backwards route coordinate;
- an arrival at the final geometry point;
- one projected `MapSceneDelta.UpdateRouteProgress`.

## Reference map-matching boundary fixture

`reference-map-matching-v0.meta.yaml` describes the code-defined fixture used by
`labs/map-matching-cli`, the fake matcher and the reusable conformance probe.

It contains:

- the same scale of four-point, two-leg synthetic route;
- six canonical `LocationSample` observations;
- four exact catalog matches;
- one normal `Unmatched(NoCandidate)` outcome;
- one distinct `Failure(ProviderUnavailable)` outcome;
- a downstream route-progress arrival;
- bounded call diagnostics.

The fake performs no spatial search or snapping. The fixture teaches the
provider boundary and result semantics, not map-matching accuracy.

## Serialization policy

Both fixtures are constructed in readable Kotlin rather than parsed from a new
file format. This keeps each slice focused on runtime semantics. A versioned
matched-position or map-match serialization format will be introduced only when
a real replay/import use case requires it.

No file in this directory contains personal coordinates or a real trip.
