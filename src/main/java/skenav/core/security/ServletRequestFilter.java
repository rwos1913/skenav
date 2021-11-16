package skenav.core.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;

public class ServletRequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("asset filter");



        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
        String path = httpServletRequest.getRequestURI();
        System.out.println("request URI is: " + path);
        if (path.startsWith("/") || path.startsWith("/static")) {
            HttpServletResponse httpServletResponse = ((HttpServletResponse) response);
            httpServletResponse.addHeader("ARTUR", "test");
            chain.doFilter(request, httpServletResponse);
        }
        else {
            throw new WebApplicationException(401);
        }


    }

    @Override
    public void destroy() {

    }
}
