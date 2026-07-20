#!/usr/bin/env python3
"""Require reviewed immutable SHAs for external GitHub Actions."""

from __future__ import annotations

import re
import sys
from pathlib import Path

USES_PATTERN = re.compile(r"^\s*-?\s*uses:\s*([^\s#]+)(?:\s+#\s*(\S.*))?\s*$")
FULL_SHA_PATTERN = re.compile(r"^[0-9a-f]{40}$")

REVIEWED_ACTIONS = {
    "actions/checkout": "34e114876b0b11c390a56381ad16ebd13914f8d5",
    "actions/setup-java": "c1e323688fd81a25caa38c78aa6df2d33d3e20d9",
    "actions/upload-artifact": "ea165f8d65b6e75b540449e92b4886f43607fa02",
    "gradle/actions/setup-gradle": "3f131e8634966bd73d06cc69884922b02e6faf92",
}


def validate_workflows(root: Path) -> tuple[int, list[str]]:
    errors: list[str] = []
    references = 0
    workflow_dir = root / ".github/workflows"
    files = sorted(workflow_dir.glob("*.yml")) + sorted(workflow_dir.glob("*.yaml"))
    if not files:
        return 0, ["no GitHub Actions workflow files found"]

    for path in files:
        for line_number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), start=1):
            match = USES_PATTERN.match(line)
            if match is None:
                continue
            reference, version_comment = match.groups()
            if reference.startswith("./") or reference.startswith("docker://"):
                continue
            references += 1
            if "@" not in reference:
                errors.append(f"{path.relative_to(root)}:{line_number}: action reference has no @ revision")
                continue
            action, revision = reference.rsplit("@", 1)
            if not FULL_SHA_PATTERN.fullmatch(revision):
                errors.append(
                    f"{path.relative_to(root)}:{line_number}: {action} is not pinned to a full 40-character SHA"
                )
                continue
            expected = REVIEWED_ACTIONS.get(action)
            if expected is None:
                errors.append(
                    f"{path.relative_to(root)}:{line_number}: {action} is not in the reviewed action allowlist"
                )
            elif revision != expected:
                errors.append(
                    f"{path.relative_to(root)}:{line_number}: {action} drifted from {expected} to {revision}"
                )
            if not version_comment:
                errors.append(
                    f"{path.relative_to(root)}:{line_number}: pinned action needs a human-readable version comment"
                )
    return references, errors


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        print("usage: check_ci_actions.py REPOSITORY_ROOT", file=sys.stderr)
        return 2
    root = Path(argv[1]).resolve()
    references, errors = validate_workflows(root)
    if errors:
        print("GitHub Actions reference validation failed:", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1
    print(f"PASS GitHub Actions: {references} reviewed external reference(s) pinned to full SHAs")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
