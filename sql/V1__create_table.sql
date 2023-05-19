create table public.Usuario (
    legajo INTEGER NOT NULL,
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    email VARCHAR(100),
    rol VARCHAR(50),
    PRIMARY KEY (legajo, rol)
);

create table public.Carrera (
    id INTEGER NOT NULL,
    nombre VARCHAR(50),
    PRIMARY KEY (id)
);

create table public.Sede (
    nombre VARCHAR(50) NOT NULL,
    comisionDesde INTEGER,
    comisionHasta INTEGER,
    PRIMARY KEY (nombre)
);

create table public.Alumno (
    legajo INTEGER NOT NULL,
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    dni INTEGER,
    email VARCHAR(100),
    PRIMARY KEY (legajo)
);

create table public.Asignatura (
    id INTEGER NOT NULL,
    nombre VARCHAR(50),
    PRIMARY KEY (id)
);

create table public.Comision (
    asignaturaId INTEGER,
    numero INTEGER,
    CONSTRAINT fk_Asignatura
    FOREIGN KEY (asignaturaId)
    REFERENCES Asignatura(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    PRIMARY KEY (asignaturaId, numero)
);

create table public.Cursada (
    idAsignatura INTEGER,
    numeroComision INTEGER,
    anio INTEGER,
    FechaInicio DATE,
    FechaFin DATE,
    PRIMARY KEY (idAsignatura, numeroComision, anio),
    CONSTRAINT fk_cursada_comision 
    FOREIGN KEY (idAsignatura, numeroComision)
    REFERENCES Comision(asignaturaId, numero)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

create table public.CriterioEvaluacion (
    IdCriterio INTEGER NOT NULL,
    Descripcion VARCHAR(30),
    PRIMARY KEY (IdCriterio)
);

create table public.Criterio_Cursada (
    asignaturaId INTEGER,
    comisionNro INTEGER,
    anioCursada INTEGER,
    criterioId INTEGER,
    valorRegular INTEGER,
    valorPromovido INTEGER,
    CONSTRAINT pk_Criterio_Cursada PRIMARY KEY (asignaturaId, comisionNro, anioCursada, criterioId),
    CONSTRAINT fk_Criterio_Cursada_Cur FOREIGN KEY (asignaturaId, comisionNro, anioCursada) REFERENCES Cursada (idAsignatura, numeroComision, anio),
    CONSTRAINT fk_Criterio_Cursada_Cri FOREIGN KEY (criterioId) REFERENCES CriterioEvaluacion (IdCriterio)
);

create table public.Cur_Doc (
    asignaturaId INTEGER,
    comisionNro INTEGER,
    anioCursada INTEGER,
    legajo INTEGER,
    rol VARCHAR(50),
    CONSTRAINT pk_cur_doc PRIMARY KEY (asignaturaId, comisionNro, anioCursada, legajo, rol),
    CONSTRAINT fk_cur_doc_cursada FOREIGN KEY (asignaturaId, comisionNro, anioCursada) REFERENCES Cursada (idAsignatura, numeroComision, anio),
    CONSTRAINT fk_cur_doc_docente FOREIGN KEY (legajo, rol) REFERENCES Usuario (legajo, rol)
);

create table public.Cur_Alum (
    asignaturaId INTEGER,
    comisionNro INTEGER,
    anioCursada INTEGER,
    legajo INTEGER,
    recursante BOOLEAN,
    condicion VARCHAR(1),
    condicionFinal VARCHAR(10),
    CONSTRAINT pk_cur_alum PRIMARY KEY (asignaturaId, comisionNro, anioCursada, legajo),
    CONSTRAINT fk_cur_alum_cursada FOREIGN KEY (asignaturaId, comisionNro, anioCursada) REFERENCES Cursada (idAsignatura, numeroComision, anio),
    CONSTRAINT fk_cur_alum_alumno FOREIGN KEY (legajo) REFERENCES Alumno (legajo)
);

create table public.Evento_Cursada (
    idAsignatura INTEGER,
    numeroComision INTEGER,
    anioCursada INTEGER,
    idEvento INTEGER,
    fecha_hora_inicio TIMESTAMP,
    fecha_hora_fin TIMESTAMP,
    tipo VARCHAR(20),
    CONSTRAINT pk_evento_cursada PRIMARY KEY (idAsignatura, numeroComision, anioCursada, idEvento),
    CONSTRAINT fk_evento_cursada_cursada FOREIGN KEY (idAsignatura, numeroComision, anioCursada) REFERENCES Cursada (idAsignatura, numeroComision, anio) ON DELETE CASCADE,
    CONSTRAINT ck_fecha CHECK (fecha_hora_inicio <= fecha_hora_fin)    
);

create table public.Eve_Cur_Alum (
    idAsignatura INTEGER,
    numeroComision INTEGER,
    anioCursada INTEGER,
    idEvento INTEGER,
    legajoAlumno INTEGER,
    asistencia boolean,
    nota INTEGER,
    CONSTRAINT pk_Eve_Cur_Alum PRIMARY KEY (idAsignatura, numeroComision, anioCursada, idEvento, legajoAlumno),
    CONSTRAINT fk_asistencia_evento_cursada FOREIGN KEY (idAsignatura, numeroComision, anioCursada, idEvento) REFERENCES EVENTO_CURSADA (idAsignatura, numeroComision, anioCursada, idEvento) ON DELETE CASCADE,
    CONSTRAINT fk_asistencia_cur_alum FOREIGN KEY (idAsignatura, numeroComision, anioCursada, legajoAlumno) REFERENCES CUR_ALUM (asignaturaId, comisionNro, anioCursada, legajo) ON DELETE CASCADE
);

/*DROP TABLE Usuario;
DROP TABLE Carrera;
DROP TABLE Sede;
DROP TABLE Alumno;
DROP TABLE Asignatura;
DROP TABLE Comision;
DROP TABLE Cursada;
DROP TABLE Cur_Doc;
DROP TABLE Evento_Cursada;
DROP TABLE Eve_Cur_Alum; */


/*DELETE FROM TABLE Usuario;
DELETE FROM TABLE Carrera;
DELETE FROM TABLE Sede;
DELETE FROM TABLE Alumno;
DELETE FROM TABLE Asignatura;
DELETE FROM TABLE Comision;
DELETE FROM TABLE Cursada;
DELETE FROM TABLE Cur_Doc;
DELETE FROM TABLE Evento_Cursada;
DELETE FROM TABLE Eve_Cur_Alum;*/
