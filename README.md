
# Voxy NeoForge 移植版（非官方）

## ⚠️ 重要声明

本仓库仅包含源代码。**不提供、也不分发任何编译好的 JAR 文件。**

Voxy 采用 **All Rights Reserved（ARR）** 协议，由 MCRcortex 所有，明确禁止再分发。本项目为**非官方移植版，仅供个人使用**。

- 你可以编译本源码**用于个人使用**。
- 你**不得**再分发编译出的 JAR 文件，无论公开还是私下。
- 本项目**与 Voxy 原作者无关**，未获其认可或支持。
- **请勿就本移植版的任何问题联系 Voxy 原作者（MCRcortex）。**

如果你是原作者并希望下架本仓库，请开 issue 或直接联系我。

## 环境要求

- JDK 25
- Git

## 编译方法

### Windows

1. 克隆仓库：

   ```bash
   git clone https://github.com/366862732/voxy-neoforge-26.1.2.git
   cd voxy-neoforge-26.1.2
   ```

2. 使用 Gradle wrapper 编译：

   ```bash
   gradlew.bat clean build
   ```

   如果是 PowerShell，需要在命令前添加 `.\`：

   ```powershell
   .\gradlew clean build
   ```

3. 编译产物位于：

   ```
   build\libs\
   ```

### Linux / macOS

1. 克隆仓库：

   ```bash
   git clone https://github.com/366862732/voxy-neoforge-26.1.2.git
   cd voxy-neoforge-26.1.2
   ```

2. 使用 Gradle wrapper 编译：

   ```bash
   ./gradlew clean build
   ```

3. 编译产物位于：

   ```
   build/libs/
   ```

## 许可协议

本项目为非官方移植版。Voxy 采用 **All Rights Reserved（ARR）** 协议，由 MCRcortex 所有。详见原项目页面。

---

# Voxy NeoForge Port (Unofficial)

## ⚠️ Important Notice

This repository contains source code only. **No compiled JAR files are provided or distributed.**

Voxy is licensed under **All Rights Reserved (ARR)** by MCRcortex, which explicitly prohibits redistribution. This project is an **unofficial port for personal use only**.

- You may compile this source code **for your own personal use**.
- You may **not** redistribute the compiled JAR, in public or private.
- This project is **not affiliated with, endorsed by, or supported by** the original Voxy author.
- **Do not contact the original Voxy author (MCRcortex) regarding any issues related to this port.**

If you are the original author and wish for this repository to be taken down, please open an issue or contact me directly.

## Requirements

- JDK 25
- Git

## How to Build

### Windows

1. Clone the repository:

   ```bash
   git clone https://github.com/366862732/voxy-neoforge-26.1.2.git
   cd voxy-neoforge-26.1.2
   ```

2. Build with the Gradle wrapper:

   ```bash
   gradlew.bat clean build
   ```

   If you're using PowerShell, prefix the command with `.\`:

   ```powershell
   .\gradlew clean build
   ```

3. The compiled JAR will be in:

   ```
   build\libs\
   ```

### Linux / macOS

1. Clone the repository:

   ```bash
   git clone https://github.com/366862732/voxy-neoforge-26.1.2.git
   cd voxy-neoforge-26.1.2
   ```

2. Build with the Gradle wrapper:

   ```bash
   ./gradlew clean build
   ```

3. The compiled JAR will be in:

   ```
   build/libs/
   ```

## License

This project is an unofficial port. Voxy is licensed under **All Rights Reserved (ARR)** by MCRcortex. See the original project page for details.
---
