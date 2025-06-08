package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/DetalleAsignaturaServlet")
public class DetalleAsignaturaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String API_BASE_URL = "http://localhost:9090/CentroEducativo";
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("key") == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        String key = (String) session.getAttribute("key");
        String dni = (String) session.getAttribute("dni");
        String jsessionId = (String) session.getAttribute("jsessionId");
        String codigo = req.getParameter("codigo");

        try {
            HttpClient client = HttpClient.newHttpClient();

            // Obtener nombre del alumno desde la API
            HttpRequest requestAlumnosInfo = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/alumnos/" + dni + "?key=" + key))
                .header("Content-Type", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET()
                .build();

            HttpResponse<String> alumnoResponse = client.send(requestAlumnosInfo, HttpResponse.BodyHandlers.ofString());
            JsonObject alumnoData = gson.fromJson(alumnoResponse.body(), JsonObject.class);
            String nombreCompleto = alumnoData.get("nombre").getAsString() + " " + alumnoData.get("apellidos").getAsString();
            req.setAttribute("nombreAlumno", nombreCompleto);

            // Detalle de asignatura
            HttpRequest detalleReq = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/asignaturas/" + codigo + "?key=" + key))
                .header("Content-Type", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET()
                .build();

            HttpResponse<String> detalleResp = client.send(detalleReq, HttpResponse.BodyHandlers.ofString());
            if (detalleResp.statusCode() != 200) {
                throw new ServletException("Error al obtener detalles de la asignatura");
            }

            JsonObject asigDetalle = gson.fromJson(detalleResp.body(), JsonObject.class);

            // Buscar nota
            HttpRequest notaReq = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/alumnos/" + dni + "/asignaturas?key=" + key))
                .header("Content-Type", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET()
                .build();

            HttpResponse<String> notaResp = client.send(notaReq, HttpResponse.BodyHandlers.ofString());
            String notaValor = "No disponible";
            if (notaResp.statusCode() == 200) {
                JsonArray notasArray = gson.fromJson(notaResp.body(), JsonArray.class);
                for (JsonElement el : notasArray) {
                    JsonObject obj = el.getAsJsonObject();
                    if (codigo.equals(obj.get("asignatura").getAsString())) {
                        notaValor = obj.has("nota") && !obj.get("nota").isJsonNull() ? obj.get("nota").getAsString() : "Sin calificar";
                        break;
                    }
                }
            }

            Map<String, Object> datos = new HashMap<>();
            datos.put("codigo", codigo);
            datos.put("nombre", asigDetalle.get("nombre").getAsString());
            datos.put("curso", asigDetalle.get("curso").getAsInt());
            datos.put("cuatrimestre", asigDetalle.get("cuatrimestre").getAsString());
            datos.put("creditos", asigDetalle.get("creditos").getAsDouble());
            datos.put("nota", notaValor);

            // Grupo y compañeros
            datos.put("grupoNombre", "Sin grupo asignado");
            datos.put("miembros", new ArrayList<String>());

            HttpRequest gruposReq = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/asignaturas/" + codigo + "/grupos?key=" + key))
                .header("Content-Type", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET()
                .build();

            HttpResponse<String> gruposResp = client.send(gruposReq, HttpResponse.BodyHandlers.ofString());

            if (gruposResp.statusCode() == 200) {
                JsonArray gruposArray = gson.fromJson(gruposResp.body(), JsonArray.class);
                if (gruposArray.size() > 0) {
                    JsonObject primerGrupo = gruposArray.get(0).getAsJsonObject();
                    String nombreGrupo = primerGrupo.get("nombre").getAsString();
                    datos.put("grupoNombre", "Grupo " + nombreGrupo);

                    // Miembros del grupo
                    HttpRequest miembrosReq = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE_URL + "/asignaturas/" + codigo + "/grupos/" + nombreGrupo + "/alumnos?key=" + key))
                        .header("Content-Type", "application/json")
                        .header("Cookie", "JSESSIONID=" + jsessionId)
                        .GET()
                        .build();

                    HttpResponse<String> miembrosResp = client.send(miembrosReq, HttpResponse.BodyHandlers.ofString());

                    if (miembrosResp.statusCode() == 200) {
                        JsonArray miembrosArray = gson.fromJson(miembrosResp.body(), JsonArray.class);
                        List<String> miembros = new ArrayList<>();
                        for (JsonElement el : miembrosArray) {
                            JsonObject mi = el.getAsJsonObject();
                            String dniMiembro = mi.get("dni").getAsString();
                            if (!dni.equals(dniMiembro)) {
                                String nombreCompletoMiembro = mi.get("nombre").getAsString() + " " + mi.get("apellidos").getAsString();
                                miembros.add(nombreCompletoMiembro);
                            }
                        }
                        datos.put("miembros", miembros);
                    }
                }
            }

            req.setAttribute("detalleAsignaturaJson", gson.toJson(datos));
            req.setAttribute("dniAlumno", dni);
            req.getRequestDispatcher("/alumno/detalleAsignatura.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Error en DetalleAsignaturaServlet: " + e.getMessage());
        }
    }
}

