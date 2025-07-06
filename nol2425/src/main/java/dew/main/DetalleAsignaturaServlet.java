package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet que obtiene los detalles de una asignatura específica
 * para un alumno autenticado: nombre, créditos, grupo y compañeros.
 */
@WebServlet("/DetalleAsignaturaServlet")
public class DetalleAsignaturaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String API_BASE_URL = "http://localhost:9090/CentroEducativo";
    private final Gson gson = new Gson();

    /**
     * Procesa la petición GET para cargar los detalles de una asignatura del alumno.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("🔵 [DetalleAsignaturaServlet] Petición GET recibida");

        // Validación de sesión
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("key") == null) {
            System.out.println("⚠️ Sesión no válida. Redirigiendo a LoginServlet");
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        // Datos de sesión y parámetro recibido
        String key = (String) session.getAttribute("key");
        String dni = (String) session.getAttribute("dni");
        String jsessionId = (String) session.getAttribute("jsessionId");
        String codigo = req.getParameter("codigo");

        System.out.println("📌 Código de asignatura recibido: " + codigo);

        try {
            HttpClient client = HttpClient.newHttpClient();

            // 1. Obtener información del alumno
            System.out.println("➡️ Solicitando datos del alumno: " + dni);
            HttpRequest requestAlumnosInfo = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/alumnos/" + dni + "?key=" + key))
                .header("Content-Type", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET().build();

            HttpResponse<String> alumnoResponse = client.send(requestAlumnosInfo, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ Datos del alumno recibidos. Código: " + alumnoResponse.statusCode());

            JsonObject alumnoData = gson.fromJson(alumnoResponse.body(), JsonObject.class);
            String nombreCompleto = alumnoData.get("nombre").getAsString() + " " + alumnoData.get("apellidos").getAsString();
            req.setAttribute("nombreAlumno", nombreCompleto);

            // 2. Obtener detalles de la asignatura
            System.out.println("➡️ Solicitando detalles de la asignatura...");
            HttpRequest detalleReq = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/asignaturas/" + codigo + "?key=" + key))
                .header("Content-Type", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET().build();

            HttpResponse<String> detalleResp = client.send(detalleReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ Detalles de asignatura recibidos. Código: " + detalleResp.statusCode());

            if (detalleResp.statusCode() != 200) {
                throw new ServletException("Error al obtener detalles de la asignatura");
            }

            JsonObject asigDetalle = gson.fromJson(detalleResp.body(), JsonObject.class);

            // 3. Obtener nota del alumno en esta asignatura
            System.out.println("➡️ Solicitando nota del alumno en la asignatura...");
            HttpRequest notaReq = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/alumnos/" + dni + "/asignaturas?key=" + key))
                .header("Content-Type", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET().build();

            HttpResponse<String> notaResp = client.send(notaReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ Notas del alumno recibidas. Código: " + notaResp.statusCode());

            String notaValor = "No disponible";
            if (notaResp.statusCode() == 200) {
                JsonArray notasArray = gson.fromJson(notaResp.body(), JsonArray.class);
                for (JsonElement el : notasArray) {
                    JsonObject obj = el.getAsJsonObject();
                    if (codigo.equals(obj.get("asignatura").getAsString())) {
                        notaValor = obj.has("nota") && !obj.get("nota").isJsonNull() ?
                                    obj.get("nota").getAsString() : "Sin calificar";
                        break;
                    }
                }
                System.out.println("🎯 Nota del alumno en " + codigo + ": " + notaValor);
            }

            // 4. Datos a pasar a la vista
            Map<String, Object> datos = new HashMap<>();
            datos.put("codigo", codigo);
            datos.put("nombre", asigDetalle.get("nombre").getAsString());
            datos.put("curso", asigDetalle.get("curso").getAsInt());
            datos.put("cuatrimestre", asigDetalle.get("cuatrimestre").getAsString());
            datos.put("creditos", asigDetalle.get("creditos").getAsDouble());
            datos.put("nota", notaValor);
            datos.put("grupoNombre", "Sin grupo asignado");
            datos.put("miembros", new ArrayList<String>());

            // 5. Obtener grupo de la asignatura (si existe)
            System.out.println("➡️ Solicitando grupos de la asignatura...");
            HttpRequest gruposReq = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/asignaturas/" + codigo + "/grupos?key=" + key))
                .header("Content-Type", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET().build();

            HttpResponse<String> gruposResp = client.send(gruposReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ Grupos obtenidos. Código: " + gruposResp.statusCode());

            if (gruposResp.statusCode() == 200) {
                JsonArray gruposArray = gson.fromJson(gruposResp.body(), JsonArray.class);
                if (!gruposArray.isEmpty()) {
                    JsonObject primerGrupo = gruposArray.get(0).getAsJsonObject();
                    String nombreGrupo = primerGrupo.get("nombre").getAsString();
                    datos.put("grupoNombre", "Grupo " + nombreGrupo);
                    System.out.println("👥 Grupo encontrado: " + nombreGrupo);

                    // 6. Obtener miembros del grupo
                    System.out.println("➡️ Solicitando alumnos del grupo...");
                    HttpRequest miembrosReq = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE_URL + "/asignaturas/" + codigo + "/grupos/" + nombreGrupo + "/alumnos?key=" + key))
                        .header("Content-Type", "application/json")
                        .header("Cookie", "JSESSIONID=" + jsessionId)
                        .GET().build();

                    HttpResponse<String> miembrosResp = client.send(miembrosReq, HttpResponse.BodyHandlers.ofString());
                    System.out.println("✅ Alumnos del grupo recibidos. Código: " + miembrosResp.statusCode());

                    if (miembrosResp.statusCode() == 200) {
                        JsonArray miembrosArray = gson.fromJson(miembrosResp.body(), JsonArray.class);
                        List<String> miembros = new ArrayList<>();
                        for (JsonElement el : miembrosArray) {
                            JsonObject mi = el.getAsJsonObject();
                            if (!dni.equals(mi.get("dni").getAsString())) {
                                String nombreCompletoMiembro = mi.get("nombre").getAsString() + " " +
                                                              mi.get("apellidos").getAsString();
                                miembros.add(nombreCompletoMiembro);
                            }
                        }
                        datos.put("miembros", miembros);
                        System.out.println("📋 Miembros del grupo: " + miembros.size());
                    } else {
                        System.out.println("⚠️ No se pudieron obtener los miembros del grupo.");
                    }
                } else {
                    System.out.println("ℹ️ La asignatura no tiene grupos definidos.");
                }
            }

            // 7. Enviar datos a la vista JSP
            req.setAttribute("detalleAsignaturaJson", gson.toJson(datos));
            req.setAttribute("dniAlumno", dni);
            req.getRequestDispatcher("/alumno/detalleAsignatura.jsp").forward(req, resp);

        } catch (Exception e) {
            System.err.println("💥 Excepción en DetalleAsignaturaServlet:");
            e.printStackTrace();
            resp.sendError(500, "Error en DetalleAsignaturaServlet: " + e.getMessage());
        }
    }
}
