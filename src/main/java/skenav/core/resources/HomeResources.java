package skenav.core.resources;

import skenav.core.views.LoginView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
//TODO: Make this uri go to the file manager page or redirect to login if filter denies access
@Path("/")
@Produces(MediaType.TEXT_HTML)
public class HomeResources {
    @GET
    public LoginView Homeview() {
        return new LoginView();
    }
}
