<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="dew.main.structures.NotaAlumno" %>
<%@ page import="dew.main.structures.Alumno" %>
<%
    String asig = (String) request.getAttribute("asig");
	List<NotaAlumno> notaAlumnos = (List<NotaAlumno>) request.getAttribute("notaAlumnos");
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
        <% 
        for (int i = 0; i < notaAlumnos.size(); i++) {
        	NotaAlumno na = notaAlumnos.get(i);
        	Alumno a = alumnos.get(i);
        %>
            <li class="list-group-item">
                <a href="verAlumno?asig=<%= asig %>&dni=<%= na.getAlumno() %>">
                    <%= a.getApellidos() %>, <%= a.getNombre() %> (<%= a.getDni() %>) [<%= na.getNota() %>]
                </a>
            </li>
        <% } %>
    </ul>
    <a href="inicio" class="btn btn-secondary mt-3">← Volver a asignaturas</a>
</body>
</html>
