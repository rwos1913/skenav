package skenav.core.resources;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("login")
@Produces(MediaType.TEXT_HTML)
public class LoginResources {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response login(
            @FormDataParam("username") final String username,
            @FormDataParam("password") final String password) throws IOException{
        System.out.println("username is: " + username);
        System.out.println("password is:" + password);
        String output = "this string is returned from the backend from the login form post request";
        return Response.ok(output).build();
    }
}
