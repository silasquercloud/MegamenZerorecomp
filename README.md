
<div align="center">

# ⚔️ MEGA MAN ZERO NATIVE

### 🔴 O despertar de uma lenda no Android!

<img src="https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExMHhkcmNnMDZyNGIwcDkxNDQzZWs4a3FkODBvd3ExejA5eGZrOTZtNyZlcD12MV9naWZzX3NlYXJjaCZjdD1n/jX080rWXtyYZ09qIJR/giphy.gif" width="400" alt="Mega Man Zero animado">

<br>

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

O projeto utiliza técnicas de **recompilação estática**, aproveitando os trabalhos MegaManZeroRecomp e gbarecomp.

Nosso objetivo é preservar a experiência original e oferecer uma versão adaptada para dispositivos móveis.

### 🎯 OBJETIVOS

- ⚔️ Executar Mega Man Zero através de código recompilado.
- 📱 Oferecer suporte ao Android ARM64.
- 🎮 Implementar controles touchscreen personalizados.
- 🕹️ Oferecer suporte a controles físicos.
- 🎨 Criar um launcher inspirado no universo Mega Man Zero.
- 🔊 Integrar áudio e renderização.
- 💾 Implementar salvamentos persistentes.
- ⚡ Melhorar estabilidade, desempenho e compatibilidade.

> [!WARNING]
> **VERSÃO EXPERIMENTAL**
>
> O launcher e a importação de ROM e BIOS já foram testados.
>
> O boot do jogo ainda não está funcionando no dispositivo testado. Gameplay, áudio, controles e salvamentos ainda precisam de validação.

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
| Logs de diagnóstico | 🔧 Em desenvolvimento |

---

## 📱 REQUISITOS

| Componente | Requisito |
|:---|:---|
| Sistema | Android 9 ou superior |
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
> ROM e BIOS não são distribuídas com o APK ou com o repositório.

---

## 🚀 COMO INSTALAR

1. Acesse a página de Releases.
2. Baixe o APK ARM64 mais recente.
3. Instale o aplicativo em um dispositivo Android compatível.
4. Abra o **Mega Man Zero Native**.
5. Importe sua ROM original.
6. Importe sua BIOS original.
7. Toque em **Jogar**.

**Atenção:** o gameplay ainda não está funcional na versão testada.

<div align="center">

[![Download APK](https://img.shields.io/badge/DOWNLOAD-ÚLTIMA_RELEASE-ED1C24?style=for-the-badge&logo=android&logoColor=white)](https://github.com/silasquercloud/MegamenZerorecomp/releases)

</div>

---

## 🛠️ TECNOLOGIAS

<div align="center">

![C++](https://img.shields.io/badge/C++-00599C?style=flat-square&logo=cplusplus&logoColor=white)
![Android](https://img.shields.io/badge/Android_NDK-3DDC84?style=flat-square&logo=android&logoColor=white)
![CMake](https://img.shields.io/badge/CMake-064F8C?style=flat-square&logo=cmake&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)

</div>

### ARQUITETURA

O projeto utiliza:

- Recompilação estática ARM/Thumb.
- C++ para o runtime nativo.
- Android NDK para compilação ARM64.
- JNI para integração com Android.
- SDL para componentes de plataforma.
- Gradle para geração do APK.
- GitHub Actions para automação de builds.

A proposta é executar código recompilado, sem substituir o projeto por um emulador GBA convencional.

---

## 🗺️ ROADMAP

### 🔴 FASE 1 — BASE ANDROID

- [x] Gerar APK ARM64.
- [x] Implementar launcher inicial.
- [x] Implementar importação de ROM.
- [x] Implementar importação de BIOS.
- [ ] Corrigir inicialização do jogo.
- [ ] Renderizar o primeiro frame real.

### ⚔️ FASE 2 — GAMEPLAY

- [ ] Validar controles.
- [ ] Validar renderização.
- [ ] Validar áudio.
- [ ] Validar salvamentos.
- [ ] Testar gameplay completo.

### 🎨 FASE 3 — INTERFACE

- [ ] Implementar launcher temático em modo retrato.
- [ ] Adicionar artes e efeitos gráficos.
- [ ] Criar controles personalizados da Z-Saber e Z-Buster.
- [ ] Personalizar posição, tamanho e transparência dos controles.
- [ ] Implementar configurações gráficas e de desempenho.

### ⚡ FASE 4 — ESTABILIDADE

- [ ] Corrigir falhas identificadas.
- [ ] Melhorar compatibilidade ARM64.
- [ ] Testar pausa e retomada.
- [ ] Publicar versão estável.

---

## 🐛 RELATAR PROBLEMAS

Encontrou algum problema?

Abra uma issue informando:

- Modelo do dispositivo.
- Versão do Android.
- Versão do APK.
- Descrição do erro.
- Logs ou capturas de tela, se disponíveis.

[🐛 ABRIR ISSUE](https://github.com/silasquercloud/MegamenZerorecomp/issues)

---

## 📜 CRÉDITOS

Este projeto utiliza trabalhos dos seguintes projetos:

- [MegaManZeroRecomp](https://github.com/mstan/MegaManZeroRecomp)
- [gbarecomp](https://github.com/mstan/gbarecomp)

Agradecimentos aos desenvolvedores e colaboradores dos projetos originais e das bibliotecas utilizadas.

**Mega Man Zero** e seus personagens pertencem à Capcom.

Este é um projeto independente, não oficial e sem afiliação com a Capcom.

As licenças e atribuições dos componentes utilizados devem ser preservadas.

---

## 🔥 O LEGADO CONTINUA

<div align="center">

<img src="https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExMmlscTNlN2thcWY1cjVzZzNqY2gxYXRvYXV0b3o2NTAyYjRpejRrdCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/jX080rWXtyYZ09qIJR/giphy.gif" width="320" alt="Mega Man Zero - animação final">

### ❤️ FEITO POR FÃS, PARA FÃS

# MEGA MAN ZERO NATIVE

**Recompiled. Reimagined. Reborn.**

*Android ARM64 • C++ • Recompilação estática*

---

### ⚔️ O despertar de Zero está apenas começando.

</div>
