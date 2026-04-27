package com.voice.voicespace.mp4tomp3.config;

import lombok.Data;


public enum UtilsCommand {
    YT_DLP("C:\\Users\\zym\\AppData\\Local\\Programs\\Python\\Python313\\Scripts\\yt-dlp.exe"),
    FFMPEG("F:\\util\\video_voice_utils\\ffmpeg\\bin\\ffmpeg.exe");

    String command;

    public String getCommand(){
        return command;
    }

    UtilsCommand(String s) {
        command = s;
    }
}
