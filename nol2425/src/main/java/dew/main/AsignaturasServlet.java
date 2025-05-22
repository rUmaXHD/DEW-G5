package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * Servlet implementation class AsignaturasServlet
 */
@WebServlet("/AsignaturasServlet")
public class AsignaturasServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 1. Obtener key de la sesión
        HttpSession session = request.getSession();
        String key = (String) session.getAttribute("key");
        
        if (key == null) {
            response.sendError(401, "No autenticado");
            return;
        }

        // 2. Consultar a CentroEducativo
        String url = "http://localhost:9090/CentroEducativo/asignaturas?key=" + key;
        String jsonResponse = hacerPeticionHttpGet(url);

        // 3. Devolver JSON
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }

    private String hacerPeticionHttpGet(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        
        try (BufferedReader reader = new BufferedReader(
             new InputStreamReader(conn.getInputStream()))) {
            
            return reader.lines().collect(Collectors.joining());
        }
    }
}