#!/bin/bash

# URL base del servicio REST
BASE_URL="http://localhost:8081/api"

echo "Iniciando la población de la base de datos..."

# Crear usuarios
curl -s -X POST "$BASE_URL/usuarios" -H "Content-Type: application/json" -d '{
  "id": 1,
  "nombre": "Juan Pérez",
  "rol": "alumno"
}'

curl -s -X POST "$BASE_URL/usuarios" -H "Content-Type: application/json" -d '{
  "id": 2,
  "nombre": "Ana Gómez",
  "rol": "profesor"
}'

# Crear asignaturas
curl -s -X POST "$BASE_URL/asignaturas" -H "Content-Type: application/json" -d '{
  "id": 1,
  "nombre": "Matemáticas",
  "profesor_id": 2
}'

# Crear matrícula
curl -s -X POST "$BASE_URL/matriculas" -H "Content-Type: application/json" -d '{
  "alumno_id": 1,
  "asignatura_id": 1
}'

# Asignar calificación
curl -s -X POST "$BASE_URL/calificaciones" -H "Content-Type: application/json" -d '{
  "alumno_id": 1,
  "asignatura_id": 1,
  "nota": 9.2
}'

echo "Población de datos completada."
