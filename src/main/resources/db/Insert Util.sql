-- Tabla carrera
INSERT INTO carrera (nombre)
SELECT
  'Carrera ' || generate_series(1, 10);

-- Tabla sede
INSERT INTO sede (nombre, comision_Desde, comision_Hasta)
SELECT
  'Sede ' || generate_series(1, 10),
  (generate_series - 1) * 10 + 1,
  generate_series * 10
FROM generate_series(1, 10);

-- Tabla alumno
INSERT INTO alumno (legajo, dni, nombre, email)
SELECT
  generate_series(1001, 1100),
  generate_series(20000000, 20000100),
  'Nombre ' || generate_series(1, 100),
  'email' || generate_series(1, 100) || '@example.com'
FROM generate_series(1, 100);

-- Tabla asignatura
INSERT INTO asignatura (id_Carrera, nombre)
SELECT
  (random() * 10 + 1)::integer,
  'Asignatura ' || generate_series(1, 50)
FROM generate_series(1, 50);

-- Tabla comision
INSERT INTO comision (id_Asignatura, numero)
SELECT
  (random() * 50 + 1)::integer,
  (random() * 5 + 1)::integer
FROM generate_series(1, 50);

-- Tabla cursada
INSERT INTO cursada (id_Comision, anio, fecha_Inicio, fecha_Fin)
SELECT
  (random() * 50 + 1)::integer,
  2023,
  '2023-01-01'::date + (random() * 365)::integer,
  '2023-01-01'::date + (random() * 365)::integer
FROM generate_series(1, 50);

-- Tabla cursada_alumno
INSERT INTO cursada_alumno (id_Cursada, id_Alumno, condicion, recursante, condicion_Final)
SELECT
  id_Cursada,
  id_Alumno,
  CASE WHEN RANDOM() < 0.5 THEN 'A' ELSE 'B' END AS condicion,
  RANDOM() < 0.2 AS recursante,
  CASE WHEN RANDOM() < 0.7 THEN 'Regular' ELSE 'Promovido' END AS condicion_Final
FROM (
  SELECT
    c.id AS id_Cursada,
    a.legajo AS id_Alumno
  FROM cursada c
  CROSS JOIN alumno a
  ORDER BY RANDOM()
  LIMIT 100  -- Cambia el número 100 por la cantidad de filas aleatorias que desees insertar
) AS random_data;

-- Tabla evento_cursada_alumno
INSERT INTO evento_cursada_alumno (id_Evento, id_Alumno, asistencia, nota)
SELECT
  id_Evento,
  id_Alumno,
  RANDOM() < 0.8 AS asistencia,
  CASE
    WHEN RANDOM() < 0.5 THEN 'A'
    WHEN RANDOM() < 0.7 THEN 'B'
    WHEN RANDOM() < 0.9 THEN 'C'
    ELSE 'D'
  END AS nota
FROM (
  SELECT
    e.id AS id_Evento,
    ca.id_Alumno
  FROM evento_cursada e
  CROSS JOIN cursada_alumno ca
  ORDER BY RANDOM()
  LIMIT 200  -- Cambia el número 200 por la cantidad de filas aleatorias que desees insertar
) AS random_data;
