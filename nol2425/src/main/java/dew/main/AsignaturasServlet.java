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


/**
 * Servlet implementation class AsignaturasServlet
 */
@WebServlet("/alumno/asignaturas")
public class AsignaturasServlet extends HttpServlet {

    private static final String API_URL_BASE = "http://localhost:9090/CentroEducativo/alumnos/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("dni") == null || session.getAttribute("key") == null) {
            response.sendRedirect(request.getContextPath() + "/login.html"); // o muestra error
            return;
        }

        String dni = (String) session.getAttribute("dni");
        String key = (String) session.getAttribute("key");

        String apiUrl = API_URL_BASE + dni + "/asignaturas";

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + key) // Cambia si tu API no usa Bearer
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> apiResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (apiResponse.statusCode() == 200) {
                String json = apiResponse.body();

                // Parsear el JSON a lista de objetos (aquí como ejemplo, puedes usar org.json, Gson o Jackson)
                request.setAttribute("asignaturasJson", json); // O puedes convertir a lista real
                request.getRequestDispatcher("/alumno/inicio.jsp").forward(request, response);
            } else if (apiResponse.statusCode() == 401 || apiResponse.statusCode() == 403) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login.html");
            } else {
                mostrarError(response, "Error al obtener asignaturas del alumno: código " + apiResponse.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError(response, "Error al conectar con la API de asignaturas.");
        }
    }

    private void mostrarError(HttpServletResponse response, String mensaje) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<html><body><h3>" + mensaje + "</h3></body></html>");
    }
}
