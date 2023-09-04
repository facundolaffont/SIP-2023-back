/*** Índice ***
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
    5) Consulta los alumnos vinculados a una cursada.
    6) Consulta los alumnos que no están vinculados a una cursada.
***/

-- (1)
select
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
    case when eca.asistencia = true then 'Sí' when eca.asistencia = false then 'No' else '-' end as "Asistió",
    case when eca.nota is null then '-' else eca.nota end as nota,
    ec.id as id_evento_cursada,
    case when ec.obligatorio = true then 'Sí' else 'No' end as "Asistencia obligatoria"
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

-- (9)
with var (cursada) as (values (1))
select
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
;

-- (10)
select
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
;

-- (11)
with var (cursada, tipo_evento) as (values (1, 'Parcial'))
select
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
;

-- (12)
with var (cursada) as (values (1))
select
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
;

-- (13)
select
    ec.id_cursada,
    ec.id_tipo,
    te.nombre,
    ec.obligatorio
from
    evento_cursada as ec
    inner join tipo_evento as te on te.id = ec.id_tipo
where
    ec.id_cursada = 1
;
