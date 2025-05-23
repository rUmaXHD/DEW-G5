package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String dni = request.getParameter("dni");
        String password = request.getParameter("password");
        
        try {
            // 1. Preparar la conexión con CentroEducativo
            URL url = new URL("http://localhost:9090/CentroEducativo/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 2. Enviar credenciales en formato JSON
            String jsonInput = String.format("{\"dni\":\"%s\",\"password\":\"%s\"}", dni, password);
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 3. Obtener respuesta
            int responseCode = conn.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Leer la key de la respuesta
                String key;
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    key = br.readLine();
                }
                
                // Guardar en sesión
                HttpSession session = request.getSession();
                session.setAttribute("key", key);
                session.setAttribute("dni", dni);
                
                // 4. Verificar si es profesor consultando la API de profesores
                URL profesorUrl = new URL("http://localhost:9090/CentroEducativo/profesores/" + dni + "?key=" + key);
                HttpURLConnection profesorConn = (HttpURLConnection) profesorUrl.openConnection();
                profesorConn.setRequestMethod("GET");
                
                if (profesorConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Es profesor
                    session.setAttribute("rol", "rolpro");
                    response.sendRedirect("profesor/inicio.jsp");
                } else {
                    // Es alumno
                    session.setAttribute("rol", "rolalu");
                    response.sendRedirect("alumno/inicio.jsp");
                }
                
            } else {
                // Error de autenticación
                response.sendRedirect("login.jsp?error=Credenciales incorrectas");
            }
            
        } catch (Exception e) {
            response.sendRedirect("login.jsp?error=" + e.getMessage());
        }
    }
} 