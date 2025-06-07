package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@WebServlet("/profesor/listaAlumnos")
public class ListaAlumnosAsignaturaServlet extends HttpServlet {

    public static class Alumno {
        public String dni, nombre, apellidos;
        public Alumno(String d, String n, String a) { dni = d; nombre = n; apellidos = a; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!req.isUserInRole("rolpro")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String asig = req.getParameter("asig");
        HttpSession sesion = req.getSession(false);
        String key = (String) sesion.getAttribute("key");

        List<Alumno> alumnos = new ArrayList<>();
        String url = "http://localhost:9090/CentroEducativo/asignaturas/" + asig + "/alumnos?key=" + key;
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        try (Scanner sc = new Scanner(conn.getInputStream())) {
            String json = sc.useDelimiter("\\A").next();
            for (String b : json.split("\\},\\{")) {
                String dni = extraer(b, "dni");
                String nom = extraer(b, "nombre");
                String ape = extraer(b, "apellidos");
                alumnos.add(new Alumno(dni, nom, ape));
            }
        }

        req.setAttribute("asig", asig);
        req.setAttribute("alumnos", alumnos);
        req.getRequestDispatcher("/profesor/listaAlumnos.jsp").forward(req, resp);
    }

    private String extraer(String json, String campo) {
        String p = "\"" + campo + "\":\"";
        int i = json.indexOf(p);
        return i == -1 ? "" : json.substring(i + p.length(), json.indexOf("\"", i + p.length()));
    }
}
