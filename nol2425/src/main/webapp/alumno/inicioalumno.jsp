<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Recuperamos atributos desde el servlet AsignaturasServlet
    String nombreAlumno = (String) request.getAttribute("nombreAlumno");
    String dniAlumno = (String) request.getAttribute("dniAlumno");
    String asignaturasJson = (String) request.getAttribute("asignaturasData"); // JSON con asignaturas
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Asignaturas del alumno</title>
    <!-- Bootstrap 5 para estilos -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Cabecera con saludo personalizado -->
    <div class="bg-primary text-white py-5 mb-4">
        <div class="container text-center">
            <h1 class="display-6">Bienvenid@, <%= nombreAlumno != null ? nombreAlumno : "Alumno" %></h1>
            <p class="mb-0"><strong>DNI:</strong> <%= dniAlumno != null ? dniAlumno : "Desconocido" %></p>
        </div>
    </div>

    <!-- Contenedor para tarjetas de asignaturas -->
    <div class="container">
        <h2 class="text-secondary mb-4">Tus asignaturas</h2>
        <div id="contenedor-asignaturas" class="row gy-4"></div>
    </div>
    
    <!-- Botón para generar el certificado académico -->
    <div class="text-center my-4">
        <a href="<%= request.getContextPath() %>/alumno/certificado" class="btn btn-success btn-lg">
            Generar certificado
        </a>
    </div>

    <!-- Botón para cerrar sesión -->
    <div class="container my-5 text-center">
        <button class="btn btn-danger px-4" onclick="confirmarLogout()">Cerrar sesión</button>
    </div>

    <!-- Script: generación dinámica de tarjetas a partir del JSON -->
    <script>
        // Parseo del JSON de asignaturas generado por el servlet y embebido en la JSP
        const asignaturas = <%= asignaturasJson %>;
        const contenedor = document.getElementById("contenedor-asignaturas");

        // Generamos una tarjeta por cada asignatura
        asignaturas.forEach(asig => {
            const col = document.createElement("div");
            col.className = "col-md-6 col-lg-4";

            const div = document.createElement("div");
            div.className = "card h-100 shadow-sm border-0";

            const body = document.createElement("div");
            body.className = "card-body d-flex flex-column justify-content-center align-items-center text-center";

            const nombre = asig.nombre || "(Sin nombre)";
            const codigo = asig.codigo || "?";

            const title = document.createElement("h5");
            title.className = "card-title mb-2 fw-bold";
            title.textContent = nombre;

            const code = document.createElement("p");
            code.className = "text-muted mb-3";
            code.textContent = "Código: " + codigo;

            const boton = document.createElement("a");
            boton.href = "DetalleAsignaturaServlet?codigo=" + encodeURIComponent(codigo);
            boton.className = "btn btn-outline-primary mt-auto";
            boton.textContent = "Ver detalles";

            // Estructura final
            body.appendChild(title);
            body.appendChild(code);
            body.appendChild(boton);
            div.appendChild(body);
            col.appendChild(div);
            contenedor.appendChild(col);
        });

        // Función para confirmar logout
        function confirmarLogout() {
            alert(" En el siguiente recuadro pulsa CANCELAR para salir completamente del sistema. NO hagas caso a los cuadros de rellenar");
            window.location.href = "<%= request.getContextPath() %>/LogoutServlet";
        }
    </script>

</body>
</html>

