-- CourseController.get-events(2).sql --


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

INSERT INTO criterio_evaluacion (nombre) VALUES
    ('Asistencias')
    ,('Trabajos prácticos aprobados')
    ,('Trabajos prácticos recuperados')
    ,('Parciales aprobados')
    ,('Promedio de parciales')
    ,('Autoevaluaciones aprobadas')
    ,('Autoevaluaciones recuperadas')
;

INSERT INTO tipo_evento (nombre) VALUES
    ('Clase')
    ,('Trabajo práctico')
    ,('Parcial')
    ,('Autoevaluación')
    ,('Recuperatorio Trabajo práctico')
    ,('Recuperatorio Parcial')
    ,('Recuperatorio Autoevaluación')
    ,('Integrador')
;

INSERT INTO usuario (id, legajo, rol, nombre, apellido, email) VALUES
    ('auth0|64f34db69140728f977c0784', 100001, 'administrador', 'Juan', 'Perez', 'juan.perez@example.com')
    ,('auth0|a564440cb48ca4dda983a73e', 100002, 'docente', 'Ana', 'Gomez', 'ana.gomez@example.com')
    ,('auth0|64b40e41f7a0dfd00ea3c4df', 100003, 'docente', 'Facundo', 'Laffont', 'facu-docente@fake.com')
;

INSERT INTO carrera (nombre) VALUES
    ('Licenciatura en Sistemas de Información')
;

INSERT INTO sede (nombre, comision_Desde, comision_Hasta) VALUES
    ('Luján', 1, 5)
    ,('Chivilcoy', 6, 10)
;

INSERT INTO alumno (legajo, dni, nombre, apellido, email) VALUES
    (150001, 32165498, 'Pedro', 'Alfonso', 'pedro.alfonso@example.com')
    ,(150002, 33698547, 'Lucía', 'Fernández', 'lucia.fernandez@example.com')
    ,(150003, 36258741, 'Carlos', 'González', 'carlos.gonzalez@example.com')
    ,(150004, 39657412, 'Federico', 'Ramírez', 'federico.ramirez@example.com')
    ,(193961, 44380431, 'GERONIMO OSCAR', 'LEDESMA ', 'geroledesma.gl2018@gmail.com')
	,(189433, 45308789, 'JUAN PABLO', 'LEONE ', 'juampi.leone2004@gmail.com')
	,(189458, 45521140, 'MELANIE YASMIN', 'LUCERO ', 'melanielucero745@gmail.com')
	,(143052, 40455863, 'AGUSTINA MICAELA', 'LULICH ', 'agustinalulich@gmail.com')
	,(171791, 42342615, 'FABRIZIO LIONEL', 'MACEDONE ', 'fabrimacedone@gmail.com')
	,(88653, 32101754, 'JUAN MANUEL', 'MAGDALENA ', 'JUANMAGDALENA4@HOTMAIL.COM')
	,(186093, 43970618, 'LUIS ALBERTO', 'MAIOLA ', 'Luismaiola8@gmail.com')
	,(181558, 44561272, 'BRISA ANABEL', 'MARCHIO ', 'brissaanabelmarchio2002@gmail.com')
	,(189442, 44335123, 'LOURDES BELEN', 'MARECO ', 'lourdesmareco12345@gmail.com')
	,(189431, 44961603, 'VALENTIN MARIANO', 'MARTINEZ ', 'valentinmm03@gmail.com')
	,(175433, 43976582, 'CARTHY PEREZ IAN ROY', 'MC ', 'ianmccarthy007@gmail.com')
	,(172891, 42940579, 'CARLOS IGNACIO', 'MENA ', 'carlosmena15@hotmail.com')
	,(101261, 32850245, 'EMMANUEL IGNACIO', 'MIROGLIO ', 'emmanuelmiroglio@gmail.com')
	,(189432, 45176397, 'GASCA IVAN', 'MOLINA ', 'ivan.molinagasca@gmail.com')
	,(181597, 43399663, 'AYLEN ARIANA', 'MONTIEL ', 'montielaylen2@gmail.com')
	,(189413, 45399528, 'GONZALO NICOLAS', 'MONTOYA ', 'gonzalomontoya222@gmail.com')
	,(189436, 45538657, 'FACUNDO JULIAN ROBER', 'MORALES ', 'facujulian2004@gmail.com')
	,(189440, 45906491, 'LUJAN', 'MUÑOZ ', 'munozlujan2004@gmail.com')
	,(189416, 43513293, 'ENZO ALEJANDRO', 'NEIRO ', 'Enzoneiro@gmail.com')
	,(189420, 45494041, 'BIANCHI LUCAS', 'OCAMPO ', 'lucas.o.bianchi@gmail.com')
	,(170774, 37221453, 'DANIEL ADRIAN', 'OLIVEIRA ', 'danieladrianoliveira@gmail.com')
	,(175432, 43356928, 'EVELYN DANIELA', 'OLIVEIRA ', 'dani.eve723@gmail.com')
	,(181505, 44110722, 'VALENTIN GABRIEL', 'ORDOQUI ', 'valentin7121928@outlook.com')
	,(112022, 29623915, 'JUAN MARTIN', 'PALOMERO ', 'PALOMERO361@GMAIL.COM')
	,(169991, 42586215, 'GIUSSANI JUAN FRANCISC', 'PEREZ ', 'jfperez291@gmail.com')
	,(191977, 45296234, 'JUAN BAUTISTA', 'POLIZZI ', 'juan.b.polizzi0803@gmail.com')
	,(89885, 31616122, 'ANDRES', 'QUISPE ', 'Orianaailen1504@gmail.com')
	,(188947, 36096315, 'LAURA LORENA', 'RAMIREZ ', 'LAURALORENARAMIREZ2@GMAIL.COM')
	,(189443, 44961513, 'FLORENCIA', 'REINOSO ', 'flor.reinoso1117@gmail.com')
	,(193590, 44004806, 'AGUSTIN URIEL', 'RINALDI ', 'urielagustin@outlook.com')
	,(191976, 44891015, 'AIZCORBE JOAQUIN MAN', 'RODEIRO ', 'joaquinrodeiro@gmail.com')
	,(181567, 44240978, 'MAYRA ANABELA', 'RODRIGUEZ ', 'may290802@gmail.com')
	,(189429, 45606381, 'AUGUSTO JAVIER', 'ROSSI ', 'augustorossi2004@gmail.com')
	,(189452, 39109678, 'DANIEL FERNANDO', 'SALVA ', 'Daniel11_fer@hotmail.com')
	,(190086, 40092483, 'GONZALO', 'SAULINO ', 'gonzasaulino1@gmail.com')
	,(189424, 45203238, 'FRANCISCO', 'SCURATI ', 'franscurati@gmail.com')
	,(181452, 43970641, 'CLEFOR MATIAS', 'SCURINI ', 'matyscurini@gmail.com')
	,(191694, 45480918, 'MARTIN JULIAN FRANCISC', 'SILVA ', 'juliansm@live.com')
	,(137994, 94855836, 'TORRES JHOSELIN DEYANI', 'SOLIS ', 'Jhosedeyanira@gmail.com')
	,(193042, 44689495, 'BRANDON MAXIMILIANO', 'SOSA ', 'sosabrandon554@gmail.com')
	,(191253, 45749492, 'MELANY AGUSTINA', 'SOSA ', 'melanysosa413@gmail.com')
	,(189427, 45687607, 'SANGUINETTI LAUTAR', 'SQUILLACE ', 'silvinasanguinetti2018@gmail.com')
	,(175370, 44163155, 'PILAR', 'SUSKI ', 'pilarsuski@outlook.com')
	,(189426, 45676946, 'MAXIMILIANO', 'TORCHIA ', 'torchia.mx@gmail.com')
	,(189422, 45521187, 'YAZMIN LORENA', 'URRIZA ', 'Yazminurriza20@gmail.com')
	,(178183, 40861078, 'LAUTARO CRISTIAN', 'VALENZUELA ', 'lautarocristianvalenzuela@gmail.com')
	,(144630, 33909284, 'SANTIAGO RAUL', 'VALENZUELA ', 'santiagovalenzuela88@gmail.com')
	,(189449, 45287246, 'MARIA CAROLINA', 'VARGAS ', 'vargasmariacarolina05@gmail.com')
	,(181524, 44207076, 'PABLO LEONEL', 'VAZQUEZ ', 'Pv3398390@gmail.com')
	,(189421, 45744069, 'FLORENCIA BELEN', 'VELIZ ', 'florveliz1126@gmail.com')
	,(189454, 44110828, 'MATEO JOSE', 'VERDEJO ', 'mateverdejo25@gmail.com')
	,(156129, 40184383, 'MEZA GONZALO JESUS', 'VIDELA ', 'Gonzalito_96@live.com')
	,(166364, 94551596, 'GALARZA HEIDY', 'VILLARROEL ', 'heidyvg580@gmail.com')
	,(176731, 43176162, 'GUADALUPE', 'VIÑES ', 'guadavies@yahoo.com.ar')
	,(189438, 45222533, 'ANDRES NICOLAS', 'YAUCK ', 'patogaturro859@gmail.com')
	,(191258, 44519126, 'MATIAS', 'ZALACAIN ', 'matiaszalacain91@gmail.com')
	,(143305, 93993728, 'ALFARO ELIAS CESAR', 'ZAVALETA ', 'eliaszavaleta42@gmail.com')
;

INSERT INTO asignatura (id_Carrera, nombre) VALUES
    (1, 'Seminario de Integración Profesional')
    ,(1, 'Sistemas Distribuidos y Programación Paralela')
    ,(1, 'Programación en Ambiente Web')
;

INSERT INTO comision (id_Asignatura, numero) VALUES
    (1, 1)
    ,(1, 2)
    ,(1, 3)
    ,(2, 1)
    ,(2, 2)
    ,(2, 3)
    ,(3, 1)
    ,(3, 2)
;

INSERT INTO cursada (id_Comision, anio, fecha_Inicio, fecha_Fin) VALUES
    (1, 2021, '2021-03-15', '2021-07-02')
    ,(2, 2021, '2021-03-15', '2021-07-02')
;

INSERT INTO criterio_cursada (id_Criterio, id_Cursada, valor_Regular, valor_Promovido) VALUES
    (1, 1, 50, 80)
    ,(2, 1, 50, 75)
    ,(3, 1, 50, 75)
    ,(4, 1, 50, 75)
    ,(5, 1, 50, 75)
    ,(6, 1, 50, 75)
    ,(7, 1, 50, 75)
    ,(2, 2, 50, 75)
;

INSERT INTO cursada_docente (id_Cursada, id_Docente, nivel_Permiso) VALUES
    (1, 'auth0|a564440cb48ca4dda983a73e', 1)
    ,(2, 'auth0|a564440cb48ca4dda983a73e', 1)
;

INSERT INTO cursada_alumno (id_Cursada, id_Alumno, previous_subjects_approved, studied_previously, condicion_Final) VALUES
    /*
    (1, 150001, false, true, 'Regular')
    ,(1, 150002, true, false, 'Promovido')
    ,(1, 150003, false, true, 'Ausente')
    */
    (1, 150001, false, true, null)
    ,(1, 150002, true, false, null)
    ,(1, 150003, false, true, null)
    ,(1, 150004, true, true, null)
    ,(1, 143305, true, true, null)
    ,(1, 191258, true, true, null)
    ,(1, 189438, true, true, null)
    ,(1, 176731, true, true, null)
    ,(1, 166364, true, true, null)
    ,(1, 156129, true, true, null)
    ,(1, 189454, true, true, null)
    ,(1, 189421, true, true, null)
;

INSERT INTO evento_cursada (id_Tipo, id_Cursada, obligatorio, fecha_Hora_Inicio, fecha_Hora_Fin) VALUES

    -- ID de cursada: 1.

        -- Tipo de evento: clase.

            (1, 1, true,    '2021-03-01 10:00:00.000000', '2021-03-01 12:00:00.000000')
            ,(1, 1, true,   '2021-03-02 10:00:00.000000', '2021-03-02 12:00:00.000000')
            ,(1, 1, true,   '2021-03-03 10:00:00.000000', '2021-03-03 12:00:00.000000')
            ,(1, 1, true,   '2021-03-04 10:00:00.000000', '2021-03-04 12:00:00.000000')
            ,(1, 1, true,   '2021-03-05 10:00:00.000000', '2021-03-05 12:00:00.000000')
            ,(1, 1, false,  '2021-03-06 10:00:00.000000', '2021-03-06 12:00:00.000000')
            ,(1, 1, true,   '2021-03-07 10:00:00.000000', '2021-03-07 12:00:00.000000')
            ,(1, 1, true,   '2021-03-08 10:00:00.000000', '2021-03-08 12:00:00.000000')
            ,(1, 1, true,   '2021-03-09 10:00:00.000000', '2021-03-09 12:00:00.000000')
            ,(1, 1, false,   '2021-03-10 10:00:00.000000', '2021-03-10 12:00:00.000000')

        -- Tipo de evento: Trabajo práctico.

            ,(2, 1, true, '2021-04-01 10:00:00.000000', '2021-04-01 12:00:00.000000')
            ,(2, 1, true, '2021-04-02 10:00:00.000000', '2021-04-02 12:00:00.000000')
            ,(2, 1, true, '2021-04-03 10:00:00.000000', '2021-04-03 12:00:00.000000')
            ,(2, 1, true, '2021-04-04 10:00:00.000000', '2021-04-04 12:00:00.000000')

        -- Tipo de evento: Parcial.

            ,(3, 1, true, '2021-05-01 10:00:00.000000', '2021-05-01 12:00:00.000000')
            ,(3, 1, true, '2021-05-02 10:00:00.000000', '2021-05-02 12:00:00.000000')

        -- Tipo de evento: Autoevaluación.

            ,(4, 1, false, '2021-06-01 10:00:00.000000', '2021-06-01 12:00:00.000000')
            ,(4, 1, false, '2021-06-02 10:00:00.000000', '2021-06-02 12:00:00.000000')
            ,(4, 1, false, '2021-06-03 10:00:00.000000', '2021-06-03 12:00:00.000000')
            ,(4, 1, false, '2021-06-04 10:00:00.000000', '2021-06-04 12:00:00.000000')
            ,(4, 1, false, '2021-06-05 10:00:00.000000', '2021-06-05 12:00:00.000000')
            ,(4, 1, false, '2021-06-06 10:00:00.000000', '2021-06-06 12:00:00.000000')

        -- Tipo de evento: Recuperatorio trabajo práctico.

            ,(5, 1, true, '2021-07-01 10:00:00.000000', '2021-07-01 12:00:00.000000')
            ,(5, 1, true, '2021-07-02 10:00:00.000000', '2021-07-02 12:00:00.000000')

        -- Tipo de evento: Recuperatorio parcial.

            ,(6, 1, true, '2021-08-01 10:00:00.000000', '2021-08-01 12:00:00.000000')

        -- Tipo de evento: Recuperatorio autoevaluación.

            ,(7, 1, false, '2021-09-01 10:00:00.000000', '2021-09-01 12:00:00.000000')
            ,(7, 1, false, '2021-09-02 10:00:00.000000', '2021-09-02 12:00:00.000000')
            ,(7, 1, false, '2021-09-03 10:00:00.000000', '2021-09-03 12:00:00.000000')

        -- Tipo de evento: Integrador.

            ,(8, 1, true, '2021-10-01 10:00:00.000000', '2021-10-01 12:00:00.000000')

;

INSERT INTO evento_cursada_alumno (id_Evento, id_Alumno, asistencia, nota) VALUES

    -- ID de cursada: 1.

        -- Tipo de evento: clase.

            -- id_evento: 1
            --(1, 1, true,    '2021-03-01 10:00:00.000000', '2021-03-01 12:00:00.000000')

                (1, 150001, false, null)
                ,(1, 150002, true, null)
                ,(1, 150003, true, null)
                ,(1, 150004, true, null)
                ,(1, 143305, false, null)
                ,(1, 191258, true, null)
                ,(1, 189438, true, null)
                ,(1, 176731, true, null)
                ,(1, 166364, true, null)
                ,(1, 156129, true, null)
                ,(1, 189454, true, null)
                ,(1, 189421, true, null)

            -- id_evento: 2
            --,(1, 1, true,   '2021-03-02 10:00:00.000000', '2021-03-02 12:00:00.000000')

                ,(2, 150001, false, null)
                ,(2, 150002, true, null)
                ,(2, 150003, true, null)
                ,(2, 150004, true, null)
                ,(2, 143305, false, null)
                ,(2, 191258, true, null)
                ,(2, 189438, false, null)
                ,(2, 176731, true, null)
                ,(2, 166364, true, null)
                ,(2, 156129, true, null)
                ,(2, 189454, true, null)
                ,(2, 189421, true, null)

            -- id_evento: 3
            --,(1, 1, true,   '2021-03-03 10:00:00.000000', '2021-03-03 12:00:00.000000')

                ,(3, 150001, false, null)
                ,(3, 150002, true, null)
                ,(3, 150003, true, null)
                ,(3, 150004, true, null)
                ,(3, 143305, true, null)
                ,(3, 191258, true, null)
                ,(3, 189438, true, null)
                ,(3, 176731, true, null)
                ,(3, 166364, true, null)
                ,(3, 156129, true, null)
                ,(3, 189454, true, null)
                ,(3, 189421, false, null)

            -- id_evento: 4
            --,(1, 1, true,   '2021-03-04 10:00:00.000000', '2021-03-04 12:00:00.000000')

                ,(4, 150001, false, null)
                ,(4, 150002, true, null)
                ,(4, 150003, true, null)
                ,(4, 150004, true, null)
                ,(4, 143305, true, null)
                ,(4, 191258, true, null)
                ,(4, 189438, true, null)
                ,(4, 176731, true, null)
                ,(4, 166364, true, null)
                ,(4, 156129, true, null)
                ,(4, 189454, true, null)
                ,(4, 189421, false, null)

            -- id_evento: 5
            --,(1, 1, true,   '2021-03-05 10:00:00.000000', '2021-03-05 12:00:00.000000')

                ,(5, 150001, false, null)
                ,(5, 150002, false, null)
                ,(5, 150003, true, null)
                ,(5, 150004, true, null)
                ,(5, 143305, true, null)
                ,(5, 191258, true, null)
                ,(5, 189438, true, null)
                ,(5, 176731, true, null)
                ,(5, 166364, true, null)
                ,(5, 156129, true, null)
                ,(5, 189454, true, null)
                ,(5, 189421, false, null)

            -- id_evento: 6
            --,(1, 1, false,  '2021-03-06 10:00:00.000000', '2021-03-06 12:00:00.000000')

                ,(6, 150001, false, null)
                ,(6, 150002, true, null)
                ,(6, 150003, true, null)
                ,(6, 150004, true, null)
                ,(6, 143305, true, null)
                ,(6, 191258, true, null)
                ,(6, 189438, true, null)
                ,(6, 176731, true, null)
                ,(6, 166364, true, null)
                ,(6, 156129, true, null)
                ,(6, 189454, true, null)
                ,(6, 189421, false, null)

            -- id_evento: 7
            --,(1, 1, true,   '2021-03-07 10:00:00.000000', '2021-03-07 12:00:00.000000')

                ,(7, 150001, false, null)
                ,(7, 150002, false, null)
                ,(7, 150003, false, null)
                ,(7, 150004, true, null)
                ,(7, 143305, true, null)
                ,(7, 191258, true, null)
                ,(7, 189438, true, null)
                ,(7, 176731, true, null)
                ,(7, 166364, true, null)
                ,(7, 156129, true, null)
                ,(7, 189454, true, null)
                ,(7, 189421, false, null)

            -- id_evento: 8
            --,(1, 1, true,   '2021-03-08 10:00:00.000000', '2021-03-08 12:00:00.000000')

                ,(8, 150001, false, null)
                ,(8, 150002, false, null)
                ,(8, 150003, false, null)
                ,(8, 150004, true, null)
                ,(8, 143305, true, null)
                ,(8, 191258, true, null)
                ,(8, 189438, true, null)
                ,(8, 176731, true, null)
                ,(8, 166364, true, null)
                ,(8, 156129, true, null)
                ,(8, 189454, true, null)
                ,(8, 189421, false, null)

            -- id_evento: 9
            --,(1, 1, true,   '2021-03-09 10:00:00.000000', '2021-03-09 12:00:00.000000')

                ,(9, 150001, true, null)
                ,(9, 150002, false, null)
                ,(9, 150003, false, null)
                ,(9, 150004, true, null)
                ,(9, 143305, true, null)
                ,(9, 191258, true, null)
                ,(9, 189438, true, null)
                ,(9, 176731, true, null)
                ,(9, 166364, true, null)
                ,(9, 156129, true, null)
                ,(9, 189454, true, null)
                ,(9, 189421, false, null)

            -- id_evento: 10
            --,(1, 1, false,   '2021-03-10 10:00:00.000000', '2021-03-10 12:00:00.000000')

                ,(10, 150001, true, null)
                ,(10, 150002, true, null)
                ,(10, 150003, true, null)
                ,(10, 150004, true, null)
                ,(10, 143305, false, null)
                ,(10, 191258, true, null)
                ,(10, 189438, true, null)
                ,(10, 176731, true, null)
                ,(10, 166364, true, null)
                ,(10, 156129, true, null)
                ,(10, 189454, true, null)
                ,(10, 189421, false, null)

        -- Tipo de evento: Trabajo práctico.

            -- id_evento: 11
            --,(2, 1, true, '2021-04-01 10:00:00.000000', '2021-04-01 12:00:00.000000')

                ,(11, 150001, true, 4)
                ,(11, 150002, true, 9)
                ,(11, 150003, true, 8)
                ,(11, 150004, true, 7)
                ,(11, 143305, true, 6)
                ,(11, 191258, true, 5)
                ,(11, 189438, true, 4)
                ,(11, 176731, true, 3)
                ,(11, 166364, true, 2)
                ,(11, 156129, true, 1)
                ,(11, 189454, true, 10)
                ,(11, 189421, true, 'A')

            -- id_evento: 12
            --,(2, 1, true, '2021-04-02 10:00:00.000000', '2021-04-02 12:00:00.000000')

                ,(12, 150001, true, 2)
                ,(12, 150002, true, 8)
                ,(12, 150003, true, 8)
                ,(12, 150004, true, 7)
                ,(12, 143305, true, 6)
                ,(12, 191258, true, 5)
                ,(12, 189438, true, 4)
                ,(12, 176731, true, 3)
                ,(12, 166364, true, 2)
                ,(12, 156129, true, 1)
                ,(12, 189454, true, 10)
                ,(12, 189421, true, 'A-')

            -- id_evento: 13
            --,(2, 1, true, '2021-04-03 10:00:00.000000', '2021-04-03 12:00:00.000000')

                ,(13, 150001, true, 1)
                ,(13, 150002, true, 2)
                ,(13, 150003, true, 8)
                ,(13, 150004, true, 7)
                ,(13, 143305, true, 6)
                ,(13, 191258, true, 5)
                ,(13, 189438, true, 4)
                ,(13, 176731, true, 3)
                ,(13, 166364, true, 2)
                ,(13, 156129, true, 1)
                ,(13, 189454, true, 10)
                ,(13, 189421, true, 'D')

            -- id_evento: 14
            --,(2, 1, true, '2021-04-04 10:00:00.000000', '2021-04-04 12:00:00.000000')

                ,(14, 150001, true, 3)
                ,(14, 150002, true, 3)
                ,(14, 150003, true, 2)
                ,(14, 150004, true, 7)
                ,(14, 143305, true, 6)
                ,(14, 191258, true, 5)
                ,(14, 189438, true, 4)
                ,(14, 176731, true, 3)
                ,(14, 166364, true, 2)
                ,(14, 156129, true, 1)
                ,(14, 189454, true, 10)
                ,(14, 189421, true, 'D')

        -- Tipo de evento: Parcial.

            -- id_evento: 15
            --,(3, 1, true, '2021-05-01 10:00:00.000000', '2021-05-01 12:00:00.000000')

                ,(15, 150001, true, 3)
                ,(15, 150002, true, 9)
                ,(15, 150003, true, 8)
                ,(15, 150004, true, 7)
                ,(15, 143305, true, 4)
                ,(15, 191258, true, 5)
                ,(15, 189438, true, 4)
                ,(15, 176731, true, 3)
                ,(15, 166364, true, 2)
                ,(15, 156129, true, 1)
                ,(15, 189454, true, 10)
                ,(15, 189421, true, 9)

            -- id_evento: 16
            --,(3, 1, true, '2021-05-02 10:00:00.000000', '2021-05-02 12:00:00.000000')

                ,(16, 150001, true, 10)
                ,(16, 150002, true, 9)
                ,(16, 150003, true, 8)
                ,(16, 150004, true, 7)
                ,(16, 143305, true, 7)
                ,(16, 191258, true, 5)
                ,(16, 189438, true, 4)
                ,(16, 176731, true, 3)
                ,(16, 166364, true, 2)
                ,(16, 156129, true, 1)
                ,(16, 189454, true, 10)
                ,(16, 189421, true, 9)

        -- Tipo de evento: Autoevaluación.

            -- id_evento: 17
            --,(4, 1, false, '2021-06-01 10:00:00.000000', '2021-06-01 12:00:00.000000')

                ,(17, 150001, null, 10)
                ,(17, 150002, null, 9)
                ,(17, 150003, null, 8)
                ,(17, 150004, null, 7)
                ,(17, 143305, null, 6)
                ,(17, 191258, null, 5)
                ,(17, 189438, null, 4)
                ,(17, 176731, null, 3)
                ,(17, 166364, null, 2)
                ,(17, 156129, null, 1)
                ,(17, 189454, null, 10)
                ,(17, 189421, null, 9)

            -- id_evento: 18
            --,(4, 1, false, '2021-06-02 10:00:00.000000', '2021-06-02 12:00:00.000000')

                ,(18, 150001, null, 10)
                ,(18, 150002, null, 9)
                ,(18, 150003, null, 8)
                ,(18, 150004, null, 7)
                ,(18, 143305, null, 6)
                ,(18, 191258, null, 5)
                ,(18, 189438, null, 4)
                ,(18, 176731, null, 3)
                ,(18, 166364, null, 2)
                ,(18, 156129, null, 1)
                ,(18, 189454, null, 10)
                ,(18, 189421, null, 9)

            -- id_evento: 19
            --,(4, 1, false, '2021-06-03 10:00:00.000000', '2021-06-03 12:00:00.000000')

                ,(19, 150001, null, 10)
                ,(19, 150002, null, 9)
                ,(19, 150003, null, 8)
                ,(19, 150004, null, 7)
                ,(19, 143305, null, 6)
                ,(19, 191258, null, 5)
                ,(19, 189438, null, 4)
                ,(19, 176731, null, 3)
                ,(19, 166364, null, 2)
                ,(19, 156129, null, 1)
                ,(19, 189454, null, 10)
                ,(19, 189421, null, 9)

            -- id_evento: 20
            --,(4, 1, false, '2021-06-04 10:00:00.000000', '2021-06-04 12:00:00.000000')

                ,(20, 150001, null, 10)
                ,(20, 150002, null, 9)
                ,(20, 150003, null, 8)
                ,(20, 150004, null, 7)
                ,(20, 143305, null, 6)
                ,(20, 191258, null, 5)
                ,(20, 189438, null, 4)
                ,(20, 176731, null, 3)
                ,(20, 166364, null, 2)
                ,(20, 156129, null, 1)
                ,(20, 189454, null, 10)
                ,(20, 189421, null, 9)

            -- id_evento: 21
            --,(4, 1, false, '2021-06-05 10:00:00.000000', '2021-06-05 12:00:00.000000')

                ,(21, 150001, null, 10)
                ,(21, 150002, null, 9)
                ,(21, 150003, null, 8)
                ,(21, 150004, null, 7)
                ,(21, 143305, null, 6)
                ,(21, 191258, null, 5)
                ,(21, 189438, null, 4)
                ,(21, 176731, null, 3)
                ,(21, 166364, null, 2)
                ,(21, 156129, null, 1)
                ,(21, 189454, null, 10)
                ,(21, 189421, null, 9)

            -- id_evento: 22
            --,(4, 1, false, '2021-06-06 10:00:00.000000', '2021-06-06 12:00:00.000000')

                ,(22, 150001, null, 10)
                ,(22, 150002, null, 9)
                ,(22, 150003, null, 8)
                ,(22, 150004, null, 7)
                ,(22, 143305, null, 6)
                ,(22, 191258, null, 5)
                ,(22, 189438, null, 4)
                ,(22, 176731, null, 3)
                ,(22, 166364, null, 2)
                ,(22, 156129, null, 1)
                ,(22, 189454, null, 10)
                ,(22, 189421, null, 9)

        -- Tipo de evento: Recuperatorio trabajo práctico.

            -- id_evento: 23
            --,(5, 1, true, '2021-07-01 10:00:00.000000', '2021-07-01 12:00:00.000000')

                ,(23, 150001, true, 1)
                ,(23, 150002, true, 8)
                ,(23, 150003, true, 8)
                ,(23, 150004, true, 7)
                ,(23, 143305, true, 6)
                ,(23, 191258, true, 5)
                ,(23, 189438, true, 4)
                ,(23, 176731, true, 3)
                ,(23, 166364, true, 2)
                ,(23, 156129, true, 1)
                ,(23, 189454, true, 10)
                ,(23, 189421, true, 9)

            -- id_evento: 24
            --,(5, 1, true, '2021-07-02 10:00:00.000000', '2021-07-02 12:00:00.000000')

                ,(24, 150001, true, 1)
                ,(24, 150002, true, 2)
                ,(24, 150003, true, 8)
                ,(24, 150004, true, 10)
                ,(24, 143305, true, 6)
                ,(24, 191258, true, 5)
                ,(24, 189438, true, 4)
                ,(24, 176731, true, 3)
                ,(24, 166364, true, 2)
                ,(24, 156129, true, 1)
                ,(24, 189454, true, 10)
                ,(24, 189421, true, 9)

        -- Tipo de evento: Recuperatorio parcial.

            -- id_evento: 25
            --,(6, 1, true, '2021-08-01 10:00:00.000000', '2021-08-01 12:00:00.000000')

                ,(25, 150001, true, 10)
                ,(25, 150002, true, 9)
                ,(25, 150003, true, 8)
                ,(25, 150004, true, 7)
                ,(25, 143305, true, 6)
                ,(25, 191258, true, 5)
                ,(25, 189438, true, 4)
                ,(25, 176731, true, 3)
                ,(25, 166364, true, 2)
                ,(25, 156129, true, 1)
                ,(25, 189454, true, 10)
                ,(25, 189421, true, 9)

        -- Tipo de evento: Recuperatorio autoevaluación.

            -- id_evento: 26
            --,(7, 1, false, '2021-09-01 10:00:00.000000', '2021-09-01 12:00:00.000000')

                ,(26, 150001, null, 10)
                ,(26, 150002, null, 9)
                ,(26, 150003, null, 8)
                ,(26, 150004, null, 7)
                ,(26, 143305, null, 6)
                ,(26, 191258, null, 5)
                ,(26, 189438, null, 4)
                ,(26, 176731, null, 3)
                ,(26, 166364, null, 2)
                ,(26, 156129, null, 1)
                ,(26, 189454, null, 10)
                ,(26, 189421, null, 9)

            -- id_evento: 27
            --,(7, 1, false, '2021-09-02 10:00:00.000000', '2021-09-02 12:00:00.000000')

                ,(27, 150001, null, 10)
                ,(27, 150002, null, 9)
                ,(27, 150003, null, 8)
                ,(27, 150004, null, 7)
                ,(27, 143305, null, 6)
                ,(27, 191258, null, 5)
                ,(27, 189438, null, 4)
                ,(27, 176731, null, 3)
                ,(27, 166364, null, 2)
                ,(27, 156129, null, 1)
                ,(27, 189454, null, 10)
                ,(27, 189421, null, 9)

            -- id_evento: 28
            --,(7, 1, false, '2021-09-03 10:00:00.000000', '2021-09-03 12:00:00.000000')

                ,(28, 150001, null, 10)
                ,(28, 150002, null, 9)
                ,(28, 150003, null, 8)
                ,(28, 150004, null, 7)
                ,(28, 143305, null, 6)
                ,(28, 191258, null, 5)
                ,(28, 189438, null, 4)
                ,(28, 176731, null, 3)
                ,(28, 166364, null, 2)
                ,(28, 156129, null, 1)
                ,(28, 189454, null, 10)
                ,(28, 189421, null, 9)

        -- Tipo de evento: Integrador.

            -- id_evento: 29
            --,(8, 1, true, '2021-10-01 10:00:00.000000', '2021-10-01 12:00:00.000000')

                ,(29, 150001, true, 10)
                ,(29, 150002, true, 9)
                ,(29, 150003, true, 8)
                ,(29, 150004, true, 7)
                ,(29, 143305, true, 6)
                ,(29, 191258, true, 5)
                ,(29, 189438, true, 4)
                ,(29, 176731, true, 3)
                ,(29, 166364, true, 2)
                ,(29, 156129, true, 1)
                ,(29, 189454, true, 10)
                ,(29, 189421, true, 9)

;
