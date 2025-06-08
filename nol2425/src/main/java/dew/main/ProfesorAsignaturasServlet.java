
package dew.main;

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

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dew.main.structures.NotaAsignatura;
import dew.main.structures.Asignatura;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/profesor/inicio")
public class ProfesorAsignaturasServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String API_URL = "http://localhost:9090/CentroEducativo";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/profesores/" + dni + "/asignaturas?key=" + key))
                .header("Content-Type", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET()
               .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            if (response.statusCode() != 200) {
                throw new ServletException("Error al obtener asignaturas del profesor");
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Asignatura> asignaturas = mapper.readValue(response.body(), new TypeReference<List<Asignatura>>() {});
            req.setAttribute("asignaturasData", asignaturas);
            
            req.getRequestDispatcher("/profesor/inicio.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en servidor");
        }
    }
}
