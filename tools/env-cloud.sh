#!/usr/bin/env bash
export ANDROID_HOME=${ANDROID_HOME:-/workspace/android-sdk}
export ANDROID_USER_HOME=${ANDROID_USER_HOME:-/workspace/.android}
export GRADLE_USER_HOME=${GRADLE_USER_HOME:-/workspace/.gradle}
export MMZ_ROM_PATH=${MMZ_ROM_PATH:-/workspace/private-assets/megaman_zero_usa.gba}
export MMZ_BIOS_PATH=${MMZ_BIOS_PATH:-/workspace/private-assets/gba_bios.bin}
if [[ -z ${JAVA_HOME:-} && -x /workspace/.mmz-jdk/jdk-17.0.20.1+1/bin/javac ]]; then
    export JAVA_HOME=/workspace/.mmz-jdk/jdk-17.0.20.1+1
fi
export PATH="${JAVA_HOME:+$JAVA_HOME/bin:}/workspace/.mmz-tools/bin:$ANDROID_HOME/platform-tools:$PATH"
# Retain TLS verification through the machine's platform-managed trust store.
if [[ -f /etc/ssl/certs/java/cacerts && ${JAVA_TOOL_OPTIONS:-} != *javax.net.ssl.trustStore* ]]; then
    export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts"
fi
# Keep emulator registration/authentication files inside the writable workspace.
export ANDROID_AVD_HOME="${ANDROID_AVD_HOME:-/workspace/.android/avd}"
export ANDROID_EMULATOR_HOME="${ANDROID_EMULATOR_HOME:-/workspace/.android}"
