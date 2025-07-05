<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
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

    <!-- Cabecera -->
    <div class="bg-primary text-white py-5 mb-5">
        <div class="container text-center">
            <h1 class="display-5">Gestión de notas</h1>
            <p class="lead mb-0">Asignatura: <strong><%= asignatura %></strong></p>
        </div>
    </div>

    <div class="container">

        <!-- Contenedor de alumno -->
        <div id="contenedorAlumno" class="card shadow-sm p-4 mb-4 d-none">
            <h4 class="mb-3 text-primary">Datos del alumno</h4>
            <p class="text-muted" id="dniAlumno"></p>

            <div class="mb-3">
                <label for="notaInput" class="form-label">Nota:</label>
                <input type="number" class="form-control" id="notaInput" step="0.1" min="0" max="10">
            </div>

            <div class="d-flex justify-content-between">
                <button id="btnAnterior" class="btn btn-outline-primary">&larr; Anterior</button>
                <button id="btnSiguiente" class="btn btn-outline-primary">Siguiente &rarr;</button>
            </div>
        </div>

        <div id="mensajeCargando" class="alert alert-info">Cargando alumnos...</div>
        <div id="mensajeError" class="alert alert-danger d-none">Error al cargar alumnos.</div>

        <!-- Volver -->
        <div class="text-start mt-4">
            <a href="inicio.jsp" class="btn btn-secondary">&larr; Volver</a>
        </div>

        <!-- Footer -->
        <footer class="text-center mt-5 pt-4 border-top">
            <p class="text-muted small">Notas Online · Gestión de asignaturas · Curso 24/25</p>
        </footer>
   		</div>

    <script>
	    const asignatura = "<%= (asignatura != null) ? asignatura.replace("\"", "\\\"") : "" %>";
	    const contextPath = "<%= request.getContextPath() %>";
	    let alumnos = [];
	    let indice = 0;
	
	    console.log("🚀 Script cargado");
	    console.log("📘 Asignatura:", asignatura);
	    console.log("📘 ContextPath:", contextPath);
	
	    async function cargarAlumnos() {
	        try {
	            console.log("🔄 Ejecutando cargarAlumnos...");
	            const url = contextPath + "/profesor/listaAlumnos?asignatura=" + asignatura + "&ajax=true";
	            console.log("📘 URL construida:", url);
	
	            const res = await fetch(url);
	            console.log("📥 Estado fetch:", res.status, res.statusText);
	            if (!res.ok) throw new Error(`Estado ${res.status} ${res.statusText}`);
	
	            alumnos = await res.json();
	            console.log("📦 JSON recibido:", alumnos);
	
	            if (!Array.isArray(alumnos) || alumnos.length === 0) {
	                throw new Error("⚠️ La lista de alumnos está vacía o no es un array");
	            }
	
	            console.log("✅ Alumnos cargados correctamente. Total:", alumnos.length);
	            document.getElementById("mensajeCargando").classList.add("d-none");
	            document.getElementById("contenedorAlumno").classList.remove("d-none");
	            mostrarAlumno(indice);
	
	        } catch (err) {
	            console.error("❌ Error al cargar alumnos:", err);
	            document.getElementById("mensajeCargando").classList.add("d-none");
	            document.getElementById("mensajeError").classList.remove("d-none");
	        }
	    }
	
	    function mostrarAlumno(i) {
	        console.log("👤 Ejecutando mostrarAlumno con índice:", i);
	        const alumno = alumnos[i];
	        console.log("👤 Alumno seleccionado:", alumno);
	
	        if (!alumno || !alumno.alumno) {
	            console.warn("⚠️ Datos incompletos del alumno");
	            document.getElementById("dniAlumno").textContent = "DNI: ---";
	            document.getElementById("notaInput").value = "";
	            return;
	        }
	
	        document.getElementById("dniAlumno").textContent = 'DNI: ' + alumno.alumno;
	
	        const notaValida = alumno.nota !== "" && !isNaN(alumno.nota) ? alumno.nota : "";
	        console.log("📝 Nota a mostrar:", notaValida);
	        document.getElementById("notaInput").value = notaValida;
	
	        document.getElementById("btnAnterior").disabled = i === 0;
	        document.getElementById("btnSiguiente").disabled = i === alumnos.length - 1;
	    }
	
	    document.getElementById("btnAnterior").addEventListener("click", () => {
	        console.log("⬅️ Clic en anterior");
	        if (indice > 0) {
	            indice--;
	            mostrarAlumno(indice);
	        }
	    });
	
	    document.getElementById("btnSiguiente").addEventListener("click", () => {
	        console.log("➡️ Clic en siguiente");
	        if (indice < alumnos.length - 1) {
	            indice++;
	            mostrarAlumno(indice);
	        }
	    });
	
	    document.getElementById("notaInput").addEventListener("change", async () => {
	        const nuevaNota = document.getElementById("notaInput").value;
	        const alumno = alumnos[indice];
	        console.log("✏️ Nota modificada:", nuevaNota, "para alumno:", alumno.alumno);
	
	        try {
	        	const urlModificarNota = contextPath + "/profesor/modificarNota"
	            const res = await fetch(urlModificarNota, {
	                method: "POST",
	                headers: { "Content-Type": "application/json" },
	                body: JSON.stringify({
	                    dni: alumno.alumno,
	                    asignatura: asignatura,
	                    nota: nuevaNota === "" ? null : parseFloat(nuevaNota)
	                })
	            });
	
	            console.log("📤 Respuesta modificarNota:", res.status);
	            if (!res.ok) throw new Error("Error al actualizar la nota");
	
	            console.log(`✅ Nota actualizada correctamente para ${alumno.alumno}: ${nuevaNota}`);
	            alumno.nota = nuevaNota;
	
	        } catch (err) {
	            console.error("❌ No se pudo guardar la nota:", err);
	            alert("Error al guardar la nota");
	        }
	    });
	
	    window.onload = () => {
	        console.log("📲 Ejecutando window.onload...");
	        cargarAlumnos();
	    };
	</script>

</body>
</html>
