package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dew.main.structures.Alumno;
import dew.main.structures.Asignatura;
import dew.main.structures.NotaAlumno;

@WebServlet("/profesor/listaAlumnos")
public class ListaAlumnosAsignaturaServlet extends HttpServlet {

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

	    if (!req.isUserInRole("rolpro")) {
	        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
	        return;
	    }

	    String asig = req.getParameter("asig");
	    HttpSession session = req.getSession(false);

	    if (session == null || session.getAttribute("key") == null || !req.isUserInRole("rolpro")) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

	    String key = (String) session.getAttribute("key");
        String jsessionId = (String) session.getAttribute("jsessionId");
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                		URI.create("http://localhost:9090/CentroEducativo/asignaturas/" + asig + "/alumnos?key=" + key)
        		)
                .header("Accept", "application/json")
                .header("Cookie", "JSESSIONID=" + jsessionId)
                .GET()
                .build();
        
        HttpResponse<String> response;
        List<NotaAlumno> notaAlumnos = new ArrayList<NotaAlumno>();
        List<Alumno> alumnos = new ArrayList<Alumno>();
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
	        notaAlumnos = new ObjectMapper().readValue(response.body(), new TypeReference<List<NotaAlumno>>() {});
	        for(NotaAlumno na : notaAlumnos) {
	        	alumnos.add(fetchAlumno(na.getAlumno(), key, jsessionId));
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}

        req.setAttribute("asig", asig);
        req.setAttribute("notaAlumnos", notaAlumnos);
        req.setAttribute("alumnos", alumnos);
        req.getRequestDispatcher("/profesor/listaAlumnos.jsp").forward(req, resp);
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
}
