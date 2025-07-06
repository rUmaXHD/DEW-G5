package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dew.main.structures.Asignatura;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Servlet que obtiene la lista de asignaturas que imparte el profesor autenticado.
 * La lista se obtiene desde la API CentroEducativo y se reenvía a inicio.jsp.
 */
@WebServlet("/profesor/inicio")
public class ProfesorAsignaturasServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String API_URL = "http://localhost:9090/CentroEducativo";

    /**
     * Procesa la solicitud GET, valida la sesión y recupera las asignaturas del profesor.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // Validar sesión activa y rol correcto
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("key") == null || !req.isUserInRole("rolpro")) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        String dni = (String) session.getAttribute("dni");
        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

        try {
            HttpClient client = HttpClient.newHttpClient();

            // Construir la petición para obtener asignaturas del profesor
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/profesores/" + dni + "/asignaturas?key=" + key))
                .header("Content-Type", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[INFO] Respuesta JSON de asignaturas del profesor:");
            System.out.println(response.body());

            if (response.statusCode() != 200) {
                throw new ServletException("Error al obtener asignaturas del profesor");
            }

            // Convertir JSON a lista de objetos Asignatura
            ObjectMapper mapper = new ObjectMapper();
            List<Asignatura> asignaturas = mapper.readValue(response.body(), new TypeReference<List<Asignatura>>() {});

            // Pasar los datos a la JSP
            req.setAttribute("asignaturasData", asignaturas);
            req.getRequestDispatcher("/profesor/inicio.jsp").forward(req, resp);

        } catch (Exception e) {
            System.err.println("[ERROR] Excepción al obtener asignaturas del profesor:");
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en servidor");
        }
    }
}

