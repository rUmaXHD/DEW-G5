#!/bin/bash

# ================================
# Script de Población para CentroEducativo
# ================================

# 🔗 Definir BASE URL
BASE_URL="http://localhost:9090/CentroEducativo"

echo "Autenticando con el usuario administrador..."

KEY=$(curl -s -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d '{"dni": "111111111", "password": "654321"}' | jq -r .key)

if [[ -z "$KEY" || "$KEY" == "null" ]]; then
  echo "ERROR: No se pudo obtener la clave de sesión."
  exit 1
fi

echo "Clave de sesión obtenida: $KEY"
echo ""

# Crear alumnos
echo "Creando alumnos..."
curl -s -X POST "$BASE_URL/alumnos?key=$KEY" \
  -H "Content-Type: application/json" \
  -d '{"dni": "11112222A", "nombre": "Nicolás", "apellidos": "Ionascu", "password": "alumno123"}'

curl -s -X POST "$BASE_URL/alumnos?key=$KEY" \
  -H "Content-Type: application/json" \
  -d '{"dni": "22223333B", "nombre": "Laura", "apellidos": "Perez", "password": "alumno123"}'

# Crear profesor
echo "Creando profesor..."
curl -s -X POST "$BASE_URL/profesores?key=$KEY" \
  -H "Content-Type: application/json" \
  -d '{"dni": "99998888Z", "nombre": "Claudia", "apellidos": "Martinez", "password": "prof123"}'

# Crear asignatura
echo "Creando asignatura..."
curl -s -X POST "$BASE_URL/asignaturas?key=$KEY" \
  -H "Content-Type: application/json" \
  -d '{"acronimo": "WEB2", "nombre": "Aplicaciones Web II", "curso": 4, "cuatrimestre": "B", "creditos": 6.0}'

# Matricular alumnos
echo "Matriculando alumnos en WEB2..."
curl -s -X PUT "$BASE_URL/alumnos/11112222A/asignaturas/WEB2?key=$KEY"
curl -s -X PUT "$BASE_URL/alumnos/22223333B/asignaturas/WEB2?key=$KEY"

# Asignar notas
echo "Asignando notas..."
curl -s -X PUT -H "Content-Type: application/json" \
  -d '{"nota": 9.1}' \
  "$BASE_URL/asignaturas/WEB2/alumnos/11112222A?key=$KEY"

curl -s -X PUT -H "Content-Type: application/json" \
  -d '{"nota": 7.6}' \
  "$BASE_URL/asignaturas/WEB2/alumnos/22223333B?key=$KEY"

# Verificar alumno
echo ""
echo "Verificando datos de Nicolás (11112222A):"
curl -s "$BASE_URL/alumnos/11112222A?key=$KEY" | jq

