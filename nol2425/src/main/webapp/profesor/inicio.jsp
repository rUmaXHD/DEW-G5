<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String asignaturasJson = (String) request.getAttribute("asignaturasData");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Asignaturas del Profesor</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 2rem;
        }
        .asignatura {
            border: 2px solid #007bff;
            background: #f4faff;
            padding: 1rem;
            margin-bottom: 1rem;
            cursor: pointer;
        }
        .alumnos {
            margin-top: 1rem;
            padding-left: 2rem;
        }
        .alumno-card {
            border: 1px solid #ccc;
            margin-bottom: 1rem;
            padding: 1rem;
            background: #fff;
        }
        img.foto {
            max-width: 80px;
            margin-right: 1rem;
            float: left;
            border-radius: 8px;
        }
    </style>
</head>
<body>
    <h1>Asignaturas que impartes</h1>

    <div id="asignaturas"></div>

    <!-- JSON embebido -->
    <script id="json-data" type="application/json">
<%= asignaturasJson %>
    </script>

    <script>
        const asignaturas = JSON.parse(document.getElementById("json-data").textContent || "[]");
        const contenedor = document.getElementById("asignaturas");

        if (!asignaturas.length) {
            contenedor.innerHTML = "<p>No tienes asignaturas asignadas.</p>";
        } else {
            asignaturas.forEach(asig => {
                const div = document.createElement("div");
                div.className = "asignatura";
                div.innerHTML = `
                    <h3>${asig.nombre} (${asig.acronimo})</h3>
                    <p><strong>Curso:</strong> ${asig.curso} | <strong>Cuatrimestre:</strong> ${asig.cuatrimestre} | <strong>Créditos:</strong> ${asig.creditos}</p>
                    <div class="alumnos" id="alumnos-${asig.acronimo.replaceAll(' ', '_')}">Haz clic para ver alumnos</div>
                `;
                div.addEventListener("click", () => cargarAlumnos(asig.acronimo));
                contenedor.appendChild(div);
            });
        }

        function cargarAlumnos(acronimo) {
            const target = document.getElementById("alumnos-" + acronimo.replaceAll(' ', '_'));
            target.innerHTML = "Cargando alumnos...";

            fetch("ajax/alumnos?asignatura=" + encodeURIComponent(acronimo))
                .then(resp => resp.json())
                .then(alumnos => {
                    if (!alumnos.length) {
                        target.innerHTML = "<p>No hay alumnos inscritos.</p>";
                        return;
                    }

                    target.innerHTML = "";
                    alumnos.forEach(alumno => {
                        const card = document.createElement("div");
                        card.className = "alumno-card";

                        // Foto (si existe)
                        if (alumno.foto) {
                            const img = document.createElement("img");
                            img.className = "foto";
                            img.src = alumno.foto;
                            img.alt = "Foto de " + alumno.nombre;
                            card.appendChild(img);
                        }

                        // Datos del alumno
                        const nombre = `${alumno.nombre} ${alumno.apellidos}`;
                        const dni = alumno.dni;
                        const nota = alumno.nota ?? "";

                        const info = document.createElement("p");
                        info.innerHTML = `<strong>${nombre}</strong> (<em>${dni}</em>)`;
                        card.appendChild(info);

                        // Campo de nota
                        const notaLabel = document.createElement("label");
                        notaLabel.innerHTML = `Nota: <input type="number" id="nota-${dni}" value="${nota}" step="0.1" min="0" max="10">`;
                        card.appendChild(notaLabel);

                        // Botón de guardar
                        const btn = document.createElement("button");
                        btn.textContent = "Guardar";
                        btn.onclick = () => guardarNota(dni, acronimo);
                        card.appendChild(btn);

                        target.appendChild(card);
                    });
                })
                .catch(err => {
                    console.error("❌ Error al cargar alumnos:", err);
                    target.innerHTML = "<p>Error al cargar alumnos.</p>";
                });
        }

        function guardarNota(dni, acronimo) {
            const nota = document.getElementById("nota-" + dni).value;
            fetch("ajax/modificarNota?dniAlumno=" + encodeURIComponent(dni) + "&asignatura=" + encodeURIComponent(acronimo), {
                method: "PUT",
                headers: { "Content-Type": "text/plain" },
                body: nota
            })
            .then(resp => {
                if (resp.ok) {
                    alert("✅ Nota actualizada");
                } else {
                    alert("❌ Error al actualizar la nota");
                }
            })
            .catch(err => {
                console.error("❌ Error en PUT:", err);
                alert("❌ Error al guardar nota");
            });
        }
    </script>
</body>
</html>

