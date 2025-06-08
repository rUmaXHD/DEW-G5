<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String asignaturasJson = (String) request.getAttribute("asignaturasData");
    String nombreProfesor = (String) request.getAttribute("nombreProfesor");
    String dniProfesor = (String) request.getAttribute("dniProfesor");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Inicio - Profesor</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 2rem;
        }

        .asignatura {
            border: 2px solid #007bff;
            background: #f4faff;
            padding: 1rem;
            margin-bottom: 1.5rem;
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

        button {
            margin-top: 0.5rem;
            padding: 0.5rem 1rem;
        }
    </style>
</head>
<body>
    <h1>Profesor: <%= nombreProfesor != null ? nombreProfesor : "Desconocido" %></h1>
    <p><strong>DNI:</strong> <%= dniProfesor != null ? dniProfesor : "N/A" %></p>

    <h2>Asignaturas que imparte</h2>
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

                        if (alumno.foto) {
                            const img = document.createElement("img");
                            img.className = "foto";
                            img.src = alumno.foto;
                            img.alt = "Foto de " + alumno.nombre;
                            card.appendChild(img);
                        }

                        const nombre = `${alumno.nombre} ${alumno.apellidos}`;
                        const dni = alumno.dni;
                        const nota = alumno.nota ?? "";

                        const info = document.createElement("p");
                        info.innerHTML = `<strong>${nombre}</strong> (<em>${dni}</em>)`;
                        card.appendChild(info);

                        const notaInput = document.createElement("input");
                        notaInput.type = "number";
                        notaInput.id = "nota-" + dni;
                        notaInput.value = nota;
                        notaInput.step = "0.1";
                        notaInput.min = "0";
                        notaInput.max = "10";

                        const notaLabel = document.createElement("label");
                        notaLabel.innerHTML = `Nota: `;
                        notaLabel.appendChild(notaInput);
                        card.appendChild(notaLabel);

                        const btn = document.createElement("button");
                        btn.textContent = "Guardar";
                        btn.onclick = () => guardarNota(dni, acronimo);
                        card.appendChild(btn);

                        // Reactivar botón si cambia la nota
                        notaInput.addEventListener("input", () => {
                            btn.disabled = false;
                            btn.textContent = "Guardar";
                        });

                        target.appendChild(card);
                    });
                })
                .catch(err => {
                    console.error("❌ Error al cargar alumnos:", err);
                    target.innerHTML = "<p>Error al cargar alumnos.</p>";
                });
        }

        function guardarNota(dni, acronimo) {
            const input = document.getElementById("nota-" + dni);
            const nota = input.value;
            const btn = input.closest(".alumno-card").querySelector("button");

            btn.disabled = true;
            btn.textContent = "Guardando…";

            fetch("ajax/modificarNota?dniAlumno=" + encodeURIComponent(dni) + "&asignatura=" + encodeURIComponent(acronimo), {
                method: "PUT",
                headers: { "Content-Type": "text/plain" },
                body: nota
            })
            .then(resp => {
                if (resp.ok) {
                    btn.textContent = "Guardado";
                    setTimeout(() => {
                        btn.disabled = false;
                        btn.textContent = "Guardar";
                    }, 1000);
                } else {
                    throw new Error("Error al actualizar la nota");
                }
            })
            .catch(err => {
                console.error("❌ Error en PUT:", err);
                alert("❌ Error al guardar nota");
                btn.disabled = false;
                btn.textContent = "Guardar";
            });
        }
    </script>
</body>
</html>

