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
