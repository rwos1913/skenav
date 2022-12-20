package skenav.core.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import skenav.core.Cache;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthFilter implements Filter {
	//byte[] key = Cache.INSTANCE.getCookieKey();


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest servletrequest = (HttpServletRequest) request;
			HttpServletResponse servletresponse = (HttpServletResponse) response;
			String path = servletrequest.getRequestURI();
			System.out.println("path from filter is" + path);
			if (
					path.equals("/login") || path.equals("/static/js/login.js") || path.equals("/static/css/login.css") || path.equals("/static/image/logo.svg") || path.equals("/login/submitlogin") || path.equals("/setup") || path.equals("/setup/submitowner") || path.equals("/static/js/setup.js") || path.startsWith("/.well-known/acme-challenge/")
			){
				chain.doFilter(request, response);
				return;
			}
			else {
				Cookie[] cookies;
				cookies = servletrequest.getCookies();
				String cookiename = "SkenavAuth";
				String cookievalue = null;
				if (cookies == null) {
					servletresponse.sendRedirect("/login");
				}
				else {
					for (int i = 0; i < cookies.length; i++) {
						Cookie cookie = cookies[i];
						if (cookiename.equals(cookie.getName())) {
							cookievalue = cookie.getValue();
							System.out.println("value is" + cookievalue);
							break;
						}
					}
					if (cookiename != null) {
						if (checkAuthN(cookievalue) == true) {
							chain.doFilter(request, response);
							return;
						} else {
							servletresponse.sendRedirect("/login");
						}
					} else {
						servletresponse.sendRedirect("/login");
					}
				}


			}
			chain.doFilter(request, response);
		}
	}
	//TODO: make each skenav generate a unique cookie name
	private boolean checkAuthN (String ciphertext) {
		System.out.println("ct is" + ciphertext);
		//System.out.println("key is" + key);
		byte[] key = Cache.INSTANCE.getCookieKey();
		String encodedkey = Crypto.base64Encode(key);
		System.out.println("generated test key is" + encodedkey);
		String plaintext = Crypto.decrypt(ciphertext, key);
		System.out.println("plaintext is" + plaintext);
		if (plaintext != null) {
			return true;
		}
		else{
			return false;
		}
	}
	private Map<String, String> deserializeServletCookie (String cookievalue) throws JsonProcessingException {
		byte[] key = Cache.INSTANCE.getCookieKey();
		String decryptedjson = Crypto.decrypt(cookievalue, key);
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> cookiemap = objectMapper.readValue(decryptedjson, HashMap.class);
		return cookiemap;
	}
}
