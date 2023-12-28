-- V1__create_table.sql --


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
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT uk_legajo_rol_usuario UNIQUE (legajo, rol)
);

CREATE TABLE carrera (
    id SERIAL NOT NULL,
    nombre VARCHAR(128) NOT NULL,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id)
);

CREATE TABLE sede (
    id SERIAL NOT NULL,
    nombre VARCHAR(32) NOT NULL,
    comision_Desde INTEGER NOT NULL,
    comision_Hasta INTEGER NOT NULL,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id)
);

CREATE TABLE alumno (
    legajo INTEGER NOT NULL,
    dni INTEGER NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    apellido VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (legajo),
    CONSTRAINT uk_dni_alumno UNIQUE (dni)
);

CREATE TABLE asignatura (
    id SERIAL NOT NULL,
    id_Carrera INTEGER NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_asignatura_carrera FOREIGN KEY (id_Carrera) REFERENCES carrera(id)
);

CREATE TABLE comision (
    id SERIAL NOT NULL,
    id_Asignatura INTEGER NOT NULL,
    numero INTEGER NOT NULL,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_comision_asignatura FOREIGN KEY (id_Asignatura) REFERENCES asignatura(id),
    CONSTRAINT uk_asignatura_numero_comision UNIQUE (id_Asignatura, numero)
);

CREATE TABLE cursada (
    id SERIAL NOT NULL,
    id_Comision INTEGER NOT NULL,
    anio INTEGER NOT NULL,
    fecha_Inicio DATE,
    fecha_Fin DATE,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_cursada_comision FOREIGN KEY (id_Comision) REFERENCES comision(id),
    CONSTRAINT uk_comision_anio_cursada UNIQUE (id_Comision, anio)
);

CREATE TABLE criterio_evaluacion (
    id SERIAL NOT NULL,
    nombre VARCHAR(64) NOT NULL,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id)
);

CREATE TABLE criterio_cursada (
    id SERIAL NOT NULL,
    id_Criterio INTEGER NOT NULL,
    id_Cursada INTEGER NOT NULL,
    valor_Regular INTEGER NOT NULL,
    valor_Promovido INTEGER NOT NULL,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_criterio_cursada_cri FOREIGN KEY (id_Criterio) REFERENCES criterio_evaluacion(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_criterio_cursada_cur FOREIGN KEY (id_Cursada) REFERENCES cursada(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uk_criterio_cursada UNIQUE (id_Criterio, id_Cursada)
);

CREATE TABLE cursada_docente (
    id SERIAL NOT NULL,
    id_Cursada INTEGER NOT NULL,
    id_Docente VARCHAR(64) NOT NULL,
    nivel_Permiso INTEGER NOT NULL,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_cursada_docente_cur FOREIGN KEY (id_Cursada) REFERENCES cursada(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cursada_docente_doc FOREIGN KEY (id_Docente) REFERENCES usuario(id),
    CONSTRAINT uk_cursada_docente UNIQUE (id_Cursada, id_Docente)
);

CREATE TABLE cursada_alumno (
    id SERIAL NOT NULL,
    id_Cursada INTEGER NOT NULL,
    id_Alumno INTEGER NOT NULL,
    previous_subjects_approved BOOLEAN NOT NULL,
    studied_previously BOOLEAN NOT NULL,
    condicion_Final VARCHAR(16),
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_cursada_alumno_cur FOREIGN KEY (id_Cursada) REFERENCES cursada(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cursada_alumno_alu FOREIGN KEY (id_Alumno) REFERENCES alumno(legajo),
    CONSTRAINT uk_cursada_alumno UNIQUE (id_Cursada, id_Alumno)
);

CREATE TABLE tipo_evento (
    id SERIAL NOT NULL,
    nombre VARCHAR(32) NOT NULL,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id)
);

CREATE TABLE evento_cursada (
    id SERIAL NOT NULL,
    id_Tipo INTEGER NOT NULL,
    id_Cursada INTEGER NOT NULL,
    obligatorio BOOLEAN NOT NULL,
    fecha_Hora_Inicio TIMESTAMP,
    fecha_Hora_Fin TIMESTAMP,
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_evento_cursada_eve FOREIGN KEY (id_Tipo) REFERENCES tipo_evento(id),
    CONSTRAINT fk_evento_cursada_cur FOREIGN KEY (id_Cursada) REFERENCES cursada(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT ck_fechas_evento CHECK (fecha_Hora_Inicio <= fecha_Hora_Fin)
);

CREATE TABLE evento_cursada_alumno (
    id SERIAL NOT NULL,
    id_Evento INTEGER NOT NULL,
    id_Alumno INTEGER NOT NULL,
    asistencia BOOLEAN,
    nota VARCHAR(16),
    creation_date TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_evento_cursada_alumno_eve FOREIGN KEY (id_Evento) REFERENCES evento_cursada(id),
    CONSTRAINT fk_evento_cursada_alumno_alu FOREIGN KEY (id_Alumno) REFERENCES alumno(legajo),
    CONSTRAINT uk_evento_cursada_alumno UNIQUE (id_Evento, id_Alumno)
);