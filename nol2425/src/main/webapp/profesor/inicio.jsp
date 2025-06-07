<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%
    List<Map<String, String>> asignaturas = (List<Map<String, String>>) request.getAttribute("asignaturas");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Inicio Profesor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="#">Bienvenido Profesor</a>
        </div>
    </nav>
    
    <div class="container mt-4">
        <h2>Asignaturas que impartes</h2>
        <div id="asignaturas" class="mt-3">
            <% if (asignaturas != null && !asignaturas.isEmpty()) {
                   for (Map<String, String> a : asignaturas) { %>
                <div class="card mb-2">
                    <div class="card-body">
                        <a href="listaAlumnos?asig=<%= a.get("acronimo") %>" class="text-decoration-none">
                            <%= a.get("nombre") %> (<%= a.get("acronimo") %>)
                        </a>
                    </div>
                </div>
            <% } } else { %>
                <div class="alert alert-warning">No se han encontrado asignaturas.</div>
            <% } %>
        </div>
    </div>
</body>
</html>
