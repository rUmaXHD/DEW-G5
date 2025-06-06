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
        
        <div id="asignaturas-container" class="mt-3">
            <ul id="asignaturas-list" class="list-group"></ul>
        </div>
    </div>
    
    
    <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            fetch('${pageContext.request.contextPath}/AsignaturasServlet')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error en la respuesta');
                    }
                    return response.json();
                })
                .then(asignaturas => {
                    const list = document.getElementById('asignaturas-list');
                    list.innerHTML = ''; // Limpiar lista
                    
                    if (asignaturas.length > 0) {
                        asignaturas.forEach(asig => {
                            const item = document.createElement('li');
                            item.className = 'list-group-item';
                            item.textContent = asig;
                            list.appendChild(item);
                        });
                    } else {
                        const item = document.createElement('li');
                        item.className = 'list-group-item text-danger';
                        item.textContent = 'No estás matriculado en ninguna asignatura.';
                        list.appendChild(item);
                    }
                })
                .catch(error => {
                    const list = document.getElementById('asignaturas-list');
                    list.innerHTML = '<li class="list-group-item text-danger">Error al cargar las asignaturas</li>';
                    console.error('Error:', error);
                });
        });
    </script>
</body>
</html>