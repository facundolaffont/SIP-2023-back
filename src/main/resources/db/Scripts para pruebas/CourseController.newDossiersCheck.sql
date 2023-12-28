-- CourseController.newDossiersCheck.sql --


/* Limpieza de BD */

DELETE FROM evento_cursada_alumno;
ALTER SEQUENCE evento_cursada_alumno_id_seq RESTART WITH 1;

DELETE FROM evento_cursada;
ALTER SEQUENCE evento_cursada_id_seq RESTART WITH 1;

DELETE FROM tipo_evento;
ALTER SEQUENCE tipo_evento_id_seq RESTART WITH 1;

DELETE FROM cursada_alumno;
ALTER SEQUENCE cursada_alumno_id_seq RESTART WITH 1;

DELETE FROM cursada_docente;
ALTER SEQUENCE cursada_docente_id_seq RESTART WITH 1;

DELETE FROM criterio_cursada;
ALTER SEQUENCE criterio_cursada_id_seq RESTART WITH 1;

DELETE FROM criterio_evaluacion;
ALTER SEQUENCE criterio_evaluacion_id_seq RESTART WITH 1;

DELETE FROM cursada;
ALTER SEQUENCE cursada_id_seq RESTART WITH 1;

DELETE FROM comision;
ALTER SEQUENCE comision_id_seq RESTART WITH 1;

DELETE FROM asignatura;
ALTER SEQUENCE asignatura_id_seq RESTART WITH 1;

DELETE FROM alumno;

DELETE FROM sede;
ALTER SEQUENCE sede_id_seq RESTART WITH 1;

DELETE FROM carrera;
ALTER SEQUENCE carrera_id_seq RESTART WITH 1;

DELETE FROM usuario;


/* Generación de datos. */

INSERT INTO usuario (id, legajo, rol, nombre, apellido, email) VALUES
    ('auth0|64f34db69140728f977c0784', 100001, 'administrador', 'Juan', 'Perez', 'juan.perez@example.com'),
    ('auth0|64b40e41f7a0dfd00ea3c4df', 100003, 'docente', 'Facundo', 'Laffont', 'facu-docente@fake.com');

INSERT INTO carrera (/* id, */ nombre) VALUES
    (/* 1, */ 'Licenciatura en Sistemas de Información');

INSERT INTO sede (/* id, */ nombre, comision_Desde, comision_Hasta) VALUES
    (/* 1, */ 'Luján', 1, 5);

INSERT INTO alumno (legajo, dni, nombre, apellido, email) VALUES
    (191694, 45480918, 'SILVA MARTIN JULIAN FRANCISC', 'SILVA', 'juliansm@live.com'),
    (137994, 94855836, 'SOLIS TORRES JHOSELIN DEYANI', 'SOLIS', 'Jhosedeyanira@gmail.com'),
    (193042, 44689495, 'SOSA BRANDON MAXIMILIANO', 'SOSA', 'sosabrandon554@gmail.com'),
    (191253, 45749492, 'SOSA MELANY AGUSTINA', 'SOSA', 'melanysosa413@gmail.com'),
    (189427, 45687607, 'SQUILLACE SANGUINETTI LAUTAR', 'SQUILLACE', 'silvinasanguinetti2018@gmail.com'),
    (175370, 44163155, 'SUSKI PILAR', 'SUSKI', 'pilarsuski@outlook.com'),
    (189426, 45676946, 'TORCHIA MAXIMILIANO', 'TORCHIA', 'torchia.mx@gmail.com'),
    (189422, 45521187, 'URRIZA YAZMIN LORENA', 'URRIZA', 'Yazminurriza20@gmail.com'),
    (178183, 40861078, 'VALENZUELA LAUTARO CRISTIAN ', 'VALENZUELA', 'lautarocristianvalenzuela@gmail.com'),
    (144630, 33909284, 'VALENZUELA SANTIAGO RAUL', 'VALENZUELA', 'santiagovalenzuela88@gmail.com');
