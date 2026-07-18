#!/usr/bin/env python3
"""Verify the committed Gradle Wrapper against TDNA's reviewed policy."""

from __future__ import annotations

import hashlib
import json
import os
import stat
import sys
import zipfile
from dataclasses import dataclass
from pathlib import Path
from urllib.parse import urlparse

EXPECTED_SCHEMA_VERSION = 1
EXPECTED_PROPERTY_KEYS = {
    "distributionBase",
    "distributionPath",
    "distributionSha256Sum",
    "distributionUrl",
    "networkTimeout",
    "retries",
    "retryBackOffMs",
    "validateDistributionUrl",
    "zipStoreBase",
    "zipStorePath",
}
REQUIRED_JAR_ENTRIES = {
    "META-INF/MANIFEST.MF",
    "org/gradle/wrapper/GradleWrapperMain.class",
}


@dataclass(frozen=True)
class WrapperReport:
    gradle_version: str
    distribution_url: str
    distribution_sha256: str
    wrapper_jar_sha256: str
    gradlew_sha256: str
    gradlew_bat_sha256: str
    properties_sha256: str


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def parse_properties(path: Path) -> dict[str, str]:
    properties: dict[str, str] = {}
    for line_number, raw in enumerate(path.read_text(encoding="utf-8").splitlines(), start=1):
        line = raw.strip()
        if not line or line.startswith("#") or line.startswith("!"):
            continue
        if "=" not in line:
            raise ValueError(f"{path}:{line_number}: expected key=value")
        key, value = line.split("=", 1)
        key = key.strip()
        value = value.strip()
        if not key or key in properties:
            raise ValueError(f"{path}:{line_number}: blank or duplicate property {key!r}")
        properties[key] = value
    return properties


def normalized_distribution_url(value: str) -> str:
    return value.replace(r"\:", ":")


def validate_jar(path: Path) -> None:
    with zipfile.ZipFile(path) as archive:
        names = set(archive.namelist())
        missing = REQUIRED_JAR_ENTRIES - names
        if missing:
            raise ValueError(f"wrapper JAR is missing entries: {sorted(missing)}")
        for name in names:
            candidate = Path(name)
            if candidate.is_absolute() or ".." in candidate.parts:
                raise ValueError(f"wrapper JAR contains unsafe path: {name}")
        manifest = archive.read("META-INF/MANIFEST.MF").decode("utf-8", errors="strict")
        if "Main-Class: org.gradle.wrapper.GradleWrapperMain" not in manifest:
            raise ValueError("wrapper JAR manifest has an unexpected main class")
        if "SPDX-License-Identifier: Apache-2.0" not in manifest:
            raise ValueError("wrapper JAR manifest is missing its Apache-2.0 SPDX identifier")


def validate_wrapper(root: Path) -> WrapperReport:
    policy_path = root / "gradle/wrapper/tdna-wrapper-policy.json"
    properties_path = root / "gradle/wrapper/gradle-wrapper.properties"
    jar_path = root / "gradle/wrapper/gradle-wrapper.jar"
    gradlew_path = root / "gradlew"
    gradlew_bat_path = root / "gradlew.bat"

    required = [policy_path, properties_path, jar_path, gradlew_path, gradlew_bat_path]
    missing = [str(path.relative_to(root)) for path in required if not path.is_file()]
    if missing:
        raise ValueError(f"missing Gradle Wrapper file(s): {missing}")

    policy = json.loads(policy_path.read_text(encoding="utf-8"))
    if policy.get("schemaVersion") != EXPECTED_SCHEMA_VERSION:
        raise ValueError("unsupported wrapper policy schema")

    properties = parse_properties(properties_path)
    if set(properties) != EXPECTED_PROPERTY_KEYS:
        missing_keys = sorted(EXPECTED_PROPERTY_KEYS - set(properties))
        extra_keys = sorted(set(properties) - EXPECTED_PROPERTY_KEYS)
        raise ValueError(
            f"wrapper properties keys drifted; missing={missing_keys}, extra={extra_keys}"
        )

    distribution_url = normalized_distribution_url(properties["distributionUrl"])
    parsed_url = urlparse(distribution_url)
    if parsed_url.scheme != "https" or parsed_url.netloc != "services.gradle.org":
        raise ValueError("wrapper distribution URL must use https://services.gradle.org")

    gradle_version = str(policy["gradleVersion"])
    expected_url = f"https://services.gradle.org/distributions/gradle-{gradle_version}-bin.zip"
    if distribution_url != policy["distributionUrl"] or distribution_url != expected_url:
        raise ValueError("wrapper distribution URL differs from the reviewed policy")
    if properties["distributionSha256Sum"] != policy["distributionSha256"]:
        raise ValueError("distribution SHA-256 differs from the reviewed policy")
    if properties["validateDistributionUrl"] != "true":
        raise ValueError("validateDistributionUrl must remain true")
    if properties["networkTimeout"] != "10000":
        raise ValueError("networkTimeout differs from the reviewed value")
    if properties["retries"] != "0" or properties["retryBackOffMs"] != "500":
        raise ValueError("wrapper retry policy differs from the reviewed values")

    hashes = {
        "wrapperJarSha256": sha256(jar_path),
        "gradlewSha256": sha256(gradlew_path),
        "gradlewBatSha256": sha256(gradlew_bat_path),
        "propertiesSha256": sha256(properties_path),
    }
    for key, actual in hashes.items():
        expected = policy.get(key)
        if actual != expected:
            raise ValueError(f"{key} drifted: expected {expected}, got {actual}")

    if os.name != "nt":
        mode = gradlew_path.stat().st_mode
        if not mode & (stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH):
            raise ValueError("gradlew is not executable")

    wrapper_jars = sorted(root.rglob("gradle-wrapper.jar"))
    if wrapper_jars != [jar_path]:
        rendered = [str(path.relative_to(root)) for path in wrapper_jars]
        raise ValueError(f"expected exactly one wrapper JAR, found {rendered}")

    validate_jar(jar_path)

    return WrapperReport(
        gradle_version=gradle_version,
        distribution_url=distribution_url,
        distribution_sha256=properties["distributionSha256Sum"],
        wrapper_jar_sha256=hashes["wrapperJarSha256"],
        gradlew_sha256=hashes["gradlewSha256"],
        gradlew_bat_sha256=hashes["gradlewBatSha256"],
        properties_sha256=hashes["propertiesSha256"],
    )


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        print("usage: check_gradle_wrapper.py REPOSITORY_ROOT", file=sys.stderr)
        return 2
    root = Path(argv[1]).resolve()
    try:
        report = validate_wrapper(root)
    except (OSError, ValueError, KeyError, json.JSONDecodeError, zipfile.BadZipFile) as error:
        print(f"Gradle Wrapper validation failed: {error}", file=sys.stderr)
        return 1
    print(
        "PASS Gradle Wrapper: "
        f"Gradle {report.gradle_version}, JAR {report.wrapper_jar_sha256}"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
