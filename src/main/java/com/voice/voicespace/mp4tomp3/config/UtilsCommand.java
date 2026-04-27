package com.voice.voicespace.mp4tomp3.config;

import lombok.Data;


public enum UtilsCommand {
    YT_DLP(System.getenv("YT_DLP_PATH")),
    FFMPEG(System.getenv("FFMPEG_PATH"));

    String command;

    public String getCommand(){
        return command;
    }

    UtilsCommand(String s) {
        command = s;
    }
}
