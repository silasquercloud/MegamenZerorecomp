#!/usr/bin/env bash
set -euo pipefail
repo_dir=$(cd "$(dirname "$0")/.." && pwd)
source "$repo_dir/tools/env-cloud.sh"
if [[ ! -f "$repo_dir/android/generated/game/dispatch_table.cpp" || ! -f "$repo_dir/android/generated/bios/bios_recompiled.cpp" ]]; then
    echo 'Faltam fontes nativos do jogo/BIOS. Execute tools/generate.py --rom ARQUIVO --bios ARQUIVO.' >&2
    exit 2
fi
cd "$repo_dir/android"
./gradlew --no-daemon :app:assembleRelease -PgbaAbis=arm64-v8a -PgbaNativeJobs=4
python3 "$repo_dir/tools/verify-apk.py" app/build/outputs/apk/release/app-release.apk
"$ANDROID_HOME/build-tools/35.0.0/apksigner" verify --verbose app/build/outputs/apk/release/app-release.apk
"$ANDROID_HOME/build-tools/35.0.0/zipalign" -c -P 16 -v 4 app/build/outputs/apk/release/app-release.apk
