# Mega Man Zero Native 1.0.0-rc.1

## Status e limites de validação

Release Candidate. Não há dispositivo Android conectado nem emulador Android operacional neste ambiente. O usuário relatou introdução real na versão anterior; isso não valida esta versão. Não declaramos a lentidão no aparelho resolvida, Vulkan Android validado ou estabilidade final.

O APK implementa Vulkan Android via NDK/C++: instância, dispositivo, ANativeWindow/superfície, swapchain FIFO com três imagens quando suportado, duas áreas de upload, fences, semáforos de aquisição e semáforos de apresentação por imagem. Barreiras fazem as transições de layout, upload RGB→RGBA e blits nearest/bilinear. Há recriação de swapchain e fallback SDL. Retorno de apresentação bem-sucedida conta como apresentação enfileirada; não é medição física de scanout.

O mesmo núcleo Vulkan apresentou imagens reais com Mesa/Lavapipe, SDL/X11 e validation layers no Linux. O teste percorreu escalas 1–8×, filtros, resize e fechamento/reabertura. Isso verifica o núcleo, não a integração Android SurfaceView/lifecycle. Controles e FPS são Views Android transparentes compostas sobre a SurfaceView Vulkan pelo Android, não desenhos dentro do command buffer Vulkan. O menu ImGui do upstream continua disponível pelo backend Anterior; o painel AJUSTES Android fornece vídeo e controles nos dois backends.

## Recursos implementados

- API Automática/Vulkan/Anterior, indicação da API efetiva, fallback com logs MMZ_VK_FALLBACK.
- AJUSTES durante o jogo: CRT, scanlines, LCD e monocromático, nearest/bilinear, escala 1–8×, proporções, FPS e controles. Preferências persistentes e aplicação ao vivo. Efeitos processam o framebuffer 240×160 antes da apresentação; escala é upscale, não expansão da resolução original.
- Controles multitouch independentes, analógico digital GBA, L/R/A/B/SELECT/START e macro R+A remapeável. Liberação por ponteiro, cancelamento, perda de foco e pausa. Edição de posições, tamanho e transparência. Artes locais originais inspiradas em Zero: não foi encontrada spritesheet fornecida para este layout.
- Macro envia simultaneamente as teclas selecionadas enquanto o toque durar. Não promete ativação universal de Cyber-Elf nem adiciona mecânicas ao jogo.
- FPS de apresentação, Hz de simulação, frametime médio/p95/p99, CPU da thread e tempos por subsistema. Logs MMZ_PHASE/MMZ_PERF e CSV exportável. Tempos Vulkan são medidas de CPU (espera/upload/submissão), não queries de tempo GPU.

## Frequência e otimização

O jogo/PPU produz aproximadamente 59,7275 frames/s, ligado à temporização GBA. A arquitetura atual não produz snapshots de cena independentes para interpolação. 90/120/ilimitado ficam explicitamente limitados a essa produção: não há frames interpolados, duplicados contabilizados como gameplay novo, nem aceleração da lógica/áudio. O modo 30 reduz apresentações mantendo a simulação.

Foi removido um segundo limitador próximo de 60 Hz que competia com o FramePacer original. A margem de espera ativa caiu de 1.200 para 100 µs, preservando os deadlines e a sincronização do jogo. Em 120 frames Linux: CPU de pacing 138,489→7,482 ms; duração total 2.009,637→2.010,723 ms. Isso não quantifica ganho Android.

A experiência com otimização -O2 não demonstrou ganho controlado; o corpus permanece -O1. As saídas de 12.000 frames O1/O2 foram idênticas. Não removemos sincronização de áudio ou locks com base em suspeitas não medidas. Upscale automático usa 1× para minimizar custo; escalas maiores e filtros podem aumentar carga.

## Execução recompilada e fallback

MegaManZeroRecomp fornece o programa; gbarecomp fornece runtime C++, PPU, áudio, barramento e fallback ARMv4T. Android NDK compila o corpus gerado e tabelas de dispatch em libmain.so ARM64. Java carrega SDL2/main; SDL chama SDL_main → android_game_main → entrada do jogo → funções recompiladas/tabelas → runtime → framebuffer → Vulkan ou SDL. JNI configura preferências/input/métricas, sem chamada Java por pixel/frame.

SDL é a camada de plataforma, não evidência de emulação. Há código recompilado estaticamente **e** interpretador fallback upstream para dispatch ausente. Não é correto chamar esta execução de 100% livre de interpretação. Nos testes anteriores de 12.000 frames foram 12 misses/78 instruções interpretadas; não se substituiu a recompilação por emulador convencional. MMZ_INTERPRETER mede duração acumulada da ponte em entradas ausentes, com logging limitado.

## Testes e reprodução

- `tools/test-enhanced-video.sh`: SDL real em software, escalas 1–8, alteração de pixels nearest/bilinear, proporções e caps sem reduzir iterações da simulação.
- `tools/test-vulkan.sh`: WSI Vulkan real; validation layers; resize/reabertura. CI `.github/workflows/rc-validation.yml` também compila presenter, bridge JNI e métricas com NDK ARM64 e testa filtros/estado multitouch.
- Seis testes upstream: ARM/Thumb, PPU, áudio, configuração/save e touch. Teste de regressão do argumento de boot e 120 frames reais sem dispatch miss.
- `backend-comparison.log`: mesma imagem sintética e destino 640×426, SDL/OpenGL versus Vulkan/Lavapipe, escalas 1/4/8, filtros, 30 frames de aquecimento + 90 medidos, cadência 59,7275. Linux com renderização por software e CPU compartilhada; não extrapolar para GPU Android.
- Build Gradle/C++ completo local usa corpus privado derivado dos dumps autorizados. CI público compila componentes nativos e verifica/publica o APK; não recompila o corpus privado. Registro de runner privado foi negado pela API GitHub (403), portanto não houve build completo do APK via Actions.
- APK assinado com a chave de teste existente para preservar atualização; não é chave de produção. Recursos locais e ELF ARM64 verificados, alinhamento 16 KB, ROM/BIOS ausentes.

## Critérios ainda pendentes para Final

Em Android ARM64 real: comprovar API Vulkan efetiva e pixels apresentados; comparar APIs no mesmo trecho; verificar filtros ao vivo, touch simultâneo e macro, introdução/diálogos, movimentação/pulo/wall jump/dash, Z-Buster/Z-Saber/arma secundária conforme disponibilidade, pausa/menus, áudio físico, save/reload, rotação/pausa/retomada, troca de API e sessão prolongada sem travamento. Confirmar layout visual e ausência de regressões com capturas reais do APK. Nenhuma captura Linux é apresentada como captura Android.

## Resultados do corpus final

Em 600 frames da introdução: SDL 59,277 FPS e Vulkan 57,557 FPS, ambos sem instruções interpretadas nesse trecho. Vulkan teve custo médio de present de 2,795 ms contra 2,402 ms no SDL. O ensaio Vulkan inclui validation layers e janela auxiliar 640×480; SDL usa a janela padrão 800×600, portanto esse percurso é validação funcional e perfil exploratório, não comparação isolada de eficiência. O benchmark sintético separado usa destinos iguais e mostrou custos menores no Vulkan, especialmente com upscale alto; há picos severos no SDL/OpenGL software. Nenhum dos resultados comprova ganho no telefone. Consulte runtime-comparison.txt e backend-comparison.log.

No teste final headless com entrada `campaign-safe`, 12.000 frames produziram PNG byte a byte idêntico à referência O1 (SHA-256 `8e83f9ee32401cba1b04c46988d0c4541b61664a33f073dd64c516fa39d7b780`). Foram 12 dispatch misses e 78 instruções interpretadas, sem IO não mapeado; a ponte acumulou 1,168746 ms nesse percurso Linux. Isso não identifica o fallback como gargalo desse trecho e não cobre todos os níveis/armas do jogo. Log resumido em native-12000.txt.

Pacote final: 7.704.712 bytes; SHA-256 `d5365cebc694abea1944aedc156c226b8b83d3ef2e5d6661cac216e471bffa24`. Fonte compilada `5e62f3e`. Gradle/C++ completo passou; assinatura APK v2, alinhamento ELF/ZIP 16 KB e recursos pad_* conferidos pelo mapeamento AAPT (nomes dos arquivos otimizados). Símbolos mostram tabelas de jogo/BIOS, JNI, SDL_main, runtime_dispatch_miss e chamadas Vulkan. CI do mesmo commit passou: https://github.com/silasquercloud/MegamenZerorecomp/actions/runs/38185290401 . A imagem Linux-Vulkan-capture-not-Android.png captura a janela real Vulkan mostrando a abertura Capcom; não é captura Android nem comprovação de gameplay interativo.
