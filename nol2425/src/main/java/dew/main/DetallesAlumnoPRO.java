
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
import dew.main.structures.Alumno;
import dew.main.structures.Asignatura;
import dew.main.structures.NotaAlumno;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/profesor/verAlumno")
public class DetallesAlumnoPRO extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String API_URL = "http://localhost:9090/CentroEducativo";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String asig = req.getParameter("asig");
    	String dni = req.getParameter("dni");
    	
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("key") == null || !req.isUserInRole("rolpro")) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }
        System.out.println("✅ Iniciando consulta de asignaturas del profesor:");
        String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");

        try {
            Alumno alumno  = fetchAlumno(dni, key, jsessionId);
            NotaAsignatura notaAsignatura = fetchAsignaturaAlumno(dni, asig, key, jsessionId); 

            req.setAttribute("alumno", alumno);
            req.setAttribute("notaAsignatura", notaAsignatura);
            req.setAttribute("asig", asig);
            req.setAttribute("dni", dni);
            req.setAttribute("key", key);
            req.setAttribute("jsessionid", jsessionId);
            
            req.getRequestDispatcher("/profesor/detallesAlumnoPRO.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en servidor");
        }
    }
    
	private Alumno fetchAlumno(String dni, String key, String jsessionId) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                		URI.create("http://localhost:9090/CentroEducativo/alumnos/" + dni + "?key=" + key)
        		)
                .header("Accept", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readValue(response.body(), Alumno.class);
	}
    
	private NotaAsignatura fetchAsignaturaAlumno(String dni, String acronimo, String key, String jsessionId) throws IOException, InterruptedException {
		for(NotaAsignatura na : fetchAsignaturasAlumno(dni, key, jsessionId)) {
			if(na.getAsignatura().equals(acronimo))
				return na;
		}
		return null;
	}
	
	
	private List<NotaAsignatura> fetchAsignaturasAlumno(String dni, String key, String jsessionId) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                		URI.create("http://localhost:9090/CentroEducativo/alumnos/" + dni + "/asignaturas?key=" + key)
        		)
                .header("Accept", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readValue(response.body(), new TypeReference<List<NotaAsignatura>>() {});
	}
}
