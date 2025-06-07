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
<<<<<<< HEAD
        <h2>Mis Asignaturas</h2>
        
        <div id="asignaturas" class="mt-3">
            <ul class="list-group" id="asignaturas-list"></ul>
        </div>
    </div>
    
    <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>

    <script>
        // Función para cargar las asignaturas via AJAX
        function cargarAsignaturas() {
            fetch('${pageContext.request.contextPath}/AsignaturasServlet')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error en la respuesta del servidor');
                    }
                    return response.json();
                })
                .then(data => {
                    const ul = document.getElementById("asignaturas-list");
                    ul.innerHTML = ''; // Limpiar lista antes de agregar elementos
                    
                    if (Array.isArray(data) && data.length > 0) {
                        data.forEach(a => {
                            const li = document.createElement("li");
                            li.className = "list-group-item";
                            li.textContent = a;
                            ul.appendChild(li);
                        });
                    } else {
                        const li = document.createElement("li");
                        li.className = "list-group-item text-danger";
                        li.textContent = "No estás matriculado en ninguna asignatura.";
                        ul.appendChild(li);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    const ul = document.getElementById("asignaturas-list");
                    ul.innerHTML = '<li class="list-group-item text-danger">Error al cargar las asignaturas</li>';
                });
        }

        // Llamar a la función cuando la página se cargue
        document.addEventListener('DOMContentLoaded', cargarAsignaturas);
    </script>
=======
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
>>>>>>> Andrés
</body>
</html>
