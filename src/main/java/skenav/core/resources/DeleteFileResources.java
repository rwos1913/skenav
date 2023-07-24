package skenav.core.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import skenav.core.OS;
import skenav.core.db.Database;
import skenav.core.security.UserManagement;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;


@Path("delete")
@Produces(MediaType.TEXT_HTML)
public class DeleteFileResources {

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response deleteFile (
			@CookieParam("SkenavAuth") final Cookie cookie,
			String filename) throws WebApplicationException, JsonProcessingException {

		String user = UserManagement.parseCookieForUserName(cookie);
		Database database = new Database();
		if (database.checkFileOwner(filename, user)){
			File file = new File(OS.getUserFilesDirectory(user) + filename);
			if (file.delete()) {
				database.deleteFile(filename);
				System.out.println("file deleted");
			}
			else {
				throw new WebApplicationException("file failed to delete");
			}
		}
		else throw new WebApplicationException("invalid credentials");
		return Response.ok().build();
	}

}
