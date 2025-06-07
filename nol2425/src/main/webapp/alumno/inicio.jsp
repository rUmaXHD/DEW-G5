<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
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
    <style>
        body { font-family: Arial, sans-serif; margin: 2rem; }
        .asignatura { border: 1px solid #ccc; border-radius: 8px; padding: 1rem; margin-bottom: 1rem; background-color: #f9f9f9; }
        .asignatura h3 { margin-top: 0; }
        ul { padding-left: 1.5rem; }
    </style>
</head>		
<body>
    <h1>Bienvenido, <%= nombreAlumno != null ? nombreAlumno : "Alumno" %></h1>
    <p><strong>DNI:</strong> <%= dniAlumno != null ? dniAlumno : "Desconocido" %></p>

    <h2>Asignaturas</h2>
    <div id="asignaturas"></div>

    <script>
        const asignaturas = <%= asignaturasJson != null ? asignaturasJson : "[]" %>;

        const contenedor = document.getElementById("asignaturas");

        if (asignaturas.length === 0) {
            contenedor.innerHTML = "<p>No estás inscrito en ninguna asignatura.</p>";
        } else {
            asignaturas.forEach(asig => {
                const div = document.createElement("div");
                div.className = "asignatura";

                const miembros = asig.miembros && asig.miembros.length > 0
                    ? `<ul>${asig.miembros.map(m => `<li>${m}</li>`).join("")}</ul>`
                    : "<p><em>Sin compañeros asignados.</em></p>";

                div.innerHTML = `
                    <h3>${asig.nombre} (${asig.codigo})</h3>
                    <p><strong>Curso:</strong> ${asig.curso}</p>
                    <p><strong>Cuatrimestre:</strong> ${asig.cuatrimestre}</p>
                    <p><strong>Créditos:</strong> ${asig.creditos}</p>
                    <p><strong>Grupo:</strong> ${asig.grupoNombre ?? 'N/D'}</p>
                    <p><strong>Miembros:</strong></p>
                    ${miembros}
                `;
                contenedor.appendChild(div);
            });
        }
    </script>
</body>
</html>
