package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * AccesoServlet actúa como punto intermedio inicial de acceso a la aplicación.
 * Redirige siempre al LoginServlet, que está protegido con autenticación BASIC.
 *
 * Su uso permite:
 * - Controlar el acceso centralizadamente.
 * - Asegurar que solo usuarios autenticados continúan el flujo.
 */
@WebServlet("/AccesoServlet")
public class AccesoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Procesa peticiones GET redirigiendo siempre al LoginServlet.
     *
     * @param request  petición HTTP entrante
     * @param response respuesta HTTP saliente
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error de E/S
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Log de depuración (puedes quitarlo en producción)
        System.out.println("[INFO] AccesoServlet redirige a LoginServlet");

        // Redirige al LoginServlet, que sí está protegido por autenticación BASIC
        response.sendRedirect("LoginServlet");
    }
}
