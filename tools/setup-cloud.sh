#!/usr/bin/env bash
set -euo pipefail
repo_dir=$(cd "$(dirname "$0")/.." && pwd)
mkdir -p /workspace/.mmz-build /workspace/.android /workspace/.gradle
if [[ ! -x ${JAVA_HOME:-/usr/lib/jvm/default-java}/bin/javac && ! -x /workspace/.mmz-jdk/jdk-17.0.20.1+1/bin/javac ]]; then
    jdk_archive=$(mktemp /tmp/mmz-jdk.XXXXXX.tar.gz)
    curl -fsSL --retry 2 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.20.1%2B1/OpenJDK17U-jdk_x64_linux_hotspot_17.0.20.1_1.tar.gz' -o "$jdk_archive"
    python3 - "$jdk_archive" <<'PY'
import hashlib, pathlib, sys, tarfile
archive = pathlib.Path(sys.argv[1])
with archive.open('rb') as f:
    assert hashlib.file_digest(f, 'sha256').hexdigest() == '3808d1d15e3ec6bd5b84057fb5d84c33d8a1536a258146bcea2e603fc726e08e', 'JDK checksum mismatch'
destination = pathlib.Path('/workspace/.mmz-jdk')
destination.mkdir(exist_ok=True)
with tarfile.open(archive) as t:
    t.extractall(destination, filter='data')
archive.unlink()
PY
fi
source "$repo_dir/tools/env-cloud.sh"
if [[ ! -x /workspace/.mmz-tools/bin/cmake ]]; then
    python3 -m venv /workspace/.mmz-tools
    /workspace/.mmz-tools/bin/pip install --no-cache-dir 'cmake==3.31.6' 'ninja==1.11.1.4'
fi
if [[ ! -x "$ANDROID_HOME/cmdline-tools/19.0/bin/sdkmanager" ]]; then
    tools_archive=$(mktemp /tmp/mmz-sdk.XXXXXX.zip)
    curl -fsSL --retry 2 https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip -o "$tools_archive"
    python3 - "$tools_archive" "$ANDROID_HOME" <<'PY'
import hashlib, os, pathlib, sys, zipfile
archive, sdk = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
assert hashlib.sha1(archive.read_bytes()).hexdigest() == '5fdcc763663eefb86a5b8879697aa6088b041e70', 'SDK checksum mismatch'
destination = sdk / 'cmdline-tools'
destination.mkdir(parents=True, exist_ok=True)
with zipfile.ZipFile(archive) as z:
    z.extractall(destination)
(destination / 'cmdline-tools').rename(destination / '19.0')
for name in ('sdkmanager', 'avdmanager', 'apkanalyzer'):
    (destination / '19.0/bin' / name).chmod(0o755)
archive.unlink()
PY
fi
python3 - <<'PY'
import os, pathlib, subprocess
from urllib.parse import urlsplit
gradle = pathlib.Path(os.environ['GRADLE_USER_HOME'])
# Only transport configuration; authentication stays in the platform proxy.
proxy = urlsplit(os.environ.get('HTTPS_PROXY', ''))
if proxy.hostname and not proxy.username and not proxy.password:
    props = gradle / 'gradle.properties'
    old = props.read_text() if props.exists() else ''
    if 'systemProp.https.proxyHost=' not in old:
        lines = [f'systemProp.{s}.proxyHost={proxy.hostname}\nsystemProp.{s}.proxyPort={proxy.port or 80}' for s in ('http', 'https')]
        props.write_text(old + '\n' + '\n'.join(lines) + '\n')
sdk = os.environ['ANDROID_HOME']
subprocess.run([sdk + '/cmdline-tools/19.0/bin/sdkmanager', '--sdk_root=' + sdk,
    'platform-tools', 'platforms;android-35', 'build-tools;35.0.0',
    'ndk;27.1.12297006', 'cmake;3.22.1'], input='y\n' * 20, text=True, check=True)
PY
bash "$repo_dir/tools/check-host.sh"
