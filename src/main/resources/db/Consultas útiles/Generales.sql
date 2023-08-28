/*** Índice ***
    1) Consulta los resultados de los eventos de cursada de un alumno específico.
    7) Consulta los resultados de los eventos de cursada de todos los alumnos de una cursada.
    2) Consulta los eventos de cursada de un tipo de evento de cursada específico.
    3) Consulta los criterios de evaluación de un tipo de evento de cursada específico.
    8) Consulta los criterios de evaluación de todos los tipos de evento de cursada de una cursada particular.
    4) Enlista los alumnos registrados.
    5) Consulta los alumnos vinculados a una cursada.
    6) Consulta los alumnos que no están vinculados a una cursada.
***/

-- (1)
select
    *
from
    evento_cursada_alumno as eca
    inner join evento_cursada as ec on ec.id = eca.id_evento
    inner join tipo_evento as te on te.id = ec.id_tipo
where
    eca.id_alumno = 150001
;

-- (2)
select
    *
from
    evento_cursada as ec
    inner join tipo_evento as te on te.id = ec.id_tipo
where
    te.nombre = 'Trabajo práctico'
;

-- (3)
select
    *
from
    criterio_evaluacion as ce
    inner join criterio_cursada as cc on cc.id_criterio = ce.id
where
    cc.id_cursada = 1
;

-- (4)
select * from alumno order by legajo asc;

-- (5)
select
    ca.id_cursada,
    a.legajo,
    a.dni,
    a.nombre,
    a.apellido
from
    cursada_alumno as ca
    inner join alumno as a on a.legajo = ca.id_Alumno
where
    ca.id_cursada = 1
order by
    a.legajo asc
;

-- (6)
select
    a.*
from
    alumno as a
where
    a.legajo not in (
        select
            id_alumno
        from
            cursada_alumno as ca
        where
            ca.id_cursada = 1
    )
order by
    a.legajo asc
;

-- (7)
select
    ec.id_cursada,
    a.legajo,
    a.dni,
    a.nombre,
    a.apellido,
    te.nombre,
    eca.asistencia,
    eca.nota
from
    evento_cursada_alumno as eca
    inner join evento_cursada as ec on ec.id = eca.id_evento
    inner join tipo_evento as te on te.id = ec.id_tipo
    inner join alumno as a on a.legajo = eca.id_alumno
where
    ec.id_cursada = 1
order by
    a.legajo asc,
    te.nombre asc
;

-- (8)
select
    cc.id_cursada,
    ce.nombre as criterio,
    cc.valor_regular,
    cc.valor_promovido
from
    criterio_evaluacion as ce
    inner join criterio_cursada as cc on cc.id_criterio = ce.id
where
    cc.id_cursada = 1
;