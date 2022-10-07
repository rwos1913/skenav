package skenav.core.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import skenav.core.OS;
import skenav.core.ThreadManagement;
import skenav.core.VideoEncoder;
import skenav.core.db.Database;
import skenav.core.security.UserManagement;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Path("video")
@Produces(MediaType.APPLICATION_JSON)
public class VideoResources{
    Database database;
    public VideoResources(Database database)  {
        this.database = database;
    }

    @GET
    public String videoRequestHandler(
            @QueryParam("name") String filename,
            @CookieParam("SkenavAuth") Cookie cookie
    ) throws IOException, InterruptedException {
        Map<String, String> map = UserManagement.cookieToMap(cookie);
        String username = map.get("username");
        if(database.checkFileOwner(filename, username) == false) {
            throw new WebApplicationException(401);
        }
        String uploaddirectory = OS.getUserContentDirectory();
        String hlsdirectory = uploaddirectory + "hls" + OS.pathSeparator() + username;
        FileUtils.cleanDirectory(new File(hlsdirectory));
        String hlsfilename = parseHlsFileName(filename);
        // calls method to encode video
        VideoEncoder videoEncoder = new VideoEncoder();
        videoEncoder.encodeVideo(filename,uploaddirectory,hlsfilename, hlsdirectory);
        //ThreadManagement threadManagement = new ThreadManagement();
        //threadManagement.executeThread(filename, uploadDirectory, hlsfilename);
        // waits for 3 second
        //TimeUnit.SECONDS.sleep(3);
        //System.out.println("testing time delay");
        // writes filename to json and returns to front end javascript
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> videomap = new HashMap<>();
        videomap.put("username", username);
        videomap.put("hlsfilename", hlsfilename);
        String json = new String();
        try {
            // sets string json as the filebundle string when converted to json
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(videomap);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    @GET
    @Path("hlsfiles/{filename}")
    @Produces({"video/mp2t", "video/mp4", "application/x-mpegURL"})
    public Response getHlsFiles (
            @CookieParam("SkenavAuth") Cookie cookie,
            @PathParam("filename") String filename
    ) throws JsonProcessingException {
        Map<String, String> map = UserManagement.cookieToMap(cookie);
        String username = map.get("username");
        String hlsdirectory = OS.getUserContentDirectory() +"hls" + OS.pathSeparator() + username;
        String extension = FilenameUtils.getExtension(filename);
        String contenttype = null;
        File file = new File(hlsdirectory + OS.pathSeparator() + filename);
        switch (extension) {
            case "m3u8":
                contenttype = "application/x-mpegURL";
                break;

            case "fmp4":
                contenttype = "video/mp4";
                break;

            case "ts":
                contenttype = "video/mp2t";
                break;
        }
        return Response.ok(file, contenttype).build();
    }
    private String parseHlsFileName(String filename) {
        String noextfilename = FilenameUtils.removeExtension(filename);
        String hlsfilename = noextfilename + ".m3u8";
        System.out.println(hlsfilename);
        return hlsfilename;
    }
    //TODO: Make encoding methods for other types of video

}
