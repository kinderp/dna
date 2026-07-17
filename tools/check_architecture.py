#!/usr/bin/env python3
"""Check source-level module import boundaries for the shared Kotlin seed."""
from __future__ import annotations

import re
import sys
from pathlib import Path

IMPORT = re.compile(r"^\s*import\s+([^\s]+)", re.MULTILINE)
RULES = {
    "shared/plugin-sdk": ("kotlin.",),
    "shared/routing-contracts": ("kotlin.", "org.traveldna.plugin.sdk."),
    "shared/routing-testkit": (
        "kotlin.",
        "org.traveldna.plugin.sdk.",
        "org.traveldna.routing.contracts.",
    ),
    "shared/fake-route-planner": (
        "kotlin.",
        "org.traveldna.plugin.sdk.",
        "org.traveldna.routing.contracts.",
        "org.traveldna.routing.testkit.",
    ),
}
FORBIDDEN_TEXT = (
    "maplibre",
    "valhalla",
    "ferrostar",
    "google.maps",
    "waze",
    "sygic",
    "org.traveldna.reference.routing",
)


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        print("usage: check_architecture.py REPOSITORY_ROOT", file=sys.stderr)
        return 2
    root = Path(argv[1]).resolve()
    errors: list[str] = []
    checked = 0
    for module, prefixes in RULES.items():
        source_root = root / module / "src" / "commonMain" / "kotlin"
        if not source_root.exists():
            continue
        for path in sorted(source_root.rglob("*.kt")):
            checked += 1
            text = path.read_text(encoding="utf-8")
            lower = text.lower()
            for token in FORBIDDEN_TEXT:
                if token in lower:
                    errors.append(f"{path.relative_to(root)}: forbidden provider/Lab token: {token}")
            for imported in IMPORT.findall(text):
                if not imported.startswith(prefixes):
                    errors.append(
                        f"{path.relative_to(root)}: import {imported} violates {module} boundary"
                    )
    if errors:
        print("Architecture validation failed:", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1
    print(f"PASS architecture: {checked} shared Kotlin source files")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
