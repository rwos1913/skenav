package skenav.core;

import org.apache.commons.io.IOUtils;
import org.bytedeco.javacpp.Loader;
import skenav.core.security.Crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class VideoEncoder{
    // TODO: Some mp4 files are h264 video some are hevc, support hevc
    String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
    public void encodeVideo(String filename, String uploaddirectory, String hlsfilename) throws IOException {
        // get upload directory
        String ffprobe = Loader.load(org.bytedeco.ffmpeg.ffprobe.class);
        String pathToVideo = uploaddirectory + "usercontent" + OS.pathSeparator() + filename;
        ProcessBuilder findcodec = new ProcessBuilder(ffprobe, "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=codec_name", "-of", "default=nokey=1:noprint_wrappers=1", pathToVideo);
        String badcodec = convertInputStreamToString(findcodec.start().getInputStream());
        String codec = badcodec.substring(0, badcodec.length() - 1);
        /*try {
            codec = IOUtils.toString(findcodec.start().getInputStream(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        System.out.println("codec is " + codec);
        String h264 = "h264";
        //byte[] bytes = rawh264.getBytes("UTF-8");
        //String h264 = new String(bytes, "UTF-8");
        System.out.println("string literal is " + h264);
        String hashfunkystring = Crypto.sha3(codec);
        String hashnormalstring = Crypto.sha3(h264);
        System.out.println("the hash of the derived string is :" + hashfunkystring);
        System.out.println("the hash of the declared string is :" + hashnormalstring);
        if (codec.equals(h264)) {
            System.out.println("h264 if statement test");
            encodeH264(filename, pathToVideo, hlsfilename, uploaddirectory);
        }
        else if (codec.equals("hevc")) {
            encodeHevc(filename, pathToVideo, hlsfilename, uploaddirectory);
        }
        else {
            System.out.println("you fucked up");
        }
    }

    public void encodeH264(String filename, String pathtovideo, String hlsfilename, String uploaddirectory){
        System.out.println("encode H264 method called");
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", pathtovideo, "-codec", "copy", "-start_number", "0", "-hls_time", "10", "-hls_list_size", "0", "-f", "hls", uploaddirectory +"usercontent" + OS.pathSeparator() + "hlstestfolder" + OS.pathSeparator() + hlsfilename);
        try {
            pb.inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    public void encodeHevc(String filename, String pathtovideo, String hlsfilename, String uploaddirectory){
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", pathtovideo, "-c:v", "copy", "-start_number", "0", "-tag:v", "hvc1", "-hls_time", "10", "-hls_list_size", "0", "-hls_segment_type", "fmp4", "-hls_segment_filename", uploaddirectory + "usercontent" + OS.pathSeparator() + "hlstestfolder"+ OS.pathSeparator() + "fileSequence%d.m4s", "-f", "hls", uploaddirectory +"usercontent" + OS.pathSeparator() + "hlstestfolder" + OS.pathSeparator() + hlsfilename);
        try {
            pb.inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    private static String convertInputStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }
}
