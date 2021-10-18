package skenav.code;

import org.bytedeco.javacpp.Loader;

import javax.security.auth.login.Configuration;
import java.io.IOException;

public class VideoEncoder implements Runnable{
    private String filename;
    String uploaddirectory;
    public VideoEncoder(String s, String uploaddirectory){
        filename = s;
        this.uploaddirectory = uploaddirectory;
    }
    public void run() {
        testString();
        // get upload directory
        String pathToVideo = uploaddirectory + "usercontent/" + filename;
        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", pathToVideo, "-codec", "copy", "-start_number", "0", "-hls_time", "10", "-hls_list_size", "0", "-f", "hls", "/Users/currycarr/usercontent/testhlsjava.m3u8");
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
