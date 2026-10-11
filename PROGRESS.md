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
| Recompiler host | Compilado; gerou 16 shards, dispatch e símbolos do jogo |
| CPU ARM/Thumb, PPU, áudio, save config, toque | 6 executáveis de teste passaram, 0 falhas |
| C++ gerado para ARM64 | 18 unidades passaram pela verificação do compilador NDK; não é link do jogo |
| Runtime ARM64 + SDL + UI/mods | Biblioteca estática compilada com NDK; não é carregamento JNI em dispositivo |
| Gradle + launcher/Java/recursos/manifest | `projects` e `compileReleaseJavaWithJavac` passaram |
| Setup repetido | Ferramentas e testes reinstanciados sem alterar fontes upstream |
| APK compilado/assinado/alinhado | Não executado: falta BIOS para gerar o corpus LLE |
| Instalável / JNI carregando / runtime inicializando | Não testado; ADB sem dispositivos conectados |
| ROM/BIOS reconhecidas no Android / gameplay / controles / áudio / saves | Não testados em dispositivo |
| Actions / release / download | Workflow implementado; não executado e nenhum APK publicado |

Java inicialmente disponível era somente JRE; instalado JDK completo 17.0.20.1+1 com SHA-256 oficial. Wrapper do exemplo Android era de outra versão; substituído pelo wrapper/scripts oficiais 8.11.1 e verificado seu checksum. Proxy Java configurado sem expor credenciais e sem desativar TLS.

## Próximo bloqueio

A BIOS GBA original de 16.384 bytes, SHA-1 `300c20df6731a33952ded8c436f7f186d25d3492`, ainda não foi fornecida. Depois dela: gerar BIOS, compilar/linkar o APK real, verificar ABI/assinatura/alinhamento e testar em dispositivo. A API GitHub permite ler o repositório e indica escrita; a consulta de runners retorna 403, portanto não foi possível confirmar runner disponível. Nenhum link de instalação foi inventado.
