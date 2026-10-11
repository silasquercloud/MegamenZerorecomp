## Mega Man Zero Native 1.0.0-rc.1 — Android ARM64

Candidata à versão 1.0. Não classificada como Final: não houve dispositivo Android disponível para comprovar Vulkan, controles, áudio físico e estabilidade desta versão.

- Backend Vulkan NDK com superfície/swapchain/apresentação reais e fallback SDL. Núcleo testado com Vulkan/Lavapipe e validation layers no Linux; integração Android ainda requer teste em aparelho.
- Controles minimalistas multitouch, posições/tamanho/transparência persistentes e macro R+A remapeável. A macro não é comando universal de Cyber-Elf.
- **AJUSTES durante o jogo:** CRT, scanlines, LCD, monocromático, nearest/bilinear, upscale 1–8×, proporções, API e overlay FPS/frametime/p95/p99.
- Espera ativa do pacer reduzida e limitador redundante removido. Ganho no telefone ainda não comprovado. 90/120/ilimitado continuam limitados à produção original de ~59,73 FPS; sem frames fictícios ou aceleração da lógica.
- Lógica recompilada ARM64 com fallback ARMv4T explicitamente documentado. 12.000 frames de teste Linux preservaram a imagem de referência; 12 misses/78 instruções interpretadas.

Fonte compilada: `5e62f3e3da80d422638523927acae3de571a6741`. APK completo compilado localmente com corpus privado; Actions compila componentes NDK públicos, executa testes e verifica/publica o APK. Registro de runner privado retornou 403; não houve rebuild completo do corpus em Actions.

APK ARM64 assinado com a mesma chave de teste anterior; Android 9+. Importação obrigatória de ROM USA rev. 0 e BIOS original, não incluídas. Assinatura, ELF/ZIP 16 KiB, recursos locais e ausência dos dumps verificados. Checksums e relatório anexados.

A captura anexada é **Linux Vulkan**, não screenshot do APK. O relatório lista todos os testes Android pendentes para promover esta candidata a Final. O menu avançado ImGui do upstream utiliza o backend Anterior; AJUSTES Android funciona como interface compartilhada de vídeo/controles.
