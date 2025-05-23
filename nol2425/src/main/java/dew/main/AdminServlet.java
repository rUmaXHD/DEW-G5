package dew.main;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
    
    private static final String API_URL = "http://localhost:9090/CentroEducativo";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        String key = (String) session.getAttribute("key");
        
        if (key == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autenticado");
            return;
        }

        if ("addAlumno".equals(action)) {
            agregarAlumno(request, response, key);
        }
    }

    private void agregarAlumno(HttpServletRequest request, HttpServletResponse response, String key) 
            throws IOException {
        
        // Leer datos del formulario
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        // Configurar conexión a CentroEducativo
        String url = API_URL + "/alumnos?key=" + URLEncoder.encode(key, StandardCharsets.UTF_8);
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Enviar datos
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBuilder.toString().getBytes(StandardCharsets.UTF_8));
        }

        // Procesar respuesta
        try (BufferedReader reader = new BufferedReader(
             new InputStreamReader(conn.getInputStream()))) {
            
            String respuesta = reader.lines().collect(Collectors.joining());
            response.setContentType("text/plain");
            response.getWriter().write("Alumno agregado exitosamente");
        } catch (IOException e) {
            try (BufferedReader errorReader = new BufferedReader(
                 new InputStreamReader(conn.getErrorStream()))) {
                
                String error = errorReader.lines().collect(Collectors.joining());
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
            }
        }
    }
}