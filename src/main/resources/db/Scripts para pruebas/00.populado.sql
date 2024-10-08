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

INSERT INTO criterio_evaluacion (/* id, */ nombre) VALUES
    (/* 1, */ 'Asistencias'),
    (/* 2, */ 'Trabajos prácticos aprobados'),
    (/* 3, */ 'Trabajos prácticos recuperados'),
    (/* 4, */ 'Parciales aprobados'),
    (/* 5, */ 'Promedio de parciales'),
    (/* 6, */ 'Autoevaluaciones aprobadas'),
    (/* 7, */ 'Autoevaluaciones recuperadas');

INSERT INTO tipo_evento (/* id, */nombre) VALUES
    (/* 1, */ 'Clase'),
    (/* 2, */ 'Trabajo práctico'),
    (/* 3, */ 'Parcial'),
    (/* 4, */ 'Autoevaluación'),
    (/* 5, */ 'Recuperatorio Trabajo práctico'),
    (/* 6, */ 'Recuperatorio Parcial'),
    (/* 7, */ 'Recuperatorio Autoevaluación'),
    (/* 8, */ 'Integrador');
    (/* 9, */ 'Final');

INSERT INTO usuario (id, legajo, rol, nombre, apellido, email) VALUES
    ('auth0|64f34db69140728f977c0784', 100001, 'administrador', 'Juan', 'Perez', 'juan.perez@example.com'),
    ('auth0|a564440cb48ca4dda983a73e', 100002, 'docente', 'Ana', 'Gomez', 'ana.gomez@example.com'),
    ('auth0|10fd4a208f65a290017b4d2a', 100003, 'docente', 'Pedro', 'Rodriguez', 'pedro.rodriguez@example.com');

INSERT INTO carrera (/* id, */ nombre) VALUES
    (/* 1, */ 'Licenciatura en Sistemas de Información');

INSERT INTO sede (/* id, */ nombre, comision_Desde, comision_Hasta) VALUES
    (/* 1, */ 'Luján', 1, 5),
    (/* 2, */ 'Chivilcoy', 6, 10);

INSERT INTO alumno (legajo, dni, nombre, email) VALUES
    (150001, 32165498, 'Pedro Alfonso', 'pedro.alfonso@example.com'),
    (150002, 33698547, 'Lucía Fernández', 'lucia.fernandez@example.com'),
    (150003, 36258741, 'Carlos González', 'carlos.gonzalez@example.com'),
    (150004, 39657412, 'Federico Ramírez', 'federico.ramirez@example.com'),
    (193961, 44380431, 'LEDESMA GERONIMO OSCAR', 'geroledesma.gl2018@gmail.com'),
	(189433, 45308789, 'LEONE JUAN PABLO', 'juampi.leone2004@gmail.com'),
	(189458, 45521140, 'LUCERO MELANIE YASMIN', 'melanielucero745@gmail.com'),
	(143052, 40455863, 'LULICH AGUSTINA MICAELA', 'agustinalulich@gmail.com'),
	(171791, 42342615, 'MACEDONE FABRIZIO LIONEL', 'fabrimacedone@gmail.com'),
	(88653, 32101754, 'MAGDALENA JUAN MANUEL', 'JUANMAGDALENA4@HOTMAIL.COM'),
	(186093, 43970618, 'MAIOLA LUIS ALBERTO', 'Luismaiola8@gmail.com'),
	(181558, 44561272, 'MARCHIO BRISA ANABEL', 'brissaanabelmarchio2002@gmail.com'),
	(189442, 44335123, 'MARECO LOURDES BELEN', 'lourdesmareco12345@gmail.com'),
	(189431, 44961603, 'MARTINEZ VALENTIN MARIANO', 'valentinmm03@gmail.com'),
	(175433, 43976582, 'MC CARTHY PEREZ IAN ROY', 'ianmccarthy007@gmail.com'),
	(172891, 42940579, 'MENA CARLOS IGNACIO', 'carlosmena15@hotmail.com'),
	(101261, 32850245, 'MIROGLIO EMMANUEL IGNACIO', 'emmanuelmiroglio@gmail.com'),
	(189432, 45176397, 'MOLINA GASCA IVAN', 'ivan.molinagasca@gmail.com'),
	(181597, 43399663, 'MONTIEL AYLEN ARIANA', 'montielaylen2@gmail.com'),
	(189413, 45399528, 'MONTOYA GONZALO NICOLAS', 'gonzalomontoya222@gmail.com'),
	(189436, 45538657, 'MORALES FACUNDO JULIAN ROBER', 'facujulian2004@gmail.com'),
	(189440, 45906491, 'MUÑOZ LUJAN', 'munozlujan2004@gmail.com'),
	(189416, 43513293, 'NEIRO ENZO ALEJANDRO', 'Enzoneiro@gmail.com'),
	(189420, 45494041, 'OCAMPO BIANCHI LUCAS', 'lucas.o.bianchi@gmail.com'),
	(170774, 37221453, 'OLIVEIRA DANIEL ADRIAN', 'danieladrianoliveira@gmail.com'),
	(175432, 43356928, 'OLIVEIRA EVELYN DANIELA', 'dani.eve723@gmail.com'),
	(181505, 44110722, 'ORDOQUI VALENTIN GABRIEL', 'valentin7121928@outlook.com'),
	(112022, 29623915, 'PALOMERO JUAN MARTIN', 'PALOMERO361@GMAIL.COM'),
	(169991, 42586215, 'PEREZ GIUSSANI JUAN FRANCISC', 'jfperez291@gmail.com'),
	(191977, 45296234, 'POLIZZI JUAN BAUTISTA', 'juan.b.polizzi0803@gmail.com'),
	(89885, 31616122, 'QUISPE ANDRES', 'Orianaailen1504@gmail.com'),
	(188947, 36096315, 'RAMIREZ LAURA LORENA', 'LAURALORENARAMIREZ2@GMAIL.COM'),
	(189443, 44961513, 'REINOSO FLORENCIA', 'flor.reinoso1117@gmail.com'),
	(193590, 44004806, 'RINALDI AGUSTIN URIEL', 'urielagustin@outlook.com'),
	(191976, 44891015, 'RODEIRO AIZCORBE JOAQUIN MAN', 'joaquinrodeiro@gmail.com'),
	(181567, 44240978, 'RODRIGUEZ MAYRA ANABELA', 'may290802@gmail.com'),
	(189429, 45606381, 'ROSSI AUGUSTO JAVIER', 'augustorossi2004@gmail.com'),
	(189452, 39109678, 'SALVA DANIEL FERNANDO', 'Daniel11_fer@hotmail.com'),
	(190086, 40092483, 'SAULINO GONZALO', 'gonzasaulino1@gmail.com'),
	(189424, 45203238, 'SCURATI FRANCISCO', 'franscurati@gmail.com'),
	(181452, 43970641, 'SCURINI CLEFOR MATIAS', 'matyscurini@gmail.com'),
	(191694, 45480918, 'SILVA MARTIN JULIAN FRANCISC', 'juliansm@live.com'),
	(137994, 94855836, 'SOLIS TORRES JHOSELIN DEYANI', 'Jhosedeyanira@gmail.com'),
	(193042, 44689495, 'SOSA BRANDON MAXIMILIANO', 'sosabrandon554@gmail.com'),
	(191253, 45749492, 'SOSA MELANY AGUSTINA', 'melanysosa413@gmail.com'),
	(189427, 45687607, 'SQUILLACE SANGUINETTI LAUTAR', 'silvinasanguinetti2018@gmail.com'),
	(175370, 44163155, 'SUSKI PILAR', 'pilarsuski@outlook.com'),
	(189426, 45676946, 'TORCHIA MAXIMILIANO', 'torchia.mx@gmail.com'),
	(189422, 45521187, 'URRIZA YAZMIN LORENA', 'Yazminurriza20@gmail.com'),
	(178183, 40861078, 'VALENZUELA LAUTARO CRISTIAN', 'lautarocristianvalenzuela@gmail.com'),
	(144630, 33909284, 'VALENZUELA SANTIAGO RAUL', 'santiagovalenzuela88@gmail.com'),
	(189449, 45287246, 'VARGAS MARIA CAROLINA', 'vargasmariacarolina05@gmail.com'),
	(181524, 44207076, 'VAZQUEZ PABLO LEONEL', 'Pv3398390@gmail.com'),
	(189421, 45744069, 'VELIZ FLORENCIA BELEN', 'florveliz1126@gmail.com'),
	(189454, 44110828, 'VERDEJO MATEO JOSE', 'mateverdejo25@gmail.com'),
	(156129, 40184383, 'VIDELA MEZA GONZALO JESUS', 'Gonzalito_96@live.com'),
	(166364, 94551596, 'VILLARROEL GALARZA HEIDY', 'heidyvg580@gmail.com'),
	(176731, 43176162, 'VIÑES GUADALUPE', 'guadavies@yahoo.com.ar'),
	(189438, 45222533, 'YAUCK ANDRES NICOLAS', 'patogaturro859@gmail.com'),
	(191258, 44519126, 'ZALACAIN MATIAS', 'matiaszalacain91@gmail.com'),
	(143305, 93993728, 'ZAVALETA ALFARO ELIAS CESAR', 'eliaszavaleta42@gmail.com');

INSERT INTO asignatura (/* id, */ id_Carrera, nombre) VALUES
    (/* 1, */ 1, 'Seminario de Integración Profesional'),
    (/* 2, */ 1, 'Sistemas Distribuidos y Programación Paralela'),
    (/* 3, */ 1, 'Programación en Ambiente Web');

INSERT INTO comision (/* id, */ id_Asignatura, numero) VALUES
    (/* 1, */ 1, 1),
    (/* 2, */ 1, 2),
    (/* 3, */ 1, 3),
    (/* 4, */ 2, 1),
    (/* 5, */ 2, 2),
    (/* 6, */ 2, 3),
    (/* 7, */ 3, 1),
    (/* 8, */ 3, 2);

INSERT INTO cursada (/* id, */ id_Comision, anio, fecha_Inicio, fecha_Fin) VALUES
    (/* 1, */ 1, 2021, '2021-03-15', '2021-07-02'),
    (/* 2, */ 2, 2021, '2021-03-15', '2021-07-02');

INSERT INTO criterio_cursada (/* id, */ id_Criterio, id_Cursada, valor_Regular, valor_Promovido) VALUES

    /* id_Cursada = 1 */
    (/* 1, */ 1, 1, 50, 80), /* Asistencias */
    (/* 2, */ 2, 1, 50, 75), /* Trabajos prácticos aprobados */
    (/* 3, */ 3, 1, 50, 75), /* Trabajos prácticos recuperados */
    (/* 4, */ 4, 1, 50, 75), /* Parciales aprobados */
    (/* 5, */ 5, 1, 50, 75), /* Promedio de parciales */
    (/* 6, */ 6, 1, 50, 75), /* Autoevaluaciones aprobadas */
    (/* 7, */ 7, 1, 50, 75), /* Autoevaluaciones recuperadas */

    (/* 8, */ 2, 2, 50, 75);

INSERT INTO cursada_docente (/* id, */ id_Cursada, id_Docente, nivel_Permiso) VALUES
    (/* 1, */ 1, 'auth0|a564440cb48ca4dda983a73e', 1),
    (/* 2, */ 2, 'auth0|a564440cb48ca4dda983a73e', 1),
    (/* 3, */ 2, 'auth0|10fd4a208f65a290017b4d2a', 2);

INSERT INTO cursada_alumno (/* id, */ id_Cursada, id_Alumno, previous_subjects_approved, studied_previously, condicion_Final) VALUES
    (/* 1, */ 1, 150001, false, true, 'Regular'),
    (/* 2, */ 1, 150002, true, false, 'Promovido'),
    (/* 3, */ 1, 150003, false, true, 'Ausente'),
    (/* 4, */ 1, 150004, true, true, null),
    (/* 5, */ 1, 143305, true, true, null),
    (/* 6, */ 1, 191258, true, true, null),
    (/* 7, */ 1, 189438, true, true, null),
    (/* 8, */ 1, 176731, true, true, null),
    (/* 9, */ 1, 166364, true, true, null),
    (/* 10, */ 1, 156129, true, true, null),
    (/* 11, */ 1, 189454, true, true, null),
    (/* 12, */ 1, 189421, true, true, null);

INSERT INTO evento_cursada (/* id, */ id_Tipo, id_Cursada, obligatorio, fecha_Hora_Inicio, fecha_Hora_Fin) VALUES
    (/* 1, */ 1, 1, true, '2021-03-25 10:00:00.000000', '2021-03-25 12:00:00.000000'),
    (/* 2, */ 1, 1, false, '2021-03-26 10:00:00.000000', '2021-03-26 12:00:00.000000'),
    (/* 3, */ 1, 1, true, '2021-03-27 10:00:00.000000', '2021-03-27 12:00:00.000000'),
    (/* 4, */ 1, 1, false, '2021-03-28 10:00:00.000000', '2021-03-28 12:00:00.000000'),
    (/* 5, */ 1, 1, true, '2021-03-29 10:00:00.000000', '2021-03-29 12:00:00.000000'),
    (/* 6, */ 1, 1, false, '2021-03-30 10:00:00.000000', '2021-03-30 12:00:00.000000'),
    (/* 7, */ 2, 1, true, '2021-03-30 10:00:00.000000', '2021-03-30 12:00:00.000000'),
    (/* 8, */ 3, 1, true, '2021-03-30 10:00:00.000000', '2021-03-30 12:00:00.000000'),
    (/* 9, */ 4, 1, true, '2021-03-30 10:00:00.000000', '2021-03-30 12:00:00.000000'),
    (/* 10, */ 5, 1, true, '2021-03-30 10:00:00.000000', '2021-03-30 12:00:00.000000'),
    (/* 11, */ 6, 1, true, '2021-03-30 10:00:00.000000', '2021-03-30 12:00:00.000000'),
    (/* 12, */ 7, 1, true, '2021-03-30 10:00:00.000000', '2021-03-30 12:00:00.000000'),
    (/* 13, */ 8, 1, true, '2021-03-30 10:00:00.000000', '2021-03-30 12:00:00.000000');

INSERT INTO evento_cursada_alumno (/* id, */ id_Evento, id_Alumno, asistencia, nota) VALUES
    (/* 1, */ 1, 150001, true, null),
    (/* 2, */ 2, 150001, true, null),
    (/* 3, */ 3, 150001, true, null),
    (/* 4, */ 4, 150001, true, null),
    (/* 5, */ 5, 150001, true, null),
    (/* 6, */ 6, 150001, true, null),
    (/* 7, */ 7, 150001, true, '9'),
    (/* 7, */ 7, 191258, true, '4'),
    (/* 7, */ 7, 189438, true, '2'),
    (/* 7, */ 7, 156129, true, '5'),
    (/* 7, */ 7, 189454, true, '4'),
    (/* 8, */ 9, 150001, true, '9'),
    (/* 8, */ 9, 143305, true, '9'),
    (/* 8, */ 9, 191258, true, '9'),
    (/* 8, */ 9, 150002, true, '9'),
    (/* 8, */ 7, 176731, true, '9'),
    (/* 8, */ 9, 176731, true, '9'),
    (/* 9, */ 1, 143305, true, '9'),
    (/* 10, */ 1, 191258, true, '9'),
    (/* 10, */ 2, 191258, true, '4'),
    (/* 10, */ 3, 191258, true, '4'),
    (/* 10, */ 4, 191258, true, '4'),
    (/* 11, */ 1, 189438, true, '9'),
    (/* 11, */ 9, 189438, true, '6'),
    (/* 11, */ 4, 189438, true, '6'),
    (/* 11, */ 2, 189438, true, '6'),
    (/* 11, */ 5, 189438, true, '6'),
    (/* 12, */ 1, 176731, true, '9'),
    (/* 13, */ 1, 166364, true, '9'),
    (/* 14, */ 1, 156129, true, '9'),
    (/* 15, */ 1, 189454, true, '9'),
    (/* 16, */ 1, 150002, false, '2'),
    (/* 2, */ 2, 176731, true, null),
    (/* 3, */ 3, 176731, true, null),
    (/* 4, */ 4, 176731, true, null),
    (/* 5, */ 5, 176731, true, null),
    (/* 6, */ 6, 176731, true, null)
;