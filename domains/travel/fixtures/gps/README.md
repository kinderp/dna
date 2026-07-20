# GPS and location replay fixtures

See the [general fixture policy](../README.md). Every fixture must state format,
version, generator or provenance, expected behavior and the tests that consume it.

## `TDNA_LOCATION_REPLAY_V0`

The first replay fixture is:

```text
reference-location-replay-v0.tdna
reference-location-replay-v0.meta.yaml
```

Format:

```text
TDNA_LOCATION_REPLAY_V0
scenario SCENARIO_ID
rate NUMERATOR DENOMINATOR
sample SEQUENCE MONOTONIC_MS LAT LON ACCURACY_M SPEED_MPS BEARING_DEG replay
expect accepted COUNT
expect rejected COUNT
expect final_time_ms VALUE
expect playback_delay_ms VALUE
expect last_sequence VALUE
expect reason non_increasing_sequence COUNT
expect reason non_increasing_monotonic_time COUNT
```

Use `-` for an unavailable speed or bearing. All samples are synthetic and must
use `replay` origin. The parser preserves declared order and never sorts input.
Expectations are ground truth and are checked against the deterministic runner.

Run:

```bash
sh tools/tdna lab location-replay
sh tools/tdna bench location-replay 10000 7
```

The benchmark is diagnostic only. It has no CI threshold and does not represent
GPS, battery, map matching or road performance.
