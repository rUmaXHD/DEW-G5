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
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Alumnos en <%= asig %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Cabecera azul -->
    <div class="bg-primary text-white py-4 mb-4">
        <div class="container text-center">
            <h1 class="h3 mb-0">Alumnos matriculados en <span class="fw-bold"><%= asig %></span></h1>
        </div>
    </div>

    <div class="container">
        <div class="list-group shadow-sm mb-4">
            <% 
            for (int i = 0; i < notaAlumnos.size(); i++) {
                NotaAlumno na = notaAlumnos.get(i);
                Alumno a = alumnos.get(i);
            %>
                <a href="verAlumno?asig=<%= asig %>&dni=<%= na.getAlumno() %>" 
                   class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                    <div>
                        <h5 class="mb-1"><%= a.getApellidos() %>, <%= a.getNombre() %></h5>
                        <small class="text-muted">DNI: <%= a.getDni() %></small>
                    </div>
                    <span class="badge bg-secondary rounded-pill">
                        <%= na.getNota() != null ? na.getNota() : "Sin calificar" %>
                    </span>
                </a>
            <% } %>
        </div>

        <div class="text-start">
            <a href="inicio" class="btn btn-outline-primary">
                &larr; Volver a asignaturas
            </a>
        </div>
    </div>

</body>
</html>

