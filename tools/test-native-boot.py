#!/usr/bin/env python3
"""Real-ROM regression: previous Android CLI failure, then original boot frames."""
import os, pathlib, subprocess, tempfile
root=pathlib.Path(__file__).resolve().parents[1]
exe=pathlib.Path('/workspace/.mmz-build/native-boot-check/mmz_boot_check')
with tempfile.TemporaryDirectory(prefix='mmz-boot-') as d:
    old=subprocess.run([str(exe),'--no-launcher'],cwd=d,capture_output=True,text=True)
    assert old.returncode==1 and 'unknown argument --no-launcher' in old.stderr,old.stderr
    png=pathlib.Path(d)/'frame.png'
    args=[str(exe),str(root/'android/game_android.toml'),'--rom',os.environ['MMZ_ROM_PATH'],'--bios',os.environ['MMZ_BIOS_PATH'],'--frames','120','--dump-png',str(png)]
    new=subprocess.run(args,cwd=d,capture_output=True,text=True,timeout=30)
    assert new.returncode==0,new.stderr
    assert 'ppu_frames=120' in new.stdout and 'dispatch_misses=0 interpreted_insns=0' in new.stdout,new.stdout
    assert png.read_bytes().startswith(b'\x89PNG\r\n\x1a\n') and png.stat().st_size>1024
    print('PASS: old Android argument rejected; corrected native boot produces 120 real GBA frames, zero dispatch misses.')
