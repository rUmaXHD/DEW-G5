<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="dew.main.structures.NotaAsignatura" %>
<%@ page import="dew.main.structures.Alumno" %>
<%
	String asig = (String) request.getAttribute("asig");
	String dni = (String) request.getAttribute("dni");
	String key = (String) request.getAttribute("key");
	String jsessionid = (String) request.getAttribute("jsessionid");

	Alumno alumno = (Alumno) request.getAttribute("alumno");
	NotaAsignatura notaAsignatura = (NotaAsignatura) request.getAttribute("notaAsignatura");
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= dni %> en <%= asig %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="p-4">
    <h2><%= dni %> en <%= asig %></h2>
    <span><%= alumno.getNombre() %> <%= alumno.getApellidos() %></span>
    <span><%= notaAsignatura.getAsignatura() %> <%= notaAsignatura.getNota() %></span>
    
    <h2>Cambiar nota</h2>
    <input type="number" id="nota" min="0" max=10 step="0.01" value="<%= notaAsignatura.getNota() %>" />
    <button onclick="cambiarNota()">Modificar Nota</button>
    <script>
        function cambiarNota() {
            const params = new URLSearchParams();
            params.append('dni', '<%= dni %>');
            params.append('asig', '<%= asig %>');
            params.append('nota', document.getElementById('nota').value);

            fetch("http://localhost:9090/CentroEducativo/alumnos/<%= dni %>/asignaturas/<%= asig %>?key=<%= key %>", {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Cookie': 'JSESSIONID=<%= jsessionid%>'
                },
                body: params.toString()
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Algo ha ido mal');
                }
                return response;
            })
            .then(data => {
            	console.log(data);
                alert(data);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred: ' + error.message);
            });
        }
    </script>
</body>
</html>
