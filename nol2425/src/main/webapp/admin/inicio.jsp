<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Panel de Administración</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script>
        async function agregarAlumno() {
            const form = document.getElementById('formAlumno');
            const formData = {
                dni: form.dni.value,
                nombre: form.nombre.value,
                apellidos: form.apellidos.value,
                password: form.password.value
            };

            try {
                const response = await fetch('AdminServlet?action=addAlumno', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                });

                const result = await response.text();
                alert(result);
                if(response.ok) {
                    form.reset();
                }
            } catch (error) {
                alert("Error: " + error.message);
            }
        }
    </script>
</head>
<body class="container mt-4">
    <h1 class="mb-4">Panel de Administración</h1>
    
    <div class="card mb-4">
        <div class="card-header">
            <h5>Agregar Nuevo Alumno</h5>
        </div>
        <div class="card-body">
            <form id="formAlumno" onsubmit="event.preventDefault(); agregarAlumno();">
                <div class="mb-3">
                    <label class="form-label">DNI</label>
                    <input type="text" class="form-control" name="dni" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Nombre</label>
                    <input type="text" class="form-control" name="nombre" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Apellidos</label>
                    <input type="text" class="form-control" name="apellidos" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Contraseña</label>
                    <input type="password" class="form-control" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary">Agregar Alumno</button>
            </form>
        </div>
    </div>
</body>
</html>


