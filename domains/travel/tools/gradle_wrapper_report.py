#!/usr/bin/env python3
"""Emit the deterministic Travel DNA build-bootstrap Lab report."""

from __future__ import annotations

import json
import sys
from pathlib import Path

from check_ci_actions import validate_workflows
from check_gradle_wrapper import validate_wrapper


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        print("usage: gradle_wrapper_report.py REPOSITORY_ROOT", file=sys.stderr)
        return 2
    root = Path(argv[1]).resolve()
    try:
        wrapper = validate_wrapper(root)
        action_count, action_errors = validate_workflows(root)
        if action_errors:
            raise ValueError("; ".join(action_errors))
    except (OSError, ValueError, KeyError) as error:
        print(f"build-bootstrap report failed: {error}", file=sys.stderr)
        return 1

    report = {
        "scenario": "build-bootstrap-v0",
        "gradle_version": wrapper.gradle_version,
        "distribution_sha256": wrapper.distribution_sha256,
        "wrapper_jar_sha256": wrapper.wrapper_jar_sha256,
        "wrapper_files": 4,
        "actions_pinned": action_count,
        "global_gradle_required": False,
        "java_required": True,
    }
    print(json.dumps(report, separators=(",", ":"), sort_keys=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
