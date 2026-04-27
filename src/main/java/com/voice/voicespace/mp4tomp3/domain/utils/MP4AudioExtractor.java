package com.voice.voicespace.mp4tomp3.domain.utils;

import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.boxes.AudioSampleEntry;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.TrakBox;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.containers.mp4.demuxer.MP4DemuxerTrack;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import static org.jcodec.common.io.NIOUtils.readableChannel;
import static org.jcodec.common.io.NIOUtils.writableChannel;

public class MP4AudioExtractor {

    public static void extractAudio(String mp4Path, String outputPath) throws IOException {
        FileChannelWrapper channel = NIOUtils.readableChannel(new File(mp4Path));
        // 解析MP4文件
        MP4Demuxer demuxer = MP4Demuxer.createMP4Demuxer(channel);

        // 查找音频轨道
        TrakBox audioTrack = findAudioTrack(demuxer);
        if (audioTrack == null) {
            System.out.println("未找到音频轨道");
            return;
        }
        MP4DemuxerTrack audioDemuxerTrack = new MP4DemuxerTrack(demuxer.getMovie(), audioTrack, channel);
        SampleEntry sampleEntry = audioTrack.getSampleEntries()[0];

        if (!(sampleEntry instanceof AudioSampleEntry audioEntry)) {
            System.out.println("轨道不是音频类型");
            return;
        }

        System.out.println("音频格式: " + audioEntry.getFormat());
        System.out.println("采样率: " + audioEntry.getSampleRate());
        System.out.println("声道数: " + audioEntry.getChannelCount());

        // 创建输出文件
        WritableByteChannel out = writableChannel(new File(outputPath));

        // 提取音频帧
        ByteBuffer frame = ByteBuffer.allocate(1920 * 1080); // 分配足够大的缓冲区
        while (true) {
            frame.clear();
            if (audioDemuxerTrack.getNextFrame(frame) == null) {
                break;
            }
            frame.flip();
            out.write(frame);
        }

        NIOUtils.closeQuietly(out);
        NIOUtils.closeQuietly(channel);
    }

    private static TrakBox findAudioTrack(MP4Demuxer demuxer) {
        for (TrakBox trak : demuxer.getMovie().getTracks()) {
            if (trak.getSampleEntries()[0] instanceof AudioSampleEntry) {
                return trak;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            extractAudio("D:\\database\\video\\【原创曲】阶段.mp4", "D:\\database\\video\\【原创曲】阶段.raw");
            System.out.println("音频提取完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}