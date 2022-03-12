package skenav.core.resources;

import org.glassfish.jersey.media.multipart.FormDataParam;
import skenav.core.security.UserManagement;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("register")
@Produces(MediaType.TEXT_HTML)
public class RegisterResources {
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response register(
            @FormDataParam("regusername") String username,
            @FormDataParam("regpassword") String password,
            @FormDataParam("confregpassword") String confirmpassword,
            @FormDataParam("invitecode") String invitecode ) throws IOException {
        System.out.println(username);
        System.out.println(password);
        System.out.println(confirmpassword);
        System.out.println(invitecode);
        // TODO: check invite code
        String output = "backend register submit received";
        if (!password.equals(confirmpassword)){
            throw new WebApplicationException(400);
        }

        return Response.ok(output).build();
    }
}
