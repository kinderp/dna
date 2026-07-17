# Navigation fixtures

This directory documents synthetic navigation-runtime fixtures that are not raw
GPS traces and are not imported from a routing provider.

## Reference route-progress fixture

`reference-route-progress-v0.meta.yaml` describes the code-defined fixture used
by `labs/route-progress-cli` and the common route-progress tests.

The fixture contains:

- one canonical route with four geometry points;
- two contiguous legs sharing geometry index `2`;
- a previous-leg and next-leg maneuver at the shared boundary;
- six ordered matched positions;
- five accepted decisions;
- one deliberate backwards route coordinate;
- an arrival at the final geometry point;
- one projected `MapSceneDelta.UpdateRouteProgress`.

The route is constructed in readable Kotlin rather than parsed from a new file
format. This keeps the current slice focused on route-progress semantics. A
versioned matched-position serialization format will be introduced only when a
real replay/import use case requires it.

No file in this directory contains personal coordinates or a real trip.
