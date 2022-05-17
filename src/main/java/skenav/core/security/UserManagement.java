package skenav.core.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import skenav.core.Cache;

import javax.ws.rs.core.Cookie;
import java.util.HashMap;
import java.util.Map;

public class UserManagement {
	public static Map cookieToMap(Cookie cookie) throws JsonProcessingException {
		String ciphertext = cookie.getValue();
		byte[] key = Cache.INSTANCE.getCookieKey();
		String plaintextjson = Crypto.decrypt(ciphertext, key);
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> cookiemap = objectMapper.readValue(plaintextjson, HashMap.class);
		return cookiemap;
	}
}
