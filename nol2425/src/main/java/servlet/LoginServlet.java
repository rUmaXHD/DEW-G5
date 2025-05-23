package servlet;

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
import java.util.HashMap;
import java.util.Map;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final String CENTRO_EDUCATIVO_URL = "http://localhost:9090";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    // Mapa para almacenar las credenciales de CentroEducativo
    private static final Map<String, UserCredentials> userCredentials = new HashMap<>();
    
    static {
        // Credenciales de ejemplo (en producción deberían estar en una base de datos)
        userCredentials.put("12345678W", new UserCredentials("12345678W", "123456"));
        userCredentials.put("23456387R", new UserCredentials("23456387R", "123456"));
        // ... añadir más usuarios según sea necesario
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String dni = request.getParameter("dni");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        
        // Verificar si ya existe una sesión con key válida
        if (session.getAttribute("key") != null) {
            response.sendRedirect("main.jsp");
            return;
        }
        
        // Verificar si el usuario está en nuestro sistema
        UserCredentials credentials = userCredentials.get(dni);
        if (credentials == null || !credentials.password.equals(password)) {
            response.sendRedirect("login.jsp?error=Credenciales inválidas");
            return;
        }
        
        try {
            // Autenticar con CentroEducativo
            String loginUrl = CENTRO_EDUCATIVO_URL + "/CentroEducativo/login";
            String jsonBody = String.format("{\"dni\":\"%s\",\"password\":\"%s\"}", dni, password);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(loginUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (httpResponse.statusCode() == 200) {
                // Guardar información en la sesión
                String key = httpResponse.body();
                session.setAttribute("key", key);
                session.setAttribute("dni", dni);
                session.setAttribute("password", password);
                
                response.sendRedirect("main.jsp");
            } else {
                response.sendRedirect("login.jsp?error=Error de autenticación con CentroEducativo");
            }
        } catch (Exception e) {
            response.sendRedirect("login.jsp?error=Error de conexión: " + e.getMessage());
        }
    }
    
    private static class UserCredentials {
        final String dni;
        final String password;
        
        UserCredentials(String dni, String password) {
            this.dni = dni;
            this.password = password;
        }
    }
} 