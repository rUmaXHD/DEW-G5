# DEW-G5
##  Problema con los enlaces de autenticación en la página de inicio

Ambos enlaces en la página principal (`inicio.jsp`) conducen al **mismo sistema de autenticación BASIC** configurado en Tomcat. Esto implica que:

- Si un **alumno** hace clic en el link referente a _"Acceder como profesor"_ e introduce sus credenciales de alumno, podrá autenticarse correctamente y acceder a la ventana de profesor, aunque **no pertenezca a ese rol**.
- De la misma forma, un **profesor** puede hacer click en el enlace del alumno e igualmente acceder a su interfaz.

 **Esto demuestra que la distinción entre los enlaces de acceso por rol no es efectiva pero si existe la distinción de roles simplemente he decidido no dedicarle mas tiempo para abarcar mas tamaño de problema si hay tiempo se solucionará pero debido a que funciona se decide prescindir temporalmente.**

---

##  Funcionalidad de grupos y miembros no implementada

Durante el desarrollo de la aplicación **Notas Online**, se decidió no implementar la parte relativa a la **gestión de grupos y visualización de compañeros** dentro de cada asignatura.

### Razón principal

Esta decisión se debe principalmente a **limitaciones de tiempo** y a una elección consciente de **priorizar funcionalidades clave** que aportan mayor valor a la experiencia del usuario y al funcionamiento general del sistema.

### Impacto

Esta funcionalidad fue descartada sin afectar al objetivo principal de la plataforma: permitir a los alumnos consultar sus calificaciones y a los profesores gestionarlas. El sistema sigue funcionando correctamente y cumple los requisitos mínimos de la práctica.

---

## Decisión sobre la funcionalidad de fotografías y datos extendidos del alumnado

Durante el desarrollo del sistema **Notas Online**, se ha tomado la decisión de **no implementar por el momento la funcionalidad relacionada con la gestión y visualización de fotografías del alumnado**, así como otros datos extendidos como nombre, apellidos o expediente completo en la vista del profesorado.

Esta decisión se debe principalmente a **limitaciones de tiempo**, ya que se ha priorizado la implementación de las funcionalidades críticas del proyecto, como la navegación AJAX, la edición de calificaciones y la integración con el backend de datos.

No obstante, se deja constancia de que esta característica se **podrá incorporar más adelante** si el calendario del desarrollo lo permite. En tal caso, se contemplará la carga dinámica de imágenes por DNI, tal y como sugiere el enunciado, así como la visualización de información ampliada para cada alumno o alumna.

---

## Flujo de Logout y Reautenticación – Sistema NOL2425

Este sistema utiliza **autenticación HTTP BASIC**, gestionada por el navegador. A diferencia de un formulario tradicional, esto implica ciertas limitaciones técnicas, pero se ha aplicado un flujo controlado para facilitar el cierre de sesión y evitar reautenticaciones no deseadas.



#### ¿Cómo funciona el cierre de sesión?

1. El usuario pulsa el botón **“Cerrar sesión”** en cualquier página protegida.
2. Antes de enviar la solicitud de logout, se ejecuta un `alert()` en el navegador con el siguiente mensaje: En el siguiente recuadro pulsa CANCELAR para salir completamente del sistema.
3. Tras aceptar el mensaje, el navegador accede a `LogoutServlet`, que:
- Invalida la sesión (`session.invalidate()`)
- Devuelve una respuesta `401 Unauthorized` con la cabecera `WWW-Authenticate`, lo que fuerza al navegador a abrir de nuevo el cuadro de login.
4. El usuario debe **pulsar “Cancelar” en ese recuadro**, lo que impide volver a autenticarse automáticamente y rompe la sesión de forma efectiva.
5. El flujo termina mostrando una página de confirmación implementada directamente desde el LogoutServelet 
6.Al clickar en volver al inicio se vuelve a la pagina de inicio (`index.jsp`) donde vuleve a empezar el bucle.



#### Cosas importantes a tener en cuenta

- **El cuadro de autenticación BASIC no se puede personalizar** ni interceptar directamente. Es una función nativa del navegador.
- **Cancelar el login o pulsar ESC sin intención puede generar un error `401 Unauthorized` en pantalla**, ya que el recurso queda inaccesible.
- **Una vez mostrado el cuadro**, si el usuario pulsa "Aceptar" con las credenciales válidas, volverá a iniciar sesión automáticamente.
- **Este comportamiento es estándar del protocolo HTTP**, no depende de nuestra aplicación ni de Java/Tomcat.
- **Debido a limitaciones de tiempo, se opta por mantener el flujo actual de cierre de sesión, ya que resulta funcional en términos generales, aunque presenta ciertos matices a nivel de experiencia de usuario. Esta decisión permite centrar los esfuerzos en completar otras partes del proyecto.** 



####  Alternativa a futuro

Para tener control total sobre el flujo de autenticación (formularios, mensajes, estilos, errores), es recomendable en versiones futuras:
- Migrar a **autenticación por formulario (FORM)** en lugar de BASIC.
- Por falta de tiempo se decide prescindir de esta migración.

---



