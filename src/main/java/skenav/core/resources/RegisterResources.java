package skenav.core.resources;

import org.glassfish.jersey.media.multipart.FormDataParam;
import skenav.core.Setup;
import skenav.core.db.Database;
import skenav.core.security.Crypto;

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
        Database database = new Database();
        if (!database.checkInviteCode(invitecode) || !password.equals(confirmpassword)){
            throw new WebApplicationException(400);
        }
        String hashedpassword = Crypto.hashPassword(password);
        database.addUser(username, hashedpassword, 2);
        Setup.addNewUserDirectories(username);

        String output = "backend register submit received";

        return Response.ok(output).build();
    }
}
