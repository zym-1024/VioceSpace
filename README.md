# VoiceSpace

B站视频下载和音频提取工具，基于 Spring Boot 3.4.5 开发。

## 功能特性

- **B站视频下载**：支持下载单个视频或整个播放列表/合集
- **音频提取**：支持从网络视频（yt-dlp）和本地 MP4 文件提取音频
- **扫码登录**：首次使用通过 Playwright 自动打开浏览器扫码登录B站
- **Cookie 管理**：自动保存和复用登录态

## 技术栈

- Spring Boot 3.4.5 (Java 17)
- Playwright 1.40.0（B站扫码登录）
- jcodec 0.2.5（本地 MP4 音频提取）
- yt-dlp + ffmpeg（网络视频音频提取）

## 项目结构

```
src/main/java/com/voice/voicespace/
├── VoiceSpaceApplication.java          # 应用入口
└── mp4tomp3/
    ├── config/
    │   ├── BilibiliConfig.java         # B站配置（Cookie路径、输出路径）
    │   └── UtilsCommand.java           # 外部工具路径配置
    ├── controller/
    │   └── BilibiliController.java     # API 控制器
    ├── domain/utils/
    │   ├── MP4AudioExtractor.java     # 本地 MP4 音频提取（PCM格式）
    │   └── WebVideoAudioExtractor.java # 网络视频音频提取（yt-dlp）
    └── service/
        ├── BilibiliCookieManager.java  # Cookie 读写管理
        ├── BilibiliLoginService.java   # Playwright 扫码登录
        ├── ExtractAudioService.java     # 音频提取服务
        └── IExtractAudioService.java    # 音频提取接口
```

## API 接口

```bash
# 下载单个视频
GET http://localhost:8080/api/bilibili/download?url=<视频URL>

# 下载整个合集/播放列表
GET http://localhost:8080/api/bilibili/download?url=<合集URL>&downloadPlaylist=true
```

## 构建与运行

```bash
# 构建项目
./mvnw clean package

# 运行应用
./mvnw spring-boot:run

# 运行测试
./mvnw test
```

## 配置说明

在 `src/main/resources/application.properties` 中配置：

```properties
bilibili.cookie-file=src/main/resources/cookies_www.bilibili.com.txt
bilibili.output-path=F:\\database\\audio\\%(title)s.%(ext)s
```

## 外部依赖

本项目需要以下外部工具（不在仓库中）：

| 工具 | 环境变量 | 用途 |
|------|------|------|
| yt-dlp.exe | `YT_DLP_PATH` | 下载网络视频/提取音频 |
| ffmpeg.exe | `FFMPEG_PATH` | 音视频格式转换 |

## 注意事项

- 首次调用 API 时会自动打开浏览器窗口，需扫码登录B站
- Cookie 文件路径需有效，登录态会自动保存复用
- 网络视频提取输出目录：`F:\database\audio\`
- **环境变量**：需设置 `YT_DLP_PATH` 和 `FFMPEG_PATH` 系统环境变量指向工具路径
