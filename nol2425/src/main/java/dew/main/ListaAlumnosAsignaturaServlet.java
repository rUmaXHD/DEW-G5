package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@WebServlet("/profesor/listaAlumnos")
public class ListaAlumnosAsignaturaServlet extends HttpServlet {

    private static final String API_BASE_URL = "http://localhost:9090/CentroEducativo";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Verificación de rol
        if (!req.isUserInRole("rolpro")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado.");
            return;
        }

        // Verificación de parámetros
        String asignatura = req.getParameter("asignatura");
        String ajax = req.getParameter("ajax");

        if (asignatura == null || asignatura.isBlank() || !"true".equalsIgnoreCase(ajax)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros.");
            return;
        }
        
        System.out.println("DEBUG - Recibida petición AJAX:");
        System.out.println("asignatura = " + req.getParameter("asignatura"));
        System.out.println("ajax = " + req.getParameter("ajax"));

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("key") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida.");
            return;
        }

        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

        try {
            // Construir URL de la API
            String url = API_BASE_URL + "/asignaturas/" + asignatura + "/alumnos?key=" + key;
            System.out.println("🔗 Consulta API alumnos: " + url);

            // Crear petición HTTP
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET();

            if (jsessionId != null) {
                requestBuilder.header("Cookie", "JSESSIONID=" + jsessionId);
            }

            HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

            System.out.println("📥 Código de estado API: " + response.statusCode());
            System.out.println("📦 Respuesta API: " + response.body());

            if (response.statusCode() == 200) {
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().write(response.body()); // devolvemos tal cual
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error desde la API");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excepción al contactar con la API");
        }
    }
}

