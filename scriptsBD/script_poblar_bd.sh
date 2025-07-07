#!/bin/bash

# Paso 1: Login como admin
echo " Haciendo login como administrador..."

KEY=$(curl -s -c session_cookie.txt \
    -X POST "http://localhost:9090/CentroEducativo/login" \
    -H "accept: text/plain" \
    -H "Content-Type: application/json" \
    -d '{ "dni": "111111111", "password": "654321" }' | tr -d '"')

echo " Key obtenida: $KEY"

# Paso 2: Crear varios estudiantes
echo " Registrando estudiantes ..."

# Array de estudiantes: "dni,nombre,apellidos"
estudiantes=(
    "888888888,Paula,Navas"
    "777777777,Iván,Ortega"
    "123123123,Clara,Castro"
    "999999999,Sergio,Salas"
    "123123123,Elena,Ruiz"
)

for alumno in "${estudiantes[@]}"; do
    IFS=',' read -r dni nombre apellidos <<< "$alumno"

    echo " Registrando alumno $nombre $apellidos ($dni)..."

    curl -s -b session_cookie.txt \
        -X POST "http://localhost:9090/CentroEducativo/alumnos?key=$KEY" \
        -H "accept: text/plain" \
        -H "Content-Type: application/json" \
        -d "{\"apellidos\": \"$apellidos\", \"dni\": \"$dni\", \"nombre\": \"$nombre\", \"password\": \"123456\"}"
done

echo " Todos los estudiantes fueron añadidos."

docentes=(
    "111222333,Raquel,Navas Valls"
    "222333444,Iván,Moreno"
    "333555777,Laura,Soler"
    "444666666,Iker,Salas"
    "555777555,David,Cano"
)

for profesor in "${docentes[@]}"; do
    IFS=',' read -r dni nombre apellidos <<< "$profesor"

    echo " Registrando profesor $nombre $apellidos ($dni)..."

    curl -s -b session_cookie.txt \
        -X POST "http://localhost:9090/CentroEducativo/profesores?key=$KEY" \
        -H "accept: text/plain" \
        -H "Content-Type: application/json" \
        -d "{\"apellidos\": \"$apellidos\", \"dni\": \"$dni\", \"nombre\": \"$nombre\", \"password\": \"123456\"}"
done

echo " Todos los docentes fueron añadidos."

asignaturas=(
    "BDA,A,Bases de datos"
    "SIN,B,Sistemas Inteligentes"
    "MAD,A,Física"
    "IIP,B,Introduccion a la Inforfamtica y la Progrmacion"
)

for asignatura in "${asignaturas[@]}"; do
    IFS=',' read -r acronimo cuatrimestre nombre <<< "$asignatura"

    echo " Registrando asignatura $nombre $cuatrimestre ($acronimo)..."

    curl -s -b session_cookie.txt \
        -X POST "http://localhost:9090/CentroEducativo/asignaturas?key=$KEY" \
        -H "Content-Type: application/json" \
        -d "{  \"acronimo\": \"$acronimo\",  \"creditos\": 4.5,  \"cuatrimestre\": \"$cuatrimestre\",  \"curso\": 3,  \"nombre\": \"$nombre\"}"
done

echo " Todas las asignaturas fueron añadidos."

acronimos=(
    "BDA"
    "SIN"
    "MAD"
    "IIP"
    "DEW"
)

dni_estudiantes=(888888888 777777777 123123123 999999999 123123123)

for acronimo in "${acronimos[@]}"; do
    echo "Registrando estudiantes a asignatura $acronimo..."

    for dni in "${dni_estudiantes[@]}"; do
        echo "  - Registrando alumno con DNI $dni a $acronimo..."

        curl -s -b session_cookie.txt \
            -X POST "http://localhost:9090/CentroEducativo/alumnos/$dni/asignaturas?key=$KEY" \
            -H "accept: text/plain" \
            -H "Content-Type: application/json" \
            -d "$acronimo"
    done
done

dni_docentes=(111222333 222333444 333555777 444666666 555777555)

for acronimo in "${acronimos[@]}"; do
    echo "Registrando docentes a asignatura $acronimo..."

    for dni in "${dni_docentes[@]}"; do
        echo "  - Registrando profesor con DNI $dni a $acronimo..."

        curl -s -b session_cookie.txt \
            -X POST "http://localhost:9090/CentroEducativo/profesores/$dni/asignaturas?key=$KEY" \
            -H "accept: text/plain" \
            -H "Content-Type: application/json" \
            -d "$acronimo"
    done
done

echo " Haciendo login como profesor..."

KEYP=$(curl -s -c profesor_cookie.txt \
    -X POST "http://localhost:9090/CentroEducativo/login" \
    -H "accept: text/plain" \
    -H "Content-Type: application/json" \
    -d '{ "dni": "111222333", "password": "123456" }' | tr -d '"')
    
echo " Key obtenida: $KEYP"

for acronimo in "${acronimos[@]}"; do
    echo "Registrando notas estudiantes a asignatura $acronimo..."

    for dni in "${dni_estudiantes[@]}"; do
        echo "  - Registrando nota a alumno con DNI $dni a $acronimo..."
        curl -s -b profesor_cookie.txt \
            -X PUT "http://localhost:9090/CentroEducativo/alumnos/$dni/asignaturas/$acronimo?key=$KEYP" \
            -H "accept: text/plain" \
            -H "Content-Type: application/json" \
            -d "5"
    done
done

echo "Sobrescribiendo nota del alumno 888888888 a 5 en todas sus asignaturas..."

for acronimo in "${acronimos[@]}"; do
    echo "  - Cambiando nota a 5 en $acronimo"
    curl -s -b profesor_cookie.txt \
        -X PUT "http://localhost:9090/CentroEducativo/alumnos/888888888/asignaturas/$acronimo?key=$KEYP" \
        -H "accept: text/plain" \
        -H "Content-Type: application/json" \
        -d "5"
done
