package skenav.core.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import skenav.core.db.Database;
import skenav.core.security.Crypto;
import skenav.core.security.UserManagement;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Map;

@Path("query")
@Produces(MediaType.APPLICATION_JSON)
public class QueryResources {
    // scoping Database class to this resource
    Database database;
    public QueryResources(Database database) {
        this.database = database;
    }

    //@Path("viewfiles")
    // get request that returns arraylist called from viewFiles as json string to client
    @GET
    public String viewFilesToJSON(
            // adding queryparams to be called by client using js
            @DefaultValue("")
            @QueryParam("search") String search,
            @DefaultValue("50")
            @QueryParam("limit") int limit,
            @DefaultValue("0")
            @QueryParam("sort") int sortby,
            @CookieParam("SkenavAuth") Cookie cookie

            // sortby: 0 sorts by most recently added 1 least recently added 2 alphabetical by file name 3 reverse alphabetical by filename
    ){
        // sets filebundle object as 2d arraylist from Database class
        Map<String, String> cookiemap;
        try {
            cookiemap = UserManagement.cookieToMap(cookie);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String owner = cookiemap.get("username");

        ArrayList<ArrayList<String>> filebundle = database.viewFiles(search, limit, sortby, owner);
        System.out.println(filebundle);
        //create json objects
        ObjectMapper objectMapper = new ObjectMapper();
        String json = new String();
        try {
            // sets string json as the filebundle string when converted to json
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(filebundle);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;

    }
}
