package skenav.core;

import org.apache.commons.io.IOUtils;
import org.bytedeco.javacpp.Loader;
import skenav.core.security.Crypto;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class VideoEncoder{
    String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
    public void encodeVideo(String filename, String uploaddirectory, String hlsfilename, String hlsdirectory) throws IOException {
        // get upload directory
        String filetype = parseFileType(filename);
        String pathToVideo = uploaddirectory + filename;
        if(filetype.equals("mp4")) {
            String ffprobe = Loader.load(org.bytedeco.ffmpeg.ffprobe.class);

            ProcessBuilder findcodec = new ProcessBuilder(ffprobe, "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=codec_name", "-of", "default=nokey=1:noprint_wrappers=1", pathToVideo);
            String badcodec = convertInputStreamToString(findcodec.start().getInputStream());
            String codec = badcodec.substring(0, badcodec.length() - 1);
            System.out.println("codec is " + codec);
            String h264 = "h264";
            System.out.println("string literal is " + h264);
            String hashfunkystring = Crypto.sha3(codec);
            String hashnormalstring = Crypto.sha3(h264);
            System.out.println("the hash of the derived string is :" + hashfunkystring);
            System.out.println("the hash of the declared string is :" + hashnormalstring);
            if (codec.equals(h264)) {
                System.out.println("h264 if statement test");
                encodeH264(filename, pathToVideo, hlsfilename, uploaddirectory, hlsdirectory);
            } else if (codec.equals("hevc")) {
                encodeHevc(filename, pathToVideo, hlsfilename, uploaddirectory, hlsdirectory);
            } else {
                System.out.println("encoding fail");
            }
        }
        else if (filetype.equals("mkv")){
            encodeMKV(filename, pathToVideo, hlsfilename, uploaddirectory, hlsdirectory);
        }
    }

    public void encodeMKV(String filename, String pathtovideo, String hlsfilename, String uploaddirectory, String hlsdirectory) {
        System.out.println("encoding mkv");
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", pathtovideo, "-codec", "copy", "-start_number", "0", "-hls_time", "10", "-hls_list_size", "0", "-f", "hls", hlsdirectory + OS.pathSeparator()+ hlsfilename);
        try{
            pb.inheritIO().start().waitFor();
        }catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void encodeH264(String filename, String pathtovideo, String hlsfilename, String uploaddirectory, String hlsdirectory){
        System.out.println("encode H264 method called");
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", pathtovideo, "-codec", "copy", "-start_number", "0", "-hls_time", "10", "-hls_list_size", "0", "-f", "hls", hlsdirectory + OS.pathSeparator() + hlsfilename);
        try {
            pb.inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    public void encodeHevc(String filename, String pathtovideo, String hlsfilename, String uploaddirectory, String hlsdirectory){
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", pathtovideo, "-c:v", "copy", "-start_number", "0", "-tag:v", "hvc1", "-hls_time", "10", "-hls_list_size", "0", "-hls_segment_type", "fmp4", "-hls_segment_filename", hlsdirectory + OS.pathSeparator() + "fileSequence%d.m4s", "-f", "hls", hlsdirectory + OS.pathSeparator() + hlsfilename);
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
    private String parseFileType(String filename) {
        String filetype;
        int i = filename.lastIndexOf('.');
        filetype = filename.substring(i+1);
        return filetype;
    }
}

