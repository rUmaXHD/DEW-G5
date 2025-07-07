# Informe de Defensa del Trabajo: Notas Online

**Alumno:** Miguel Ángel Ortiz Requena**Asignatura:** Desarrollo Web (NOL2425)**Fecha de entrega:** Julio 2025**Tipo de entrega:** Recuperación individual del trabajo en grupo

---

## 1. Justificación de la Recuperación

Este trabajo ha sido revisado y reconstruido por el alumno Miguel Ángel Ortiz Requena, como parte del proceso de recuperación individual tras haber recibido una calificación de suspenso (4,9) en su versión grupal anterior. La nueva entrega ha sido elaborada de forma autónoma e incorpora:

- Mejoras en la estructura de la interfaz.
- Refactorización y comprensión detallada del código previo.
- Creación de un script de población para facilitar pruebas.
- Documentación redactada en formato Markdown (actas, navegación, README).
- Implementación completa de funcionalidades clave tanto para alumnos como para profesores.

---

## 2. Cobertura Funcional

### Alumno

- Consulta de asignaturas asignadas.
- Vista detallada de asignatura: información, grupo, nota y compañeros.
- Generación de certificado imprimible.

### Profesor

- Visualización de asignaturas impartidas.
- Consulta y modificación de notas con AJAX.
- Navegación fluida entre alumnos.

### Seguridad

- Autenticación HTTP BASIC.
- Restricción de accesos mediante roles `rolalu` y `rolpro`.
- Validación de sesión y atributos requeridos.

---

## 3. Detalles Técnicos

- **Tecnologías:** Java, Jakarta EE (Servlets), JSP, Bootstrap 5, JSON, HTTP Client API.
- **Estructura MVC aproximada** con delegación lógica en servlets y presentación con JSP.
- **AJAX** para edición de notas sin recarga.
- **Script de inicialización** (`poblar_bd_profesor_post.sh`) funcional y ejecutable en Bash.
- **Control de sesiones** y cookies (`JSESSIONID`, `key` en query string).

---

## 4. Documentación Entregada

- `README_Diagrama_Navegacion_NotasOnline.md`
- Actas de trabajo diarias en formato Markdown.
- Justificación personal de desarrollo individual.
- Informes de población, pruebas y mejoras.

---

## 5. Valor Añadido

- Revisión completa del trabajo y comprensión del código heredado.
- Inclusión de manejo de errores tanto en cliente como en servidor.
- Homogeneización visual de la interfaz con Bootstrap.
- Refactorización del script según las pautas de la guía oficial.
- Enfoque proactivo para mejorar técnica y documentalmente el proyecto.

---

## 6. Conclusión

Este trabajo se ha realizado con el objetivo de mejorar de forma significativa la entrega anterior, abordando los aspectos técnicos, funcionales y documentales reflejados en la guía de la asignatura. 

Agradezco la oportunidad de presentar esta recuperación y quedo a disposición para cualquier revisión, comentario o aclaración necesaria. Confío en que el trabajo pueda ser valorado de acuerdo a los criterios establecidos y al esfuerzo personal invertido durante este proceso.

**Miguel Ángel Ortiz Requena**
