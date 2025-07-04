<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dew.main.structures.Asignatura" %>
<%@ page import="java.util.List" %>
<%
    List<Asignatura> asignaturas = (List<Asignatura>) request.getAttribute("asignaturasData");	
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Inicio Profesor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Cabecera azul -->
    <div class="bg-primary text-white py-5 mb-4">
        <div class="container text-center">
            <h1 class="display-6">Asignaturas que impartes</h1>
            <p class="mb-0">Selecciona una asignatura para ver y gestionar calificaciones</p>
        </div>
    </div>

    <div class="container">
        <div class="row gy-4">
            <% for (Asignatura a : asignaturas) { %>
                <div class="col-md-6 col-lg-4">
                    <div class="card shadow-sm h-100">
                        <div class="card-body d-flex flex-column justify-content-between">
                            <h5 class="card-title"><%= a.getNombre() %></h5>
                            <p class="card-text mb-1"><strong>Acrónimo:</strong> <%= a.getAcronimo() %></p>
                            <p class="card-text mb-1"><strong>Curso:</strong> <%= a.getCurso() %>º</p>
                            <p class="card-text mb-1"><strong>Cuatrimestre:</strong> <%= a.getCuatrimestre() %></p>
                            <p class="card-text"><strong>Créditos:</strong> <%= a.getCreditos() %></p>
                            <a href="<%= request.getContextPath() %>/profesor/listaAlumnos?asig=<%= a.getAcronimo() %>" 
                               class="btn btn-outline-primary mt-3">Acceder</a>
                        </div>
                    </div>
                </div>
            <% } %>
        </div>
    </div>
    <div class="container">
		<a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-danger">Cerrar sesión</a>

    </div>

</body>
</html>
