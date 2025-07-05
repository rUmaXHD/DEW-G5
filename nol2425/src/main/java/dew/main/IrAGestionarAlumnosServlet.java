package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/profesor/irAGestionar")
public class IrAGestionarAlumnosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        // ✅ Solo permitir acceso a profesores
        if (!req.isUserInRole("rolpro")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No autorizado");
            return;
        }

        // ✅ Obtener sesión activa
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        // ✅ Comprobar credenciales en sesión
        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

        if (key == null || key.isBlank()) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no válido");
            return;
        }

        // ✅ Comprobar que hay asignatura seleccionada
        String asignatura = req.getParameter("asignatura");

        if (asignatura != null && !asignatura.isBlank()) {
            session.setAttribute("asignaturaSeleccionada", asignatura);
            System.out.println(" Redirigiendo a gestionarAlumnos.jsp con asignatura: " + asignatura);
            req.getRequestDispatcher("/profesor/gestionarAlumnos.jsp").forward(req, resp);
        } else {
            System.out.println(" Asignatura no válida. Volviendo a inicio.jsp");
            resp.sendRedirect(req.getContextPath() + "/profesor/inicio.jsp");
        }
    }
}

