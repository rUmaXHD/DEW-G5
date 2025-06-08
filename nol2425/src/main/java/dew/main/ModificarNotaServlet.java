
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dew.main.structures.Nota;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/profesor/ajax/modificarNota")
public class ModificarNotaServlet extends HttpServlet {
    private static final String API_URL = "http://localhost:9090/CentroEducativo";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String dniAlumno = req.getParameter("dniAlumno");
        String acronimo = req.getParameter("asignatura");
        String nota = req.getReader().readLine(); // nota simple tipo "6.5"

        HttpSession session = req.getSession(false);
        if (session == null || !req.isUserInRole("rolpro")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL + "/asignaturas/" + acronimo + "/alumnos/" + dniAlumno + "?key=" + key))
            .header("Content-Type", "application/json")
            .header("Cookie", "JSESSIONID=" + jsessionId)
            .PUT(HttpRequest.BodyPublishers.ofString(nota)) // solo texto plano, no JSON
            .build();

        HttpResponse<String> response;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			resp.setStatus(response.statusCode());
	        resp.getWriter().write(response.body());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
    }
}
