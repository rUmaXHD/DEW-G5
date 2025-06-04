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
        procesarLogin(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarLogin(request, response);
    }

    private void procesarLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String dni = request.getRemoteUser(); // Tomcat ya autenticó
        if (dni == null) {
            response.sendRedirect("login.jsp?error=Sesion+no+iniciada");
            return;
        }

        String password = obtenerPasswordSegunDni(dni); // Valor por defecto

        try {
            String key = obtenerSessionKeyDesdeAPI(dni, password);

            HttpSession session = request.getSession(true);
            session.setAttribute("dni", dni);
            session.setAttribute("key", key);

            // Redirección basada en rol Tomcat
            if (request.isUserInRole("rolalu")) {
                response.sendRedirect("alumno/inicio.jsp");
            } else if (request.isUserInRole("rolpro")) {
                response.sendRedirect("profesor/inicio.jsp");
            } else {
                response.sendRedirect("login.jsp?error=Rol+no+reconocido");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=Error+al+obtener+session+key");
        }
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

        if (response.statusCode() == 200) {
            return response.body().replace("\"", "");
        } else {
            throw new Exception("API REST respondió con código " + response.statusCode());
        }
    }

    private String obtenerPasswordSegunDni(String dni) {
        // Contraseña por defecto (ajusta según tus reglas)
        return "123456"; // Asumido válido para pruebas
    }
}

