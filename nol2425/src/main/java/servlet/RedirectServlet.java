package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/redirect")
public class RedirectServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Si el usuario no está autenticado, redirigir al login
        if (request.getRemoteUser() == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Redirigir según el rol
        if (request.isUserInRole("rolalu")) {
            response.sendRedirect(request.getContextPath() + "/alumno/inicio.jsp");
        } else if (request.isUserInRole("rolpro")) {
            response.sendRedirect(request.getContextPath() + "/profesor/inicio.jsp");
        } else {
            // Si no tiene un rol válido, redirigir al login con error
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
} 