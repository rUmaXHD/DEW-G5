package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String dni = request.getParameter("dni");
	    String password = request.getParameter("password");

	    // Dirección del servicio externo CentroEducativo (ajusta si es necesario)
	    String urlLogin = "http://localhost:9090/login";
	 // Preparar conexión HTTP (POST)
	    URL url = new URL(urlLogin);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setDoOutput(true);
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	    // Enviar parámetros dni y password
	    String params = "dni=" + URLEncoder.encode(dni, "UTF-8")
	                  + "&password=" + URLEncoder.encode(password, "UTF-8");

	    OutputStream os = conn.getOutputStream();
	    os.write(params.getBytes());
	    os.flush();
	    os.close();

	    int status = conn.getResponseCode();

	    if (status == 200) {
	        // Leer respuesta: se espera que contenga la clave de sesión ("key")
	        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String key = reader.readLine();  // Suponemos que solo devuelve la key en texto plano
	        reader.close();

	        // Guardar la key en sesión
	        HttpSession session = request.getSession();
	        session.setAttribute("key", key);
	        session.setAttribute("dni", dni);  // Puedes guardar también el usuario si quieres

	        // Redirigir al área segura
	        response.sendRedirect("menu.jsp");  // o cualquier página protegida
	    } else {
	        // Error de autenticación
	        request.setAttribute("error", "DNI o contraseña incorrectos.");
	        RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
	        dispatcher.forward(request, response);
	    }

	    conn.disconnect();
	}

	}

}
