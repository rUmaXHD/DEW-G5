<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%
    String asignaturasJson = (String) request.getAttribute("asignaturasJson");
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
    		const data = <%= asignaturasJson %>;

		    document.write("<ul>");
		    data.forEach(a => {
		        document.write("<li>" + JSON.stringify(a) + "</li>");
		    });
		    document.write("</ul>");
	</script>
        
        
        <div id="asignaturas" class="mt-3">
            <!-- Los datos se cargarán con JavaScript -->
        </div>
    </div>
    
    <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
</body>
</html>