#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="${TMPDIR:-/tmp}/dna-reference-demo"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

mapfile -t SOURCES < <(find "$ROOT/reference/kotlin/src/main/kotlin" -name '*.kt' -type f | sort)
kotlinc "${SOURCES[@]}" -include-runtime -d "$BUILD_DIR/demo.jar"
java -cp "$BUILD_DIR/demo.jar" dna.demo.DemoKt
