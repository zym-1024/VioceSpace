package com.voice.voicespace.mp4tomp3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bilibili")
public class BilibiliConfig {
    private String cookieFile;
    private String outputPath;

    public String getCookieFile() {
        return cookieFile;
    }

    public void setCookieFile(String cookieFile) {
        this.cookieFile = cookieFile;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}