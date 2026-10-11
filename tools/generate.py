#!/usr/bin/env python3
"""Generate private game/BIOS code only from verified user-owned inputs."""
import argparse
import hashlib
import os
from pathlib import Path
import subprocess

ROOT = Path(__file__).resolve().parents[1]

def verified(path, size, sha1, label):
    path = Path(path).resolve()
    if not path.is_file() or path.stat().st_size != size:
        raise SystemExit(f'{label}: arquivo ausente ou tamanho incorreto ({size} bytes).')
    with path.open('rb') as stream:
        digest = hashlib.file_digest(stream, 'sha1').hexdigest()
    if digest != sha1:
        raise SystemExit(f'{label}: SHA-1 incorreto; esta versão não é compatível.')
    return path

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--rom', required=True)
    parser.add_argument('--bios', required=True)
    parser.add_argument('--tool', default=os.environ.get('GBARECOMP_TOOL', '/workspace/.mmz-build/host/gba_recompile'))
    args = parser.parse_args()
    rom = verified(args.rom, 8388608, '193b14120119162518a73c70876f0b8bffdbd96e', 'ROM')
    bios = verified(args.bios, 16384, '300c20df6731a33952ded8c436f7f186d25d3492', 'BIOS')
    tool = Path(args.tool).resolve()
    if not tool.is_file():
        raise SystemExit('Recompiler ausente: execute tools/check-host.sh primeiro.')
    subprocess.run([str(tool), '--rom', str(rom), '--config', str(ROOT / 'MegaManZeroRecomp/game.toml'), '--out', str(ROOT / 'android/generated/game'), '--max-functions', '65536'], check=True)
    subprocess.run([str(tool), '--bios', str(bios), '--config', str(ROOT / 'gbarecomp/bios/gba_bios.toml'), '--out', str(ROOT / 'android/generated/bios')], check=True)
    print('Fontes gerados localmente. Não versione nem publique esses fontes ou arquivos originais.')

if __name__ == '__main__':
    main()
