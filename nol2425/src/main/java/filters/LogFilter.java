package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebFilter("/*")
public class LogFilter implements Filter {
    private String logFilePath;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Obtener la ruta del archivo de log del web.xml
        logFilePath = filterConfig.getServletContext().getInitParameter("log-file");
        if (logFilePath == null) {
            throw new ServletException("No se ha especificado la ruta del archivo de log en web.xml");
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String user = httpRequest.getRemoteUser();
        if (user == null) user = "anonymous";
        
        // Formato: fecha usuario IP servlet método
        String logEntry = String.format("%s %s %s %s %s%n",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            user,
            request.getRemoteAddr(),
            httpRequest.getRequestURI().substring(httpRequest.getContextPath().length()),
            httpRequest.getMethod()
        );
        
        // Escribir en el archivo de log
        try (FileWriter fw = new FileWriter(logFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(logEntry);
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // No es necesario realizar ninguna limpieza
    }
} 