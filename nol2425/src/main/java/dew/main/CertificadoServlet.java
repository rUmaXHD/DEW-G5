package dew.main;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.regex.*;

@WebServlet("/alumno/certificado")
public class CertificadoServlet extends HttpServlet {

    private static final String API_BASE_URL = "http://localhost:9090/CentroEducativo";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("📥 [CertificadoServlet] Solicitud GET recibida");

        HttpSession session = req.getSession(false);
        if (session == null ||
            session.getAttribute("key") == null ||
            session.getAttribute("dni") == null) {
            System.out.println("❌ Sesión inválida o atributos 'key' y 'dni' no presentes");
            resp.sendRedirect(req.getContextPath() + "/AccesoServlet");
            return;
        }

        String key = (String) session.getAttribute("key");
        String dni = (String) session.getAttribute("dni");
        String jsessionId = (String) session.getAttribute("jsessionId");

        System.out.println("✅ Sesión válida. DNI: " + dni + ", KEY: " + key);

        try {
            HttpClient client = HttpClient.newHttpClient();

            // 🔹 1. Obtener nombre completo del alumno
            String urlAlumno = API_BASE_URL + "/alumnos/" + dni + "?key=" + key;
            System.out.println("🌐 GET datos del alumno: " + urlAlumno);

            HttpRequest reqAlumno = HttpRequest.newBuilder()
                    .uri(URI.create(urlAlumno))
                    .header("Content-Type", "application/json")
                    .header("Cookie", "JSESSIONID=" + jsessionId)
                    .GET()
                    .build();

            HttpResponse<String> respAlumno = client.send(reqAlumno, HttpResponse.BodyHandlers.ofString());
            System.out.println("📥 Código respuesta alumno: " + respAlumno.statusCode());

            if (respAlumno.statusCode() != 200) {
                System.err.println("❌ Error al obtener datos del alumno");
                resp.sendError(500, "No se pudieron obtener los datos del alumno");
                return;
            }

            String cuerpoAlumno = respAlumno.body();
            String nombre = extraerValor(cuerpoAlumno, "nombre");
            String apellidos = extraerValor(cuerpoAlumno, "apellidos");
            String nombreCompleto = nombre + " " + apellidos;
            System.out.println("✅ Nombre del alumno: " + nombreCompleto);

            // 🔹 2. Obtener notas
            String urlNotas = API_BASE_URL + "/alumnos/" + dni + "/asignaturas?key=" + key;
            System.out.println("🌐 GET notas: " + urlNotas);

            HttpRequest reqNotas = HttpRequest.newBuilder()
                    .uri(URI.create(urlNotas))
                    .header("Content-Type", "application/json")
                    .header("Cookie", "JSESSIONID=" + jsessionId)
                    .GET()
                    .build();

            HttpResponse<String> respNotas = client.send(reqNotas, HttpResponse.BodyHandlers.ofString());
            System.out.println("📥 Código respuesta notas: " + respNotas.statusCode());

            if (respNotas.statusCode() != 200) {
                System.err.println("❌ Error al obtener notas");
                resp.sendError(500, "No se pudieron obtener las notas");
                return;
            }

            String notasJson = respNotas.body();
            System.out.println("📦 JSON de notas obtenido:");
            System.out.println(notasJson);

            // 🔹 Pasar a JSP
            req.setAttribute("dni", dni);
            req.setAttribute("nombreAlumno", nombreCompleto);
            req.setAttribute("certificadoJson", notasJson);
            req.getRequestDispatcher("/alumno/certificado.jsp").forward(req, resp);

        } catch (Exception e) {
            System.err.println("💥 Excepción en CertificadoServlet:");
            e.printStackTrace();
            resp.sendError(500, "Error generando el certificado: " + e.getMessage());
        }
    }

    // Utilidad para extraer un campo JSON plano: "clave":"valor"
    private String extraerValor(String json, String clave) {
        try {
            String patron = "\"" + clave + "\"\\s*:\\s*\"(.*?)\"";
            Pattern pattern = Pattern.compile(patron);
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al extraer el campo " + clave);
        }
        return "";
    }
}
