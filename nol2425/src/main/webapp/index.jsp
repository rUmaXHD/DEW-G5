<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Notas Online</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Cabecera -->
    <div class="bg-primary text-white py-5 mb-5">
        <div class="container text-center">
            <h1 class="display-4">Notas Online</h1>
            <p class="lead mb-0">
                Una aplicación que cuesta más de lo que parece para conseguir menos de lo que creías... <br>
                <strong>¿¡Qué más se puede pedir!?</strong>
            </p>
        </div>
    </div>

    <div class="container">
        <div class="row mb-5">
            <!-- Accesos -->
            <div class="col-md-6 mb-4">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h2 class="h4 text-success">Si eres alumn@...</h2>
                        <p>Podrás <a href="AccesoServlet" class="text-decoration-underline">consultar</a> tus calificaciones. <br>
                        Debes contar con tus datos identificativos para acceder.</p>
                    </div>
                </div>
            </div>

            <div class="col-md-6 mb-4">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h2 class="h4 text-warning">Si eres profesor@...</h2>
                        <p>Podrás <a href="AccesoServlet" class="text-decoration-underline">consultar o modificar</a> las calificaciones en tus asignaturas. <br>
                        Debes contar con tus datos identificativos para acceder.</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Grupo -->
        <div class="row">
            <div class="col-md-12">
                <div class="card bg-white border-start border-4 border-primary shadow-sm">
                    <div class="card-body">
                        <h3 class="h5 text-primary">Grupo G5 - Laboratorio Miércoles</h3>
                        <ol class="mb-0">
                            <li>Miguel Ángel Ortiz Requena</li>
                            <li>Bogdan Nicolae Ionascu</li>
                            <li>Saúl Rabadán Sandoval</li>
                            <li>Andrés Durá Hernández</li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <footer class="text-center mt-5 pt-4 border-top">
            <p class="text-muted small">Trabajo en grupo realizado para la asignatura Desarrollo Web. Curso 24/25</p>
        </footer>
    </div>
	
	<script>
    	// Si vienes de un logout, forzar reautenticación fallida para olvidar credenciales BASIC
	    if (document.referrer.includes("LogoutServlet")) {
	        fetch("LogoutServlet", {
	            method: "GET",
	            headers: {
	                "Authorization": "Basic invalid==", // fuerza logout BASIC
	            }
	        }).then(() => {
	            console.log("Credenciales BASIC forzadas a invalidar.");
	        });
	    }
	</script>
	
</body>
</html>

