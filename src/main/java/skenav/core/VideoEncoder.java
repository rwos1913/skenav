package skenav.core;

import org.bytedeco.javacpp.Loader;

import java.io.IOException;

public class VideoEncoder implements Runnable{
    private String filename;
    String hlsfilename;
    String uploaddirectory;
    public VideoEncoder(String s, String uploaddirectory, String hlsfilename){
        filename = s;
        this.hlsfilename = hlsfilename;
        this.uploaddirectory = uploaddirectory;
    }
    public void run() {
        testString();
        // get upload directory
        String pathToVideo = uploaddirectory + "usercontent/" + filename;
        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", pathToVideo, "-codec", "copy", "-start_number", "0", "-hls_time", "10", "-hls_list_size", "0", "-hls_playlist_type", "event", "-f", "hls", uploaddirectory +"usercontent/hlstestfolder/" + hlsfilename);
        try {
            pb.inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void testString() {
        System.out.println("this string is printed from a thread");
    }
}
