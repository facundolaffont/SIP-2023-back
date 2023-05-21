/* ****************** *
 * Creación de tablas *
 * ****************** */

CREATE TABLE usuario (
    id VARCHAR(64) NOT NULL,
    legajo INTEGER NOT NULL,
    rol VARCHAR(32) NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    apellido VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_legajo_rol_usuario UNIQUE (legajo, rol)
);

CREATE TABLE carrera (
    id SERIAL NOT NULL,
    nombre VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE sede (
    id SERIAL NOT NULL,
    nombre VARCHAR(32) NOT NULL,
    comisionDesde INTEGER NOT NULL,
    comisionHasta INTEGER NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE alumno (
    legajo INTEGER NOT NULL,
    dni INTEGER NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    apellido VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL,
    PRIMARY KEY (legajo),
    CONSTRAINT uk_dni_alumno UNIQUE (dni)
);

CREATE TABLE asignatura (
    id SERIAL NOT NULL,
    idCarrera INTEGER NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_asignatura_carrera FOREIGN KEY (idCarrera) REFERENCES carrera(id)
);

CREATE TABLE comision (
    id SERIAL NOT NULL,
    idAsignatura INTEGER NOT NULL,
    numero INTEGER NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_comision_asignatura FOREIGN KEY (idAsignatura) REFERENCES asignatura(id),
    CONSTRAINT uk_asignatura_numero_comision UNIQUE (idAsignatura, numero)
);

CREATE TABLE cursada (
    id SERIAL NOT NULL,
    idComision INTEGER NOT NULL,
    anio INTEGER NOT NULL,
    fechaInicio DATE,
    fechaFin DATE,
    PRIMARY KEY (id),
    CONSTRAINT fk_cursada_comision FOREIGN KEY (idComision) REFERENCES comision(id),
    CONSTRAINT uk_comision_anio_cursada UNIQUE (idComision, anio)
);

CREATE TABLE criterio_evaluacion (
    id SERIAL NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE criterio_cursada (
    id SERIAL NOT NULL,
    idCriterio INTEGER NOT NULL,
    idCursada INTEGER NOT NULL,
    valorRegular INTEGER NOT NULL,
    valorPromovido INTEGER NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_criterio_cursada_cri FOREIGN KEY (idCriterio) REFERENCES criterio_evaluacion(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_criterio_cursada_cur FOREIGN KEY (idCursada) REFERENCES cursada(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uk_criterio_cursada UNIQUE (idCriterio, idCursada)
);

CREATE TABLE cursada_docente (
    id SERIAL NOT NULL,
    idCursada INTEGER NOT NULL,
    idDocente VARCHAR(64) NOT NULL,
    nivelPermiso INTEGER NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_cursada_docente_cur FOREIGN KEY (idCursada) REFERENCES cursada(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cursada_docente_doc FOREIGN KEY (idDocente) REFERENCES usuario(id),
    CONSTRAINT uk_cursada_docente UNIQUE (idCursada, idDocente)
);

CREATE TABLE cursada_alumno (
    id SERIAL NOT NULL,
    idCursada INTEGER NOT NULL,
    idAlumno INTEGER NOT NULL,
    condicion VARCHAR(1) NOT NULL,
    recursante BOOLEAN,
    condicionFinal VARCHAR(16),
    PRIMARY KEY (id),
    CONSTRAINT fk_cursada_alumno_cur FOREIGN KEY (idCursada) REFERENCES cursada(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cursada_alumno_alu FOREIGN KEY (idAlumno) REFERENCES alumno(legajo),
    CONSTRAINT uk_cursada_alumno UNIQUE (idCursada, idAlumno)
);

CREATE TABLE tipo_evento (
    id SERIAL NOT NULL,
    nombre VARCHAR(32) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE evento_cursada (
    id SERIAL NOT NULL,
    idTipo INTEGER NOT NULL,
    idCursada INTEGER NOT NULL,
    obligatorio BOOLEAN NOT NULL,
    fechaHoraInicio TIMESTAMP,
    fechaHoraFin TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_evento_cursada_eve FOREIGN KEY (idTipo) REFERENCES tipo_evento(id),
    CONSTRAINT fk_evento_cursada_cur FOREIGN KEY (idCursada) REFERENCES cursada(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT ck_fechas_evento CHECK (fechaHoraInicio <= fechaHoraFin)
);

CREATE TABLE evento_cursada_alumno (
    id SERIAL NOT NULL,
    idEvento INTEGER NOT NULL,
    idAlumno INTEGER NOT NULL,
    asistencia BOOLEAN,
    nota INTEGER,
    PRIMARY KEY (id),
    CONSTRAINT fk_evento_cursada_alumno_eve FOREIGN KEY (idEvento) REFERENCES evento_cursada(id),
    CONSTRAINT fk_evento_cursada_alumno_alu FOREIGN KEY (idAlumno) REFERENCES alumno(legajo),
    CONSTRAINT uk_evento_cursada_alumno UNIQUE (idEvento, idAlumno)
);

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
    ('Autoevaluaciones recuperadas');

INSERT INTO tipo_evento (nombre) VALUES
    ('Clase'),
    ('Trabajo práctico'),
    ('Parcial'),
    ('Autoevaluación'),
    ('Recuperatorio'),
    ('Integrador');