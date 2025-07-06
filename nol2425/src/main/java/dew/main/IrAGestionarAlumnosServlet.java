package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Servlet intermedio que guarda la asignatura seleccionada por el profesor
 * en la sesión y redirige a la vista de gestión de alumnos.
 */
@WebServlet("/profesor/irAGestionar")
public class IrAGestionarAlumnosServlet extends HttpServlet {

    /**
     * Verifica el rol, recupera la asignatura seleccionada y la guarda en sesión
     * para redirigir al JSP correspondiente.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        // Validar que el usuario tenga el rol de profesor
        if (!req.isUserInRole("rolpro")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No autorizado");
            return;
        }

        // Obtener sesión activa
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        // Verificar credenciales básicas de sesión
        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

        if (key == null || key.isBlank()) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no válido");
            return;
        }

        // Recuperar acrónimo de asignatura desde la URL
        String asignatura = req.getParameter("asignatura");

        if (asignatura != null && !asignatura.isBlank()) {
            session.setAttribute("asignaturaSeleccionada", asignatura);
            System.out.println("[INFO] Asignatura seleccionada: " + asignatura);
            req.getRequestDispatcher("/profesor/gestionarAlumnos.jsp").forward(req, resp);
        } else {
            System.out.println("[WARN] Asignatura no válida o vacía. Redirigiendo a inicio.jsp");
            resp.sendRedirect(req.getContextPath() + "/profesor/inicio.jsp");
        }
    }
}

