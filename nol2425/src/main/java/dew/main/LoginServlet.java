package dew.main;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    
    private static final String API_URL = "http://localhost:9090/CentroEducativo/login";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String dni = request.getParameter("dni");
        String password = request.getParameter("password");

        try {
            // 1. Autenticar en CentroEducativo
            String key = authenticateWithCentroEducativo(dni, password);
            
            if (key != null && !key.trim().isEmpty()) {
                // 2. Guardar en sesión
                HttpSession session = request.getSession();
                session.setAttribute("key", key);
                session.setAttribute("dni", dni);
                
                // 3. Redirigir según rol
                if (request.isUserInRole("rolalu")) {
                    response.sendRedirect("alumno/inicio.jsp");
                } else if (request.isUserInRole("rolpro")) {
                    response.sendRedirect("profesor/inicio.jsp");
                }
            } else {
                response.sendRedirect("login.jsp?error=Credenciales incorrectas");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=Error en el servidor");
        }
    }

    private String authenticateWithCentroEducativo(String dni, String password) {
        HttpURLConnection conn = null;
        try {
            // Configurar conexión
            URL url = new URL("http://localhost:9090/CentroEducativo/login");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            // Enviar credenciales
            String jsonInput = String.format("{\"dni\":\"%s\",\"password\":\"%s\"}", dni, password);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }
            
            // Procesar respuesta
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                     new InputStreamReader(conn.getInputStream()))) {
                    return reader.readLine();
                }
            } else {
                // Leer mensaje de error si la API lo devuelve
                try (BufferedReader reader = new BufferedReader(
                     new InputStreamReader(conn.getErrorStream()))) {
                    System.err.println("Error de API: " + reader.readLine());
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
    
    private static class AuthException extends Exception {
        public AuthException(String message) {
            super(message);
        }
    }
}
