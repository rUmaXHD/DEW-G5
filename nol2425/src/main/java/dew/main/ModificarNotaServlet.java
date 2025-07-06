package dew.main;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Servlet que permite modificar la nota de un alumno en una asignatura concreta.
 * Solo accesible mediante petición POST AJAX del profesor.
 */
@WebServlet("/profesor/modificarNota")
public class ModificarNotaServlet extends HttpServlet {

    private static final String API_BASE_URL = "http://localhost:9090/CentroEducativo";

    /**
     * Procesa la petición POST con los datos necesarios para actualizar la nota de un alumno.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Validar sesión y token
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("key") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión inválida");
            return;
        }

        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

        // Leer cuerpo JSON recibido
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
        }

        String jsonBody = sb.toString();
        String dni = extraerCampo(jsonBody, "dni");
        String asignatura = extraerCampo(jsonBody, "asignatura");
        String notaStr = extraerCampo(jsonBody, "nota");

        if (dni == null || asignatura == null || notaStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan campos obligatorios");
            return;
        }

        // Validar formato de la nota
        double nota;
        try {
            nota = Double.parseDouble(notaStr);
            if (nota < 0 || nota > 10) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La nota debe estar entre 0 y 10");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nota inválida");
            return;
        }

        try {
            // Preparar petición PUT a la API
            String url = API_BASE_URL + "/alumnos/" + dni + "/asignaturas/" + asignatura + "?key=" + key;
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "*/*")
                    .PUT(HttpRequest.BodyPublishers.ofString(String.valueOf(nota)));

            if (jsessionId != null) {
                requestBuilder.header("Cookie", "JSESSIONID=" + jsessionId);
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(
                    requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar nota en API");
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Excepción al enviar nota a API:");
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excepción al realizar petición PUT");
        }
    }

    /**
     * Extrae un campo simple de un cuerpo JSON plano con expresiones regulares.
     *
     * @param json   Cuerpo JSON como String
     * @param campo  Nombre del campo a extraer
     * @return Valor del campo o null si no se encuentra
     */
    private String extraerCampo(String json, String campo) {
        try {
            String patron = "\"" + campo + "\"\\s*:\\s*\"?(.*?)\"?(,|})";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patron);
            java.util.regex.Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.err.println("[WARN] Error al extraer el campo: " + campo);
        }
        return null;
    }
}
