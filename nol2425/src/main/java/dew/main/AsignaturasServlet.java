package dew.main;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Arrays;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/AsignaturasServlet")
public class AsignaturasServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String BASE_URL = "http://localhost:9090/CentroEducativo";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarAsignaturas(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarAsignaturas(request, response);
    }

    private void procesarAsignaturas(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	
        HttpSession session = request.getSession();
        String dni = (String) session.getAttribute("dni");
        String key = (String) session.getAttribute("key");
        System.out.println(key);

        if (dni == null || key == null || dni.isBlank() || key.isBlank()) {
            mostrarAlertaError(response, "Sesión inválida o expirada.");
            return;
        }

        boolean esAlumno = request.isUserInRole("rolalu");
        boolean esProfesor = request.isUserInRole("rolpro");

        if (!esAlumno && !esProfesor) {
            mostrarAlertaError(response, "Rol no reconocido.");
            return;
        }

        try {
            String[] asignaturas = obtenerAsignaturasDesdeAPI(dni, key, esAlumno);
            
            // Configurar respuesta JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            if (asignaturas == null || asignaturas.length == 0) {
                response.getWriter().write("[]");
            } else {
                response.getWriter().write(Arrays.toString(asignaturas));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al obtener asignaturas\"}");
        }
    }

    private String[] obtenerAsignaturasDesdeAPI(String dni, String key, boolean esAlumno) throws Exception {
        String endpoint = esAlumno ? "/alumnos/" : "/profesores/";
        String url = BASE_URL + endpoint + dni + "?key=" + key;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(peticion, HttpResponse.BodyHandlers.ofString());
        System.out.println("Respuesta CentroEducativo: " + response.body());

        if (response.statusCode() == 200) {
            String body = response.body();

            // Buscar manualmente el array de asignaturas
            int start = body.indexOf("\"asignaturas\"");
            if (start == -1) return new String[0];

            int arrayStart = body.indexOf("[", start);
            int arrayEnd = body.indexOf("]", arrayStart);

            if (arrayStart == -1 || arrayEnd == -1) return new String[0];

            String rawArray = body.substring(arrayStart + 1, arrayEnd).replace("\"", "").trim();
            if (rawArray.isEmpty()) return new String[0];

            return Arrays.stream(rawArray.split(","))
                         .map(String::trim)
                         .toArray(String[]::new);
        }

        return new String[0];
    }

    private void mostrarAlertaError(HttpServletResponse response, String mensaje) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(
                "<html><head><script type='text/javascript'>"
                        + "alert('" + mensaje.replace("'", "\\'") + "');"
                        + "history.back();"
                        + "</script></head><body></body></html>");
    }
}
