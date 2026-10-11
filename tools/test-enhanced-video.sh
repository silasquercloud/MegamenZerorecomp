#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
source tools/env-cloud.sh
build_dir=/workspace/.mmz-build
mkdir -p "$build_dir"
sdl_library=/usr/lib/x86_64-linux-gnu/libSDL2-2.0.so.0
c++ -std=c++20 -Iandroid/native -Igbarecomp/platform/android/third_party/SDL/include \
  -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" \
  tools/tests/enhanced-video.cpp android/native/enhanced.cpp "$sdl_library" \
  -pthread -o "$build_dir/test-enhanced-video"
"$build_dir/test-enhanced-video"
