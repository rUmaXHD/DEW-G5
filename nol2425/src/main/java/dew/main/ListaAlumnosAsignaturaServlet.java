package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Servlet que devuelve, vía AJAX, la lista de alumnos de una asignatura concreta.
 * Solo puede ser accedido por usuarios con rol de profesor.
 */
@WebServlet("/profesor/listaAlumnos")
public class ListaAlumnosAsignaturaServlet extends HttpServlet {

    private static final String API_BASE_URL = "http://localhost:9090/CentroEducativo";

    /**
     * Maneja la petición GET para devolver los alumnos de una asignatura en formato JSON.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Verificar que el usuario tiene rol de profesor
        if (!req.isUserInRole("rolpro")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado.");
            return;
        }

        // Verificar parámetros obligatorios
        String asignatura = req.getParameter("asignatura");
        String ajax = req.getParameter("ajax");

        if (asignatura == null || asignatura.isBlank() || !"true".equalsIgnoreCase(ajax)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros.");
            return;
        }

        System.out.println("[INFO] Petición AJAX recibida:");
        System.out.println(" - asignatura: " + asignatura);
        System.out.println(" - ajax: " + ajax);

        // Verificar sesión activa
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("key") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida.");
            return;
        }

        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

        try {
            // Construir URL de la API para obtener alumnos de la asignatura
            String url = API_BASE_URL + "/asignaturas/" + asignatura + "/alumnos?key=" + key;
            System.out.println("[INFO] URL de consulta API: " + url);

            // Crear petición HTTP
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET();

            // Añadir cookie de sesión si existe
            if (jsessionId != null) {
                requestBuilder.header("Cookie", "JSESSIONID=" + jsessionId);
            }

            HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

            System.out.println("[INFO] Código de estado API: " + response.statusCode());
            System.out.println("[INFO] Cuerpo JSON recibido:\n" + response.body());

            if (response.statusCode() == 200) {
                // Devolver JSON directamente al cliente
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().write(response.body());
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error desde la API");
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Excepción al contactar con la API:");
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excepción al contactar con la API");
        }
    }
}

