#!/usr/bin/env bash
set -euo pipefail
repo_dir=$(cd "$(dirname "$0")/.." && pwd)
build_dir=${MMZ_HOST_BUILD:-/workspace/.mmz-build/host}
export PATH="/workspace/.mmz-tools/bin:$PATH"
git -C "$repo_dir" submodule update --init MegaManZeroRecomp gbarecomp
git -C "$repo_dir/MegaManZeroRecomp" submodule update --init recomp-ui
git -C "$repo_dir/gbarecomp" submodule update --init --recursive external/arm-recomp-core platform/android/third_party/SDL
cmake -S "$repo_dir/gbarecomp" -B "$build_dir" -G Ninja -DCMAKE_BUILD_TYPE=Release -DGBARECOMP_COMPILER_CACHE=OFF
cmake --build "$build_dir" --target gba_recompile armv4t_tests thumb_tests ppu_smoke_tests audio_playback_tests save_config_tests touch_input_tests --parallel 4
ctest --test-dir "$build_dir" --output-on-failure --no-tests=error -R '^(armv4t_tests|thumb_tests|ppu_smoke_tests|audio_playback_tests|save_config_tests|touch_input_tests)$'
