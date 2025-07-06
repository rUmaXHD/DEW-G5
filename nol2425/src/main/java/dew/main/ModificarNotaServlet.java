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

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("key") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión inválida");
            return;
        }

        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

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
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excepción al realizar petición PUT");
        }
    }

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

