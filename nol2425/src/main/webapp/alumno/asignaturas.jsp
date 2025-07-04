<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String asignaturasJson = (String) request.getAttribute("asignaturasData");
    String nombreAlumno = (String) request.getAttribute("nombreAlumno");
    String dniAlumno = (String) request.getAttribute("dniAlumno");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Inicio - Alumno</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Cabecera azul -->
    <div class="bg-primary text-white py-5 mb-4">
        <div class="container text-center">
            <h1 class="display-6">Bienvenid@, <%= nombreAlumno != null ? nombreAlumno : "Alumno" %></h1>
            <p class="mb-0"><strong>DNI:</strong> <%= dniAlumno != null ? dniAlumno : "Desconocido" %></p>
        </div>
    </div>

    <div class="container">
        <h2 class="text-secondary mb-4">Asignaturas matriculadas</h2>
        <div id="asignaturas" class="row gy-4"></div>
    </div>

    <!-- JSON seguro embebido -->
    <script id="json-data" type="application/json">
<%= asignaturasJson %>
    </script>

    <!-- Script para generar tarjetas -->
    <script>
        let asignaturas = [];
        try {
            const raw = document.getElementById("json-data").textContent;
            asignaturas = JSON.parse(raw);
        } catch (e) {
            console.error("❌ Error al parsear el JSON:", e);
        }

        const contenedor = document.getElementById("asignaturas");
        contenedor.innerHTML = "";

        if (!asignaturas || asignaturas.length === 0) {
            contenedor.innerHTML = "<p>No estás inscrito en ninguna asignatura.</p>";
        } else {
            asignaturas.forEach(asig => {
                const col = document.createElement("div");
                col.className = "col-md-6 col-lg-4";

                const card = document.createElement("div");
                card.className = "card shadow-sm h-100";

                const body = document.createElement("div");
                body.className = "card-body";

                const title = document.createElement("h5");
                title.className = "card-title";
                title.textContent = `${asig.nombre || "(Sin nombre)"} (${asig.codigo || "?"})`;
                body.appendChild(title);

                const info = [
                    ["Curso", asig.curso ?? "?"],
                    ["Cuatrimestre", asig.cuatrimestre ?? "?"],
                    ["Créditos", asig.creditos ?? "?"],
                    ["Grupo", asig.grupoNombre ?? "Sin grupo asignado"]
                ];

                info.forEach(([label, value]) => {
                    const p = document.createElement("p");
                    p.innerHTML = `<strong>${label}:</strong> ${value}`;
                    body.appendChild(p);
                });

                const miembrosSection = document.createElement("div");
                const miembrosTitle = document.createElement("p");
                miembrosTitle.innerHTML = "<strong>Miembros:</strong>";
                miembrosSection.appendChild(miembrosTitle);

                if (asig.miembros && asig.miembros.length > 0) {
                    const ul = document.createElement("ul");
                    asig.miembros.forEach(m => {
                        const li = document.createElement("li");
                        li.textContent = m;
                        ul.appendChild(li);
                    });
                    miembrosSection.appendChild(ul);
                } else {
                    const p = document.createElement("p");
                    p.className = "fst-italic";
                    p.textContent = "Sin compañeros asignados.";
                    miembrosSection.appendChild(p);
                }

                body.appendChild(miembrosSection);
                card.appendChild(body);
                col.appendChild(card);
                contenedor.appendChild(col);
            });
        }
    </script>

</body>
</html>
