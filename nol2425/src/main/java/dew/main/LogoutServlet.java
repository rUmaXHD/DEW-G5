package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession(false); // no crear si no existe
        if (session != null) {
            session.invalidate(); //  Destruye la sesión
        }

        // Opcional: limpiar cabeceras de autenticación BASIC (no se puede por código directamente)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Redirigir al inicio o a una pantalla de login
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}

