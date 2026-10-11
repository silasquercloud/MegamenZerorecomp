## Mega Man Zero Native 0.2.0-test.1

A integração Android corrige o argumento de boot rejeitado, inclui logs persistentes/exportáveis e um launcher retrato com artes locais adaptadas da referência. Importe sua ROM USA rev. 0 e BIOS original; os dumps não são distribuídos.

Teste nativo Linux chegou à fase inicial em 12.000 frames com entrada roteirizada. Isso não comprova jogabilidade ARM64 em aparelho Android. Consulte PROGRESS.md para cobertura estática, limitações e resultados atuais.

Regressão de boot (reutiliza corpus privado existente):
```bash
source tools/env-cloud.sh
cmake -S tools/native-boot-check -B /workspace/.mmz-build/native-boot-check -G Ninja -DCMAKE_BUILD_TYPE=Release
cmake --build /workspace/.mmz-build/native-boot-check --target mmz_boot_check -j4
python tools/test-native-boot.py
```

# Mega Man Zero Native — Android ARM64

Port em desenvolvimento do [MegaManZeroRecomp](https://github.com/mstan/MegaManZeroRecomp), usando o runtime [gbarecomp](https://github.com/mstan/gbarecomp). O APK de teste `0.1.0-test.1` foi compilado e validado como pacote ARM64; gameplay em dispositivo ainda não foi testado. Consulte [PROGRESS.md](PROGRESS.md) e [Releases](https://github.com/silasquercloud/MegamenZerorecomp/releases).

[Baixar APK ARM64 0.1.0-test.1](https://github.com/silasquercloud/MegamenZerorecomp/releases/download/android-v0.1.0-test.1/MegaManZeroNative-0.1.0-test.1-arm64-v8a.apk). Download público conferido e SHA-256 comparado com o build local.

## Build

Linux x86-64, Python 3.12+, Git, curl e GCC/G++. O setup instala ferramentas em `/workspace`, sem modificar fontes do upstream. Os submódulos estão fixados por commit; recomp-ui fornece o menu nativo de configurações.

```sh
bash tools/setup-cloud.sh
source tools/env-cloud.sh
python3 tools/generate.py --rom /caminho/megaman_zero_usa.gba --bios /caminho/gba_bios.bin
bash tools/build-android.sh
```

O APK será `android/app/build/outputs/apk/release/app-release.apk`, para Android 9+ ARM64, com bibliotecas alinhadas para páginas de 16 KiB. SDK 35, NDK 27.1.12297006, CMake 3.22.1, AGP 8.10.1 e Gradle 8.11.1. O wrapper e os downloads de ferramentas usam checksums oficiais e TLS verificado. A release de teste usa a chave de desenvolvimento local quando não há configuração de assinatura; mantenha uma chave estável para atualizações futuras. Licenças upstream são incluídas no APK e podem ser lidas em Créditos e versão.

Entradas necessárias para gerar o código e para importar no aplicativo:

| Arquivo | Tamanho | SHA-1 |
|---|---:|---|
| Mega Man Zero USA revisão 0 | 8.388.608 | `193b14120119162518a73c70876f0b8bffdbd96e` |
| BIOS GBA original | 16.384 | `300c20df6731a33952ded8c436f7f186d25d3492` |

ROM, BIOS, saves e C++ derivado são locais e ignorados pelo Git. O build recusa fontes ausentes e a inclusão de ROM/BIOS no APK. O jogo e a BIOS são recompilados; gaps de dispatch continuam usando o fallback de interpretação reportado pelo upstream. Não é ativado o modo de interpretação integral. A cadência, áudio, controles SDL e SRAM permanecem sob responsabilidade do runtime original.

## GitHub Actions e instalação

O workflow manual `Android ARM64` prepara ferramentas, testa o framework, gera o corpus, compila, verifica ABI/assinatura/alinhamento, publica o APK como artefato e cria uma prerelease. Requer runner Linux x64 com label `mmz-private-assets`, acesso a `/workspace`, Python 3.12+, GCC/G++, Git, curl e `gh`; provisione sua ROM/BIOS fora do checkout e configure os caminhos nas variáveis de repositório `MMZ_ROM_PATH` e `MMZ_BIOS_PATH`. Arquivos proprietários não são enviados como artefatos. O workflow ainda não foi executado neste projeto.

Após um build real, instale o APK e importe os dois arquivos pelo seletor Android. “Jogar” verifica os hashes e abre `GbaGameActivity`, que carrega SDL e o runtime nativo. Instalação, biblioteca carregando e gameplay precisam ser verificados separadamente em dispositivo. Os dados ficam no armazenamento privado do aplicativo; desinstalar apaga os saves.

A prerelease atual foi publicada pelo [job de publicação verificada](https://github.com/silasquercloud/MegamenZerorecomp/actions/runs/38124640099): o upload direto do ambiente falhou na autenticação de `uploads.github.com`, e o job verificou o APK pré-compilado e usou seu próprio token para publicar. A branch `codex/verified-apk-publication` contém somente o código, workflow e pacote público de teste; não contém ROM/BIOS. Esse job não executou uma nova recompilação do jogo. O workflow completo de build continua dependendo do runner e arquivos privados descritos acima.

## Créditos

Matthew Stanley e colaboradores: MegaManZeroRecomp, gbarecomp e recomp-ui, sob PolyForm Noncommercial 1.0.0 (licenças preservadas nos submódulos). SDL e demais componentes mantêm suas licenças e atribuições upstream. Mega Man Zero e seus arquivos originais pertencem aos respectivos titulares.
