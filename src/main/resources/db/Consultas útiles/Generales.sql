/*** Índice ***

16) Consulta los eventos.
1) Consulta los resultados de los eventos de cursada de un alumno específico.
10) Consulta los resultados de los eventos de cursada de un alumno y tipo de evento específicos.
7) Consulta los resultados, de todos los tipos de evento de cursada, de todos los alumnos de una cursada.
9) Consulta el porcentaje de asistencia, a todos los tipos de evento de cursada, de todos los alumnos de una cursada.
11) Consulta el promedio de notas, de todos los alumnos de una cursada, de un tipo de evento de evaluación específico.
12) Consulta el promedio de cantidad de instancias de evaluación aprobadas de todos los eventos de evaluación de todos
    los alumnos de una cursada.
2) Consulta los eventos de cursada de un tipo de evento de cursada específico.
13) Consulta los parámetros de los eventos de cursada de todos los tipos de evento de una cursada específica.
3) Consulta los criterios de evaluación de un tipo de evento de cursada específico.
8) Consulta los criterios de evaluación de todos los tipos de evento de cursada de una cursada particular.
4) Enlista los alumnos registrados.
5) Enlista los alumnos vinculados a una cursada.
6) Enlista los alumnos que no están vinculados a una cursada.
14) Enlista los alumnos que tienen registro en un evento de cursada.
18) Enlista los alumnos que tienen registro en los eventos de una cursada.
17) Enlista los alumnos que no tienen registro en un evento de cursada, pero sí en la cursada (y no
    necesariamente en otro evento de la cursada).
15) Enlista los eventos vinculados con cada docente.

***/

-- (1)
select
    -- generales.sql > 1
    eca.id_alumno legajo,
    eca.id_evento,
    te.nombre,
    ec.obligatorio,
    eca.asistencia,
    eca.nota
from
    evento_cursada_alumno as eca
    inner join evento_cursada as ec on ec.id = eca.id_evento
    inner join tipo_evento as te on te.id = ec.id_tipo
where
    eca.id_alumno = 143305

-- (2)
select
    -- generales.sql > 2
    *
from
    evento_cursada as ec
    inner join tipo_evento as te on te.id = ec.id_tipo
where
    te.nombre = 'Trabajo práctico'

-- (3)
select
    -- generales.sql > 3
    *
from
    criterio_evaluacion as ce
    inner join criterio_cursada as cc on cc.id_criterio = ce.id
where
    cc.id_cursada = 1

-- (4)
select
    -- generales.sql > 4
    *
from
    alumno
order by
    legajo asc

-- (5)
select
    -- generales.sql > 5
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

-- (6)
select
    -- generales.sql > 6
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

-- (7)
with var (cursada) as (values (1))
select
    -- generales.sql > 7
    ec.id_cursada,
    a.legajo,
    a.dni,
    a.nombre,
    a.apellido,
    ec.id id_evento_cursada,
    te.nombre,
    case when ec.obligatorio = true then 'Sí' else 'No' end as "Asistencia obligatoria",
    case when eca.asistencia = true then 'Sí' when eca.asistencia = false then 'No' else '-' end as "Asistió",
    case when eca.nota is null then '-' else eca.nota end as nota
from
    var,
    evento_cursada_alumno as eca
    inner join evento_cursada as ec on ec.id = eca.id_evento
    inner join tipo_evento as te on te.id = ec.id_tipo
    inner join alumno as a on a.legajo = eca.id_alumno
where
    ec.id_cursada = var.cursada
order by
    a.legajo asc,
    a.dni asc,
    ec.id asc

-- (8)
select
    -- generales.sql > 8
    cc.id_cursada,
    ce.nombre as criterio,
    cc.valor_regular,
    cc.valor_promovido
from
    criterio_evaluacion as ce
    inner join criterio_cursada as cc on cc.id_criterio = ce.id
where
    cc.id_cursada = 1

-- (9)
with var (cursada) as (values (1))
select
    -- generales.sql > 9
    c1.id_alumno,
    te.nombre,
    round(cast((c1.clases_asistidas::float / c2.clases)*100 as numeric), 2) || '%' as asistencia
from
    (
        select
            c1a.id_alumno,
            c1a.id_tipo,
            count(c1a.id_tipo) as clases_asistidas
        from (
            select
                eca.id_evento,
                eca.id_alumno,
                eca.asistencia,
                ec.id_tipo,
                ec.id_cursada,
                ec.obligatorio
            from
                var,
                evento_cursada_alumno eca
                inner join evento_cursada ec on ec.id = eca.id_evento
                inner join tipo_evento te on te.id = ec.id_tipo
            where
                ec.id_cursada = var.cursada
                and ec.obligatorio = true
                and eca.asistencia = true
        ) c1a
        group by
            c1a.id_alumno,
            c1a.id_tipo
    ) c1
    inner join (
        select
            c2a.id_alumno,
            c2a.id_tipo,
            count(c2a.id_tipo) as clases
        from (
            select
                eca.id_evento,
                eca.id_alumno,
                eca.asistencia,
                ec.id_tipo,
                ec.id_cursada,
                ec.obligatorio
            from
                var,
                evento_cursada_alumno eca
                inner join evento_cursada ec on ec.id = eca.id_evento
                inner join tipo_evento te on te.id = ec.id_tipo
            where
                ec.id_cursada = var.cursada
                and ec.obligatorio = true
        ) c2a
        group by
            c2a.id_alumno,
            c2a.id_tipo
    ) c2 on c2.id_alumno = c1.id_alumno
        and c2.id_tipo = c1.id_tipo
    inner join tipo_evento te on te.id = c2.id_tipo
order by
    te.nombre asc,
    c1.id_alumno asc

-- (10)
select
    -- generales.sql > 10
    eca.id_alumno legajo,
    eca.id_evento,
    te.nombre,
    ec.obligatorio,
    eca.asistencia,
    eca.nota
from
    evento_cursada_alumno as eca
    inner join evento_cursada as ec on ec.id = eca.id_evento
    inner join tipo_evento as te on te.id = ec.id_tipo
where
    eca.id_alumno = 143305
    and te.nombre = 'Clase'

-- (11)
with var (cursada, tipo_evento) as (values (1, 'Parcial'))
select
    -- generales.sql > 11
    pe.id_alumno,
    pe.nombre,
    sum(pe.nota::float) / count(pe.nota) as promedio_nota
from
    (
        select
            eca.id_alumno,
            te.nombre,
            eca.nota
        from
            var,
            evento_cursada_alumno eca
            inner join evento_cursada ec on ec.id = eca.id_evento
            inner join tipo_evento te on te.id = ec.id_tipo
        where
            ec.id_cursada = var.cursada
            and te.nombre = var.tipo_evento
    ) pe
group by
    pe.id_alumno,
    pe.nombre
order by
    pe.id_alumno asc,
    pe.nombre asc

-- (12)
with var (cursada) as (values (1))
select
    -- generales.sql > 12
    pe.id_alumno,
    pe.nombre,
    (sum(pe.aprobada::float) / count(pe.aprobada))*100 || '%' as porcentaje_aprobados
from
    (
        select
            eca.id_alumno,
            te.nombre,
            case when eca.nota similar to '([4-9]|10|A-?)' then 1 else 0 end as aprobada
        from
            var,
            evento_cursada_alumno eca
            inner join evento_cursada ec on ec.id = eca.id_evento
            inner join tipo_evento te on te.id = ec.id_tipo
        where
            ec.id_cursada = var.cursada
            and te.nombre != 'Clase'
    ) pe
group by
    pe.id_alumno,
    pe.nombre
order by
    pe.id_alumno asc,
    pe.nombre asc

-- (13)
select
    -- generales.sql > 13
    ec.id_cursada,
    te.nombre,
    ec.fecha_hora_inicio,
    ec.fecha_hora_fin,
    ec.id id_evento,
    case when ec.obligatorio = true then 'Sí' else 'No' end obligatorio
from
    evento_cursada as ec
    inner join tipo_evento as te on te.id = ec.id_tipo
where
    ec.id_cursada = 1

-- (14)
with var (id_evento) as (
    values (1)
)
select
    ec.id_cursada "ID de cursada",
    ec.id "ID de evento",
    te.nombre "Tipo de evento",
    ec.fecha_hora_inicio "Fechahora inicial",
    ec.fecha_hora_fin "Fechahora final",
    eca.id_alumno "Legajo",
    a.dni "DNI",
    a.nombre "Nombre",
    a.apellido "Apellido",
    case
        when eca.asistencia = true then 'Asistió'
        when eca.asistencia = false then 'No asistió'
    end "Asistencia",
    eca.nota "Nota"
from
    var,
    evento_cursada ec
    inner join tipo_evento te on te.id = ec.id_tipo
    inner join evento_cursada_alumno eca on eca.id_evento = ec.id
    inner join alumno a on a.legajo = eca.id_alumno
where
    ec.id = var.id_evento

-- (15)
select
    -- generales.sql > 15
    u.legajo "Legajo",
    u.nombre "Nombre",
    u.apellido "Apellido",
    u.email "Email",
    ec.id_cursada "ID de cursada",
    ec.id "ID de evento",
    te.nombre "Tipo de evento"
from
    evento_cursada ec
    inner join tipo_evento te on te.id = ec.id_tipo
    inner join cursada_docente cd on cd.id_cursada = ec.id_cursada
    inner join usuario u on u.id = cd.id_docente and u.rol = 'docente'
order by
    u.legajo asc

-- (16)
select
    -- generales.sql > 16
    ec.id_cursada "ID de cursada",
    ec.id "ID de evento",
    te.nombre "Tipo de evento"
from
    evento_cursada ec
    inner join tipo_evento te on te.id = ec.id_tipo
order by
    ec.id_cursada asc,
    ec.id asc,
    te.nombre asc

-- (17)
with var (id_evento) as (
    values (1)
)
select
    -- generales.sql > 17
    ca.id_cursada "Cursada",
    a.legajo "Legajo",
    a.dni "DNI",
    a.nombre "Nombre",
    a.apellido "Apellido"
from
    var,
    cursada_alumno ca
    inner join alumno a on a.legajo = ca.id_alumno
where

    -- El ID de la cursada debe ser el mismo que el de la cursada
    -- a la cual pertenece el evento var.id_evento.
    ca.id_cursada = (
        select
            id_cursada
        from
            evento_cursada ec
        where
            ec.id = var.id_evento
    )

    -- El legajo del alumno no debe estar registrado en el evento
    -- var.id_evento.
    and ca.id_alumno not in (
        select
            id_alumno
        from
            evento_cursada_alumno eca
        where
            eca.id_evento = var.id_evento
    )

;

-- (18)
with var (id_cursada) as (
    values (1)
)
select
    ec.id_cursada "ID de cursada",
    ec.id "ID de evento",
    te.nombre "Tipo de evento",
    ec.fecha_hora_inicio "Fechahora inicial",
    ec.fecha_hora_fin "Fechahora final",
    eca.id_alumno "Legajo",
    a.dni "DNI",
    a.nombre "Nombre",
    a.apellido "Apellido",
    case
        when eca.asistencia = true then 'Asistió'
        when eca.asistencia = false then 'No asistió'
    end "Asistencia",
    eca.nota "Nota"
from
    var,
    evento_cursada ec
    inner join tipo_evento te on te.id = ec.id_tipo
    inner join evento_cursada_alumno eca on eca.id_evento = ec.id
    inner join alumno a on a.legajo = eca.id_alumno
where
    ec.id_cursada = var.id_cursada