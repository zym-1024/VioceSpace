package com.voice.voicespace.mp4tomp3.domain.utils;

import com.voice.voicespace.mp4tomp3.config.UtilsCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebVideoAudioExtractor {

    public static boolean extractVideoFromWebVideo(String videoUrl, String outputPath, String cookiePath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                UtilsCommand.YT_DLP.getCommand(),
                "--cookies", cookiePath,
                "--no-check-certificates",
                "--extractor-args", "bilibili:player_webpage=1",
                "--user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36",
                "-o", outputPath,
                videoUrl
        );
        return commandExec(pb);
    }

    public static boolean extractAudioFromWebVideo(String videoUrl, String outputPath, String cookiePath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                UtilsCommand.YT_DLP.getCommand(),
                "-x",
                "--cookies", cookiePath,
                "--no-check-certificates",
                "--extractor-args", "bilibili:player_webpage=1",
                "--user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36",
                "--audio-quality", "0",
                "-o", outputPath,
                videoUrl
        );
        return commandExec(pb);
    }

    public static boolean extractPlaylistAudioFromWebVideo(String videoUrl, String outputPath, String cookiePath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                UtilsCommand.YT_DLP.getCommand(),
                "-x",
                "--yes-playlist",
                "--cookies", cookiePath,
                "--no-check-certificates",
                "--extractor-args", "bilibili:player_webpage=1",
                "--user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36",
                "--audio-quality", "0",
                "-o", outputPath,
                videoUrl
        );
        return commandExec(pb);
    }

    private static boolean commandExec(ProcessBuilder pb) throws IOException, InterruptedException {
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("下载失败, exit code: " + exitCode);
        }
        return true;
    }
}