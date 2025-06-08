# ACTA DE REUNIÓN - PROYECTO NOL_G5

**Fecha:** Sábado, 25 de mayo de 2025  
**Lugar:** Reunión telemática por Discord  
**Asistentes:** Saúl Rabadán Sandoval, Miguel Ángel Ortiz Requena, Bogdan Nicolae Ionascu, Andrés Durá Hernández

---

## Orden del día:

- Revisión de entregables del Hito 1  
- Planificación de funcionalidades del Hito 2  
- Distribución inicial de servlets y vistas  
- Análisis inicial de CentroEducativo y autenticación BASIC

---

## Desarrollo de la reunión:

Tras una evaluación del trabajo entregado en el Hito 1, se trazó el plan de desarrollo para completar el Hito 2. Se identificaron las funcionalidades clave y se distribuyeron las tareas entre los integrantes del grupo, centrándose en los siguientes aspectos:

- Creación de vistas JSP específicas para alumno y profesor.  
- Interacción fluida con la API REST usando peticiones autenticadas.  
- Sistema de navegación coherente y diseño uniforme con Bootstrap 5.  
- Implementación de funcionalidades CRUD mediante servlets.

---

## Reparto de tareas basado en servlets reales:

- **Saúl Rabadán Sandoval:**  
  Encargado de `ModificarNotaServlet.java`, `ListaAlumnosAsignaturaServlet.java` y `ProfesorAsignaturasServlet.java`, incluyendo AJAX para edición de notas.

- **Miguel Ángel Ortiz Requena:**  
  Desarrollo del certificado del alumno (basado en `DetallesAlumnoPRO.java`) y control de sesión para alumnos. Responsable de `login.jsp`, `alumno/*.jsp`.

- **Bogdan Nicolae Ionascu:**  
  Implementación y configuración del filtro de logs (`filtros/Logs.java`) y aseguramiento de la trazabilidad de accesos vía `web.xml`.

- **Andrés Durá Hernández:**  
  Migración de interfaces HTML a JSP (`login.jsp`, `login-error.jsp`, etc.), estructura de navegación y desarrollo de `LoginServlet.java`, `LogoutServlet.java`.

---

## Acuerdos:

1. La estructura final del proyecto seguirá una separación clara entre vistas `alumno` y `profesor`.  
2. Todos los servlets serán documentados y registrados en el `web.xml`.  
3. La funcionalidad de login usará autenticación BASIC de Tomcat.  
4. Siguiente reunión: sábado 1 de junio para revisión de avances y pruebas REST.

---

## Observaciones adicionales:

- El uso de `structures/` permite mantener clases comunes como modelos de datos (POJOs).  
- El filtro de logs debe validar todos los accesos, incluidos los fallidos.

