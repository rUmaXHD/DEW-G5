package dew.main;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@WebServlet("/profesor/modificarNota")
public class ModificarNotaServlet extends HttpServlet {

    private static final String API_BASE_URL = "http://localhost:9090/CentroEducativo";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("🔔 [ModificarNotaServlet] Petición recibida");

        // Leer sesión y claves
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("key") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión inválida");
            return;
        }

        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

        // Leer JSON del cuerpo
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
        }

        String jsonBody = sb.toString();
        System.out.println("📥 JSON recibido: " + jsonBody);

        // Extraer campos manualmente (sin librerías externas)
        String dni = extraerCampo(jsonBody, "dni");
        String asignatura = extraerCampo(jsonBody, "asignatura");
        String nota = extraerCampo(jsonBody, "nota");

        if (dni == null || asignatura == null || nota == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan campos obligatorios");
            return;
        }

        System.out.println("➡️ Enviando PATCH a /alumnos/" + dni + "/asignaturas/" + asignatura);
        System.out.println("➡️ Nota: " + nota);

        try {
            // Construcción de la URL
            String url = API_BASE_URL + "/alumnos/" + dni + "/asignaturas/" + asignatura + "?key=" + key;
            System.out.println("🌐 URL destino del PUT: " + url);

            // Construcción del cuerpo JSON
            String payload = String.format(
                "{\"dni\":\"%s\",\"asignatura\":\"%s\",\"nota\":%s}",
                dni, asignatura, nota
            );
            System.out.println("📦 JSON a enviar:");
            System.out.println(payload);

            // Preparación de la solicitud PUT
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            	    .uri(URI.create(url))
            	    .header("Content-Type", "application/json")
            	    .header("Accept", "application/json")  // ✅ Añadir esta línea
            	    .PUT(HttpRequest.BodyPublishers.ofString(payload));


            if (jsessionId != null) {
                requestBuilder.header("Cookie", "JSESSIONID=" + jsessionId);
                System.out.println("🔐 Cabecera Cookie enviada: JSESSIONID=" + jsessionId);
            } else {
                System.out.println("⚠️ No se encontró JSESSIONID en sesión");
            }

            // Envío de la petición
            HttpClient client = HttpClient.newHttpClient();
            System.out.println("🚀 Enviando solicitud PUT...");
            HttpResponse<String> response = client.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofString()
            );

            // Resultado
            System.out.println("📥 Código de respuesta: " + response.statusCode());
            System.out.println("📄 Respuesta cuerpo:");
            System.out.println(response.body());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                System.out.println("✅ Nota modificada correctamente");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                System.err.println("❌ Error en la respuesta del backend al hacer PUT");
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar nota en API");
            }

        } catch (Exception e) {
            System.err.println("💥 Excepción al realizar PUT:");
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excepción al realizar petición PUT");
        }


    }

    // Función auxiliar para extraer campos simples de un JSON plano
    private String extraerCampo(String json, String campo) {
        try {
            String patron = "\"" + campo + "\"\\s*:\\s*\"?(.*?)\"?(,|})";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patron);
            java.util.regex.Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al extraer campo: " + campo);
        }
        return null;
    }
}
