<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String certificadoJson = (String) request.getAttribute("certificadoJson");
    String nombreAlumno = (String) request.getAttribute("nombreAlumno");
    String dniAlumno = (String) request.getAttribute("dni");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Certificado de calificaciones</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        @media print {
            .no-print {
                display: none;
            }
        }
    </style>
</head>
<body class="bg-light">

    <!-- Cabecera azul coherente -->
    <div class="bg-primary text-white py-5 mb-4">
        <div class="container text-center">
            <h1 class="display-6">Certificado Académico</h1>
            <p class="mb-1">Alumno: <strong><%= nombreAlumno != null ? nombreAlumno : "Alumno" %></strong></p>
            <p class="mb-0">DNI: <strong><%= dniAlumno != null ? dniAlumno : "Desconocido" %></strong></p>
        </div>
    </div>

    <div class="container">
        <h4 class="text-secondary mb-3">Relación de asignaturas y calificaciones</h4>

        <div class="table-responsive">
            <table class="table table-bordered table-striped shadow-sm">
                <thead class="table-secondary">
                    <tr>
                        <th>Asignatura</th>
                        <th>Calificación</th>
                    </tr>
                </thead>
                <tbody id="tablaNotas"></tbody>
            </table>
        </div>

        <div class="text-center no-print mt-4">
            <button class="btn btn-primary me-2" onclick="window.print()">🖨️ Imprimir certificado</button>
            <a href="<%= request.getContextPath() %>/alumno/inicioAlumno.jsp" class="btn btn-outline-secondary">Volver</a>
        </div>

        <footer class="text-center text-muted small mt-5 pt-4 border-top">
            <p>Notas Online · Certificado académico · Curso 24/25</p>
        </footer>
    </div>

    <!-- JSON seguro embebido -->
    <script id="certificado-data" type="application/json">
<%= certificadoJson %>
    </script>

    <!-- Script para rellenar la tabla -->
    <script>
        let datos = [];
        try {
            const raw = document.getElementById("certificado-data").textContent;
            datos = JSON.parse(raw);
        } catch (e) {
            console.error("❌ Error al parsear el JSON:", e);
        }

        const tbody = document.getElementById("tablaNotas");

        if (Array.isArray(datos) && datos.length > 0) {
            datos.forEach(item => {
                const tr = document.createElement("tr");

                const tdAsignatura = document.createElement("td");
                tdAsignatura.textContent = item.asignatura || "—";

                const tdNota = document.createElement("td");
                tdNota.textContent = (item.nota !== undefined && item.nota !== null) ? item.nota : "Sin calificar";

                tr.appendChild(tdAsignatura);
                tr.appendChild(tdNota);
                tbody.appendChild(tr);
            });
        } else {
            const fila = document.createElement("tr");
            const td = document.createElement("td");
            td.colSpan = 2;
            td.className = "text-center text-muted";
            td.textContent = "No hay asignaturas disponibles.";
            fila.appendChild(td);
            tbody.appendChild(fila);
        }
    </script>

</body>
</html>


