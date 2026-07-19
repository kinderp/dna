#!/usr/bin/env python3
"""Check source-level module import boundaries for shared Kotlin modules."""
from __future__ import annotations

import re
import sys
from pathlib import Path

IMPORT = re.compile(r"^\s*import\s+([^\s]+)", re.MULTILINE)
TRIPLE_STRING = re.compile(r'""".*?"""', re.DOTALL)
BLOCK_COMMENT = re.compile(r"/\*.*?\*/", re.DOTALL)
LINE_COMMENT = re.compile(r"//[^\n]*")
STRING = re.compile(r'"(?:\\.|[^"\\])*"')
CHAR = re.compile(r"'(?:\\.|[^'\\])'")

RULES = {
    "shared/plugin-sdk": ("kotlin.",),
    "shared/geo-contracts": ("kotlin.",),
    "shared/routing-contracts": ("kotlin.", "org.traveldna.plugin.sdk.", "org.traveldna.geo.contracts."),
    "shared/routing-testkit": ("kotlin.", "org.traveldna.plugin.sdk.", "org.traveldna.geo.contracts.", "org.traveldna.routing.contracts."),
    "shared/fake-route-planner": ("kotlin.", "org.traveldna.plugin.sdk.", "org.traveldna.geo.contracts.", "org.traveldna.routing.contracts.", "org.traveldna.routing.testkit."),
    "shared/map-contracts": ("kotlin.", "org.traveldna.plugin.sdk.", "org.traveldna.geo.contracts.", "org.traveldna.routing.contracts."),
    "shared/map-testkit": ("kotlin.", "org.traveldna.plugin.sdk.", "org.traveldna.geo.contracts.", "org.traveldna.routing.contracts.", "org.traveldna.map.contracts."),
    "shared/fake-map-renderer": ("kotlin.", "org.traveldna.plugin.sdk.", "org.traveldna.geo.contracts.", "org.traveldna.routing.contracts.", "org.traveldna.map.contracts.", "org.traveldna.map.testkit."),
    "shared/route-map-projector": ("kotlin.", "org.traveldna.geo.contracts.", "org.traveldna.routing.contracts.", "org.traveldna.map.contracts."),
    "shared/location-contracts": ("kotlin.", "org.traveldna.geo.contracts."),
    "shared/location-replay": ("kotlin.", "org.traveldna.location.contracts."),
    "shared/navigation-contracts": ("kotlin.", "org.traveldna.location.contracts.", "org.traveldna.routing.contracts."),
    "shared/route-progress": ("kotlin.", "org.traveldna.navigation.contracts.", "org.traveldna.routing.contracts."),
    "shared/route-progress-map-projector": ("kotlin.", "org.traveldna.navigation.contracts.", "org.traveldna.map.contracts.", "org.traveldna.routing.contracts."),
    "shared/map-matching-contracts": ("kotlin.", "org.traveldna.plugin.sdk.", "org.traveldna.location.contracts.", "org.traveldna.navigation.contracts.", "org.traveldna.routing.contracts."),
    "shared/map-matching-testkit": ("kotlin.", "org.traveldna.location.contracts.", "org.traveldna.navigation.matching.contracts.", "org.traveldna.routing.contracts."),
    "shared/fake-map-matcher": ("kotlin.", "org.traveldna.plugin.sdk.", "org.traveldna.geo.contracts.", "org.traveldna.location.contracts.", "org.traveldna.navigation.contracts.", "org.traveldna.navigation.matching.contracts.", "org.traveldna.routing.contracts."),
    "shared/off-route-contracts": ("kotlin.", "org.traveldna.location.contracts.", "org.traveldna.routing.contracts."),
    "shared/off-route-state-machine": ("kotlin.", "org.traveldna.navigation.offroute.contracts.", "org.traveldna.routing.contracts."),
    "shared/reroute-coordinator": ("kotlin.", "org.traveldna.navigation.offroute.contracts.", "org.traveldna.routing.contracts."),
}
FORBIDDEN_CODE_TOKENS = (
    "maplibre", "valhalla", "ferrostar", "google.maps", "waze", "sygic",
    "android.", "androidx.", "corelocation", "cllocation",
    "org.traveldna.reference.routing",
)


def strip_non_code(text: str) -> str:
    stripped = TRIPLE_STRING.sub('""', text)
    stripped = BLOCK_COMMENT.sub("", stripped)
    stripped = LINE_COMMENT.sub("", stripped)
    stripped = STRING.sub('""', stripped)
    stripped = CHAR.sub("''", stripped)
    return stripped


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
            code_lower = strip_non_code(text).lower()
            for token in FORBIDDEN_CODE_TOKENS:
                if token in code_lower:
                    errors.append(
                        f"{path.relative_to(root)}: forbidden provider/platform/Lab code token: {token}"
                    )
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
