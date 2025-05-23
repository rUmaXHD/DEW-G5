package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.*;
import java.net.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String dni = request.getParameter("dni");
        String password = request.getParameter("password");
        
        try {
            // 1. Autenticar con CentroEducativo
            URL url = new URL("http://localhost:9090/CentroEducativo/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/plain");
            conn.setDoOutput(true);

            // Enviar credenciales
            String jsonInput = "{\"dni\":\"" + dni + "\",\"password\":\"" + password + "\"}";
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Obtener respuesta
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Leer la key de la respuesta
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    String key = br.readLine();
                    
                    // Guardar en sesión
                    HttpSession session = request.getSession();
                    session.setAttribute("key", key);
                    session.setAttribute("dni", dni);
                    
                    // Determinar el rol y redirigir
                    if (dni.equals("23456733H") || dni.equals("10293756L") || 
                        dni.equals("06374291A") || dni.equals("65748923M")) {
                        response.sendRedirect("profesor/index.jsp");
                    } else {
                        response.sendRedirect("alumno/index.jsp");
                    }
                }
            } else {
                response.sendRedirect("login.jsp?error=Error de autenticación");
            }
        } catch (Exception e) {
            response.sendRedirect("login.jsp?error=" + e.getMessage());
        }
    }
} 