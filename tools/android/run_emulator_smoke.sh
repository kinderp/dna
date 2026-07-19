#!/usr/bin/env bash
set -euo pipefail

ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/../.." && pwd)
OUTPUT_DIR="$ROOT/build/android-emulator"
API_LEVEL=${TDNA_EMULATOR_API_LEVEL:-35}
ABI=${TDNA_EMULATOR_ABI:-x86_64}
DEVICE_PROFILE=${TDNA_EMULATOR_DEVICE_PROFILE:-pixel_2}
AVD_NAME=${TDNA_EMULATOR_AVD_NAME:-tdna_pilot0_api${API_LEVEL}}
BOOT_TIMEOUT_SECONDS=${TDNA_EMULATOR_BOOT_TIMEOUT_SECONDS:-360}
SYSTEM_IMAGE="system-images;android-${API_LEVEL};default;${ABI}"
APP_ID="org.traveldna.android"
SUBSTANTIVE_SHA=${TDNA_SUBSTANTIVE_SHA:-${GITHUB_SHA:-local}}

: "${ANDROID_HOME:?ANDROID_HOME must point to an Android SDK}"

SDKMANAGER="$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager"
AVDMANAGER="$ANDROID_HOME/cmdline-tools/latest/bin/avdmanager"
EMULATOR="$ANDROID_HOME/emulator/emulator"
ADB="$ANDROID_HOME/platform-tools/adb"

for executable in "$SDKMANAGER" "$AVDMANAGER" "$ADB"; do
    if [[ ! -x "$executable" ]]; then
        printf 'ERROR: required Android SDK tool is missing: %s\n' "$executable" >&2
        exit 1
    fi
done
if ! command -v timeout >/dev/null 2>&1; then
    printf 'ERROR: required command timeout is missing\n' >&2
    exit 1
fi

rm -rf "$OUTPUT_DIR"
mkdir -p "$OUTPUT_DIR"
AVD_HOME="$OUTPUT_DIR/avd"
AVD_PATH="$AVD_HOME/$AVD_NAME.avd"
mkdir -p "$AVD_HOME"
export ANDROID_AVD_HOME="$AVD_HOME"
EMULATOR_PID=""

capture_diagnostics() {
    set +e
    timeout 10s "$ADB" devices -l > "$OUTPUT_DIR/adb-devices.txt" 2>&1
    timeout 10s "$ADB" logcat -d -v threadtime > "$OUTPUT_DIR/logcat.txt" 2>&1
    timeout 10s "$ADB" shell getprop > "$OUTPUT_DIR/device-properties.txt" 2>&1
    timeout 10s "$ADB" shell screencap -p \
        /sdcard/tdna-emulator-failure.png >/dev/null 2>&1
    timeout 10s "$ADB" pull /sdcard/tdna-emulator-failure.png \
        "$OUTPUT_DIR/failure-screen.png" >/dev/null 2>&1
}

cleanup() {
    set +e
    timeout 10s "$ADB" emu kill >/dev/null 2>&1
    if [[ -n "$EMULATOR_PID" ]] && kill -0 "$EMULATOR_PID" 2>/dev/null; then
        kill "$EMULATOR_PID" >/dev/null 2>&1
        for _ in {1..10}; do
            kill -0 "$EMULATOR_PID" 2>/dev/null || break
            sleep 1
        done
        kill -9 "$EMULATOR_PID" >/dev/null 2>&1
    fi
    if [[ -n "$EMULATOR_PID" ]]; then
        wait "$EMULATOR_PID" >/dev/null 2>&1
    fi
}

on_exit() {
    status=$?
    trap - EXIT
    if [[ $status -ne 0 ]]; then
        capture_diagnostics
    fi
    cleanup
    exit "$status"
}
trap on_exit EXIT

printf 'Installing emulator package %s\n' "$SYSTEM_IMAGE"
timeout 900s "$SDKMANAGER" --install \
    "platform-tools" "emulator" "$SYSTEM_IMAGE"

if [[ ! -x "$EMULATOR" ]]; then
    printf 'ERROR: emulator binary is missing after SDK installation: %s\n' \
        "$EMULATOR" >&2
    exit 1
fi

printf 'Creating AVD %s under %s\n' "$AVD_NAME" "$AVD_HOME"
printf 'no\n' | timeout 60s "$AVDMANAGER" create avd \
    --force \
    --name "$AVD_NAME" \
    --path "$AVD_PATH" \
    --package "$SYSTEM_IMAGE" \
    --device "$DEVICE_PROFILE" \
    > "$OUTPUT_DIR/avdmanager-create.log" 2>&1

"$EMULATOR" -list-avds > "$OUTPUT_DIR/avd-list.txt"
if ! grep -Fx "$AVD_NAME" "$OUTPUT_DIR/avd-list.txt" >/dev/null; then
    printf 'ERROR: created AVD is not visible to the emulator: %s\n' \
        "$AVD_NAME" >&2
    exit 1
fi

if [[ -e /dev/kvm ]]; then
    sudo chmod 666 /dev/kvm
fi

"$ADB" kill-server
"$ADB" start-server

printf 'Starting AVD %s\n' "$AVD_NAME"
"$EMULATOR" -avd "$AVD_NAME" \
    -no-window \
    -no-audio \
    -no-boot-anim \
    -no-snapshot \
    -wipe-data \
    -camera-back none \
    -camera-front none \
    -gpu swiftshader_indirect \
    > "$OUTPUT_DIR/emulator.log" 2>&1 &
EMULATOR_PID=$!

boot_deadline=$((SECONDS + BOOT_TIMEOUT_SECONDS))
while [[ "$("$ADB" get-state 2>/dev/null || true)" != "device" ]]; do
    if ! kill -0 "$EMULATOR_PID" 2>/dev/null; then
        printf 'ERROR: emulator process exited before registering with adb\n' >&2
        exit 1
    fi
    if (( SECONDS >= boot_deadline )); then
        printf 'ERROR: emulator did not register with adb within %s seconds\n' \
            "$BOOT_TIMEOUT_SECONDS" >&2
        exit 1
    fi
    sleep 2
done

while [[ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" != "1" ]]; do
    if ! kill -0 "$EMULATOR_PID" 2>/dev/null; then
        printf 'ERROR: emulator process exited before boot completed\n' >&2
        exit 1
    fi
    if (( SECONDS >= boot_deadline )); then
        printf 'ERROR: emulator did not boot within %s seconds\n' \
            "$BOOT_TIMEOUT_SECONDS" >&2
        exit 1
    fi
    sleep 2
done

"$ADB" shell input keyevent 82 >/dev/null
"$ADB" shell settings put global window_animation_scale 0
"$ADB" shell settings put global transition_animation_scale 0
"$ADB" shell settings put global animator_duration_scale 0

cd "$ROOT"
./gradlew --no-daemon --stacktrace \
    :apps:android:assembleDebug \
    :apps:android:assembleDebugAndroidTest \
    :apps:android:connectedDebugAndroidTest

APP_APK=$(find "$ROOT/apps/android/build/outputs/apk/debug" \
    -type f -name '*.apk' | sort | sed -n '1p')
test -n "$APP_APK"

"$ADB" install -r "$APP_APK" > "$OUTPUT_DIR/adb-install.txt"
grep -Fx 'Success' "$OUTPUT_DIR/adb-install.txt" >/dev/null
"$ADB" shell am force-stop "$APP_ID"
"$ADB" shell am start -W -n "$APP_ID/.MainActivity" \
    > "$OUTPUT_DIR/activity-start.txt"
grep -F 'Status: ok' "$OUTPUT_DIR/activity-start.txt" >/dev/null
sleep 2
"$ADB" shell screencap -p /sdcard/tdna-pilot0.png
"$ADB" pull /sdcard/tdna-pilot0.png \
    "$OUTPUT_DIR/pilot0-screen.png" >/dev/null
"$ADB" shell pm path "$APP_ID" > "$OUTPUT_DIR/package-path.txt"
grep -F 'package:' "$OUTPUT_DIR/package-path.txt" >/dev/null
sha256sum "$APP_APK" > "$OUTPUT_DIR/app-apk-sha256.txt"

MODEL=$("$ADB" shell getprop ro.product.model | tr -d '\r')
DEVICE_API=$("$ADB" shell getprop ro.build.version.sdk | tr -d '\r')
SERIAL=$("$ADB" get-serialno | tr -d '\r')

python3 - "$OUTPUT_DIR/emulator-smoke.json" \
    "$SUBSTANTIVE_SHA" "$API_LEVEL" "$DEVICE_API" "$ABI" "$MODEL" "$SERIAL" <<'PY'
import json
import sys
from pathlib import Path

path, head, requested_api, device_api, abi, model, serial = sys.argv[1:]
report = {
    "scenario": "android-pilot0-emulator-smoke-v0",
    "head_sha": head,
    "requested_api": int(requested_api),
    "device_api": int(device_api),
    "abi": abi,
    "model": model,
    "serial": serial,
    "instrumentation_task": "connectedDebugAndroidTest",
    "app_installed": True,
    "activity_started": True,
    "package_visible": True,
    "sensitive_permissions_requested": False,
    "road_evidence": False,
}
Path(path).write_text(
    json.dumps(report, separators=(",", ":"), sort_keys=True) + "\n",
    encoding="utf-8",
)
print(json.dumps(report, separators=(",", ":"), sort_keys=True))
PY

printf 'PASS Android emulator smoke evidence written under %s\n' "$OUTPUT_DIR"
