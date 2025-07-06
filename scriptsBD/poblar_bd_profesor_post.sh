#!/bin/bash

# ==============================================
# Script: poblar base de datos (vincula asignaturas a profesor correctamente)
# ==============================================

BASE_URL="http://localhost:9090/CentroEducativo"
COOKIE_JAR="cucu"

echo "Autenticando con el usuario administrador..."

KEY=$(curl -s -c $COOKIE_JAR -b $COOKIE_JAR -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  --data '{"dni":"111111111","password":"654321"}')

echo "Clave recibida: $KEY"

if [[ -z "$KEY" || "$KEY" == "null" ]]; then
  echo "ERROR: No se pudo obtener la clave de sesión."
  exit 1
fi

# Crear alumno
echo "Creando alumno..."
curl -s -X POST "$BASE_URL/alumnos?key=$KEY" -H "Content-Type: application/json" \
  -d '{"dni": "33445566X", "nombre": "John", "apellidos": "Wick", "password": "cuidadin"}' \
  -c $COOKIE_JAR -b $COOKIE_JAR

# Crear profesor
echo "Creando profesor..."
curl -s -X POST "$BASE_URL/profesores?key=$KEY" -H "Content-Type: application/json" \
  -d '{"dni": "55556666P", "nombre": "Luis", "apellidos": "Sánchez", "password": "123456"}' \
  -c $COOKIE_JAR -b $COOKIE_JAR

# Crear asignatura
echo "Creando asignatura..."
curl -s -X POST "$BASE_URL/asignaturas?key=$KEY" -H "Content-Type: application/json" \
  -d '{"acronimo": "DEW", "nombre": "Desarrollo Web", "curso": 3, "cuatrimestre": "B", "creditos": 6}' \
  -c $COOKIE_JAR -b $COOKIE_JAR

# Matricular alumno en DEW
echo "Matriculando alumno en DEW..."
curl -s -X POST "$BASE_URL/asignaturas/DEW/alumnos?key=$KEY" \
  -H "Content-Type: application/json" \
  -d "$(printf '%s' '"33445566X"')" \
  -c $COOKIE_JAR -b $COOKIE_JAR

# Asignar asignatura al profesor usando POST /profesores/{dni}/asignaturas
echo "Asignando DEW al profesor 55556666P..."
curl -s -X POST "$BASE_URL/profesores/55556666P/asignaturas?key=$KEY" \
  -H "Content-Type: application/json" \
  -d "$(printf '%s' '"DEW"')" \
  -c $COOKIE_JAR -b $COOKIE_JAR

# Asignar nota al alumno
echo "Asignando nota al alumno..."
curl -s -X PUT "$BASE_URL/alumnos/33445566X/asignaturas/DEW?key=$KEY" \
  -H "Content-Type: application/json" \
  -d "$(printf '%s' '9.2')" \
  -c $COOKIE_JAR -b $COOKIE_JAR

echo "Script completado correctamente."
