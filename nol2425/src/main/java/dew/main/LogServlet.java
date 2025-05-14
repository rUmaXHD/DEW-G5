package dew.main;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;

@WebServlet("/LogServlet")
public class LogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
	private String logFilePath;

    @Override
    public void init() throws ServletException {
        // Ruta relativa desde webapp
        String relativePath = "/WEB-INF/logs/nol_logs.txt";
        
        // Convertir a ruta absoluta
        logFilePath = getServletContext().getRealPath(relativePath);
        
        // Crear directorio si no existe
        try {
            Path logDir = Paths.get(logFilePath).getParent();
            Files.createDirectories(logDir);
        } catch (IOException e) {
            throw new ServletException("Error creando directorio de logs", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String logEntry = String.format("[%s] Acceso desde %s", 
            java.time.LocalDateTime.now(),
            request.getRemoteAddr());

        // Escribir en archivo (sincronizado para hilos)
        synchronized (this) {
            try (PrintWriter writer = new PrintWriter(
                 new FileWriter(logFilePath, true))) {
                writer.println(logEntry);
            }
        }

        response.getWriter().println("Log registrado en: " + logFilePath);
    }
}