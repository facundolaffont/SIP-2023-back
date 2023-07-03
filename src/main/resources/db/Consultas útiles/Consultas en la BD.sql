/*** Índice ***
    1) Muestra los esquemas de la BD.
    2) Muestra la zona horario de la BD.
    3) Muestra todas las zonas horarias soportadas.
***/

-- 1) Muestra los esquemas de la BD.
SELECT sequence_name FROM information_schema.sequences;

-- 2) Muestra la zona horario de la BD.
show timezone;

-- 3) Muestra todas las zonas horarias soportadas.
SELECT * FROM pg_timezone_names;