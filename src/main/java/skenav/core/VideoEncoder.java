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
        //String filetype = parseFileType(filename);
        String pathtovideo = uploaddirectory + filename;
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", pathtovideo, "-c:v", "copy", "-start_number", "0", "-tag:v", "hvc1", "-hls_time", "10", "-hls_list_size", "0", "-hls_segment_type", "fmp4", "-hls_segment_filename", hlsdirectory + OS.pathSeparator() + "fileSequence%d.m4s", "-f", "hls", hlsdirectory + OS.pathSeparator() + hlsfilename);
        try {
            pb.inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
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
    /*private String parseFileType(String filename) {
        String filetype;
        int i = filename.lastIndexOf('.');
        filetype = filename.substring(i+1);
        return filetype;
    }*/
}

