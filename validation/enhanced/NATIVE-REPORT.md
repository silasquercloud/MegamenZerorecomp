# Enhanced 0.2.1-test.1 — execução e desempenho

O usuário relatou cenas reais da introdução da 0.2.0 em seu Android ARM64. Isso é evidência fornecida pelo usuário; este ambiente não mediu seu aparelho. A 0.2.1 foi compilada e testada conforme abaixo, sem declarar gameplay Android novamente validado.

## Caminho auditado

`GameActivity → SDLActivity (SDL2/main, setupJNI) → nativeRunMain → SDL_main → mobile_run_with_stack → android_game_main → mmz_game_main → run_game → runtime_init → tabelas de dispatch → funções C++ recompiladas → PPU → HostWindow → SDL_RenderPresent`.

O NDK 27.1 compila `libmain.so` ELF64 AArch64; SDL é plataforma/input/áudio/apresentação. O APK carrega `libSDL2.so`, `libmain.so` e `libc++_shared.so`. Inclui 16 shards do jogo e tabelas recompiladas da BIOS. `native-symbols.txt` contém os exports reais do binário, incluindo SDL_main, JNI de configuração/métricas e tabelas jogo/BIOS. Os tamanhos de tabelas são 178608 e 82288 bytes.

Não foi adicionado emulador convencional. Porém o gbarecomp inclui um interpretador ARMv4T de fallback e modos de diagnóstico `GBARECOMP_FORCE_INTERP`. O aplicativo usa o backend estático, não ativa force-interp nem recompilação dinâmica. BIOS é LLE recompilada, com HLE desabilitado. Portanto **não é correto declarar cobertura 100% estática**: teste Linux anterior de 12000 frames teve 12 dispatch misses/78 instruções interpretadas. O teste de boot de 120 frames foi repetido nesta versão: frames reais, zero misses/instruções interpretadas. Capturas anteriores em validation/ são Linux, não screenshots do APK.

## Configurações efetivas

- Temporização original `FramePacer` preservada, ~59,7275 Hz; não usamos `--uncapped`. Gate de apresentação executa antes do upload/texturas/overlay SDL, mantendo áudio e input bombeados a cada frame simulado. 30 limita apresentação; demais opções não geram mais frames distintos que o jogo original. 90/120/ilimitado aparecem explicitamente com efetivo ~60. Não há interpolação nem desbloqueio real acima de 60.
- Framebuffer continua 240×160. Escalas 2–8 usam textura SDL render-target real até 1920×1280; 1× e automático econômico dispensam esse passe. O overlay mostra tamanho efetivamente usado e destino na tela. Se o backend rejeitar a textura, registra erro e usa o framebuffer original, sem declarar upscale aplicado.
- `SDL_SetTextureScaleMode` aplica nearest ou bilinear ao framebuffer e ao render-target. Teste compara pixels lidos de renderer real e verifica diferenças, não apenas valores de preferências.
- Automático preserva espaço original para controles. Original 3:2 ocupa a maior área compatível com a tela; razões alternativas alteram retângulo de destino e distorcem a imagem. Preencher usa todo o destino. Sem expansão do mundo ou widescreen nativo. Coordenadas de input recebem o mesmo layout de apresentação.
- Preferências persistem em SharedPreferences. Botão VÍDEO durante gameplay altera atomicamente pedidos JNI; somente a thread SDL cria texturas/altera o renderer. Volume/importação/controles/saves existentes preservados.

## Métricas e limites de medição

FPS conta chamadas concluídas de `SDL_RenderPresent`, não frames meramente simulados. É taxa de apresentação da aplicação; SDL não fornece confirmação individual de scanout/composição física. Sim Hz conta avanço de frame da PPU, separado de apresentação. Overlay atualiza a cada 500 ms, tem quatro posições e pode ser ocultado. Logs `MMZ_PERF` são emitidos a cada ~5 segundos e exportados pelo launcher.

Frametime é média entre apresentações na janela de medição; pico é máximo nessa janela. CPU usa CLOCK_THREAD_CPUTIME_ID da thread SDL/runtime, por apresentação (inclui lógica entre frames; não soma threads de áudio/driver). Vídeo mede wall time do upload/upscale/layers/present, incluindo bloqueio de vsync se houver; não é medição isolada de GPU. Pausas/alterações reiniciam a janela quando configurações são reaplicadas.

`sdl-video-tests.log`: execução real em SDL renderer de software Linux. 1–8×/dois filtros, leitura de pixels, texturas reais, seis proporções e todos os caps. Benchmark representa custo de submissão/upscale no host, não no Android. Não se deve comparar seus tempos diretamente com GPU do aparelho. Automático econômico evita um passe extra; overlay/logs são amostrados, sem remover sincronização do GBA. Não afirmamos ter corrigido a causa da lentidão ocasional no aparelho sem uma sessão instrumentada dele.

## Validação e pendências

Build Gradle/C++/Java/JNI ARM64, assinatura de teste compatível com 0.2.0, ELF/ZIP 16 KiB, recursos de ícone adaptativo e mdpi–xxxhdpi, ausência de dumps ROM/BIOS; seis testes ARM/Thumb/PPU/áudio/save/input e regressão de boot nativo. Ícone local gerado para esta versão, sem imagem remota em execução.

Sem dispositivo Android conectado e sem KVM. Emulador por software previamente não concluiu inicialização; não repetimos essa tentativa sem nova evidência. Overlay Android, seleção ao vivo, controles/áudio físico, estabilidade prolongada e FPS do aparelho ainda precisam de teste nele. Para medir: repetir a mesma cena por 60 s em auto/nearest, auto/bilinear, 4× e 8×; exportar logs; comparar FPS/sim Hz/picos/CPU/vídeo, respeitando temperatura/carga do aparelho. APK é prerelease experimental.

GitHub Actions verifica e publica o APK já compilado localmente; não recompila o corpus privado em runner público. Nenhuma ROM/BIOS é distribuída.
