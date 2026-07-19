#!/usr/bin/env python3
"""Validate DNA v0.1 schemas and examples without network access."""
from __future__ import annotations

import json
import sys
from pathlib import Path

try:
    from jsonschema import Draft202012Validator
    from referencing import Registry, Resource
except ImportError as exc:
    raise SystemExit(
        "Missing development dependency. Run: python3 -m pip install -r requirements-dev.txt"
    ) from exc

ROOT = Path(__file__).resolve().parents[1]
SCHEMA_DIR = ROOT / "schemas" / "v0.1"
EXAMPLE_DIR = ROOT / "examples" / "v0.1"
EXAMPLE_TO_SCHEMA = {
    "travel-intent.json": "dna-intent.schema.json",
    "shopping-intent.json": "dna-intent.schema.json",
    "dna-trace.json": "dna-trace.schema.json",
    "transport-policy.json": "transport-policy.schema.json",
    "georoom.json": "georoom.schema.json",
}


def load_json(path: Path) -> dict:
    try:
        with path.open("r", encoding="utf-8") as handle:
            return json.load(handle)
    except (OSError, json.JSONDecodeError) as exc:
        raise RuntimeError(f"Cannot load {path}: {exc}") from exc


def build_registry(schemas: dict[str, dict]) -> Registry:
    registry = Registry()
    for filename, schema in schemas.items():
        resource = Resource.from_contents(schema)
        schema_id = schema.get("$id")
        if not schema_id:
            raise RuntimeError(f"Schema {filename} has no $id")
        registry = registry.with_resource(schema_id, resource)
        registry = registry.with_resource(filename, resource)
    return registry


def main() -> int:
    schema_paths = sorted(SCHEMA_DIR.glob("*.schema.json"))
    if not schema_paths:
        print("No schemas found", file=sys.stderr)
        return 1

    schemas = {path.name: load_json(path) for path in schema_paths}
    for name, schema in schemas.items():
        Draft202012Validator.check_schema(schema)
        print(f"schema ok: {name}")

    registry = build_registry(schemas)
    failures = 0
    for example_name, schema_name in EXAMPLE_TO_SCHEMA.items():
        example_path = EXAMPLE_DIR / example_name
        if not example_path.exists():
            print(f"missing example: {example_name}", file=sys.stderr)
            failures += 1
            continue
        validator = Draft202012Validator(schemas[schema_name], registry=registry)
        errors = sorted(validator.iter_errors(load_json(example_path)), key=lambda error: list(error.path))
        if errors:
            failures += 1
            print(f"invalid example: {example_name}", file=sys.stderr)
            for error in errors:
                location = "/".join(str(part) for part in error.path) or "<root>"
                print(f"  {location}: {error.message}", file=sys.stderr)
        else:
            print(f"example ok: {example_name}")

    if failures:
        print(f"Validation failed with {failures} problem(s)", file=sys.stderr)
        return 1
    print("All DNA schemas and examples are valid")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
