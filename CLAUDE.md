# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供在此代码库中工作的指导。

## 构建命令

```bash
./mvnw clean package              # 构建项目
./mvnw clean install              # 构建并安装到本地仓库
./mvnw test                       # 运行所有测试
./mvnw test -Dtest=VoiceSpaceApplicationTests  # 运行单个测试类
./mvnw spring-boot:run            # 运行应用程序
```

## 项目架构

Spring Boot 3.4.5 (Java 17) 应用，支持B站视频下载和音频提取。主要包路径 `com.voice.voicespace.mp4tomp3`。

### 核心组件

- **ExtractAudioService** (`service/ExtractAudioService.java`) - 实现 `IExtractAudioService` 接口，音频提取入口。
- **WebVideoAudioExtractor** (`domain/utils/WebVideoAudioExtractor.java`) - 使用 `yt-dlp.exe -x` 从网络视频提取音频，支持Cookie认证。
- **MP4AudioExtractor** (`domain/utils/MP4AudioExtractor.java`) - 使用 jcodec 库从本地 MP4 文件提取音频（输出为原始 PCM 格式）。
- **UtilsCommand** (`config/UtilsCommand.java`) - 枚举类，存放外部工具路径：
  - `YT_DLP`: `C:\Users\zym\AppData\Local\Programs\Python\Python313\Scripts\yt-dlp.exe`
  - `FFMPEG`: `F:\util\video_voice_utils\ffmpeg\bin\ffmpeg.exe`

### B站登录模块

- **BilibiliLoginService** (`service/BilibiliLoginService.java`) - Playwright扫码登录B站，自动获取Cookie。
- **BilibiliCookieManager** (`service/BilibiliCookieManager.java`) - Cookie读写与有效性检测。
- **BilibiliConfig** (`config/BilibiliConfig.java`) - B站相关配置（Cookie文件路径、输出路径）。

### API接口

```bash
# 下载单个视频
GET http://localhost:8080/api/bilibili/download?url=<视频URL>

# 下载整个合集/播放列表
GET http://localhost:8080/api/bilibili/download?url=<合集URL>&downloadPlaylist=true
```

首次调用时会启动Chrome浏览器窗口显示B站二维码，扫码登录后自动开始下载。

### 配置项 (application.properties)

```properties
bilibili.cookie-file=src/main/resources/cookies_www.bilibili.com.txt
bilibili.output-path=F:\\database\\audio\\%(title)s.%(ext)s
```

### 输出路径

网络视频提取输出目录：`F:\database\audio\%(title)s.%(ext)s`

## 已知问题

- `IExtractAudioService.extreactAudio` 方法名有拼写错误（应为 `extractAudio`）

## 依赖

- Spring Boot Web、Lombok、jcodec 0.2.5
- Playwright 1.40.0（用于B站扫码登录）
- 外部工具（不在仓库中）：yt-dlp.exe、ffmpeg.exe