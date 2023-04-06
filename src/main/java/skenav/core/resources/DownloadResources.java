package skenav.core.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import skenav.core.OS;
import skenav.core.db.Database;
import skenav.core.security.UserManagement;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("download")
public class DownloadResources {
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(
			@HeaderParam("File-Name") String filename,
			@CookieParam("SkenavAuth") Cookie cookie
			) throws JsonProcessingException {
		String user = UserManagement.parseCookieForUserName(cookie);
		String filepath = OS.getUserFilesDirectory(user) + filename;
		Database database = new Database();
		if(!database.checkFileOwner(filename, user)){
			throw new WebApplicationException(Response.Status.FORBIDDEN);

		}

		StreamingOutput filestream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try {
					java.nio.file.Path path = Paths.get(filepath);
					byte[] data = Files.readAllBytes(path);
					output.write(data);
					output.flush();
				}catch (Exception e){
					throw new WebApplicationException("file not found");
				}
			}
		};
		return Response
				.ok(filestream, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition", "attachment; filename = " + filename)
				.build();
	}
}
