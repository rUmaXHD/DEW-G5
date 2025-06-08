package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.*;

import com.google.gson.*;

@WebServlet("/InicioProfesorServlet")
public class InicioProfesorServlet extends HttpServlet {
    private static final String API_URL = "http://localhost:9090/CentroEducativo";
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.isUserInRole("rolpro")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("key") == null) {
			resp.sendRedirect(req.getContextPath() + "/LoginServlet");
			return;
		}
        String dni = (String) session.getAttribute("dni");
        String key = (String) session.getAttribute("key");
		String password = (String) session.getAttribute("password");
		String jsessionId = (String) session.getAttribute("jsessionId");

        if (dni == null || key == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();

            // Obtener nombre del profesor
            HttpRequest requestInfo = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/profesores/" + dni + "?key=" + key))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> infoResponse = client.send(requestInfo, HttpResponse.BodyHandlers.ofString());
            String nombreCompleto = "Profesor desconocido";

            if (infoResponse.statusCode() == 200) {
                JsonObject prof = gson.fromJson(infoResponse.body(), JsonObject.class);
                nombreCompleto = prof.get("nombre").getAsString() + " " + prof.get("apellidos").getAsString();
            }

            // Obtener asignaturas del profesor
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/profesores/" + dni + "/asignaturas?key=" + key))
                    .header("Content-Type", "application/json")
    		        .header("Cookie", "JSESSIONID=" + jsessionId)
    		        .GET()
    		        .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ServletException("Error al obtener asignaturas del profesor");
            }

            JsonArray asignaturasJson = gson.fromJson(response.body(), JsonArray.class);
            List<Map<String, String>> asignaturasInfo = new ArrayList<>();

            for (JsonElement el : asignaturasJson) {
                JsonObject obj = el.getAsJsonObject();
                Map<String, String> asig = new HashMap<>();
                asig.put("acronimo", obj.get("acronimo").getAsString());
                asig.put("nombre", obj.get("nombre").getAsString());
                asig.put("curso", obj.get("curso").getAsString());
                asig.put("cuatrimestre", obj.get("cuatrimestre").getAsString());
                asig.put("creditos", obj.get("creditos").getAsString());
                asignaturasInfo.add(asig);
            }

            // Pasar los datos como JSON String a la JSP
            req.setAttribute("asignaturasJson", gson.toJson(asignaturasInfo));
            req.setAttribute("dniProfesor", dni);
            req.setAttribute("nombreProfesor", nombreCompleto);

            req.getRequestDispatcher("/profesor/inicio.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error procesando datos: " + e.getMessage());
        }
    }
}
