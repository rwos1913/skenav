package skenav.code.resources;

import skenav.code.views.HomeView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class HomeResources {
    @GET
    public HomeView Homeview() {
        return new HomeView();
    }
}
