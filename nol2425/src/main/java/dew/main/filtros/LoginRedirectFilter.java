package dew.main.filtros;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class LoginRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (request.getRemoteUser() != null 
            && request.getSession().getAttribute("key") == null
            && !request.getRequestURI().endsWith("PostLoginServlet")
            && !request.getRequestURI().contains("j_security_check")) {

            response.sendRedirect(request.getContextPath() + "/PostLoginServlet");
            return;
        }

        chain.doFilter(req, resp);
    }
}
