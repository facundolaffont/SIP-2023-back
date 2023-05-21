INSERT INTO usuario (id, legajo, rol, nombre, apellido, email) VALUES
    ('auth0|64f34db69140728f977c0784', 100001, 'administrador', 'Juan', 'Perez', 'juan.perez@example.com'),
    ('auth0|a564440cb48ca4dda983a73e', 100002, 'docente', 'Ana', 'Gomez', 'ana.gomez@example.com'),
    ('auth0|10fd4a208f65a290017b4d2a', 100003, 'docente', 'Pedro', 'Rodriguez', 'pedro.rodriguez@example.com');

INSERT INTO carrera (nombre) VALUES ('Licenciatura en Sistemas de Información');

INSERT INTO sede (nombre, comisionDesde, comisionHasta) VALUES
    ('Luján', 1, 5),
    ('Chivilcoy', 6, 10);

INSERT INTO alumno (legajo, dni, nombre, apellido, email) VALUES
    (150001, 32165498, 'Pedro', 'Alfonso', 'pedro.alfonso@example.com'),
    (150002, 33698547, 'Lucía', 'Fernández', 'lucia.fernandez@example.com'),
    (150003, 36258741, 'Carlos', 'González', 'carlos.gonzalez@example.com'),
    (150004, 39657412, 'Federico', 'Ramírez', 'federico.ramirez@example.com');

INSERT INTO asignatura (idCarrera, nombre) VALUES
    (1, 'Seminario de Integración Profesional'),
    (1, 'Sistemas Distribuidos y Programación Paralela'),
    (1, 'Programación en Ambiente Web');

INSERT INTO comision (idAsignatura, numero) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 2),
    (2, 3),
    (3, 1),
    (3, 2);

INSERT INTO cursada (idComision, anio, fechaInicio, fechaFin) VALUES
    (1, 2021, '2021-03-15', '2021-07-02'),
    (2, 2021, '2021-03-15', '2021-07-02');

INSERT INTO criterio_cursada (idCriterio, idCursada, valorRegular, valorPromovido) VALUES
    (1, 1, 50, 80),
    (2, 2, 50, 75);

INSERT INTO cursada_docente (idCursada, idDocente, nivelPermiso) VALUES
    (1, 'auth0|a564440cb48ca4dda983a73e', 1),
    (2, 'auth0|a564440cb48ca4dda983a73e', 1),
    (2, 'auth0|10fd4a208f65a290017b4d2a', 2);

INSERT INTO cursada_alumno (idCursada, idAlumno, condicion, recursante, condicionFinal) VALUES
    (1, 150001, '', true, 'Regular'),
    (1, 150002, 'P', false, 'Promovido'),
    (1, 150003, '', true, 'Ausente'),
    (1, 150004, 'P', true, 'Ausente');

INSERT INTO evento_cursada (idTipo, idCursada, obligatorio, fechaHoraInicio, fechaHoraFin) VALUES
    (1, 1, false, '2021-03-25 10:00:00.000000', '2021-03-25 12:00:00.000000'),
    (2, 1, true, '2021-03-29 15:00:00.000000', '2021-03-29 17:00:00.000000');

INSERT INTO evento_cursada_alumno (idEvento, idAlumno, asistencia, nota) VALUES
    (1, 150001, true, 9),
    (1, 150002, false, 2);