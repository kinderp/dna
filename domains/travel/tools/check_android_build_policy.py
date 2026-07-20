#!/usr/bin/env python3
"""Validate the mixed Android/KMP Gradle plugin-classpath contract."""
from __future__ import annotations

import sys
import tomllib
from pathlib import Path

ROOT_PLUGIN_LINES = (
    "alias(libs.plugins.android.application) apply false",
    "alias(libs.plugins.compose.compiler) apply false",
    "alias(libs.plugins.kotlin.multiplatform) apply false",
    "alias(libs.plugins.kotlin.jvm) apply false",
)
APP_REQUIRED_LINES = (
    "alias(libs.plugins.android.application)",
    "alias(libs.plugins.compose.compiler)",
)
APP_FORBIDDEN_TOKENS = (
    "org.jetbrains.kotlin.android",
    "libs.plugins.kotlin.android",
    "kotlin-android",
)
FORBIDDEN_GRADLE_PROPERTIES = (
    "android.builtInKotlin",
    "android.newDsl",
)


def read(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except OSError as error:
        raise ValueError(f"cannot read {path}: {error}") from error


def validate(root: Path) -> tuple[str, str]:
    root_build = read(root / "build.gradle.kts")
    app_build = read(root / "apps/android/build.gradle.kts")
    gradle_properties = read(root / "gradle.properties")

    try:
        catalog = tomllib.loads(read(root / "gradle/libs.versions.toml"))
    except tomllib.TOMLDecodeError as error:
        raise ValueError(f"invalid version catalog: {error}") from error

    for line in ROOT_PLUGIN_LINES:
        if line not in root_build:
            raise ValueError(
                "root plugin classpath contract is incomplete; missing " + repr(line)
            )

    for line in APP_REQUIRED_LINES:
        if line not in app_build:
            raise ValueError("Android app plugin contract is incomplete; missing " + repr(line))

    for token in APP_FORBIDDEN_TOKENS:
        if token in app_build:
            raise ValueError(
                "Android app must use AGP built-in Kotlin; forbidden token " + repr(token)
            )

    for key in FORBIDDEN_GRADLE_PROPERTIES:
        if any(
            line.strip().startswith(key + "=")
            for line in gradle_properties.splitlines()
            if line.strip() and not line.lstrip().startswith("#")
        ):
            raise ValueError(
                f"temporary AGP compatibility switch {key!r} must not be enabled"
            )

    versions = catalog.get("versions")
    plugins = catalog.get("plugins")
    if not isinstance(versions, dict) or not isinstance(plugins, dict):
        raise ValueError("version catalog must contain [versions] and [plugins]")

    kotlin_version = versions.get("kotlin")
    agp_version = versions.get("agp")
    if not isinstance(kotlin_version, str) or not kotlin_version:
        raise ValueError("version catalog has no Kotlin version")
    if not isinstance(agp_version, str) or not agp_version:
        raise ValueError("version catalog has no AGP version")

    expected_plugins = {
        "android-application": ("com.android.application", "agp"),
        "compose-compiler": ("org.jetbrains.kotlin.plugin.compose", "kotlin"),
        "kotlin-multiplatform": ("org.jetbrains.kotlin.multiplatform", "kotlin"),
        "kotlin-jvm": ("org.jetbrains.kotlin.jvm", "kotlin"),
    }
    for alias, (expected_id, expected_ref) in expected_plugins.items():
        declaration = plugins.get(alias)
        if not isinstance(declaration, dict):
            raise ValueError(f"version catalog is missing plugin alias {alias!r}")
        if declaration.get("id") != expected_id or declaration.get("version", {}).get("ref") != expected_ref:
            raise ValueError(f"plugin alias {alias!r} drifted from the reviewed ID/version ref")

    return agp_version, kotlin_version


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        print("usage: check_android_build_policy.py REPOSITORY_ROOT", file=sys.stderr)
        return 2
    root = Path(argv[1]).resolve()
    try:
        agp_version, kotlin_version = validate(root)
    except ValueError as error:
        print(f"Android build policy validation failed: {error}", file=sys.stderr)
        return 1
    print(
        "PASS Android build policy: "
        f"AGP {agp_version}, Kotlin {kotlin_version}, unified root plugin classpath"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
