package dew.main;
import java.io.*;
import java.net.*;
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
        
        // 1. Obtener credenciales
        String dni = request.getParameter("dni");
        String password = request.getParameter("password");
        
        // 2. Autenticar en CentroEducativo
        try {
            String key = authenticateWithCentroEducativo(dni, password);
            
            // 3. Guardar en sesión
            HttpSession session = request.getSession();
            session.setAttribute("key", key);
            session.setAttribute("dni", dni);
            
            // 4. Redirigir según rol (Tomcat)
            if (request.isUserInRole("rolalu")) {
                response.sendRedirect("alumno/inicio.jsp");
            } else if (request.isUserInRole("rolpro")) {
                response.sendRedirect("profesor/inicio.jsp");
            } else {
                response.sendRedirect("login.jsp?error=Rol no asignado");
            }
            
        } catch (AuthException e) {
            response.sendRedirect("login.jsp?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
    
    private String authenticateWithCentroEducativo(String dni, String password) throws AuthException {
        try {
            // Configurar conexión
            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            // Enviar credenciales
            String json = String.format("{\"dni\":\"%s\",\"password\":\"%s\"}", dni, password);
            conn.getOutputStream().write(json.getBytes());
            
            // Procesar respuesta
            if (conn.getResponseCode() == 200) {
                return new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
            } else {
                String error = new BufferedReader(new InputStreamReader(conn.getErrorStream())).readLine();
                throw new AuthException(error != null ? error : "Credenciales incorrectas");
            }
        } catch (IOException e) {
            throw new AuthException("Error conectando con CentroEducativo");
        }
    }
    
    private static class AuthException extends Exception {
        public AuthException(String message) {
            super(message);
        }
    }
}
