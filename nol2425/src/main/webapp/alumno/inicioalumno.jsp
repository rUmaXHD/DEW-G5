<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String nombreAlumno = (String) request.getAttribute("nombreAlumno");
    String dniAlumno = (String) request.getAttribute("dniAlumno");
    String asignaturasJson = (String) request.getAttribute("asignaturasData");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Asignaturas del alumno</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container my-5">
    <h1 class="mb-1">Bienvenido, <%= nombreAlumno %></h1>
    <p class="text-muted"><strong>DNI:</strong> <%= dniAlumno %></p>

    <h2 class="mt-4 mb-3">Asignaturas</h2>
    <div id="contenedor-asignaturas" class="row gy-3"></div>
</div>

<!-- TU JS FUNCIONAL, con Bootstrap aplicado -->
<script>
    const asignaturas = <%= asignaturasJson %>;
    const contenedor = document.getElementById("contenedor-asignaturas");

    asignaturas.forEach(asig => {
        const col = document.createElement("div");
        col.className = "col-md-6 col-lg-4";

        const div = document.createElement("div");
        div.className = "card h-100 shadow-sm";

        const body = document.createElement("div");
        body.className = "card-body d-flex flex-column justify-content-center align-items-center text-center h-100";

        const nombre = asig.nombre || "(Sin nombre)";
        const codigo = asig.codigo || "?";

        const title = document.createElement("h5");
        title.className = "card-title mb-3";
        title.textContent = nombre + " (" + codigo + ")";


        const boton = document.createElement("a");
        boton.href = "DetalleAsignaturaServlet?codigo=" + encodeURIComponent(codigo);
        boton.className = "btn btn-outline-primary mt-3";
        boton.textContent = "Ver detalles";

        body.appendChild(title);
        body.appendChild(boton);
        div.appendChild(body);
        col.appendChild(div);
        contenedor.appendChild(col);
    });
</script>

</body>
</html>



