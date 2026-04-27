package com.voice.voicespace.mp4tomp3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.voice.voicespace.mp4tomp3.config.BilibiliConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class BilibiliCookieManager {

    @Autowired
    private BilibiliConfig config;

    private static final String[] REQUIRED_COOKIES = {"SESSDATA", "bili_jct", "DedeUserID"};

    public boolean isCookieValid() {
        File cookieFile = getCookieFile();
        if (!cookieFile.exists()) {
            return false;
        }
        Map<String, String> cookies = readCookies();
        for (String key : REQUIRED_COOKIES) {
            if (!cookies.containsKey(key) || cookies.get(key).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Map<String, String> readCookies() {
        Map<String, String> cookies = new HashMap<>();
        File cookieFile = getCookieFile();
        if (!cookieFile.exists()) {
            return cookies;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(cookieFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\t");
                if (parts.length >= 7) {
                    cookies.put(parts[5], parts[6]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cookies;
    }

    public void saveCookies(Map<String, String> cookies) throws IOException {
        File cookieFile = getCookieFile();
        cookieFile.getParentFile().mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(cookieFile))) {
            writer.println("# Netscape HTTP Cookie File");
            writer.println("# This is a generated file! Do not edit.");
            long expireTime = System.currentTimeMillis() / 1000 + 30 * 24 * 60 * 60;
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                writer.printf(".bilibili.com\tTRUE\t/\tFALSE\t%d\t%s\t%s%n",
                        expireTime, entry.getKey(), entry.getValue());
            }
        }
    }

    public String getCookieFilePath() {
        return getCookieFile().getAbsolutePath();
    }

    private File getCookieFile() {
        String path = config.getCookieFile();
        if (path.startsWith("src/")) {
            path = System.getProperty("user.dir") + "/" + path;
        }
        return new File(path);
    }
}