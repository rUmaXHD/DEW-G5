
# Diagrama de navegación – Aplicación Notas Online

Este documento describe el flujo de navegación completo de la aplicación **Notas Online**, incluyendo el comportamiento para los roles **alumno** y **profesor**, y explicando el papel de cada servlet y vista JSP asociada.

---

## 1. Entrada: `index.jsp`

La aplicación comienza en la página de bienvenida `index.jsp`, donde solo se permite una acción: **iniciar sesión**.

---

## 2. Autenticación

Al enviar credenciales:

- Se accede a `AccesoServlet.java`, que actúa como **puente** para validar la sesión y redirigir al flujo correspondiente.
- Luego se llama a `LoginServlet.java`, que ejecuta la lógica de autenticación:
  - Verifica usuario y contraseña
  - Determina el rol del usuario (alumno o profesor)
  - Redirige a la interfaz correspondiente

A partir de aquí, el flujo se bifurca:

---

# Rutina del Alumno

## ➤ `inicioAlumno.jsp`

- Es la página principal del alumno autenticado.
- Carga automáticamente las asignaturas en las que está matriculado el alumno mediante el servlet `AsignaturasServlet.java`.

Desde esta página se pueden realizar tres acciones:

---

### 1. Ver detalles de una asignatura

- Se accede desde un botón asociado a cada asignatura.
- Se llama a `DetalleAsignaturaServlet.java`, que obtiene:
  - Nombre
  - Créditos
  - Nota obtenida
  - Grupo y compañeros
- Se muestra la información en `detalleAsignatura.jsp`.

> Se puede volver a `inicioAlumno.jsp` mediante un botón que redirige a `AccesoServlet.java`, asegurando que la sesión siga activa.

---

### 2. Generar certificado académico

- El alumno puede acceder a un listado de todas sus asignaturas y calificaciones.
- Esto lo gestiona el servlet `CertificadoServlet.java`.
- Se visualiza en `certificado.jsp`, que está diseñado para ser imprimible mediante `window.print()`.

> Al igual que antes, se puede volver atrás usando el botón que pasa por `AccesoServlet.java`.

---

### 3. Cerrar sesión

- Redirige a `LogoutServlet.java`, que:
  - Finaliza la sesión del usuario
  - Fuerza el olvido de credenciales BASIC (mediante un `fetch` manual)
  - Muestra una confirmación y redirige a `index.jsp`

---

# Rutina del Profesor

## ➤ `inicio.jsp`

- Página de inicio del profesor autenticado.
- Al cargar, se invoca `ProfesorAsignaturasServlet.java` para obtener todas las asignaturas que imparte.

Desde aquí, el profesor tiene dos opciones:

---

### 1. Gestionar alumnos de una asignatura

- Desde el botón **"Acceder"** de cada asignatura, se accede a `IrAGestionarAlumnosServlet.java`, que:
  - Guarda el acrónimo de la asignatura en sesión
  - Redirige a `gestionarAlumnos.jsp`

- Esta página activa automáticamente `ListaAlumnosAsignaturaServlet.java`, que:
  - Devuelve los alumnos de la asignatura
  - Permite navegar con botones **Siguiente** y **Anterior**
  - Muestra y edita las notas

#### ✔ Modificación de nota:

- Al cambiar la nota, se realiza una petición AJAX a `ModificarNotaServlet.java` que actualiza la calificación directamente en el backend.

#### Ventajas del uso de AJAX:

- **Mejora de experiencia de usuario:** no se necesita recargar toda la página.
- **Interacción inmediata:** los cambios son visibles de forma dinámica.
- **Menor carga para el servidor:** solo se transmiten los datos necesarios.
- **Interfaz fluida:** navegación entre alumnos sin perder contexto ni romper la sesión.

> Desde `gestionarAlumnos.jsp` también se puede volver atrás usando `AccesoServlet.java`, que revalida la sesión y redirige a `inicio.jsp`.

---

### 2. Cerrar sesión

- Igual que en el caso del alumno:
  - Se accede a `LogoutServlet.java`
  - Se borra la sesión
  - Se vuelve a `index.jsp`

---

# Rol del AccesoServlet

`AccesoServlet.java` es clave para mantener la seguridad y coherencia de la navegación. Su función es:

- **Verificar que la clave de sesión (`key`) siga activa**
- **Redirigir correctamente** al inicio correspondiente (`inicioAlumno.jsp` o `inicio.jsp`) según el rol
- **Proteger rutas críticas** para evitar accesos sin sesión válida

---
