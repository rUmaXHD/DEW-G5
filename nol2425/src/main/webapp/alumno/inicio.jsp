<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%
    String asignaturasSerializado = (String) request.getAttribute("asignaturasSerializado");
	if (asignaturasSerializado == null) asignaturasSerializado = "[]";
%>
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
        
        <script>
    		const data = <%= asignaturasSerializado %>;
            const ul = document.getElementById("asignaturas-list");
            
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
		</script>
        
        
        <div id="asignaturas" class="mt-3">
			<ul class="list-group" id="asignaturas-list"></ul>
        </div>
    </div>
    
    <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
</body>
</html>