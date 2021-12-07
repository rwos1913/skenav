package skenav.core.resources;

import skenav.core.views.LoginView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class HomeResources {
    //Database database;

    //public void HomeResources (Database database) {
    //    this.database = database;
    //}

    @GET
    public LoginView Homeview() {
        return new LoginView();
    }
}
