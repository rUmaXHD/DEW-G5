<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Inicio Alumno</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="#">Bienvenido Alumno</a>
        </div>
    </nav>
    
    <div class="container mt-4">
        <h2>Mis Asignaturas</h2>
        <%-- Aquí irá el listado dinámico --%>
        <div id="asignaturas" class="mt-3">
            <!-- Los datos se cargarán con JavaScript -->
        </div>
    </div>

    <script>
        // JavaScript para cargar datos via AJAX
        fetch('AsignaturasServlet')
            .then(response => response.json())
            .then(data => {
                let html = '';
                data.forEach(asignatura => {
                    html += `<div class="card mb-2">
                                <div class="card-body">
                                    ${asignatura.nombre} (${asignatura.acronimo})
                                </div>
                            </div>`;
                });
                document.getElementById('asignaturas').innerHTML = html;
            });
    </script>
</body>
</html>