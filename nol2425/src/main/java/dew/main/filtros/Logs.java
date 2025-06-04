package dew.main.filtros;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebFilter("/*") // Se aplica a todas las URLs de la aplicación
public class Logs implements Filter {

    private Path logFilePath;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        String path = context.getInitParameter("log-file-path");

        // Usamos la ruta absoluta directamente
        logFilePath = Paths.get(path);

        try {
            // Crear carpeta si no existe
            Files.createDirectories(logFilePath.getParent());
            // Crear archivo si no existe
            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
            }
        } catch (IOException e) {
            throw new ServletException("No se pudo crear el archivo de logs en " + logFilePath, e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        // Fecha y hora actuales
        String now = LocalDateTime.now().format(formatter);
        // Usuario autenticado, o "anonimo" si no hay
        String usuario = (req.getRemoteUser() != null) ? req.getRemoteUser() : "anonimo";
        // IP cliente
        String ip = request.getRemoteAddr();
        // URI solicitada
        String uri = req.getRequestURI();
        // Método HTTP
        String metodo = req.getMethod();

        // Línea a escribir en el log
        String logEntry = String.format("%s %s %s %s %s%n", now, usuario, ip, uri, metodo);

        // Escribir en el fichero de log (en modo append)
        try {
            Files.writeString(logFilePath, logEntry, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Opcional: podrías manejar el error o dejar que falle silenciosamente
            e.printStackTrace();
        }

        // Continuar con la cadena de filtros y servlet
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No se requiere nada especial aquí
    }
}
