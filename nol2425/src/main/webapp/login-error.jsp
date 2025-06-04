<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error de autenticación</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .error-container {
            max-width: 500px;
            margin: 100px auto;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 15px rgba(0,0,0,0.1);
        }
        .error-icon {
            font-size: 5rem;
            color: #dc3545;
            margin-bottom: 20px;
        }
    </style>
</head>
<body class="bg-light">
    <div class="container">
        <div class="error-container bg-white text-center">
            <div class="error-icon">
                <i class="bi bi-exclamation-triangle-fill"></i>
            </div>
            <h2 class="text-danger mb-4">Error de autenticación</h2>
            
            <c:choose>
                <c:when test="${not empty param.error}">
                    <p class="lead">Credenciales incorrectas</p>
                    <p>El DNI y/o contraseña que ingresaste no son válidos.</p>
                </c:when>
                <c:otherwise>
                    <p class="lead">Acceso no autorizado</p>
                    <p>No tienes permisos para acceder a este recurso.</p>
                </c:otherwise>
            </c:choose>
            
            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-primary">
                    <i class="bi bi-arrow-left"></i> Volver al login
                </a>
            </div>
            
            <!-- Solo para desarrollo - Mostrar detalles del error -->
            <c:if test="${pageContext.request.serverName == 'localhost'}">
                <div class="mt-4 p-3 bg-light text-start small">
                    <p class="mb-1"><strong>Detalles técnicos:</strong></p>
                    <p class="mb-1">Error: ${requestScope['jakarta.servlet.error.message']}</p>
                    <p class="mb-1">Código: ${requestScope['jakarta.servlet.error.status_code']}</p>
                    <p>URI: ${requestScope['jakarta.servlet.error.request_uri']}</p>
                </div>
            </c:if>
        </div>
    </div>

    
</body>
</html>