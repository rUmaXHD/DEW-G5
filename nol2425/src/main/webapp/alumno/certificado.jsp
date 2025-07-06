<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Atributos enviados desde CertificadoServlet
    String certificadoJson = (String) request.getAttribute("certificadoJson");
    String nombreAlumno = (String) request.getAttribute("nombreAlumno");
    String dniAlumno = (String) request.getAttribute("dni");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Certificado de calificaciones</title>
    <!-- Bootstrap 5 para estilos -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        /* Oculta elementos con clase no-print al imprimir */
        @media print {
            .no-print {
                display: none;
            }
        }
    </style>
</head>
<body class="bg-light">

    <!-- Cabecera del certificado -->
    <div class="bg-primary text-white py-5 mb-4">
        <div class="container text-center">
            <h1 class="display-6">Certificado Académico</h1>
            <p class="mb-1">Alumno: <strong><%= nombreAlumno != null ? nombreAlumno : "Alumno" %></strong></p>
            <p class="mb-0">DNI: <strong><%= dniAlumno != null ? dniAlumno : "Desconocido" %></strong></p>
        </div>
    </div>

    <!-- Tabla de asignaturas -->
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

        <!-- Botones de acción -->
        <div class="text-center no-print mt-4">
            <button class="btn btn-primary me-2" onclick="window.print()">Imprimir certificado</button>
            <a href="<%= request.getContextPath() %>/AccesoServlet" class="btn btn-outline-secondary">Volver</a>
        </div>

        <!-- Pie de página -->
        <footer class="text-center text-muted small mt-5 pt-4 border-top">
            <p>Notas Online · Certificado académico · Curso 24/25</p>
        </footer>
    </div>

    <!-- JSON embebido en la página -->
    <script id="certificado-data" type="application/json">
<%= certificadoJson %>
    </script>

    <!-- Script que rellena la tabla con datos del certificado -->
    <script>
        let datos = [];
        try {
            const raw = document.getElementById("certificado-data").textContent;
            datos = JSON.parse(raw);
        } catch (e) {
            console.error("Error al parsear el JSON:", e);
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
            // Fila por defecto si no hay asignaturas
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



