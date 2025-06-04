
package dew.main;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/LogServlet")
public class LogServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
	// Ruta FIJa del archivo de logs (ajústala según tu sistema)
    //private static final String LOG_FILE_PATH = "/nol2425/src/main/webapp/WEB-INF/logs/nol_logs.txt";
    private static final String LOG_FILE_PATH = "/nol2425/src/main/webapp/WEB-INF/logs/nol_logs.txt";
    private static final DateTimeFormatter DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void init() throws ServletException {
        // Crear directorio si no existe
        new File(LOG_FILE_PATH).getParentFile().mkdirs();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Generar el mensaje de log
        String logMessage = generateLogMessage(request);
        
        // 2. Escribir en archivo (persistencia)
        writeToLogFile(logMessage);
        
        // 3. Mostrar en el navegador
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(logMessage + "\n(Registro guardado en: " + LOG_FILE_PATH + ")");
    }

    private String generateLogMessage(HttpServletRequest request) {
        return String.format("[%s] %s %s %s %s Params: %s",
            LocalDateTime.now().format(DATE_FORMAT),
            request.getRemoteUser() != null ? request.getRemoteUser() : "anonimo",
            request.getRemoteAddr(),
            request.getMethod(),
            request.getRequestURI(),
            request.getQueryString() != null ? request.getQueryString() : "null"
        );
    }

    private synchronized void writeToLogFile(String message) {
        try (PrintWriter writer = new PrintWriter(
             new FileWriter(LOG_FILE_PATH, true))) { // 'true' para append
            
            writer.println(message);
        } catch (IOException e) {
            System.err.println("Error escribiendo en log: " + e.getMessage());
        }
    }
}