<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String asignaturasJson = (String) request.getAttribute("asignaturasData");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Asignaturas - Profesor</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 2rem;
        }
        .asignatura {
            border: 2px solid #007bff;
            background: #f8f9ff;
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
        }
    </style>
</head>
<body>
    <h1>Asignaturas que impartes</h1>

    <div id="asignaturas"></div>

    <!-- ✅ JSON embebido -->
    <script id="json-data" type="application/json">
<%= asignaturasJson %>
    </script>

    <script>
        const contenedor = document.getElementById("asignaturas");
        const json = document.getElementById("json-data").textContent;
        let asignaturas = [];

        try {
            asignaturas = JSON.parse(json);
        } catch (e) {
            console.error("Error al parsear asignaturas:", e);
        }

        if (!asignaturas || asignaturas.length === 0) {
            contenedor.innerHTML = "<p>No tienes asignaturas asignadas.</p>";
        } else {
            asignaturas.forEach(asig => {
                const div = document.createElement("div");
                div.className = "asignatura";
                div.innerHTML = `<h3>${asig.nombre} (${asig.acronimo})</h3>
                    <p><strong>Curso:</strong> ${asig.curso} | <strong>Cuatrimestre:</strong> ${asig.cuatrimestre} | <strong>Créditos:</strong> ${asig.creditos}</p>
                    <div class="alumnos" id="alumnos-${asig.acronimo}">Haz clic para ver alumnos</div>`;
                div.addEventListener("click", () => cargarAlumnos(asig.acronimo));
                contenedor.appendChild(div);
            });
        }

        function cargarAlumnos(acronimo) {
            const target = document.getElementById("alumnos-" + acronimo);
            target.innerHTML = "Cargando alumnos...";

            fetch(`profesor/ajax/alumnos?asignatura=${encodeURIComponent(acronimo)}`)
                .then(r => r.json())
                .then(data => {
                    if (!data.length) {
                        target.innerHTML = "<p>No hay alumnos inscritos.</p>";
                        return;
                    }

                    target.innerHTML = "";
                    data.forEach(alumno => {
                    	const card = document.createElement("div");
                    	card.className = "alumno-card";

                    	if (alumno.foto) {
                    	    const img = document.createElement("img");
                    	    img.className = "foto";
                    	    img.src = alumno.foto;
                    	    img.alt = "Foto";
                    	    card.appendChild(img);
                    	}

                    	const nombre = `${alumno.nombre} ${alumno.apellidos}`;
                    	const dni = alumno.dni;
                    	const nota = alumno.nota || "";

                    	const info = document.createElement("p");
                    	info.innerHTML = `<strong>${nombre}</strong> (<em>${dni}</em>)`;
                    	card.appendChild(info);

                    	const label = document.createElement("label");
                    	label.innerHTML = `Nota: <input type="number" min="0" max="10" step="0.1" id="nota-${dni}" value="${nota}">`;
                    	card.appendChild(label);

                    	const btn = document.createElement("button");
                    	btn.textContent = "Guardar";
                    	btn.onclick = () => guardarNota(dni, acronimo);
                    	card.appendChild(btn);

                    	target.appendChild(card);

                    });
                })
                .catch(err => {
                    console.error(err);
                    target.innerHTML = "<p>Error al cargar alumnos.</p>";
                });
        }

        function guardarNota(dni, asignatura) {
            const nota = document.getElementById("nota-" + dni).value;
            fetch(`profesor/ajax/modificarNota?dniAlumno=${encodeURIComponent(dni)}&asignatura=${encodeURIComponent(asignatura)}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "text/plain"
                },
                body: nota
            }).then(resp => {
                if (resp.ok) {
                    alert("Nota actualizada.");
                } else {
                    alert("Error al guardar nota.");
                }
            }).catch(e => {
                console.error(e);
                alert("Error al enviar petición.");
            });
        }
    </script>
</body>
</html>
