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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final String CENTRO_EDUCATIVO_URL = "http://localhost:9090";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
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
        
        try {
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
                
                // Redirigir según el rol
                if (request.isUserInRole("rolpro")) {
                    response.sendRedirect("profesor/index.jsp");
                } else {
                    response.sendRedirect("alumno/index.jsp");
                }
            } else {
                response.sendRedirect("login.jsp?error=Error de autenticación con CentroEducativo: " + 
                    httpResponse.statusCode());
            }
        } catch (Exception e) {
            response.sendRedirect("login.jsp?error=Error de conexión: " + e.getMessage());
        }
    }
} 