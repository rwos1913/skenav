package skenav.code.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.bytecode.stackmap.BasicBlock;
import org.bytedeco.javacpp.Loader;
import skenav.code.ThreadManagement;
import skenav.code.VideoEncoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.io.IOException;

@Path("video")
@Produces(MediaType.APPLICATION_JSON)
public class VideoResources{
String uploadDirectory;
    public VideoResources(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    @GET
    public String videoRequestHandler(
            @QueryParam("name") String filename
    ) throws IOException, InterruptedException {
        System.out.println(filename);
        // call encoder thread thing
        //Runnable r1 = new VideoEncoder(filename);
        ThreadManagement threadManagement = new ThreadManagement();
        threadManagement.executeThread(filename, uploadDirectory);
        System.out.println("testing if this thread works");
        ObjectMapper mapper = new ObjectMapper();
        String json = new String();
        try {
            // sets string json as the filebundle string when converted to json
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(filename);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
// encodes video from mp4to hls
    //TODO: Make encoding methods for other types of video

}
