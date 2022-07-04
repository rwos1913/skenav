package skenav.core.resources;

import org.apache.commons.lang3.StringUtils;
import skenav.core.Cache;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(".well-known/acme-challenge/{token}")
public class CertificateResources {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getrequestedtoken(@PathParam("token") String requestedtoken) {
		String content = Cache.INSTANCE.getTlsAuthContent();
		String token = StringUtils.substringBefore(content, ".");
		System.out.println("token from resource is: " + token);
		if (!requestedtoken.equals(token)) {
			throw new WebApplicationException();
		}
		return Response.ok(token, MediaType.TEXT_PLAIN).build();

	}
}
