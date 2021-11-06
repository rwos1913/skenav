package skenav.core.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import skenav.core.OS;
import skenav.core.ThreadManagement;
import skenav.core.VideoEncoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Path("video")
@Produces(MediaType.APPLICATION_JSON)
public class VideoResources{
String uploadDirectory;
    public VideoResources(String uploadDirectory)  {
        this.uploadDirectory = uploadDirectory;
    }

    @GET
    public String videoRequestHandler(
            @QueryParam("name") String filename
    ) throws IOException, InterruptedException {
        System.out.println(filename);
        FileUtils.cleanDirectory(new File(uploadDirectory + OS.pathSeparator() + "usercontent" + OS.pathSeparator() + "hlstestfolder"));
        String hlsfilename = parseHlsFileName(filename);
        // calls method to encode video
        VideoEncoder videoEncoder = new VideoEncoder();
        videoEncoder.encodeVideo(filename,uploadDirectory,hlsfilename);
        //ThreadManagement threadManagement = new ThreadManagement();
        //threadManagement.executeThread(filename, uploadDirectory, hlsfilename);
        // waits for 3 second
        //TimeUnit.SECONDS.sleep(3);
        //System.out.println("testing time delay");
        // writes filename to json and returns to front end javascript
        ObjectMapper mapper = new ObjectMapper();
        String json = new String();
        try {
            // sets string json as the filebundle string when converted to json
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(hlsfilename);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
    private String parseHlsFileName(String filename) {
        String noextfilename = FilenameUtils.removeExtension(filename);
        String hlsfilename = noextfilename + ".m3u8";
        System.out.println(hlsfilename);
        return hlsfilename;
    }
    //TODO: Make encoding methods for other types of video

}
