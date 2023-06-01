INSERT INTO usuario (id, legajo, rol, nombre, apellido, email) VALUES
    ('auth0|64f34db69140728f977c0784', 100001, 'administrador', 'Juan', 'Perez', 'juan.perez@example.com'),
    ('auth0|a564440cb48ca4dda983a73e', 100002, 'docente', 'Ana', 'Gomez', 'ana.gomez@example.com'),
    ('auth0|10fd4a208f65a290017b4d2a', 100003, 'docente', 'Pedro', 'Rodriguez', 'pedro.rodriguez@example.com');

INSERT INTO carrera (nombre) VALUES ('Licenciatura en Sistemas de Información');

INSERT INTO sede (nombre, comision_Desde, comision_Hasta) VALUES
    ('Luján', 1, 5),
    ('Chivilcoy', 6, 10);

INSERT INTO alumno (legajo, dni, nombre, apellido, email) VALUES
    (150001, 32165498, 'Pedro', 'Alfonso', 'pedro.alfonso@example.com'),
    (150002, 33698547, 'Lucía', 'Fernández', 'lucia.fernandez@example.com'),
    (150003, 36258741, 'Carlos', 'González', 'carlos.gonzalez@example.com'),
    (150004, 39657412, 'Federico', 'Ramírez', 'federico.ramirez@example.com');

INSERT INTO asignatura (id_Carrera, nombre) VALUES
    (1, 'Seminario de Integración Profesional'),
    (1, 'Sistemas Distribuidos y Programación Paralela'),
    (1, 'Programación en Ambiente Web');

INSERT INTO comision (id_Asignatura, numero) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 2),
    (2, 3),
    (3, 1),
    (3, 2);

INSERT INTO cursada (id_Comision, anio, fecha_Inicio, fecha_Fin) VALUES
    (1, 2021, '2021-03-15', '2021-07-02'),
    (2, 2021, '2021-03-15', '2021-07-02');

INSERT INTO criterio_cursada (id_Criterio, id_Cursada, valor_Regular, valor_Promovido) VALUES
    (1, 1, 50, 80),
    (2, 2, 50, 75);

INSERT INTO cursada_docente (id_Cursada, id_Docente, nivel_Permiso) VALUES
    (1, 'auth0|a564440cb48ca4dda983a73e', 1),
    (2, 'auth0|a564440cb48ca4dda983a73e', 1),
    (2, 'auth0|10fd4a208f65a290017b4d2a', 2);

INSERT INTO cursada_alumno (id_Cursada, id_Alumno, condicion, recursante, condicion_Final) VALUES
    (1, 150001, '', true, 'Regular'),
    (1, 150002, 'P', false, 'Promovido'),
    (1, 150003, '', true, 'Ausente'),
    (1, 150004, 'P', true, 'Ausente');

INSERT INTO evento_cursada (id_Tipo, id_Cursada, obligatorio, fecha_Hora_Inicio, fecha_Hora_Fin) VALUES
    (1, 1, false, '2021-03-25 10:00:00.000000', '2021-03-25 12:00:00.000000'),
    (2, 1, true, '2021-03-29 15:00:00.000000', '2021-03-29 17:00:00.000000');

INSERT INTO evento_cursada_alumno (id_Evento, id_Alumno, asistencia, nota) VALUES
    (1, 150001, true, '9'),
    (1, 150002, false, '2');