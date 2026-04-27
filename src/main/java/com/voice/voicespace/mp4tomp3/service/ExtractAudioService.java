package com.voice.voicespace.mp4tomp3.service;

import com.voice.voicespace.mp4tomp3.config.UtilsCommand;
import com.voice.voicespace.mp4tomp3.domain.utils.WebVideoAudioExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExtractAudioService implements IExtractAudioService{

    @Autowired
    private BilibiliCookieManager cookieManager;

    @Autowired
    private BilibiliLoginService loginService;

    private static final String OUT_PATH = "F:\\database\\audio\\%(title)s.%(ext)s";

    @Override
    public boolean extreactAudio(String url) {
        try {
            if (!cookieManager.isCookieValid()) {
                System.out.println("Cookie无效或不存在，正在启动扫码登录...");
                if (!loginService.loginWithQrCode()) {
                    throw new RuntimeException("登录失败，无法下载视频");
                }
            }
            String cookiePath = cookieManager.getCookieFilePath();
            return WebVideoAudioExtractor.extractAudioFromWebVideo(url, "\"" + OUT_PATH + "\"", cookiePath);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean extractPlaylistAudio(String url) {
        try {
            if (!cookieManager.isCookieValid()) {
                System.out.println("Cookie无效或不存在，正在启动扫码登录...");
                if (!loginService.loginWithQrCode()) {
                    throw new RuntimeException("登录失败，无法下载视频");
                }
            }
            String cookiePath = cookieManager.getCookieFilePath();

            // 从合集页面获取所有视频URL
            List<String> videoUrls = fetchPlaylistVideos(url, cookiePath);
            System.out.println("合集共有 " + videoUrls.size() + " 个视频");

            for (String videoUrl : videoUrls) {
                System.out.println("开始下载: " + videoUrl);
                WebVideoAudioExtractor.extractAudioFromWebVideo(videoUrl, "\"" + OUT_PATH + "\"", cookiePath);
            }
            return true;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> fetchPlaylistVideos(String playlistUrl, String cookiePath) throws IOException, InterruptedException {
        List<String> urls = new ArrayList<>();

        // 使用yt-dlp --flat-playlist获取播放列表内容
        ProcessBuilder pb = new ProcessBuilder(
                UtilsCommand.YT_DLP.getCommand(),
                "--flat-playlist",
                "--print", "%(url)s",
                "--cookies", cookiePath,
                playlistUrl
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("https://www.bilibili.com/video/")) {
                urls.add(line.trim());
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("获取播放列表失败, exit code: " + exitCode);
        }
        return urls;
    }

    public static void main(String[] args) {
        List<String> urls = new ArrayList<>();
        String videoUrl = "https://www.bilibili.com/video/BV19x4y1n7fH/";
        urls.add(videoUrl);
        ExtractAudioService service = new ExtractAudioService();
        // 注入依赖
        service.cookieManager = new BilibiliCookieManager();
        service.loginService = new BilibiliLoginService();
        urls.forEach(service::extreactAudio);
    }
}