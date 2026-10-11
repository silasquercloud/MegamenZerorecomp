#!/usr/bin/env python3
"""Check packaged ABI, exported native entry and absence of private payloads."""
import argparse
import struct
import zipfile

def verify(path):
    with zipfile.ZipFile(path) as apk:
        names = apk.namelist()
        native = [n for n in names if n.startswith('lib/') and n.endswith('.so')]
        expected = {'lib/arm64-v8a/libmain.so', 'lib/arm64-v8a/libSDL2.so', 'lib/arm64-v8a/libc++_shared.so'}
        if not expected.issubset(native) or any(n.split('/')[1] != 'arm64-v8a' for n in native):
            raise ValueError('APK deve conter main, SDL2 e libc++ exclusivamente ARM64.')
        for name in native:
            data = apk.read(name)
            if data[:6] != b'\x7fELF\x02\x01' or struct.unpack_from('<H', data, 18)[0] != 183:
                raise ValueError(f'Biblioteca não é ELF64 AArch64: {name}')
            phoff = struct.unpack_from('<Q', data, 32)[0]
            phsize, phcount = struct.unpack_from('<HH', data, 54)
            for index in range(phcount):
                header = phoff + index * phsize
                if struct.unpack_from('<I', data, header)[0] == 1:  # PT_LOAD
                    offset, address = struct.unpack_from('<QQ', data, header + 8)
                    alignment = struct.unpack_from('<Q', data, header + 48)[0]
                    if alignment < 16384 or offset % 16384 != address % 16384:
                        raise ValueError(f'Segmento ELF incompatível com páginas de 16 KiB: {name}')
        # Inspect every ZIP member, including oddly named files, for known dumps.
        import hashlib
        prohibited = {'193b14120119162518a73c70876f0b8bffdbd96e', '300c20df6731a33952ded8c436f7f186d25d3492'}
        for name in names:
            if name.lower().endswith(('.gba', '.sav', '.bios')) or name.startswith(('assets/payload/roms/', 'assets/payload/bios/')):
                raise ValueError(f'Arquivo privado dentro do APK: {name}')
            if hashlib.sha1(apk.read(name)).hexdigest() in prohibited:
                raise ValueError(f'Dump proprietário dentro do APK: {name}')
        if b'SDL_main' not in apk.read('lib/arm64-v8a/libmain.so'):
            raise ValueError('Entrada SDL_main ausente.')
    print('Estrutura APK verificada: ARM64, ELF 16 KiB, entrada SDL e sem ROM/BIOS. Instalação e gameplay precisam de dispositivo.')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('apk')
    verify(parser.parse_args().apk)
