<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String detalleJson = (String) request.getAttribute("detalleAsignaturaJson");
    String nombreAlumno = (String) request.getAttribute("nombreAlumno");
    String dniAlumno = (String) request.getAttribute("dniAlumno");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle Asignatura</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .nota-pendiente {
            color: #b30000;
            font-style: italic;
        }
    </style>
</head>
<body class="bg-light">

<div class="container my-5">

    <!-- Tarjeta de bienvenida estilizada -->
    <div class="card shadow-sm border-0 mb-4 bg-primary text-white">
        <div class="card-body">
            <h1 class="card-title">Bienvenid@, <%= nombreAlumno != null ? nombreAlumno : "Alumno" %></h1>
            <p class="card-text"><strong>DNI:</strong> <%= dniAlumno != null ? dniAlumno : "Desconocido" %></p>
        </div>
    </div>

    <h2 class="mb-3">Detalle de la asignatura</h2>
    <div id="detalle" class="card p-4 shadow-sm bg-white border"></div>

    <div class="mt-4">
        <a href="AsignaturasServlet" class="btn btn-outline-primary">&larr; Volver a asignaturas</a>
    </div>
</div>

<!-- JSON embebido -->
<script id="json-data" type="application/json">
<%= detalleJson %>
</script>

<script>
    let datos = {};
    try {
        const raw = document.getElementById("json-data").textContent;
        datos = JSON.parse(raw);
        console.log("📦 Detalle recibido:", datos);
    } catch (e) {
        console.error("❌ Error al parsear el JSON:", e);
    }

    const contenedor = document.getElementById("detalle");
    contenedor.innerHTML = "";

    function creaParrafo(etiqueta, valor, extraClass = "") {
        const p = document.createElement("p");
        if (extraClass) p.className = extraClass;
        const strong = document.createElement("strong");
        strong.textContent = etiqueta;
        p.appendChild(strong);
        p.appendChild(document.createTextNode(" " + valor));
        return p;
    }

    const div = document.createElement("div");

    div.appendChild(creaParrafo("Nombre:", datos.nombre ?? "?"));
    div.appendChild(creaParrafo("Código:", datos.codigo ?? "?"));
    div.appendChild(creaParrafo("Curso:", datos.curso ?? "?"));
    div.appendChild(creaParrafo("Cuatrimestre:", datos.cuatrimestre ?? "?"));
    div.appendChild(creaParrafo("Créditos:", datos.creditos ?? "?"));

    const nota = datos.nota;
    const claseNota = (nota === "Sin calificar" || nota === "No disponible") ? "nota-pendiente" : "";
    div.appendChild(creaParrafo("Nota:", nota, claseNota));

    div.appendChild(creaParrafo("Grupo:", typeof datos.grupoNombre === "string" ? datos.grupoNombre : "Sin grupo asignado"));

    // Miembros
    const pMiembros = document.createElement("p");
    const strongMiembros = document.createElement("strong");
    strongMiembros.textContent = "Miembros:";
    pMiembros.appendChild(strongMiembros);
    div.appendChild(pMiembros);

    const miembrosContainer = document.createElement("div");
    if (datos.miembros && datos.miembros.length > 0) {
        const ul = document.createElement("ul");
        ul.className = "list-group";
        datos.miembros.forEach(m => {
            const li = document.createElement("li");
            li.className = "list-group-item";
            li.textContent = m;
            ul.appendChild(li);
        });
        miembrosContainer.appendChild(ul);
    } else {
        const p = document.createElement("p");
        p.className = "fst-italic text-muted";
        p.textContent = "Sin compañeros asignados.";
        miembrosContainer.appendChild(p);
    }

    div.appendChild(miembrosContainer);
    contenedor.appendChild(div);
</script>

</body>
</html>

