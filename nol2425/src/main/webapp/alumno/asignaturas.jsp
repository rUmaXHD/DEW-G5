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
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 2rem;
        }
        .asignatura {
            border: 2px solid red;
            background: #fffff0;
            padding: 1rem;
            margin-bottom: 1rem;
        }
        ul {
            padding-left: 1.5rem;
        }
    </style>
</head>
<body>
    <h1>Bienvenido, <%= nombreAlumno != null ? nombreAlumno : "Alumno" %></h1>
    <p><strong>DNI:</strong> <%= dniAlumno != null ? dniAlumno : "Desconocido" %></p>

    <h2>Asignaturas</h2>
    <div id="asignaturas"></div>

    <!-- ✅ JSON seguro embebido como texto -->
    <script id="json-data" type="application/json">
<%= asignaturasJson %>
    </script>

    <script>
        let asignaturas = [];

       try {
        	console.log("📦 JSON original crudo:");
        	console.log(document.getElementById("json-data").textContent);
        	const raw = document.getElementById("json-data").textContent;
            asignaturas = JSON.parse(raw);
            console.log("✅ JSON parseado correctamente:", asignaturas);
        } catch (e) {
            console.error("❌ Error al parsear el JSON:", e);
        }

        const contenedor = document.getElementById("asignaturas");
        contenedor.innerHTML = "";

        if (!asignaturas || asignaturas.length === 0) {
            contenedor.innerHTML = "<p>No estás inscrito en ninguna asignatura.</p>";
        } else {
        	asignaturas.forEach(asig => {
        		console.log("🔍 Nombre:", asig.nombre, " | Código:", asig.codigo, " | typeof:", typeof asig.codigo);
        	    const div = document.createElement("div");
        	    div.className = "asignatura";

        	    // Título con nombre y código
        	    const h3 = document.createElement("h3");
        	    const txtNombre = document.createTextNode(asig.nombre || "(Sin nombre)");
        	    const txtCodigo = document.createTextNode(" (" + asig.codigo + ")" || " (" + "?" + ")");
        	    h3.appendChild(txtNombre);
        	    h3.appendChild(txtCodigo);
        	    div.appendChild(h3);

        	    // Función auxiliar para crear <p><strong>Etiqueta:</strong> valor</p>
        	    function creaParrafo(etiqueta, valor) {
        	        const p = document.createElement("p");
        	        const strong = document.createElement("strong");
        	        strong.textContent = etiqueta;
        	        p.appendChild(strong);
        	        p.appendChild(document.createTextNode(" " + valor));
        	        return p;
        	    }

        	    // Curso, Cuatrimestre, Créditos, Grupo
        	    div.appendChild(creaParrafo("Curso:", asig.curso ?? "?"));
        	    div.appendChild(creaParrafo("Cuatrimestre:", asig.cuatrimestre ?? "?"));
        	    div.appendChild(creaParrafo("Créditos:", asig.creditos ?? "?"));
        	    div.appendChild(creaParrafo("Grupo:", asig.grupoNombre ?? "Sin grupo asignado"));

        	    // Miembros
        	    const pMiembros = document.createElement("p");
        	    const strongMiembros = document.createElement("strong");
        	    strongMiembros.textContent = "Miembros:";
        	    pMiembros.appendChild(strongMiembros);
        	    div.appendChild(pMiembros);

        	    const miembrosContainer = document.createElement("div");
        	    if (asig.miembros && asig.miembros.length > 0) {
        	        const ul = document.createElement("ul");
        	        asig.miembros.forEach(m => {
        	            const li = document.createElement("li");
        	            li.textContent = m;
        	            ul.appendChild(li);
        	        });
        	        miembrosContainer.appendChild(ul);
        	    } else {
        	        const p = document.createElement("p");
        	        p.style.fontStyle = "italic";
        	        p.textContent = "Sin compañeros asignados.";
        	        miembrosContainer.appendChild(p);
        	    }

        	    div.appendChild(miembrosContainer);
        	    contenedor.appendChild(div);
        	});

        }
    </script>
</body>
</html>


