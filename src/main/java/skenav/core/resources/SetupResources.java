package skenav.core.resources;

import org.glassfish.jersey.media.multipart.FormDataParam;
import skenav.core.Cache;
import skenav.core.OS;
import skenav.core.Setup;
import skenav.core.db.Database;
import skenav.core.security.Crypto;
import skenav.core.views.SetupView;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
//TODO: pass upload directory as html variable instead of request
@Path("setup")
@Produces(MediaType.TEXT_HTML)
public class SetupResources {
    @GET
    public SetupView Setupview() {
        return new SetupView(getDefaultUploadDirectory());
    }

    public String getDefaultUploadDirectory() {
        String defaultuploaddirectory = OS.getHomeDirectory() + "usercontent" + OS.pathSeparator();
        return defaultuploaddirectory;
    }

    @POST
    @Path("submitowner")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response registerAdmin (
            @FormDataParam("username") String username,
            @FormDataParam("password") String password,
            @FormDataParam("confpassword") String confirmpassword,
            @FormDataParam("uploaddirectory") String uploaddirectory
    ){
        System.out.println("username is" + username);
        System.out.println("password is" + password);
        System.out.println("confirmed password is" + confirmpassword);
        System.out.println("Upload directory is" + uploaddirectory);
        if (!password.equals(confirmpassword)) {
            throw new WebApplicationException("passwords do not match", 400);
        }
        //TODO: if passwords do not match show message on front end
        System.out.println("uploaddirectory in setup resource is: " + uploaddirectory);
        Cache cache = Cache.INSTANCE;
        cache.setUploaddirectory(uploaddirectory);
        Setup setup = new Setup();
        setup.finalizeSetup(true, username, password);
        Setup.addUserHlsDirectory(username);
        /*Database database = new Database();
        Database.createTable(uploaddirectory);
        database.addUser(username,hashedpassword,0);
        database.addToAppData("upload directory", uploaddirectory);
        */


        return Response.ok().build();
    }

}
