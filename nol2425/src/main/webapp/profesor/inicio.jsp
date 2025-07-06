<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dew.main.structures.Asignatura" %>
<%@ page import="java.util.List" %>
<%
    // Lista de asignaturas que imparte el profesor, proporcionada por el servlet
    List<Asignatura> asignaturas = (List<Asignatura>) request.getAttribute("asignaturasData");	
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Inicio Profesor – Notas Online</title>
    <!-- Bootstrap 5 para estilos -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Cabecera principal -->
    <div class="bg-primary text-white py-5 mb-5">
        <div class="container text-center">
            <h1 class="display-5">Asignaturas que impartes</h1>
            <p class="lead mb-0">Selecciona una asignatura para consultar o modificar calificaciones</p>
        </div>
    </div>

    <!-- Tarjetas generadas dinámicamente con los datos de las asignaturas -->
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
                            <a href="irAGestionar?asignatura=<%= a.getAcronimo() %>" class="btn btn-outline-primary mt-3">Acceder</a>
                        </div>
                    </div>
                </div>
            <% } %>
        </div>

        <!-- Botón para cerrar sesión -->
        <div class="text-center mt-5">
            <button class="btn btn-danger px-4" onclick="confirmarLogout()">Cerrar sesión</button>
        </div>

        <!-- Pie de página -->
        <footer class="text-center mt-5 pt-4 border-top">
            <p class="text-muted small">Notas Online · Gestión de asignaturas · Curso 24/25</p>
        </footer>
    </div>

    <!-- Script para forzar cierre de sesión -->
    <script>
        function confirmarLogout() {
            alert("En el siguiente recuadro pulsa CANCELAR para salir completamente del sistema. NO rellenes credenciales.");
            window.location.href = "<%= request.getContextPath() %>/LogoutServlet";
        }
    </script>

</body>
</html>

