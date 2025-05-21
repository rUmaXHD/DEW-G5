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
    
    private static final long serialVersionUID = 1L;
	private static final String CENTRO_EDUCATIVO_URL = "http://localhost:9090";

    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Obtener credenciales del formulario
        String dni = request.getParameter("dni");
        String password = request.getParameter("password");
        
        // 2. Preparar JSON para la autenticación
        String jsonInput = String.format("{\"dni\":\"%s\",\"password\":\"%s\"}", dni, password);
        
        try {
            // 3. Crear conexión HTTP con CentroEducativo
            @SuppressWarnings("deprecation")
			URL url = new URL(CENTRO_EDUCATIVO_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            // 4. Enviar credenciales
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            // 5. Procesar respuesta
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Leer la clave de sesión (key)
                String key;
                try(InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    key = reader.readLine();
                }
                
                // 6. Almacenar key en la sesión
                HttpSession session = request.getSession();
                session.setAttribute("key", key);
                session.setAttribute("dni", dni);
                session.setAttribute("password", password);
                
                // 7. Redirigir según rol (simplificado)
                if (request.isUserInRole("rolalu")) {
                    response.sendRedirect("alumno/inicio.jsp");
                } else if (request.isUserInRole("rolpro")) {
                    response.sendRedirect("profesor/inicio.jsp");
                } else {
                    response.sendRedirect("error.jsp?msg=Rol no válido");
                }
                
            } else {
                // Error en la autenticación
                response.sendRedirect("login.jsp?error=Credenciales incorrectas");
            }
            
        } catch (Exception e) {
            // Error de conexión con CentroEducativo
            response.sendRedirect("login.jsp?error=Error al conectar con el servidor");
        }
    }
}

