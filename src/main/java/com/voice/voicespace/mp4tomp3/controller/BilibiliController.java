package com.voice.voicespace.mp4tomp3.controller;

import com.voice.voicespace.mp4tomp3.service.ExtractAudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bilibili")
public class BilibiliController {

    @Autowired
    private ExtractAudioService extractAudioService;

    @GetMapping("/download")
    public String downloadVideo(@RequestParam String url, @RequestParam(defaultValue = "false") boolean downloadPlaylist) {
        try {
            if (downloadPlaylist) {
                extractAudioService.extractPlaylistAudio(url);
            } else {
                extractAudioService.extreactAudio(url);
            }
            return "下载成功: " + url;
        } catch (Exception e) {
            return "下载失败: " + e.getMessage();
        }
    }
}