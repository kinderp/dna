#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="${TMPDIR:-/tmp}/dna-reference-test"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

mapfile -t MAIN_SOURCES < <(find "$ROOT/reference/kotlin/src/main/kotlin" -name '*.kt' -type f | sort)
mapfile -t TEST_SOURCES < <(find "$ROOT/reference/kotlin/src/test/kotlin" -name '*.kt' -type f | sort)

kotlinc "${MAIN_SOURCES[@]}" "${TEST_SOURCES[@]}" -include-runtime -d "$BUILD_DIR/tests.jar"
java -cp "$BUILD_DIR/tests.jar" dna.ReferenceTestsKt
python3 "$ROOT/scripts/validate-schemas.py"
