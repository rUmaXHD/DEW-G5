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

@WebServlet("/auth")
public class LoginServlet extends HttpServlet {
    private static final String CENTRO_EDUCATIVO_URL = "http://localhost:9090";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Este método se llamará después de que Tomcat haya autenticado al usuario
        String dni = request.getRemoteUser(); // Obtener el DNI del usuario autenticado
        HttpSession session = request.getSession();
        
        // Verificar si ya existe una sesión con key válida
        if (session.getAttribute("key") != null) {
            redirectToUserPage(request, response);
            return;
        }
        
        try {
            // Obtener la contraseña del usuario de Tomcat (esto debería configurarse en tomcat-users.xml)
            String password = "123456"; // La contraseña por defecto que configuramos
            
            // Crear el cuerpo JSON para la petición
            String jsonBody = String.format("{\"dni\": \"%s\", \"password\": \"%s\"}", dni, password);
            
            // Crear y enviar la petición HTTP POST
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(CENTRO_EDUCATIVO_URL + "/CentroEducativo/login"))
                    .header("Content-Type", "application/json")
                    .header("accept", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (httpResponse.statusCode() == 200) {
                // Guardar información en la sesión
                String key = httpResponse.body();
                session.setAttribute("key", key);
                session.setAttribute("dni", dni);
                
                redirectToUserPage(request, response);
            } else {
                response.sendRedirect("login.jsp?error=Error de autenticación con CentroEducativo: " + 
                    httpResponse.statusCode());
            }
        } catch (Exception e) {
            response.sendRedirect("login.jsp?error=Error de conexión: " + e.getMessage());
        }
    }
    
    private void redirectToUserPage(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        if (request.isUserInRole("rolpro")) {
            response.sendRedirect("profesor/index.jsp");
        } else {
            response.sendRedirect("alumno/index.jsp");
        }
    }
} 