<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Panel de Profesor - Notas Online</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-5">
    <%
        // Verificar si el usuario está autenticado y es profesor
        if (session.getAttribute("key") == null || !request.isUserInRole("rolpro")) {
            response.sendRedirect("../login.jsp?error=Acceso no autorizado");
            return;
        }
    %>
    
    <div class="row">
        <div class="col-md-12">
            <h1>Panel de Profesor</h1>
            <p>Bienvenido, profesor con DNI: <%= session.getAttribute("dni") %></p>
            <hr>
            <!-- Aquí irá el contenido específico del profesor -->
        </div>
    </div>
</body>
</html> 