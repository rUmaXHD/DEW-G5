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

        String dni = request.getRemoteUser(); // Usuario autenticado por BASIC Auth

        if (dni == null) {
            mostrarAlertaError(response, "Sesión no iniciada");
            return;
        }

        HttpSession session = request.getSession();

        if (session.getAttribute("key") != null) {
            redirigirSegunRol(request, response);
            return;
        }

        String password = obtenerPasswordPorDni(dni); // En entorno real: usar almacén seguro

        try {
            String key = obtenerSessionKeyDesdeAPI(dni, password);
            if (key == null || key.isBlank()) {
                mostrarAlertaError(response, "Credenciales inválidas en CentroEducativo");
                return;
            }

            session.setAttribute("dni", dni);
            session.setAttribute("password", password);
            session.setAttribute("key", key);

            redirigirSegunRol(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError(response, "Error al conectar con CentroEducativo");
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

    private void redirigirSegunRol(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.isUserInRole("rolalu")) {
            // Redirige a /alumno/inicio.jsp
            response.sendRedirect(request.getContextPath() + "/alumno/inicio.jsp");
        } else if (request.isUserInRole("rolpro")) {
            // Redirige a /profesor/inicio.jsp
            response.sendRedirect(request.getContextPath() + "/profesor/inicio.jsp");
        } else {
            // Rol no reconocido, muestra error con alert
            mostrarAlertaError(response, "Rol no reconocido");
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
            // Se espera algo como: {"key":"abc123"}
            String responseBody = response.body();
            int start = responseBody.indexOf(":\"") + 2;
            int end = responseBody.lastIndexOf("\"");
            return (start > 0 && end > start) ? responseBody.substring(start, end) : null;
        } else {
            return null;
        }
    }


    private String obtenerPasswordPorDni(String dni) {
        // En producción deberías obtener esto de un almacén seguro o base de datos
        return "123456"; // Contraseña por defecto para todos los usuarios demo
    }
}


