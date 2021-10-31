package skenav.core;

import org.bytedeco.javacpp.Loader;

import java.io.IOException;

public class VideoEncoder{
    public void encodeVideo(String filename, String uploaddirectory, String hlsfilename) {
        // get upload directory
        String pathToVideo = uploaddirectory + "usercontent" + OS.pathSeparator() + filename;
        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", pathToVideo, "-codec", "copy", "-start_number", "0", "-hls_time", "10", "-hls_list_size", "0", "-f", "hls", uploaddirectory +"usercontent" + OS.pathSeparator() + "hlstestfolder" + OS.pathSeparator() + hlsfilename);
        try {
            pb.inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
