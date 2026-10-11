
<div align="center">

# ⚔️ MEGA MAN ZERO NATIVE

### 🔴 O despertar de uma lenda no Android!

<img src="https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExMHhkcmNnMDZyNGIwcDkxNDQzZWs4a3FkODBvd3ExejA5eGZrOTZtNyZlcD12MV9naWZzX3NlYXJjaCZjdD1n/jX080rWXtyYZ09qIJR/giphy.gif" width="400" alt="Mega Man Zero animado">

<br><br>

![Android](https://img.shields.io/badge/Android-9%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![ARM64](https://img.shields.io/badge/Arquitetura-ARM64-ED1C24?style=for-the-badge)
![C++](https://img.shields.io/badge/C%2B%2B-Nativo-00599C?style=for-the-badge&logo=cplusplus)
![Status](https://img.shields.io/badge/Status-Experimental-FFB020?style=for-the-badge)

<br>

**Port experimental de Mega Man Zero para Android ARM64 utilizando recompilação estática.**

### ⚡ RECOMPILED • ANDROID • ARM64

[![Baixar APK](https://img.shields.io/badge/BAIXAR_APK-ANDROID_ARM64-C11232?style=for-the-badge&logo=android&logoColor=white)](https://github.com/silasquercloud/MegamenZerorecomp/releases)

[📦 Releases](https://github.com/silasquercloud/MegamenZerorecomp/releases) • [🐛 Reportar erro](https://github.com/silasquercloud/MegamenZerorecomp/issues)

</div>

---

## 🌌 SOBRE O PROJETO

**Mega Man Zero Native** é um projeto independente que busca executar o clássico Mega Man Zero, originalmente lançado para Game Boy Advance, em dispositivos Android ARM64.

A proposta utiliza a recompilação estática do código original, adaptando o runtime para Android.

### 🎯 Objetivos

- ⚔️ Executar o jogo através de código recompilado.
- 📱 Oferecer suporte nativo ao Android ARM64.
- 🎮 Implementar controles touchscreen.
- 🕹️ Oferecer suporte a gamepads.
- 🎨 Criar um launcher personalizado.
- 🔊 Integrar gráficos e áudio.
- 💾 Validar salvamentos.
- ⚡ Melhorar desempenho e estabilidade.

> [!WARNING]
> **VERSÃO EXPERIMENTAL**
>
> O launcher e a importação de ROM/BIOS funcionam no dispositivo testado.
> O boot do jogo ainda não foi concluído e o gameplay não está validado.

---

## ⚔️ ZERO EM AÇÃO

<div align="center">

<img src="https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExenVpNW5nMTZnajJ3cjl5aHR4eWk0NXJ3Y2kxcG82NWVmdDU1dXlzaSZlcD12MV9zdGlja2Vyc19zZWFyY2gmY3Q9cw/Tj4gzTSsr9Gec/giphy.gif" width="200" alt="Sticker animado do Zero">

### A lenda da Z-Saber continua!

*O despertar de Zero está apenas começando.*

</div>

---

## 🎮 FUNCIONALIDADES

| Funcionalidade | Estado |
|:---|:---:|
| APK Android ARM64 | ✅ Disponível |
| Launcher Android | ✅ Funcional |
| Importação de ROM | ✅ Testada |
| Importação de BIOS | ✅ Testada |
| Boot do jogo | 🚧 Em desenvolvimento |
| Renderização | 🧪 Não validada |
| Áudio | 🧪 Não validado |
| Controles touchscreen | 🚧 Em desenvolvimento |
| Gamepad | 🧪 Não validado |
| Salvamentos | 🧪 Não validados |
| Launcher temático | 🎨 Planejado |

---

## 📱 REQUISITOS

| Componente | Requisito |
|:---|:---|
| Android | 9 ou superior |
| Arquitetura | ARM64 / arm64-v8a |
| Jogo | Mega Man Zero |
| Plataforma original | Game Boy Advance |
| Região | USA |
| Revisão | 0 |
| Resolução original | 240 × 160 |

### 📂 ROM E BIOS

Você precisa dos seus próprios arquivos originais.

**ROM — Mega Man Zero (USA), revisão 0**

SHA-1:

`193b14120119162518a73c70876f0b8bffdbd96e`

**BIOS original do Game Boy Advance**

SHA-1:

`300c20df6731a33952ded8c436f7f186d25d3492`

> [!IMPORTANT]
> ROM e BIOS não são distribuídas com o projeto.

---

## 🚀 COMO INSTALAR

1. Acesse a página de Releases.
2. Baixe o APK ARM64.
3. Instale o aplicativo no Android.
4. Abra o Mega Man Zero Native.
5. Importe a ROM compatível.
6. Importe a BIOS original.
7. Toque em **Jogar**.

**Atenção:** o gameplay ainda não está funcional na versão testada.

<div align="center">

[![Download APK](https://img.shields.io/badge/📥_DOWNLOAD-ÚLTIMA_RELEASE-ED1C24?style=for-the-badge)](https://github.com/silasquercloud/MegamenZerorecomp/releases)

</div>

---

## 🛠️ TECNOLOGIAS

- C++
- Android NDK
- JNI
- SDL
- Gradle
- CMake
- ARM64
- Recompilação estática
- GitHub Actions

---

## 🗺️ ROADMAP

### Fase 1 — Android

- [x] Gerar APK ARM64.
- [x] Implementar launcher inicial.
- [x] Importar ROM.
- [x] Importar BIOS.
- [ ] Corrigir inicialização do jogo.
- [ ] Renderizar o primeiro frame real.

### Fase 2 — Gameplay

- [ ] Validar controles.
- [ ] Validar áudio.
- [ ] Validar salvamentos.
- [ ] Testar gameplay completo.

### Fase 3 — Interface

- [ ] Implementar launcher temático em retrato.
- [ ] Adicionar artes e efeitos gráficos.
- [ ] Implementar controles da Z-Saber e Z-Buster.
- [ ] Personalizar tamanho e posição dos controles.

### Fase 4 — Estabilidade

- [ ] Corrigir falhas.
- [ ] Melhorar compatibilidade.
- [ ] Testar pausa e retomada.
- [ ] Publicar versão estável.

---

## 🐛 RELATAR PROBLEMAS

Se encontrar algum problema, abra uma issue informando:

- Modelo do dispositivo.
- Versão do Android.
- Versão do APK.
- Descrição do erro.
- Logs ou capturas de tela, se disponíveis.

[🐛 ABRIR ISSUE](https://github.com/silasquercloud/MegamenZerorecomp/issues)

---

## 📜 CRÉDITOS

Este projeto é baseado nos seguintes trabalhos:

- [MegaManZeroRecomp](https://github.com/mstan/MegaManZeroRecomp)
- [gbarecomp](https://github.com/mstan/gbarecomp)

Agradecimentos aos desenvolvedores dos projetos originais e das bibliotecas utilizadas.

**Mega Man Zero** e seus personagens pertencem à Capcom.

Projeto independente, não oficial e sem afiliação com a Capcom.

As licenças e atribuições dos componentes utilizados devem ser preservadas.

---

<div align="center">

## ❤️ FEITO POR FÃS, PARA FÃS

### MEGA MAN ZERO NATIVE

**Recompiled. Reimagined. Reborn.**

*Android ARM64 • C++ • Recompilação estática*

---

**O despertar de Zero está apenas começando. ⚔️**

</div>
