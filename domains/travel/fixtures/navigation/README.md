# Navigation fixtures

This directory documents synthetic navigation-runtime fixtures that are not raw
personal GPS traces and are not imported from a routing provider.

## Route progress

`reference-route-progress-v0.meta.yaml` describes a code-defined two-leg route,
shared maneuver boundary, monotonic progress, one deliberate regression and a
compact map delta.

## Map matching boundary

`reference-map-matching-v0.meta.yaml` describes six canonical samples producing
four exact fake matches, one normal unmatched result and one provider failure.
The fake performs no spatial search or snapping.

## Missed exit and reroute

`reference-missed-exit-v0.meta.yaml` describes:

- a first suspicious episode followed by recovery;
- a second episode with two suspicious observations, one indeterminate gap and
  a final suspicious confirmation;
- policy count `3` and duration `2000 ms`;
- one in-flight reroute attempt;
- one duplicate begin and one stale outcome;
- old-route retention;
- a canonical replacement route with a new ID;
- creation of a new empty progress tracker.

The fixture teaches state, correlation and atomic replacement. Its thresholds
are not production recommendations.

## Serialization policy

These fixtures are constructed in readable Kotlin rather than parsed from a new
file format. A versioned matched/off-route serialization format will be added
only when a real replay/import use case requires it.

No file in this directory contains a personal route or real trip.
