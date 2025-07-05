package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //  Invalida la sesión del servidor
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        //  Forzar al navegador a olvidar las credenciales BASIC
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setHeader("WWW-Authenticate", "Basic realm=\"NotasOnline\"");
        response.setContentType("text/html;charset=UTF-8");

        //  Mostrar mensaje y opción de volver al inicio
        response.getWriter().write("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Sesión cerrada</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
            </head>
            <body class="bg-light d-flex flex-column justify-content-center align-items-center vh-100">
                <div class="text-center">
                    <h2 class="mb-4"> Has cerrado sesión</h2>
                    <p class="mb-4">Las credenciales han sido olvidadas.</p>
                    <a href="index.jsp" class="btn btn-primary">Volver al inicio</a>
                </div>
            </body>
            </html>
        """);
    }
}


