# Evidência de execução nativa Linux

As imagens são framebuffers GBA REAIS produzidos pelo executável `mmz_boot_check`, com o mesmo C++ gerado do jogo/BIOS usado no APK. Não são screenshots Android, nem mockups do launcher.

- 120 frames: boot até CAPCOM, `cpu_backend=static-recompiled`, `bios_backend=LLE (recompiled BIOS)`, `dispatch_misses=0 interpreted_insns=0`.
- 12.000 frames: `GBARECOMP_DEMO_INPUT=campaign-safe`, entradas no KEYINPUT original, fase inicial com Zero visível. `dispatch_misses=12 interpreted_insns=78`, sem self-heal/recompilação automática. O fallback preexistente cobriu esses misses. Sem acesso de memória não mapeado ou IO não tratado.

Não comprova áudio físico, controles touch, gamepad, desempenho, salvar/reabrir ou gameplay em ARM64 Android. Hashes dos dumps privados são validados antes do teste e dumps não estão versionados.
