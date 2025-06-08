# ACTA DE REUNIÓN - PROYECTO NOL_G5

**Fecha:** Domingo, 1 de junio de 2025  
**Lugar:** Reunión telemática por Discord  
**Asistentes:** Saúl Rabadán Sandoval, Miguel Ángel Ortiz Requena, Bogdan Nicolae Ionascu, Andrés Durá Hernández

---

## Orden del día:

- Seguimiento del desarrollo de servlets y JSPs  
- Validación de conexión con CentroEducativo  
- Coordinación entre roles y vistas  
- Revisión de control de sesión y autenticación

---

## Desarrollo de la reunión:

Cada integrante presentó los avances:

- **Saúl**: completó `ListaAlumnosAsignaturaServlet.java` y `ModificarNotaServlet.java`, mostrando que las calificaciones ya se pueden modificar desde la vista del profesor mediante AJAX.  
- **Miguel**: trabajó con éxito en `DetallesAlumnoPRO.java` y mejoró el JSP de certificado, integrando dinámicamente la foto y datos del alumno.  
- **Bogdan**: finalizó el filtro de logs persistente en `filtros/`, incluyendo su activación mediante parámetros de contexto.  
- **Andrés**: integró `LoginServlet.java`, `LogoutServlet.java` y adaptó correctamente `login.jsp`, además de crear navegación entre vistas con Bootstrap.

También se acordó mantener una sesión `HttpSession` que contenga `dni`, `password` y `key`, para poder realizar llamadas REST al backend de forma segura y eficaz.

---

## Revisión de tareas:

- **Saúl:** optimizar AJAX con JSON bidireccional para edición de calificaciones.  
- **Miguel:** finalizar certificado y vista de detalles con `alumno/detalle.jsp`.  
- **Bogdan:** testeo completo del log persistente y su comportamiento en errores.  
- **Andrés:** unificación visual de `login.jsp`, `login-error.jsp` y `logout` con navegación fluida.

---

## Acuerdos:

1. Crear servlets únicos para listar asignaturas tanto de profesor (`ProfesorAsignaturasServlet`) como de alumno (`ListaAsignaturasAlumnoServlet`).  
2. Implementar `requestDispatcher` y uso de atributos con `setAttribute()` para pasar datos a vistas.  
3. Centralizar la recuperación de fotos desde un servlet específico vinculado al `dni`.

---

## Observaciones adicionales:

- El sistema de seguridad implementado con `tomcat-users.xml` está operativo.  
- Las credenciales válidas se comprobarán en sesión antes de acceder a cada servlet.

