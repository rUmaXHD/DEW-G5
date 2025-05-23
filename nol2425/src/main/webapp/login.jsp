<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Notas Online</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h1 class="text-center mb-4">Notas Online</h1>
            
            <%-- Mostrar errores --%>
            <% if (request.getParameter("error") != null) { %>
                <div class="alert alert-danger">
                    Credenciales incorrectas. Por favor, intente de nuevo.
                </div>
            <% } %>
            
            <form action="j_security_check" method="post">
                <div class="mb-3">
                    <label for="j_username" class="form-label">DNI</label>
                    <input type="text" class="form-control" id="j_username" name="j_username" required>
                </div>
                <div class="mb-3">
                    <label for="j_password" class="form-label">Contraseña</label>
                    <input type="password" class="form-control" id="j_password" name="j_password" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">Ingresar</button>
            </form>
        </div>
    </div>
</body>
</html>