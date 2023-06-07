/* **************************** *
 * Inserción de datos iniciales *
 * **************************** */

INSERT INTO criterio_evaluacion (nombre) VALUES
    ('Asistencias'),
    ('Trabajos prácticos aprobados'),
    ('Trabajos prácticos recuperados'),
    ('Parciales aprobados'),
    ('Promedio de parciales'),
    ('Autoevaluaciones aprobadas'),
    ('Autoevaluaciones recuperadas')
;

INSERT INTO tipo_evento (nombre) VALUES
    ('Clase'),
    ('Trabajo práctico'),
    ('Parcial'),
    ('Autoevaluación'),
    ('Recuperatorio'),
    ('Integrador')
;

INSERT INTO usuario (id, legajo, rol, nombre, apellido, email) VALUES
    ('auth0|64f34db69140728f977c0784', 100001, 'administrador', 'Juan', 'Perez', 'juan.perez@example.com'),
    ('auth0|a564440cb48ca4dda983a73e', 100002, 'docente', 'Ana', 'Gomez', 'ana.gomez@example.com'),
    ('auth0|10fd4a208f65a290017b4d2a', 100003, 'docente', 'Pedro', 'Rodriguez', 'pedro.rodriguez@example.com')
;

INSERT INTO carrera (nombre) VALUES ('Licenciatura en Sistemas de Información');

INSERT INTO sede (/* id, */ nombre, comision_Desde, comision_Hasta) VALUES
    (/* 1, */ 'Luján', 1, 5),
    (/* 2, */ 'Chivilcoy', 6, 10)
;

INSERT INTO alumno (legajo, dni, apellido, nombre, email) VALUES
    (193961, 44380431, 'LEDESMA ', 'GERONIMO OSCAR', 'geroledesma.gl2018@gmail.com'),
	(189433, 45308789, 'LEONE ', 'JUAN PABLO', 'juampi.leone2004@gmail.com'),
	(189458, 45521140, 'LUCERO ', 'MELANIE YASMIN', 'melanielucero745@gmail.com'),
	(143052, 40455863, 'LULICH ', 'AGUSTINA MICAELA', 'agustinalulich@gmail.com'),
	(171791, 42342615, 'MACEDONE ', 'FABRIZIO LIONEL', 'fabrimacedone@gmail.com'),
	(88653, 32101754, 'MAGDALENA ', 'JUAN MANUEL', 'JUANMAGDALENA4@HOTMAIL.COM'),
	(186093, 43970618, 'MAIOLA ', 'LUIS ALBERTO', 'Luismaiola8@gmail.com'),
	(181558, 44561272, 'MARCHIO ', 'BRISA ANABEL', 'brissaanabelmarchio2002@gmail.com'),
	(189442, 44335123, 'MARECO ', 'LOURDES BELEN', 'lourdesmareco12345@gmail.com'),
	(189431, 44961603, 'MARTINEZ ', 'VALENTIN MARIANO', 'valentinmm03@gmail.com'),
	(175433, 43976582, 'MC ', 'CARTHY PEREZ IAN ROY', 'ianmccarthy007@gmail.com'),
	(172891, 42940579, 'MENA ', 'CARLOS IGNACIO', 'carlosmena15@hotmail.com'),
	(101261, 32850245, 'MIROGLIO ', 'EMMANUEL IGNACIO', 'emmanuelmiroglio@gmail.com'),
	(189432, 45176397, 'MOLINA ', 'GASCA IVAN', 'ivan.molinagasca@gmail.com'),
	(181597, 43399663, 'MONTIEL ', 'AYLEN ARIANA', 'montielaylen2@gmail.com'),
	(189413, 45399528, 'MONTOYA ', 'GONZALO NICOLAS', 'gonzalomontoya222@gmail.com'),
	(189436, 45538657, 'MORALES ', 'FACUNDO JULIAN ROBER', 'facujulian2004@gmail.com'),
	(189440, 45906491, 'MUÑOZ ', 'LUJAN', 'munozlujan2004@gmail.com'),
	(189416, 43513293, 'NEIRO ', 'ENZO ALEJANDRO', 'Enzoneiro@gmail.com'),
	(189420, 45494041, 'OCAMPO ', 'BIANCHI LUCAS', 'lucas.o.bianchi@gmail.com'),
	(170774, 37221453, 'OLIVEIRA ', 'DANIEL ADRIAN', 'danieladrianoliveira@gmail.com'),
	(175432, 43356928, 'OLIVEIRA ', 'EVELYN DANIELA', 'dani.eve723@gmail.com'),
	(181505, 44110722, 'ORDOQUI ', 'VALENTIN GABRIEL', 'valentin7121928@outlook.com'),
	(112022, 29623915, 'PALOMERO ', 'JUAN MARTIN', 'PALOMERO361@GMAIL.COM'),
	(169991, 42586215, 'PEREZ ', 'GIUSSANI JUAN FRANCISC', 'jfperez291@gmail.com'),
	(191977, 45296234, 'POLIZZI ', 'JUAN BAUTISTA', 'juan.b.polizzi0803@gmail.com'),
	(89885, 31616122, 'QUISPE ', 'ANDRES', 'Orianaailen1504@gmail.com'),
	(188947, 36096315, 'RAMIREZ ', 'LAURA LORENA', 'LAURALORENARAMIREZ2@GMAIL.COM'),
	(189443, 44961513, 'REINOSO ', 'FLORENCIA', 'flor.reinoso1117@gmail.com'),
	(193590, 44004806, 'RINALDI ', 'AGUSTIN URIEL', 'urielagustin@outlook.com'),
	(191976, 44891015, 'RODEIRO ', 'AIZCORBE JOAQUIN MAN', 'joaquinrodeiro@gmail.com'),
	(181567, 44240978, 'RODRIGUEZ ', 'MAYRA ANABELA', 'may290802@gmail.com'),
	(189429, 45606381, 'ROSSI ', 'AUGUSTO JAVIER', 'augustorossi2004@gmail.com'),
	(189452, 39109678, 'SALVA ', 'DANIEL FERNANDO', 'Daniel11_fer@hotmail.com'),
	(190086, 40092483, 'SAULINO ', 'GONZALO', 'gonzasaulino1@gmail.com'),
	(189424, 45203238, 'SCURATI ', 'FRANCISCO', 'franscurati@gmail.com'),
	(181452, 43970641, 'SCURINI ', 'CLEFOR MATIAS', 'matyscurini@gmail.com'),
	(191694, 45480918, 'SILVA ', 'MARTIN JULIAN FRANCISC', 'juliansm@live.com'),
	(137994, 94855836, 'SOLIS ', 'TORRES JHOSELIN DEYANI', 'Jhosedeyanira@gmail.com'),
	(193042, 44689495, 'SOSA ', 'BRANDON MAXIMILIANO', 'sosabrandon554@gmail.com'),
	(191253, 45749492, 'SOSA ', 'MELANY AGUSTINA', 'melanysosa413@gmail.com'),
	(189427, 45687607, 'SQUILLACE ', 'SANGUINETTI LAUTAR', 'silvinasanguinetti2018@gmail.com'),
	(175370, 44163155, 'SUSKI ', 'PILAR', 'pilarsuski@outlook.com'),
	(189426, 45676946, 'TORCHIA ', 'MAXIMILIANO', 'torchia.mx@gmail.com'),
	(189422, 45521187, 'URRIZA ', 'YAZMIN LORENA', 'Yazminurriza20@gmail.com'),
	(178183, 40861078, 'VALENZUELA ', 'LAUTARO CRISTIAN', 'lautarocristianvalenzuela@gmail.com'),
	(144630, 33909284, 'VALENZUELA ', 'SANTIAGO RAUL', 'santiagovalenzuela88@gmail.com'),
	(189449, 45287246, 'VARGAS ', 'MARIA CAROLINA', 'vargasmariacarolina05@gmail.com'),
	(181524, 44207076, 'VAZQUEZ ', 'PABLO LEONEL', 'Pv3398390@gmail.com'),
	(189421, 45744069, 'VELIZ ', 'FLORENCIA BELEN', 'florveliz1126@gmail.com'),
	(189454, 44110828, 'VERDEJO ', 'MATEO JOSE', 'mateverdejo25@gmail.com'),
	(156129, 40184383, 'VIDELA ', 'MEZA GONZALO JESUS', 'Gonzalito_96@live.com'),
	(166364, 94551596, 'VILLARROEL ', 'GALARZA HEIDY', 'heidyvg580@gmail.com'),
	(176731, 43176162, 'VIÑES ', 'GUADALUPE', 'guadavies@yahoo.com.ar'),
	(189438, 45222533, 'YAUCK ', 'ANDRES NICOLAS', 'patogaturro859@gmail.com'),
	(191258, 44519126, 'ZALACAIN ', 'MATIAS', 'matiaszalacain91@gmail.com'),
	(143305, 93993728, 'ZAVALETA ', 'ALFARO ELIAS CESAR', 'eliaszavaleta42@gmail.com')
;

INSERT INTO asignatura (/* id, */ id_Carrera, nombre) VALUES
    (/* 1, */ 1, 'Seminario de Integración Profesional'),
    (/* 2, */ 1, 'Sistemas Distribuidos y Programación Paralela'),
    (/* 3, */ 1, 'Programación en Ambiente Web')
;

INSERT INTO comision (/* id, */ id_Asignatura, numero) VALUES
    (/* 1, */ 1, 1),
    (/* 2, */ 1, 2),
    (/* 3, */ 1, 3),
    (/* 4, */ 2, 1),
    (/* 5, */ 2, 2),
    (/* 6, */ 2, 3),
    (/* 7, */ 3, 1),
    (/* 8, */ 3, 2)
;

INSERT INTO cursada (/* id, */ id_Comision, anio, fecha_Inicio, fecha_Fin) VALUES
    (/* 1, */ 1, 2021, '2021-03-15', '2021-07-02'),
    (/* 2, */ 2, 2021, '2021-03-15', '2021-07-02')
;

INSERT INTO criterio_cursada (/* id, */ id_Criterio, id_Cursada, valor_Regular, valor_Promovido) VALUES
    (/* 1, */ 1, 1, 50, 80),
    (/* 2, */ 2, 2, 50, 75)
;

INSERT INTO cursada_docente (/* id, */ id_Cursada, id_Docente, nivel_Permiso) VALUES
    (/* 1, */ 1, 'auth0|a564440cb48ca4dda983a73e', 1),
    (/* 2, */ 2, 'auth0|a564440cb48ca4dda983a73e', 1),
    (/* 3, */ 2, 'auth0|10fd4a208f65a290017b4d2a', 2)
;

INSERT INTO evento_cursada (/* id, */ id_Tipo, id_Cursada, obligatorio, fecha_Hora_Inicio, fecha_Hora_Fin) VALUES
    (/* 1, */ 1, 1, false, '2021-03-25 10:00:00.000000', '2021-03-25 12:00:00.000000'),
    (/* 2, */ 2, 1, true, '2021-03-29 15:00:00.000000', '2021-03-29 17:00:00.000000')
;

INSERT INTO evento_cursada_alumno (/* id, */ id_Evento, id_Alumno, asistencia, nota) VALUES
    (/* 1, */ 1, 150001, true, '9'),
    (/* 2, */ 1, 150002, false, '2')
;