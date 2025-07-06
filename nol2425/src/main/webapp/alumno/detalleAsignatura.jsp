<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Recuperar los atributos enviados desde DetalleAsignaturaServlet
    String detalleJson = (String) request.getAttribute("detalleAsignaturaJson");
    String nombreAlumno = (String) request.getAttribute("nombreAlumno");
    String dniAlumno = (String) request.getAttribute("dniAlumno");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle Asignatura</title>
    <!-- Bootstrap 5 para estilos -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        /* Estilo para notas aún no calificadas */
        .nota-pendiente {
            color: #b30000;
            font-style: italic;
        }
    </style>
</head>
<body class="bg-light">

    <!-- Cabecera personalizada -->
    <div class="bg-primary text-white py-5 mb-4">
        <div class="container text-center">
            <h1 class="display-6">Bienvenid@, <%= nombreAlumno != null ? nombreAlumno : "Alumno" %></h1>
            <p class="mb-0"><strong>DNI:</strong> <%= dniAlumno != null ? dniAlumno : "Desconocido" %></p>
        </div>
    </div>

    <!-- Contenedor de detalle -->
    <div class="container">
        <h2 class="text-secondary mb-4">Detalle de la asignatura</h2>
        <div id="detalle" class="card shadow-sm border p-4 bg-white mb-4"></div>

        <!-- Botón para volver atrás -->
        <div class="text-start">
            <a href="AsignaturasServlet" class="btn btn-outline-primary">&larr; Volver a asignaturas</a>
        </div>
    </div>

    <!-- JSON embebido desde el servidor -->
    <script id="json-data" type="application/json">
<%= detalleJson %>
    </script>

    <!-- Script para construir dinámicamente la tarjeta de detalles -->
    <script>
        let datos = {};
        try {
            const raw = document.getElementById("json-data").textContent;
            datos = JSON.parse(raw);
        } catch (e) {
            console.error("❌ Error al parsear el JSON:", e);
        }

        const contenedor = document.getElementById("detalle");
        contenedor.innerHTML = "";

        /**
         * Crea un párrafo con una etiqueta en negrita y su valor.
         * Si se pasa una clase adicional, se aplica al párrafo.
         */
        function creaParrafo(etiqueta, valor, extraClass = "") {
            const p = document.createElement("p");
            if (extraClass) p.className = extraClass;
            const strong = document.createElement("strong");
            strong.textContent = etiqueta;
            p.appendChild(strong);
            p.appendChild(document.createTextNode(" " + valor));
            return p;
        }

        // Contenedor principal
        const div = document.createElement("div");

        // Datos básicos
        div.appendChild(creaParrafo("Nombre:", datos.nombre ?? "?"));
        div.appendChild(creaParrafo("Código:", datos.codigo ?? "?"));
        div.appendChild(creaParrafo("Curso:", datos.curso ?? "?"));
        div.appendChild(creaParrafo("Cuatrimestre:", datos.cuatrimestre ?? "?"));
        div.appendChild(creaParrafo("Créditos:", datos.creditos ?? "?"));

        // Nota del alumno
        const nota = datos.nota;
        const claseNota = (nota === "Sin calificar" || nota === "No disponible") ? "nota-pendiente" : "";
        div.appendChild(creaParrafo("Nota:", nota, claseNota));

        // Grupo
        div.appendChild(creaParrafo("Grupo:", typeof datos.grupoNombre === "string" ? datos.grupoNombre : "Sin grupo asignado"));

        // Miembros del grupo
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

