# Progresso técnico

## Fontes e integração

- O HEAD original `3f4223a` continha gitlinks sem `.gitmodules`; os commits de MegaManZeroRecomp apontados não existem na origem fornecida. Restaurado o mapeamento e fixado o commit público `218adfddf3572a6eaf7ffbec858d95c71bf53935`, com créditos/licença preservados.
- gbarecomp mantido em `5e89943528aee7bc1c399bdbd035364a4b41dd10`; arm-recomp-core, SDL e recomp-ui inicializados nas revisões fixadas.
- Implementados projeto Android, biblioteca `main`, entrada `SDL_main`, preparação mobile do upstream e thread com sua pilha de 256 MiB. O entry chama o main real de MegaManZeroRecomp, incluindo seus hooks nativos.
- Launcher com tema preto/vermelho/ciano, importação SAF, hashes e erros em português, acesso ao runtime e diagnóstico. As páginas gráficas/controles/desempenho explicam os recursos do runtime; não há validação de interação em dispositivo. Ícones dinâmicos de equipamento e overlay tematizado ainda pendentes; controles atuais são os do upstream.
- Configuração fiel 240×160, BIOS LLE e SRAM persistente. Sem opção de empacotar ROM/BIOS, sem modo integral de interpretação forçado, sem hacks de FPS.

## Evidências nesta máquina

| Verificação | Resultado |
|---|---|
| ROM enviada | 8 MiB e SHA-1 exato confirmado |
| BIOS enviada | 16 KiB e SHA-1 exato confirmado; 770 funções BIOS recompiladas |
| Recompiler host | Compilado; gerou 16 shards, dispatch e símbolos do jogo |
| CPU ARM/Thumb, PPU, áudio, save config, toque | 6 executáveis de teste passaram, 0 falhas |
| C++ gerado para ARM64 | 18 unidades passaram pela verificação do compilador NDK; não é link do jogo |
| Runtime ARM64 + SDL + UI/mods | `libmain.so` linkada com jogo e BIOS nativos, sem símbolos indefinidos |
| Entrada SDL/JNI | `SDL_main`, `setSafeInsets`, `setDisplayDpi` e tabelas de dispatch do jogo/BIOS presentes no ELF; carregamento não testado em dispositivo |
| Gradle + launcher/Java/recursos/manifest | `projects` e `compileReleaseJavaWithJavac` passaram |
| Setup repetido | Ferramentas e testes reinstanciados sem alterar fontes upstream |
| APK compilado/assinado/alinhado | Passou: ARM64, assinatura v2, ELF/ZIP 16 KiB e nenhum dump ROM/BIOS no pacote |
| Instalável / JNI carregando / runtime inicializando | Não testado; ADB sem dispositivos conectados |
| ROM/BIOS reconhecidas no Android / gameplay / controles / áudio / saves | Não testados em dispositivo |
| Actions / release / download | Job de verificação/publicação passou; prerelease pública e download HTTPS conferidos com SHA-256 idêntico ao build local |

Java inicialmente disponível era somente JRE; instalado JDK completo 17.0.20.1+1 com SHA-256 oficial. Wrapper do exemplo Android era de outra versão; substituído pelo wrapper/scripts oficiais 8.11.1 e verificado seu checksum. Proxy Java configurado sem expor credenciais e sem desativar TLS.

## APK e limitações

APK `0.1.0-test.1`, versionCode 1, Android 9+ ARM64: 6.704.324 bytes. SHA-256 `37fdc85734ee7a9481475f18e1f71509f4deec28df2d28a9f7ac284b42565181`. O build usa a chave de desenvolvimento local; chave privada e dumps não são versionados nem publicados.

O primeiro link completo identificou a ausência do provedor ImGui/modelo de configurações; corrigido pelo helper oficial `recomp_target_launcher_ui()` em biblioteca separada, reutilizando o corpus compilado. NDK configurado com `ANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON` para todas as bibliotecas. A compilação final e verificações de pacote passaram.

Ainda faltam validação de instalação, boot e gameplay em dispositivo, controles/áudio/saves/pausa reais e overlay de armas tematizado. A consulta de runners GitHub retorna 403; o workflow self-hosted não foi executado. A prerelease deve descrever essas limitações, sem tratar o build como prova de gameplay.

## Publicação concluída

- [Release android-v0.1.0-test.1](https://github.com/silasquercloud/MegamenZerorecomp/releases/tag/android-v0.1.0-test.1).
- [APK ARM64](https://github.com/silasquercloud/MegamenZerorecomp/releases/download/android-v0.1.0-test.1/MegaManZeroNative-0.1.0-test.1-arm64-v8a.apk).
- [GitHub Actions 38124640099](https://github.com/silasquercloud/MegamenZerorecomp/actions/runs/38124640099): checksum, ABI/ELF, ausência de dumps, assinatura, zipalign, artefato e publicação concluídos. Job recebeu o APK já compilado; não reexecutou o build nativo.
- Upload direto falhou com HTTP 401 no host de assets, apesar de API e Git funcionarem. Publicação realizada por workflow na branch separada `codex/verified-apk-publication`, usando o token do job, sem solicitar credenciais novas.
- APK público baixado via HTTPS e hash conferido: `37fdc85734ee7a9481475f18e1f71509f4deec28df2d28a9f7ac284b42565181`.
- Entradas do usuário preservadas somente no ambiente privado em `/workspace/private-assets`; `tools/env-cloud.sh` ativa os caminhos padrão para futuras regenerações.

## 0.2.0-test.1 — boot e launcher retrato

- Causa reproduzida: `mobile_prepare_process` acrescenta `--no-launcher`, opção do preboot de launcher desktop. A integração Android chama diretamente `run_game`, cujo parser retorna `unknown argument --no-launcher` (exit 1). SDLMain finaliza a Activity ao retornar. A entrada Android agora remove apenas esse argumento, preservando configuração, hooks e dispatch originais.
- Diagnóstico persistente Java (clique, hashes, libs, JNI, exceções/stack) e nativo (thread, SDL, CPU/bus, renderer, primeiro framebuffer, retorno e sinais com handler Android preservado). Logs visualizáveis e exportáveis via SAF. Falhas de validação, Activity e retorno pré-frame oferecem acesso aos logs. Crash nativo exige reabrir o launcher para ler o registro.
- MMZ_BOOT_10 permanece reservado para gameplay interativo confirmado. Intro/frame isolado não gera confirmação falsa de gameplay. Isso não impede o loop original; evita tratar CAPCOM como prova de jogo controlável.
- Launcher: retrato, cabeçalho em camadas (cena cinza, Zero transparente, logo vetorial, Z-Saber ciano), arte local nos painéis, molduras angulares vermelhas/ciano, nove destinos funcionais e rodapé. Não há screenshot inteira usada como tela ou botão. Arte adaptada por geração local a partir da referência fornecida; aviso de propriedade e licença da tipografia incluídos. Filtro linear e volume são passados ao runtime; visibilidade touch grava config.ini preservando demais seções.
- Teste real Linux do MESMO corpus: antigo argumento rejeitado com exit 1; boot corrigido executou BIOS LLE + ROM e gerou framebuffer CAPCOM em 120 frames, zero dispatch misses/instruções interpretadas. 12.000 frames com entrada roteirizada chegaram à fase inicial com Zero visível. Nesse percurso houve 12 misses/78 instruções no fallback upstream; não é cobertura 100% estática. Nenhum emulador convencional substituiu a arquitetura.
- Screenshots desses testes são framebuffers GBA gerados no Linux, NÃO capturas do APK Android. Android SDL/superfície, áudio real, touch/gamepad e save/reload no aparelho ainda precisam de validação.
- Seis testes existentes passaram (ARM, Thumb, PPU, áudio, configuração de save, toque). APK ARM64 compilou com C++/JNI e tabelas jogo/BIOS, assinatura de desenvolvimento, alinhamento ELF/ZIP 16 KiB, recursos gráficos presentes e sem dumps ROM/BIOS. Recursos públicos incluem vetores e duas ilustrações WebP locais.

### Publicação 0.2.0-test.1 confirmada

- Fonte compilada: `bff8c5272ef1f5590681a85cd465c7eb9ecbccfa` (`codex/android-arm64`).
- APK: 7.202.880 bytes; SHA-256 `7cad474c19fa376e5d1ac87a4a192b302244783a02d839e0f80e6359c2eaf710`.
- GitHub Actions: https://github.com/silasquercloud/MegamenZerorecomp/actions/runs/38127501507 — sucesso. O job verificou checksum, ELF ARM64/16 KiB, assinatura e zipalign, depois publicou o APK pré-compilado. Não representa um rebuild público do corpus privado.
- Prerelease pública: https://github.com/silasquercloud/MegamenZerorecomp/releases/tag/android-v0.2.0-test.1
- APK HTTPS: https://github.com/silasquercloud/MegamenZerorecomp/releases/download/android-v0.2.0-test.1/MegaManZeroNative-0.2.0-test.1-arm64-v8a.apk
- Download público realizado novamente, SHA-256 idêntico ao APK local. Avisos de artes/fonte presentes no APK final. Mesma chave de teste de 0.1 para permitir atualização preservando arquivos privados.
- Tentativa Android adicional: SDK Emulator e imagem Google APIs 30 x86_64 instalados, emulador iniciado sem KVM com `-accel off`, ADB conectado. A imagem anuncia ARM64 e `libndk_translation.so`. Porém o boot do sistema não disponibilizou o serviço `package`; instalação retornou `Can't find service: package`. Após mais de 15 minutos de inicialização por software, não houve `sys.boot_completed=1`. Portanto não foram validados launcher renderizado/toques, superfície Android, áudio físico, gameplay Android ou save/reload. Não há screenshot do APK apresentada como aprovada. Framebuffers em validation/ são Linux e estão identificados.

## Enhanced 0.2.1-test.1

- Usuário confirmou introdução real da 0.2.0 no Android ARM64; lentidão ocasional ainda sem perfil do aparelho.
- Overlay Android com FPS de apresentações SDL reais, frametime/pico, sim Hz, CPU da thread, tempo de vídeo e dimensões efetivas. Toggle/quatro posições/preferências persistentes; amostragem 500 ms e logs MMZ_PERF ~5 s.
- Caps 30/60/90/120/ilimitado/auto: apresentação 30 separada da lógica; acima de 60 explicitamente limitado ao framebuffer original. FramePacer/áudio/input preservados; sem --uncapped.
- Upscale SDL real 1–8×, automático econômico 1×; sete modos de proporção; nearest/bilinear aplicados às texturas. Alteração ao vivo pelo botão VÍDEO, JNI atômico e operações SDL exclusivamente na thread de renderização.
- Novo ícone de capacete vermelho gerado/local, adaptativo e cinco densidades, sem placeholder. Importações, launcher, volume, controles, saves e logs preservados.
- Teste SDL software real: texturas 1–8×, filtros com pixels distintos, proporções, caps; 60 iterações simuladas/30 apresentações no modo 30 e 60 nos demais. Benchmark host registrado; não representa GPU Android. Boot real 120 frames repetido; seis testes existentes executados. Relatório em validation/enhanced/NATIVE-REPORT.md reconhece fallback ARMv4T e limites de validação Android.

### Publicação Enhanced confirmada

- Fonte compilada: `f3e88d7` (`codex/android-arm64`); versão instalada 0.2.1-test.1 / versionCode 3 / nome Enhanced.
- APK ARM64: 7.498.240 bytes; SHA-256 `3259c6cf69b4701f62316b98daf6b3c86e82b6845fd7c9f9a73e10abf4ddbb0d`.
- Prerelease pública: https://github.com/silasquercloud/MegamenZerorecomp/releases/tag/android-v0.2.1-test.1
- APK HTTPS: https://github.com/silasquercloud/MegamenZerorecomp/releases/download/android-v0.2.1-test.1/MegaManZeroNativeEnhanced-0.2.1-test.1-arm64-v8a.apk
- Actions https://github.com/silasquercloud/MegamenZerorecomp/actions/runs/38130128081 — sucesso: checksums de todos os anexos, APK ELF/ABI/ausência de dumps, assinatura, zipalign e publicação. É verificação do APK compilado localmente, não rebuild do corpus privado.
- Relatório, símbolos ARM64, logs SDL, metadados APK e SHA256SUMS anexados. Download HTTPS público comparado byte a byte com APK local: idêntico.
- ADB sem dispositivo. Nenhuma captura Android desta versão foi gerada; overlay/opções ao vivo/desempenho físico ainda pendentes no aparelho. O interpretador fallback upstream permanece documentado; FPS acima de ~60 não foi desbloqueado.

## 1.0.0-rc.1 — conclusão técnica da candidata

- Vulkan NDK real integrado com superfície Android, swapchain FIFO, uploads por slots, fences/semáforos/barreiras, nearest/bilinear e fallback SDL. Núcleo testado no Linux com validation layers, escalas 1–8, resize e reabertura; integração e lifecycle no aparelho ainda pendentes.
- AJUSTES ao vivo reúne CRT/scanlines/LCD/monocromático, escalas/proporções/filtros, API, FPS e controles persistentes. Novo overlay multitouch com artes locais, macro simultânea remapeável e posições/tamanho/opacidade editáveis. Atualizada a ajuda do launcher.
- Removida limitação redundante perto de 60 Hz; espera ativa reduzida preservando FramePacer. Sem alteração da velocidade de simulação/áudio. Opções acima de 60 continuam com limitação explicitada. Métricas por subsistema, p95/p99, custo da ponte interpretada e marcadores de primeira apresentação real/reabertura.
- Corpus mantido em -O1 após experiência O2 sem ganho comprovado. 12.000 frames reais preservam PNG de referência; 12 misses/78 instruções interpretadas, custo total da ponte ~1,17 ms nesse percurso Linux. 600 frames com SDL e Vulkan, respectivamente 59,277/57,557 FPS, sem fallback interpretado nesse trecho. Comparação exploratória com diferenças documentadas; não prova desempenho de GPU Android.
- Testes SDL (escala/filtros/proporções/caps), estado multitouch/macro, efeitos, seis testes upstream, regressão de boot e Vulkan passaram. Relatório e resultados em validation/rc1/.
- GitHub Actions 38153319029: testes Vulkan/controles/filtros e compilação NDK ARM64 dos componentes públicos passaram. Full APK usa corpus privado e build local; registro de runner privado recebeu 403. Não apresentar CI parcial como rebuild completo.
- Nenhum dispositivo ADB disponível. Final exige validação real de gameplay, áudio físico, controles/macro, saves, troca de API, pausa/retomada e estabilidade. Publicar como prerelease RC, mantendo pendências explícitas.

### Publicação RC confirmada

- Fonte compilada `5e62f3e3da80d422638523927acae3de571a6741`; Gradle/C++ passou em 3m04s; versionCode 4 / 1.0.0-rc.1.
- APK 7.704.712 bytes, SHA-256 `d5365cebc694abea1944aedc156c226b8b83d3ef2e5d6661cac216e471bffa24`; assinatura v2 pela chave de teste existente, páginas de 16 KB e ausência de dumps verificados. As seis artes de controles foram localizadas pelo mapa de recursos AAPT e verificadas como PNGs não vazios.
- CI fonte https://github.com/silasquercloud/MegamenZerorecomp/actions/runs/38185290401 passou testes Vulkan/controles/filtros e compilação ARM64 dos componentes públicos.
- Publicação https://github.com/silasquercloud/MegamenZerorecomp/actions/runs/38185516505 passou checksums, ELF/ABI, assinatura e alinhamento e publicou os oito anexos. Primeira tentativa detectou corretamente o ZIP de logs omitido por .gitignore; corrigido em ee4eaa0 sem alterar APK/checksums.
- Prerelease pública: https://github.com/silasquercloud/MegamenZerorecomp/releases/tag/android-v1.0.0-rc.1
- APK: https://github.com/silasquercloud/MegamenZerorecomp/releases/download/android-v1.0.0-rc.1/MegaManZeroNative-1.0.0-rc.1-arm64-v8a.apk
- Download público HTTPS comparado byte a byte com o APK local: idêntico. Relatório, logs, símbolos, screenshot Linux claramente identificado e SHA256SUMS anexados. Permanecem os critérios Android real para promoção a Final; não foi declarada eliminação da lentidão física nem FPS real acima de ~60.
