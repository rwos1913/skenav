package skenav.core.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataParam;
import skenav.core.db.Database;
import skenav.core.security.Crypto;
import skenav.core.security.UserManagement;
import skenav.core.views.SettingsView;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Path("settings")
@Produces(MediaType.TEXT_HTML)
public class SettingsResources {
	@GET
	public SettingsView settingsView() {return new SettingsView();}

	@POST
	@Path("generateinvite")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateInvite(
			@CookieParam("SkenavAuth") Cookie cookie,
			@FormDataParam("nickname") String nickname
			) throws JsonProcessingException {
		Map<String,String> map = UserManagement.cookieToMap(cookie);
		String inviter = map.get("username");
		String authlevelstring = map.get("authorization");
		int authlevel = Integer.parseInt(authlevelstring);
		if (authlevel != 0 && authlevel != 1){
			throw new WebApplicationException(400);
		}
		UUID uuid = UUID.randomUUID();
		String invitecode = uuid.toString();
		Database database = new Database();
		database.addInvite(invitecode,nickname,inviter);
		Map<String, String> returneduserinfo = new HashMap<>();
		returneduserinfo.put("invitecode", invitecode);
		returneduserinfo.put("nickname" , nickname);
		ObjectMapper objectMapper = new ObjectMapper();
		String json = new String();
		try {
			json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(returneduserinfo);
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.ok().entity(json).build();
	}
}
