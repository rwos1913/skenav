package skenav.core.resources;

import skenav.core.views.FileMgrView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("filemgr")
@Produces(MediaType.TEXT_HTML)
public class FileMgrResources {
    @GET
    public FileMgrView FileMgrView() {
        return new FileMgrView();
    }
}
