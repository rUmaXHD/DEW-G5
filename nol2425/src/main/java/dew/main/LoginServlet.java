package dew.main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String API_URL = "http://localhost:9090/CentroEducativo/login";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarPostLogin(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarPostLogin(request, response);
    }

    private void procesarPostLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String[] creds = obtenerCredencialesDesdeAuthorization(request);
        if (creds == null) {
            mostrarAlertaError(response, "No se pudieron obtener las credenciales.");
            return;
        }

        String dni = creds[0];
        String password = creds[1];

        if (dni == null || dni.isBlank()) {
            mostrarAlertaError(response, "Sesión no iniciada");
            return;
        }

        HttpSession session = request.getSession();

        try {
            String key = obtenerSessionKeyDesdeAPI(dni, password);
            if (key == null || key.isBlank()) {
                mostrarAlertaError(response, "Credenciales inválidas en CentroEducativo");
                session.invalidate(); 
                return;
            }

            session.setAttribute("dni", dni);
            session.setAttribute("password", password);
            session.setAttribute("key", key);
            

            // Redirige según el rol
            if (request.isUserInRole("rolalu")) {
                response.sendRedirect(request.getContextPath() + "/alumno/inicio.jsp");
            } else if (request.isUserInRole("rolpro")) {
                response.sendRedirect(request.getContextPath() + "/profesor/inicio.jsp");
            } else {
                mostrarAlertaError(response, "Rol no reconocido");
                session.invalidate(); 
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError(response, "Error al conectar con CentroEducativo");
        }
    }

    private String[] obtenerCredencialesDesdeAuthorization(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic ".length());
            byte[] credDecoded = java.util.Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded);
            return credentials.split(":", 2);
        }
        return null;
    }

    private String obtenerSessionKeyDesdeAPI(String dni, String password) throws Exception {
        String json = String.format("{\"dni\":\"%s\", \"password\":\"%s\"}", dni, password);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Respuesta login API: " + response.body());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            return responseBody;
        } else {
            return null;
        }
    }

    private void mostrarAlertaError(HttpServletResponse response, String mensaje) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(
            "<html><head><script type='text/javascript'>"
            + "alert('" + mensaje.replace("'", "\\'") + "');"
            + "history.back();"
            + "</script></head><body></body></html>"
        );
    }
}

