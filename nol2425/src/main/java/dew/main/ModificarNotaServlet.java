package dew.main;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@WebServlet("/profesor/modificarNota")
public class ModificarNotaServlet extends HttpServlet {

    private static final String API_BASE_URL = "http://localhost:9090/CentroEducativo";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Verificar rol
        if (!req.isUserInRole("rolpro")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No autorizado");
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("key") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

        // Leer cuerpo JSON
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                body.append(linea);
            }
        }

        try {
            JsonObject json = JsonParser.parseString(body.toString()).getAsJsonObject();
            String dni = json.get("dni").getAsString();
            String asignatura = json.get("asignatura").getAsString();
            String nota = json.get("nota").getAsString();

            // Construir JSON para enviar a backend
            JsonObject notaJson = new JsonObject();
            notaJson.addProperty("asignatura", asignatura);
            notaJson.addProperty("nota", nota);

            String apiUrl = API_BASE_URL + "/alumnos/" + dni + "/notas?key=" + key;
            System.out.println(" PATCH a: " + apiUrl);
            System.out.println(" Payload: " + notaJson.toString());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(notaJson.toString()));

            if (jsessionId != null) {
                requestBuilder.header("Cookie", "JSESSIONID=" + jsessionId);
            }

            HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

            System.out.println(" Estado respuesta backend: " + response.statusCode());
            System.out.println(" Respuesta backend: " + response.body());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error al modificar nota");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar nota");
        }
    }
}
