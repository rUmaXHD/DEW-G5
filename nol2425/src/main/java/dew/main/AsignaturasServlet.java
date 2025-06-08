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

@WebServlet("/AsignaturasServlet")
public class AsignaturasServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Client client;
	private Gson gson;
	private static final String API_BASE_URL = "http://localhost:9090/CentroEducativo";

	@Override
	public void init() {
		client = ClientBuilder.newClient();
		gson = new Gson();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// Obtener JSESSIONID desde las cookies del navegador
		/*
		 * String jsessionId = null; jakarta.servlet.http.Cookie[] cookies =
		 * req.getCookies(); if (cookies != null) { for (jakarta.servlet.http.Cookie
		 * cookie : cookies) { if ("JSESSIONID".equals(cookie.getName())) { jsessionId =
		 * cookie.getValue(); break; } } }
		 */

		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("key") == null) {
			resp.sendRedirect(req.getContextPath() + "/LoginServlet");
			return;
		}

		String key = (String) session.getAttribute("key");
		String password = (String) session.getAttribute("password");
		String dni = (String) session.getAttribute("dni");
		String jsessionId = (String) session.getAttribute("jsessionId");

		try {
		    // Obtener información del alumno
		    System.out.println("Realizando petición a API");
		    HttpClient client = HttpClient.newHttpClient();

		    HttpRequest requestAlumnosInfo = HttpRequest.newBuilder()
		        .uri(URI.create(API_BASE_URL + "/alumnos/" + dni + "?key=" + key))
		        .header("Content-Type", "application/json")
		        .header("Cookie", "JSESSIONID=" + jsessionId)
		        .GET()
		        .build();

		    System.out.println("Usando JSESSIONID: " + jsessionId);
		    HttpResponse<String> alumnoResponse = client.send(requestAlumnosInfo, HttpResponse.BodyHandlers.ofString());
		    System.out.println("Respuesta alumnos API: " + alumnoResponse.body());

		    if (alumnoResponse.statusCode() != 200) {
		        throw new ServletException("Error al obtener información del alumno");
		    }

		    JsonObject alumnoData = gson.fromJson(alumnoResponse.body(), JsonObject.class);

		    // Obtener asignaturas del alumno
		    HttpRequest requestAsignaturas = HttpRequest.newBuilder()
		        .uri(URI.create(API_BASE_URL + "/alumnos/" + dni + "/asignaturas?key=" + key))
		        .header("Content-Type", "application/json")
		        .header("Cookie", "JSESSIONID=" + jsessionId)
		        .GET()
		        .build();

		    HttpResponse<String> asignaturasResponse = client.send(requestAsignaturas, HttpResponse.BodyHandlers.ofString());
		    System.out.println("ASIGNATURAS JSON: " + asignaturasResponse.body());

		    if (asignaturasResponse.statusCode() != 200) {
		        throw new ServletException("Error al obtener asignaturas del alumno");
		    }

		    JsonArray asignaturasArray = gson.fromJson(asignaturasResponse.body(), JsonArray.class);
		    List<Map<String, Object>> asignaturasInfo = new ArrayList<>();

		    ObjectMapper mapper = new ObjectMapper();

		    System.out.println("✅ JSON bruto de asignaturasArray: " + asignaturasArray.toString());

		    for (JsonElement asigElement : asignaturasArray) {
		        try {
		            System.out.println("→ Procesando asignatura JSON: " + asigElement.toString());
		            Nota elem = mapper.readValue(asigElement.toString(), Nota.class);
		            String acronimo = elem.getAsignatura();

		            if (acronimo == null) {
		                System.out.println("⚠️ ACRÓNIMO NULO, se omite");
		                continue;
		            }

		            System.out.println("✅ Cargando detalles para asignatura: " + acronimo);

		            // Detalle de la asignatura
		            HttpRequest requestDetalleAsig = HttpRequest.newBuilder()
		                .uri(URI.create(API_BASE_URL + "/asignaturas/" + acronimo + "?key=" + key))
		                .header("Content-Type", "application/json")
		                .header("Cookie", "JSESSIONID=" + jsessionId)
		                .GET()
		                .build();

		            HttpResponse<String> detalleResponse = client.send(requestDetalleAsig, HttpResponse.BodyHandlers.ofString());
		            System.out.println("Status detalle asignatura " + acronimo + ": " + detalleResponse.statusCode());
		            System.out.println("Cuerpo detalle asignatura " + acronimo + ": " + detalleResponse.body());

		            if (detalleResponse.statusCode() == 200) {
		                JsonObject asigDetalle = gson.fromJson(detalleResponse.body(), JsonObject.class);
		                Map<String, Object> asignaturaMap = new HashMap<>();
		                asignaturaMap.put("codigo", asigDetalle.get("acronimo").getAsString());
		                asignaturaMap.put("nombre", asigDetalle.get("nombre").getAsString());
		                asignaturaMap.put("curso", asigDetalle.get("curso").getAsInt());
		                asignaturaMap.put("cuatrimestre", asigDetalle.get("cuatrimestre").getAsString());
		                asignaturaMap.put("creditos", asigDetalle.get("creditos").getAsDouble());

		                // Inicializar por defecto
		                asignaturaMap.put("grupoNombre", "Sin grupo asignado");
		                asignaturaMap.put("miembros", new ArrayList<String>());

		                // Grupos
		                HttpRequest requestGrupos = HttpRequest.newBuilder()
		                    .uri(URI.create(API_BASE_URL + "/asignaturas/" + acronimo + "/grupos?key=" + key))
		                    .header("Content-Type", "application/json")
		                    .GET()
		                    .build();

		                HttpResponse<String> grupoResponse = client.send(requestGrupos, HttpResponse.BodyHandlers.ofString());

		                if (grupoResponse.statusCode() == 200) {
		                    JsonArray grupos = gson.fromJson(grupoResponse.body(), JsonArray.class);
		                    if (grupos.size() > 0) {
		                        JsonObject primerGrupo = grupos.get(0).getAsJsonObject();
		                        String nombreGrupo = primerGrupo.get("nombre").getAsString();
		                        asignaturaMap.put("grupoNombre", "Grupo " + nombreGrupo);

		                        // Miembros
		                        HttpRequest requestMiembros = HttpRequest.newBuilder()
		                            .uri(URI.create(API_BASE_URL + "/asignaturas/" + acronimo + "/grupos/" + nombreGrupo + "/alumnos?key=" + key))
		                            .header("Content-Type", "application/json")
		                            .GET()
		                            .build();

		                        HttpResponse<String> miembrosResponse = client.send(requestMiembros, HttpResponse.BodyHandlers.ofString());

		                        if (miembrosResponse.statusCode() == 200) {
		                            JsonArray miembrosArray = gson.fromJson(miembrosResponse.body(), JsonArray.class);
		                            List<String> miembros = new ArrayList<>();
		                            for (JsonElement miembroElement : miembrosArray) {
		                                JsonObject miembro = miembroElement.getAsJsonObject();
		                                miembros.add(miembro.get("nombre").getAsString() + " " +
		                                             miembro.get("apellidos").getAsString());
		                            }
		                            asignaturaMap.put("miembros", miembros);
		                        }
		                    }
		                }

		                asignaturasInfo.add(asignaturaMap);
		            }
		        } catch (Exception exAsig) {
		            System.out.println("❌ Error al procesar asignatura individual");
		            exAsig.printStackTrace();
		        }
		    }

		    // Enviar datos a la JSP
		    req.setAttribute("asignaturasData", gson.toJson(asignaturasInfo));
		    req.setAttribute("dniAlumno", dni);
		    req.setAttribute("nombreAlumno", alumnoData.get("nombre").getAsString() + " " + alumnoData.get("apellidos").getAsString());
		    System.out.println("ASIGNATURAS INFO JSON FINAL: " + gson.toJson(asignaturasInfo));
		    req.getRequestDispatcher("/alumno/inicioalumno.jsp").forward(req, resp);

		} catch (Exception e) {
		    e.printStackTrace();
		    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud: " + e.getMessage());
		}
	}

	@Override
	public void destroy() {
		if (client != null) {
			client.close();
		}
	}
}