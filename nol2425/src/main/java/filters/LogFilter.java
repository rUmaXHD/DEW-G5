package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.*;

@WebFilter("/*")
public class LogFilter implements Filter {
    private String logFilePath;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Obtener la ruta del archivo de log del web.xml
        logFilePath = filterConfig.getServletContext().getInitParameter("log-file");
        if (logFilePath == null) {
            throw new ServletException("No se ha especificado la ruta del archivo de log en web.xml");
        }
        
        // Reemplazar la variable de sistema para el directorio home del usuario
        logFilePath = logFilePath.replace("${user.home}", System.getProperty("user.home"));
        
        try {
            // Crear el archivo si no existe
            Path logPath = Paths.get(logFilePath);
            if (!Files.exists(logPath)) {
                Files.createFile(logPath);
            }
        } catch (IOException e) {
            throw new ServletException("No se pudo crear el archivo de log en el escritorio", e);
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Obtener los datos necesarios
        String timestamp = LocalDateTime.now().format(formatter);
        String user = httpRequest.getRemoteUser();
        if (user == null) user = "anonymous";
        String ip = request.getRemoteAddr();
        String uri = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String method = httpRequest.getMethod();
        
        // Formato: 2020-06-09T19:38:14.278 prof1 158.11.11.11 acceso GET
        String logEntry = String.format("%s %s %s %s %s%n",
            timestamp,
            user,
            ip,
            uri,
            method
        );
        
        // Escribir en el archivo de log
        try {
            Files.write(Paths.get(logFilePath), logEntry.getBytes(), 
                StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error escribiendo en el log: " + e.getMessage());
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // No es necesario realizar ninguna limpieza
    }
} 