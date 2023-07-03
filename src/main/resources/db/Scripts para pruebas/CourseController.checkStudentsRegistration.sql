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
    ('auth0|a564440cb48ca4dda983a73e', 100002, 'docente', 'Ana', 'Gomez', 'ana.gomez@example.com'),
    ('auth0|64b40e41f7a0dfd00ea3c4df', 100003, 'docente', 'Facundo', 'Laffont', 'facu-docente@fake.com');

INSERT INTO carrera (/* id, */ nombre) VALUES
    (/* 1, */ 'Licenciatura en Sistemas de Información');

INSERT INTO sede (/* id, */ nombre, comision_Desde, comision_Hasta) VALUES
    (/* 1, */ 'Luján', 1, 5);

INSERT INTO alumno (legajo, dni, nombre, apellido, email) VALUES
    (193961, 44380431, 'GERONIMO OSCAR', 'LEDESMA ', 'geroledesma.gl2018@gmail.com'),
	(189433, 45308789, 'JUAN PABLO', 'LEONE ', 'juampi.leone2004@gmail.com'),
	(189458, 45521140, 'MELANIE YASMIN', 'LUCERO ', 'melanielucero745@gmail.com'),
	(143052, 40455863, 'AGUSTINA MICAELA', 'LULICH ', 'agustinalulich@gmail.com'),
	(171791, 42342615, 'FABRIZIO LIONEL', 'MACEDONE ', 'fabrimacedone@gmail.com'),
	(88653, 32101754, 'JUAN MANUEL', 'MAGDALENA ', 'JUANMAGDALENA4@HOTMAIL.COM'),
	(186093, 43970618, 'LUIS ALBERTO', 'MAIOLA ', 'Luismaiola8@gmail.com'),
	(181558, 44561272, 'BRISA ANABEL', 'MARCHIO ', 'brissaanabelmarchio2002@gmail.com'),
	(189442, 44335123, 'LOURDES BELEN', 'MARECO ', 'lourdesmareco12345@gmail.com'),
	(189431, 44961603, 'VALENTIN MARIANO', 'MARTINEZ ', 'valentinmm03@gmail.com'),
	(175433, 43976582, 'CARTHY PEREZ IAN ROY', 'MC ', 'ianmccarthy007@gmail.com'),
	(172891, 42940579, 'CARLOS IGNACIO', 'MENA ', 'carlosmena15@hotmail.com'),
	(101261, 32850245, 'EMMANUEL IGNACIO', 'MIROGLIO ', 'emmanuelmiroglio@gmail.com'),
	(189432, 45176397, 'GASCA IVAN', 'MOLINA ', 'ivan.molinagasca@gmail.com'),
	(181597, 43399663, 'AYLEN ARIANA', 'MONTIEL ', 'montielaylen2@gmail.com'),
	(189413, 45399528, 'GONZALO NICOLAS', 'MONTOYA ', 'gonzalomontoya222@gmail.com'),
	(189436, 45538657, 'FACUNDO JULIAN ROBER', 'MORALES ', 'facujulian2004@gmail.com'),
	(189440, 45906491, 'LUJAN', 'MUÑOZ ', 'munozlujan2004@gmail.com'),
	(189416, 43513293, 'ENZO ALEJANDRO', 'NEIRO ', 'Enzoneiro@gmail.com'),
	(189420, 45494041, 'BIANCHI LUCAS', 'OCAMPO ', 'lucas.o.bianchi@gmail.com'),
	(170774, 37221453, 'DANIEL ADRIAN', 'OLIVEIRA ', 'danieladrianoliveira@gmail.com'),
	(175432, 43356928, 'EVELYN DANIELA', 'OLIVEIRA ', 'dani.eve723@gmail.com'),
	(181505, 44110722, 'VALENTIN GABRIEL', 'ORDOQUI ', 'valentin7121928@outlook.com'),
	(112022, 29623915, 'JUAN MARTIN', 'PALOMERO ', 'PALOMERO361@GMAIL.COM'),
	(169991, 42586215, 'GIUSSANI JUAN FRANCISC', 'PEREZ ', 'jfperez291@gmail.com'),
	(191977, 45296234, 'JUAN BAUTISTA', 'POLIZZI ', 'juan.b.polizzi0803@gmail.com'),
	(89885, 31616122, 'ANDRES', 'QUISPE ', 'Orianaailen1504@gmail.com'),
	(188947, 36096315, 'LAURA LORENA', 'RAMIREZ ', 'LAURALORENARAMIREZ2@GMAIL.COM'),
	(189443, 44961513, 'FLORENCIA', 'REINOSO ', 'flor.reinoso1117@gmail.com'),
	(193590, 44004806, 'AGUSTIN URIEL', 'RINALDI ', 'urielagustin@outlook.com'),
	(191976, 44891015, 'AIZCORBE JOAQUIN MAN', 'RODEIRO ', 'joaquinrodeiro@gmail.com');

INSERT INTO asignatura (/* id, */ id_Carrera, nombre) VALUES
    (/* 1, */ 1, 'Seminario de Integración Profesional');

INSERT INTO comision (/* id, */ id_Asignatura, numero) VALUES
    (/* 1, */ 1, 1),
    (/* 2, */ 1, 2);

INSERT INTO cursada (/* id, */ id_Comision, anio, fecha_Inicio, fecha_Fin) VALUES
    (/* 1, */ 1, 2021, '2021-03-15', '2021-07-02'),
    (/* 2, */ 2, 2021, '2021-03-15', '2021-07-02');

INSERT INTO cursada_docente (/* id, */ id_Cursada, id_Docente, nivel_Permiso) VALUES
    (/* 1, */ 1, 'auth0|a564440cb48ca4dda983a73e', 1),
    (/* 2, */ 2, 'auth0|a564440cb48ca4dda983a73e', 1),
    (/* 3, */ 1, 'auth0|64b40e41f7a0dfd00ea3c4df', 1);

INSERT INTO cursada_alumno (/* id, */ id_Cursada, id_Alumno, previous_subjects_approved, studied_previously, condicion_Final) VALUES
    (/* 1, */ 1, 193961, true, true, null),
    (/* 2, */ 1, 189433, false, false, null),
    (/* 3, */ 1, 189458, true, true, null),
    (/* 4, */ 1, 143052, true, true, null),
    (/* 5, */ 1, 171791, false, false, null),
    (/* 6, */ 1, 88653, false, true, null),
    (/* 7, */ 1, 186093, false, true, null),
    (/* 8, */ 1, 181558, true, true, null),
    (/* 9, */ 1, 189442, true, false, null),
    (/* 10, */ 1, 189431, false, false, null),
    (/* 11, */ 1, 175433, true, false, null),
    (/* 12, */ 1, 172891, true, false, null),
    (/* 13, */ 1, 101261, true, false, null),
    (/* 14, */ 1, 189432, true, false, null);
