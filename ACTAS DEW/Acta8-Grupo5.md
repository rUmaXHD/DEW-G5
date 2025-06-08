# ACTA DE REUNIÓN - PROYECTO NOL_G5

**Fecha:** Sábado, 7 de junio de 2025  
**Lugar:** Reunión telemática por Discord  
**Asistentes:** Saúl Rabadán Sandoval, Miguel Ángel Ortiz Requena, Bogdan Nicolae Ionascu, Andrés Durá Hernández

---

## Orden del día:

- Integración de todos los módulos  
- Validación de funcionalidades clave  
- Preparación de entrega final y demo  
- Verificación del despliegue completo en Tomcat

---

## Desarrollo de la reunión:

Se procedió a una integración completa del proyecto. Se validaron todas las rutas, la gestión de roles y la interacción con CentroEducativo. Se probaron especialmente:

- `LoginServlet.java` y `LogoutServlet.java` con autenticación BASIC.  
- `ListaAlumnosAsignaturaServlet.java` y `ModificarNotaServlet.java` funcionando con AJAX desde la vista `profesor/`.  
- `DetallesAlumnoPRO.java` generando correctamente el certificado con imagen desde `alumno/`.  
- El filtro de logs activo en todos los accesos, escribiendo en orden cronológico.

---

## Revisión final de responsabilidades:

- **Saúl:** comprobación del flujo completo de edición y guardado de notas.  
- **Miguel:** validación visual y estructural del certificado.  
- **Bogdan:** confirmación del archivo de logs y revisión del `web.xml`.  
- **Andrés:** test de navegación en vistas `login`, `profesor` y `alumno`, y control de errores de sesión.

---

## Acuerdos:

1. La entrega se realizará en formato ZIP con carpeta `docs/` incluyendo actas y scripts.  
2. Las credenciales serán probadas con usuarios de `tomcat-users.xml`.  
3. Se preparará una demo grabada si no puede hacerse en directo.  
4. Se etiquetará la versión final como `v1.0-final` en el repositorio Git.

---

## Observaciones adicionales:

- La carpeta `structures/` centraliza la lógica de datos compartida entre servlets.  
- Se han eliminado rutas relativas que daban problemas en despliegue real.  
- Todos los JSP se han probado en entorno Tomcat, tanto para alumno como para profesor.


