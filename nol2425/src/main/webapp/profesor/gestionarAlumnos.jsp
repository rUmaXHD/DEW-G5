<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
    // Recuperar la asignatura seleccionada de la sesión
    String asignatura = (String) session.getAttribute("asignaturaSeleccionada");
    if (asignatura == null || asignatura.isBlank()) {
        response.sendRedirect("inicio.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de notas – <%= asignatura %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Cabecera principal -->
    <div class="bg-primary text-white py-5 mb-5">
        <div class="container text-center">
            <h1 class="display-5">Gestión de notas</h1>
            <p class="lead mb-0">Asignatura: <strong><%= asignatura %></strong></p>
        </div>
    </div>

    <div class="container">
        <!-- Sección de alumno individual -->
        <div id="contenedorAlumno" class="card shadow-sm p-4 mb-4 d-none">
            <h4 class="mb-3 text-primary">Datos del alumno</h4>
            <p class="text-muted" id="dniAlumno"></p>

            <div class="mb-3">
                <label for="notaInput" class="form-label">Nota:</label>
                <input type="number" class="form-control" id="notaInput" step="0.1" min="0" max="10">
            </div>

            <!-- Navegación entre alumnos -->
            <div class="d-flex justify-content-between">
                <button id="btnAnterior" class="btn btn-outline-primary">&larr; Anterior</button>
                <button id="btnSiguiente" class="btn btn-outline-primary">Siguiente &rarr;</button>
            </div>
        </div>

        <!-- Estado de carga / error -->
        <div id="mensajeCargando" class="alert alert-info">Cargando alumnos...</div>
        <div id="mensajeError" class="alert alert-danger d-none">Error al cargar alumnos.</div>

        <!-- Nota media -->
        <div class="alert alert-secondary text-center mt-4" id="mediaNota">
            Nota media: <span id="valorMedia">–</span>
        </div>

        <!-- Volver -->
        <div class="text-start mt-4">
            <a href="<%= request.getContextPath() %>/AccesoServlet" class="btn btn-outline-primary">&larr; Volver a asignaturas</a>
        </div>

        <!-- Footer -->
        <footer class="text-center mt-5 pt-4 border-top">
            <p class="text-muted small">Notas Online · Gestión de asignaturas · Curso 24/25</p>
        </footer>
    </div>

    <!-- Script de lógica AJAX y navegación -->
    <script>
        const asignatura = "<%= (asignatura != null) ? asignatura.replace("\"", "\\\"") : "" %>";
        const contextPath = "<%= request.getContextPath() %>";
        let alumnos = [];
        let indice = 0;

        // Petición inicial para cargar alumnos vía AJAX
        async function cargarAlumnos() {
            try {
                const url = contextPath + "/profesor/listaAlumnos?asignatura=" + asignatura + "&ajax=true";
                const res = await fetch(url);
                if (!res.ok) throw new Error(`Estado ${res.status}`);

                alumnos = await res.json();
                if (!Array.isArray(alumnos) || alumnos.length === 0) {
                    throw new Error("La lista de alumnos está vacía o mal formada");
                }

                document.getElementById("mensajeCargando").classList.add("d-none");
                document.getElementById("contenedorAlumno").classList.remove("d-none");
                mostrarAlumno(indice);
                calcularYMostrarMedia();

            } catch (err) {
                console.error("Error al cargar alumnos:", err);
                document.getElementById("mensajeCargando").classList.add("d-none");
                document.getElementById("mensajeError").classList.remove("d-none");
            }
        }

        // Mostrar alumno actual
        function mostrarAlumno(i) {
            const alumno = alumnos[i];
            if (!alumno || !alumno.alumno) return;

            document.getElementById("dniAlumno").textContent = 'DNI: ' + alumno.alumno;
            const notaValida = alumno.nota !== "" && !isNaN(alumno.nota) ? alumno.nota : "";
            document.getElementById("notaInput").value = notaValida;

            document.getElementById("btnAnterior").disabled = i === 0;
            document.getElementById("btnSiguiente").disabled = i === alumnos.length - 1;
        }

        // Calcular nota media de los alumnos
        function calcularYMostrarMedia() {
            let suma = 0;
            let contador = 0;

            alumnos.forEach(a => {
                if (a.nota !== "" && !isNaN(a.nota)) {
                    suma += parseFloat(a.nota);
                    contador++;
                }
            });

            const media = (contador > 0) ? (suma / contador).toFixed(2) : "–";
            document.getElementById("valorMedia").textContent = media;
        }

        // Navegación con botones
        document.getElementById("btnAnterior").addEventListener("click", () => {
            if (indice > 0) {
                indice--;
                mostrarAlumno(indice);
            }
        });

        document.getElementById("btnSiguiente").addEventListener("click", () => {
            if (indice < alumnos.length - 1) {
                indice++;
                mostrarAlumno(indice);
            }
        });

        // Evento al cambiar nota del alumno
        document.getElementById("notaInput").addEventListener("change", async () => {
            const input = document.getElementById("notaInput");
            const nuevaNota = parseFloat(input.value);
            const alumno = alumnos[indice];

            if (isNaN(nuevaNota) || nuevaNota < 0 || nuevaNota > 10) {
                alert("La nota debe estar entre 0 y 10.");
                input.value = alumno.nota;
                return;
            }

            try {
                const res = await fetch(contextPath + "/profesor/modificarNota", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        dni: alumno.alumno,
                        asignatura: asignatura,
                        nota: nuevaNota
                    })
                });

                if (!res.ok) throw new Error("Error al actualizar la nota");

                alumno.nota = nuevaNota;
                calcularYMostrarMedia();

            } catch (err) {
                console.error("Error al guardar la nota:", err);
                alert("Error al guardar la nota");
            }
        });

        window.onload = cargarAlumnos;
    </script>

</body>
</html>

