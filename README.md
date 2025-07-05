# DEW-G5
## Flujo de Logout y Reautenticación – Sistema NOL2425

Este sistema utiliza **autenticación HTTP BASIC**, gestionada por el navegador. A diferencia de un formulario tradicional, esto implica ciertas limitaciones técnicas, pero se ha aplicado un flujo controlado para facilitar el cierre de sesión y evitar reautenticaciones no deseadas.

---

### ¿Cómo funciona el cierre de sesión?

1. El usuario pulsa el botón **“Cerrar sesión”** en cualquier página protegida.
2. Antes de enviar la solicitud de logout, se ejecuta un `alert()` en el navegador con el siguiente mensaje: En el siguiente recuadro pulsa CANCELAR para salir completamente del sistema.
3. Tras aceptar el mensaje, el navegador accede a `LogoutServlet`, que:
- Invalida la sesión (`session.invalidate()`)
- Devuelve una respuesta `401 Unauthorized` con la cabecera `WWW-Authenticate`, lo que fuerza al navegador a abrir de nuevo el cuadro de login.
4. El usuario debe **pulsar “Cancelar” en ese recuadro**, lo que impide volver a autenticarse automáticamente y rompe la sesión de forma efectiva.
5. El flujo termina mostrando una página de confirmación implementada directamente desde el LogoutServelet 
6.Al clickar en volver al inicio se vuelve a la pagina de inicio (`index.jsp`) donde vuleve a empezar el bucle.

---

###  Cosas importantes a tener en cuenta

- **El cuadro de autenticación BASIC no se puede personalizar** ni interceptar directamente. Es una función nativa del navegador.
- **Cancelar el login o pulsar ESC sin intención puede generar un error `401 Unauthorized` en pantalla**, ya que el recurso queda inaccesible.
- **Una vez mostrado el cuadro**, si el usuario pulsa "Aceptar" con las credenciales válidas, volverá a iniciar sesión automáticamente.
- **Este comportamiento es estándar del protocolo HTTP**, no depende de nuestra aplicación ni de Java/Tomcat.
- **Debido a limitaciones de tiempo, se opta por mantener el flujo actual de cierre de sesión, ya que resulta funcional en términos generales, aunque presenta ciertos matices a nivel de experiencia de usuario. Esta decisión permite centrar los esfuerzos en completar otras partes del proyecto.** 

---

###  Alternativa a futuro

Para tener control total sobre el flujo de autenticación (formularios, mensajes, estilos, errores), es recomendable en versiones futuras:
- Migrar a **autenticación por formulario (FORM)** en lugar de BASIC.
- Por falta de tiempo se decide prescindir de esta migración.

---

