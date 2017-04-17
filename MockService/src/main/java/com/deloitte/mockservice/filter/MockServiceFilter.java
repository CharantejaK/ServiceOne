 package com.deloitte.mockservice.filter;

   import org.springframework.stereotype.Component;

import static com.deloitte.mockservice.controller.MockupService.MOCKSERVICE;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Note this is a very simple CORS filter that is wide open.
 * This would need to be locked down.
 */
@Component
public class MockServiceFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        String requestUri = request.getRequestURI();         
        if (requestUri.startsWith(MOCKSERVICE) && !requestUri.equals(MOCKSERVICE)) {
        	request.setAttribute("serviceName", requestUri);
        	request.getRequestDispatcher(MOCKSERVICE).forward(request, response);
        } else{   
        	request.getAttribute("serviceName");
        	chain.doFilter(req, res);
        }
    }

    public void init(FilterConfig filterConfig) {}

    public void destroy() {}

}