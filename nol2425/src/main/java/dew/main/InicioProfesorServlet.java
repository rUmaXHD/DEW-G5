package dew.main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@WebServlet("/InicioProfesorServlet")
public class InicioProfesorServlet extends HttpServlet {

    public static class Asignatura {
        public String acronimo, nombre;
        public Asignatura(String a, String n) {
            this.acronimo = a;
            this.nombre = n;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!req.isUserInRole("rolpro")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        HttpSession sesion = req.getSession(false);
        String dni = (String) sesion.getAttribute("dni");
        String key = (String) sesion.getAttribute("key");

        if (key == null || dni == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        List<Map<String, String>> asignaturas;
		try {
			asignaturas = obtenerAsignaturasDeProfesor(dni, key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        req.getRequestDispatcher("/profesor/inicio.jsp").forward(req, resp);
    }

    private List<Map<String, String>> obtenerAsignaturasDeProfesor(String dni, String key) throws Exception {
        List<Map<String, String>> asignaturas = new ArrayList<>();

        String urlStr = "http://localhost:9090/CentroEducativo/profesores/" + dni + "/asignaturas?key=" + key;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String json = response.body();
            System.out.println(json + "respuesta");

            // Procesamiento manual del JSON
            String[] bloques = json.split("\\},\\{");
            for (String bloque : bloques) {
                Map<String, String> asignatura = new HashMap<>();
                asignatura.put("acronimo", extraerCampo(bloque, "acronimo"));
                asignatura.put("nombre", extraerCampo(bloque, "nombre"));
                asignatura.put("curso", extraerCampo(bloque, "curso"));
                asignatura.put("cuatrimestre", extraerCampo(bloque, "cuatrimestre"));
                asignatura.put("creditos", extraerCampo(bloque, "creditos"));
                asignaturas.add(asignatura);
            }

        } else {
            System.err.println("Error al obtener asignaturas: HTTP " + response.statusCode());
        }

        return asignaturas;
    }


    private String extraerCampo(String json, String campo) {
        String patron = "\"" + campo + "\":";
        int inicio = json.indexOf(patron);
        if (inicio == -1) return "";

        inicio += patron.length();
        char c = json.charAt(inicio);

        if (c == '"') {
            int fin = json.indexOf("\"", inicio + 1);
            return json.substring(inicio + 1, fin);
        } else {
            int fin = json.indexOf(",", inicio);
            if (fin == -1) fin = json.indexOf("}", inicio);
            return json.substring(inicio, fin).trim();
        }
    }
}