INSERT INTO public.Usuario (legajo, nombre, apellido, email, rol)
VALUES
    (1001, 'Juan', 'Perez', 'juan.perez@ejemplo.com', 'docente'),
    (1002, 'Ana', 'Gomez', 'ana.gomez@ejemplo.com', 'docente'),
    (2001, 'Pedro', 'Rodriguez', 'pedro.rodriguez@ejemplo.com', 'alumno');

INSERT INTO public.Carrera (id, nombre)
VALUES
    (1, 'Licenciatura en Sistemas de Información');

INSERT INTO public.Sede (nombre, comisionDesde, comisionHasta)
VALUES
    ('Lujan', 1, 5),    
    ('Sede B', 6, 10);

INSERT INTO public.Alumno (legajo, nombre, apellido, dni, email)
VALUES
    (2001, 'Pedro', 'Rodriguez', 32165498, 'pedro.rodriguez@ejemplo.com'),
    (2002, 'Lucia', 'Fernandez', 33698547, 'lucia.fernandez@ejemplo.com'),
    (5001, 'Carlos', 'Gonzalez', 36258741, 'carlos.gonzalez@ejemplo.com'),
    (5002, 'Federico', 'Ramirez', 39657412, 'federico.ramirez@ejemplo.com');

INSERT INTO public.Asignatura (id, nombre)
VALUES
    (1, 'Seminario de Integracion Profesional'),
    (2, 'Sistemas Distribuidos y Programacion Paralela'),
    (3, 'Programacion en Ambiente Web');

INSERT INTO public.Comision (asignaturaId, numero)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 2),
    (2, 3),
    (3, 1),
    (3, 2);

INSERT INTO public.Cursada (idAsignatura, numeroComision, anio, FechaInicio, FechaFin)
VALUES
    (1, 1, 2021, '2021-03-15', '2021-07-02'),
    (1, 2, 2021, '2021-03-15', '2021-07-02');

INSERT INTO public.Cur_Doc (asignaturaId, comisionNro, anioCursada, legajo, rol)
VALUES
    (1, 1, 2021, 1001, 'docente'),
    (1, 2, 2021, 1001, 'docente'),
    (1, 2, 2021, 1002, 'docente');

INSERT INTO public.Cur_Alum (asignaturaId, comisionNro, anioCursada, legajo, recursante, condicionFinal, correlativas)
VALUES
    (1, 1, 2021, 2001, true, 'Promovido', true),
    (1, 1, 2021, 2002, false, 'Regular', true),
    (1, 1, 2021, 5001, true, 'Ausente', false),
    (1, 1, 2021, 5002, true, 'Ausente', false);

INSERT INTO public.Evento_Cursada (idAsignatura, numeroComision, anioCursada, idEvento, fecha_hora_inicio, fecha_hora_fin, tipo)
VALUES
    (1, 1, 2021, 1, '2021-03-25 10:00:00.000000', '2021-03-25 12:00:00.000000', 'Evaluacion'),
    (1, 1, 2021, 2, '2021-03-29 15:00:00.000000', '2021-03-29 17:00:00.000000', 'Clase');

INSERT INTO public.Eve_Cur_Alum (idAsignatura, numeroComision, anioCursada, idEvento, legajoAlumno, asistencia, nota)
VALUES
    (1, 1, 2021, 1, 2001, true, 9),
    (1, 1, 2021, 1, 2002, false, 2);
