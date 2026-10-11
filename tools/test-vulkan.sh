#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
build_dir=${MMZ_TEST_BUILD:-/workspace/.mmz-build}
mkdir -p "$build_dir"
c++ -std=c++20 -O2 -Iandroid/native/vulkan -Igbarecomp/platform/android/third_party/SDL/include \
 -Igbarecomp/platform/android/third_party/SDL/src/video/khronos \
 tools/tests/vulkan-present.cpp android/native/vulkan/presenter.cpp \
 /usr/lib/x86_64-linux-gnu/libSDL2-2.0.so.0 /usr/lib/x86_64-linux-gnu/libvulkan.so.1 -pthread -o "$build_dir/test-vulkan"
"$build_dir/test-vulkan"
