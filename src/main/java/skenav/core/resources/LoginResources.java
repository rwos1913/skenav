package skenav.core.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.Uri;
import skenav.core.Cache;
import skenav.core.db.Database;
import skenav.core.security.Crypto;
import skenav.core.views.LoginView;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import static org.eclipse.jetty.http.HttpCookie.SAME_SITE_STRICT_COMMENT;

@Path("login")
@Produces(MediaType.TEXT_HTML)
public class LoginResources {
    @GET
    public LoginView LoginView() {return new LoginView();}

    @POST
    @Path("submitlogin")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response login(
            @FormDataParam("username") final String username,
            @FormDataParam("password") final String password) throws IOException, URISyntaxException {
        System.out.println("username is: " + username);
        System.out.println("password is: " + password);
        String output = "this string is returned from the backend from the login form post request";
        if(Crypto.checkPassword(username,password)) {
            Database database = new Database();
            Integer authinteger = database.getAuthzLevel(username);
            String authzlevel;
            String encryptedjson;
            String unencryptedjson;
            if (authinteger == null) {
                throw new WebApplicationException(400);
            }
            else{
                authzlevel = authinteger.toString();
            }
            Map<String, String> map = new HashMap<>();
            map.put("username", username);
            map.put("authorization", authzlevel);
            ObjectMapper mapper = new ObjectMapper();
            unencryptedjson = mapper.writeValueAsString(map);
            System.out.println(unencryptedjson);
            byte[] key = Cache.INSTANCE.getCookieKey();
            encryptedjson = Crypto.encrypt(unencryptedjson, key);
            URI indexpage = new URI("/");
            //NewCookie cookie = new NewCookie("encryptedjson", encryptedjson,"/", "", "auth cookie", 100000, false);
            return Response.ok().header("Set-Cookie", "SkenavAuth=" + encryptedjson + "; SameSite=Strict; Path=/").build();
        }
        else{
            throw new WebApplicationException("wrong password", 403);
        }
    }
}
