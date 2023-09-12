/*** Índice ***

1) Muestra los esquemas de la BD.
2) Muestra la zona horario de la BD.
3) Muestra todas las zonas horarias soportadas.

***/

-- (1)
SELECT sequence_name FROM information_schema.sequences;

-- (2)
show timezone;

-- (3)
SELECT * FROM pg_timezone_names;