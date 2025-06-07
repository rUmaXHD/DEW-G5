<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="dew.main.ListaAlumnosAsignaturaServlet.Alumno" %>
<%
    String asig = (String) request.getAttribute("asig");
    List<Alumno> alumnos = (List<Alumno>) request.getAttribute("alumnos");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Alumnos en <%= asig %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="p-4">
    <h2>Alumnos matriculados en <%= asig %></h2>
    <ul class="list-group">
        <% for (Alumno a : alumnos) { %>
            <li class="list-group-item">
                <a href="verAlumno?asig=<%= asig %>&dni=<%= a.dni %>">
                    <%= a.apellidos %>, <%= a.nombre %> (<%= a.dni %>)
                </a>
            </li>
        <% } %>
    </ul>
    <a href="inicio" class="btn btn-secondary mt-3">← Volver a asignaturas</a>
</body>
</html>
