package dew.main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet responsable del inicio de sesión.
 * Recibe credenciales vía autenticación BASIC, valida contra la API externa,
 * y redirige al usuario según su rol.
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// URL de la API del backend educativo
	private static final String API_URL = "http://localhost:9090/CentroEducativo/login";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		procesarPostLogin(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		procesarPostLogin(request, response);
	}

	/**
	 * Procesa la autenticación, valida credenciales, obtiene clave de sesión
	 * desde API, y redirige según rol.
	 */
	private void procesarPostLogin(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// Extraer JSESSIONID (aunque no se usa activamente aquí)
		String jsessionId = null;
		jakarta.servlet.http.Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (jakarta.servlet.http.Cookie cookie : cookies) {
				if ("JSESSIONID".equals(cookie.getName())) {
					jsessionId = cookie.getValue();
					break;
				}
			}
		}

		// Obtener credenciales BASIC
		String[] creds = obtenerCredencialesDesdeAuthorization(request);
		if (creds == null) {
			mostrarAlertaError(response, "No se pudieron obtener las credenciales.");
			return;
		}

		String dni = creds[0];
		String password = creds[1];

		if (dni == null || dni.isBlank()) {
			mostrarAlertaError(response, "Sesión no iniciada");
			return;
		}

		HttpSession session = request.getSession();

		try {
			// Intenta obtener clave de sesión desde la API externa
			boolean ok = obtenerSessionKeyDesdeAPI(dni, password, session);
			if (!ok) {
				System.out.println("[ERROR] Credenciales inválidas en la API CentroEducativo");
				mostrarAlertaError(response, "Credenciales inválidas en CentroEducativo");
				session.invalidate();
				return;
			}

			// Redirige según el rol del usuario autenticado
			if (request.isUserInRole("rolalu")) {
				System.out.println("[INFO] Usuario identificado como alumno. Redirigiendo a AsignaturasServlet.");
				response.sendRedirect(request.getContextPath() + "/AsignaturasServlet");
			} else if (request.isUserInRole("rolpro")) {
				System.out.println("[INFO] Usuario identificado como profesor. Redirigiendo a /profesor/inicio.");
				response.sendRedirect(request.getContextPath() + "/profesor/inicio");
			} else {
				System.out.println("[WARN] Rol no reconocido. Finalizando sesión.");
				mostrarAlertaError(response, "Rol no reconocido");
				session.invalidate();
			}

		} catch (Exception e) {
			e.printStackTrace();
			mostrarAlertaError(response, "Error al conectar con CentroEducativo");
		}
	}

	/**
	 * Extrae y decodifica las credenciales BASIC del encabezado HTTP.
	 */
	private String[] obtenerCredencialesDesdeAuthorization(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Basic ")) {
			String base64Credentials = authHeader.substring("Basic ".length());
			byte[] credDecoded = java.util.Base64.getDecoder().decode(base64Credentials);
			String credentials = new String(credDecoded);
			return credentials.split(":", 2); // [dni, password]
		}
		return null;
	}

	/**
	 * Envía credenciales a la API CentroEducativo y guarda clave y JSESSIONID en sesión.
	 */
	private boolean obtenerSessionKeyDesdeAPI(String dni, String password, HttpSession session) throws Exception {
		String json = String.format("{\"dni\":\"%s\", \"password\":\"%s\"}", dni, password);

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(API_URL))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println("[INFO] Respuesta de login API: " + response.body());

		if (response.statusCode() == 200) {
			String key = response.body();
			String setCookie = response.headers().firstValue("Set-Cookie").orElse("");

			String jsessionid = null;
			for (String cookie : setCookie.split(";")) {
				String[] parts = cookie.trim().split("=");
				if (parts.length == 2 && "JSESSIONID".equals(parts[0])) {
					jsessionid = parts[1];
					break;
				}
			}
			if (jsessionid == null)
				return false;

			// Guardar atributos en sesión
			session.setAttribute("dni", dni);
			session.setAttribute("password", password);
			session.setAttribute("key", key);
			session.setAttribute("jsessionId", jsessionid);

			System.out.println("[INFO] Sesión creada con éxito para " + dni);
			return true;
		} else {
			System.out.println("[ERROR] Código de estado recibido: " + response.statusCode());
			return false;
		}
	}

	/**
	 * Muestra una alerta en el navegador con mensaje de error.
	 */
	private void mostrarAlertaError(HttpServletResponse response, String mensaje) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write("<html><head><script type='text/javascript'>" +
				"alert('" + mensaje.replace("'", "\\'") + "');" +
				"history.back();" +
				"</script></head><body></body></html>");
	}
}
