#!/usr/bin/env python3
"""Validate the permission-free Android Pilot 0 manifest contract."""
from __future__ import annotations

import json
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ANDROID = "{http://schemas.android.com/apk/res/android}"
FORBIDDEN_PERMISSIONS = {
    "android.permission.ACCESS_COARSE_LOCATION",
    "android.permission.ACCESS_FINE_LOCATION",
    "android.permission.ACCESS_BACKGROUND_LOCATION",
    "android.permission.FOREGROUND_SERVICE",
    "android.permission.FOREGROUND_SERVICE_LOCATION",
    "android.permission.POST_NOTIFICATIONS",
    "android.permission.INTERNET",
    "android.permission.BLUETOOTH",
    "android.permission.BLUETOOTH_CONNECT",
    "android.permission.BLUETOOTH_SCAN",
    "android.permission.NEARBY_WIFI_DEVICES",
}
PERMISSION_TAGS = {
    "uses-permission",
    "uses-permission-sdk-23",
    "uses-permission-sdk-m",
}


def local_name(tag: str) -> str:
    return tag.rsplit("}", 1)[-1]


def declared_permissions(root: ET.Element) -> set[str]:
    result: set[str] = set()
    for element in root.iter():
        if local_name(element.tag) in PERMISSION_TAGS:
            name = element.attrib.get(f"{ANDROID}name")
            if name:
                result.add(name)
    return result


def parse(path: Path) -> ET.Element:
    try:
        return ET.parse(path).getroot()
    except (OSError, ET.ParseError) as error:
        raise ValueError(f"cannot parse {path}: {error}") from error


def validate_application_contract(root: ET.Element, context: str) -> None:
    application = root.find("application")
    if application is None:
        raise ValueError(f"{context} has no application element")
    if application.attrib.get(f"{ANDROID}allowBackup") != "false":
        raise ValueError(f"{context} must keep android:allowBackup=false")

    launcher_found = False
    for activity in application.findall("activity"):
        exported = activity.attrib.get(f"{ANDROID}exported")
        for intent_filter in activity.findall("intent-filter"):
            actions = {
                action.attrib.get(f"{ANDROID}name")
                for action in intent_filter.findall("action")
            }
            categories = {
                category.attrib.get(f"{ANDROID}name")
                for category in intent_filter.findall("category")
            }
            if (
                "android.intent.action.MAIN" in actions
                and "android.intent.category.LAUNCHER" in categories
            ):
                if exported != "true":
                    raise ValueError(
                        f"{context} launcher activity must remain exported=true"
                    )
                launcher_found = True
    if not launcher_found:
        raise ValueError(f"{context} has no exported launcher activity")


def find_main_merged_manifests(root: Path) -> list[Path]:
    intermediates = root / "apps/android/build/intermediates"
    if not intermediates.exists():
        return []
    manifests: list[Path] = []
    for path in intermediates.rglob("AndroidManifest.xml"):
        rendered = path.as_posix().lower()
        if "debug" not in rendered or "androidtest" in rendered:
            continue
        if "merged_manifest" in rendered or "merged_manifests" in rendered:
            manifests.append(path)
    return sorted(set(manifests))


def validate(root: Path) -> dict[str, object]:
    source_path = root / "apps/android/src/main/AndroidManifest.xml"
    if not source_path.is_file():
        raise ValueError("apps/android/src/main/AndroidManifest.xml is missing")
    source_root = parse(source_path)
    source_permissions = declared_permissions(source_root)
    if source_permissions:
        raise ValueError(
            "Pilot 0 source manifest must declare no permissions: "
            + ", ".join(sorted(source_permissions))
        )
    validate_application_contract(source_root, "Pilot 0 source manifest")

    merged_paths = find_main_merged_manifests(root)
    if not merged_paths:
        raise ValueError("no merged debug application manifest was produced")

    merged_permissions: set[str] = set()
    for path in merged_paths:
        merged_root = parse(path)
        validate_application_contract(
            merged_root,
            f"merged debug manifest {path.relative_to(root)}",
        )
        merged_permissions.update(declared_permissions(merged_root))

    forbidden = sorted(merged_permissions & FORBIDDEN_PERMISSIONS)
    if forbidden:
        raise ValueError(
            "Pilot 0 merged manifest contains forbidden permission(s): "
            + ", ".join(forbidden)
        )

    return {
        "scenario": "android-pilot0-manifest-v0",
        "source_permission_count": len(source_permissions),
        "merged_manifest_count": len(merged_paths),
        "merged_permissions": sorted(merged_permissions),
        "forbidden_permissions": forbidden,
        "allow_backup": False,
        "launcher_exported": True,
    }


def main(argv: list[str]) -> int:
    if len(argv) not in {2, 3}:
        print(
            "usage: check_android_pilot.py REPOSITORY_ROOT [REPORT_JSON]",
            file=sys.stderr,
        )
        return 2
    root = Path(argv[1]).resolve()
    try:
        report = validate(root)
    except ValueError as error:
        print(f"Android Pilot 0 manifest validation failed: {error}", file=sys.stderr)
        return 1

    rendered = json.dumps(report, separators=(",", ":"), sort_keys=True)
    if len(argv) == 3:
        report_path = Path(argv[2])
        report_path.parent.mkdir(parents=True, exist_ok=True)
        report_path.write_text(rendered + "\n", encoding="utf-8")
    print(
        "PASS Android Pilot 0 manifest: "
        f"{report['merged_manifest_count']} merged manifest(s), "
        f"{len(report['merged_permissions'])} non-forbidden merged permission(s)"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
