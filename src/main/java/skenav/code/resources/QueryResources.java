package skenav.code.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import skenav.code.db.Database;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Array;
import java.util.ArrayList;
@Path("query")
@Produces(MediaType.APPLICATION_JSON)
public class QueryResources {
    Database database;
    public QueryResources(Database database) {
        this.database = database;
    }

    //@Path("viewfiles")
    @GET
    public String viewFilesToJSON(){
        ArrayList<ArrayList<String>> filebundle = database.viewFiles();
        System.out.println(filebundle);
        //create json objects
        ObjectMapper objectMapper = new ObjectMapper();
        String json = new String();
        try {
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(filebundle);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;

    }
}
