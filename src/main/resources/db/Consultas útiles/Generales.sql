/*** Índice ***
    1) Consulta los resultados de los eventos de cursada de un alumno específico.
    7) Consulta los resultados de los eventos de cursada de todos los alumnos de una cursada.
    9) Consulta los resultados de los eventos de cursada de todos los alumnos de una cursada, mostrando
       los resultados como porcentajes.
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

/* (9)
1.Trae el porcentaje de asistencia y el promedio de nota de cada tipo de evento de
cada alumno de una cursada.

    <proyección>[
        eca.id_evento,
        eca.id_alumno,
        eca.asistencia,
        eca.nota,
        ec.id_tipo,
        ec.id_cursada,
        ec.obligatorio
    ](
        <selección>[ec.id_cursada=x] (
            evento_cursada_alumno <join>[id_evento=id] evento_cursada
        )
    ) -> B (todos los eventos de clase o evaluación de una cursada)

        B.id_Evento INTEGER NOT NULL,
        B.id_Alumno INTEGER NOT NULL,
        B.asistencia BOOLEAN,
        B.nota VARCHAR(16),
        B.id_Tipo INTEGER NOT NULL,
        B.id_Cursada INTEGER NOT NULL,
        B.obligatorio BOOLEAN NOT NULL,
    ---------------------------------------------------
    <proyección>[
        c1.id_alumno,
        c1.id_tipo,
        c2.clases::float / C1.clases_asistidas as asistencia
    ](
        <proyección>[
            eca.id_alumno,
            ec.id_tipo,
            cuenta(ec.id_tipo) as clases_asistidas,
        ](
            <selección>[
                ec.obligatorio=t,
                ec.id_tipo=clase,
                eca.asistencia=t,
            ](B) (lista de clases obligatorias asistidas por cada alumno de una cursada)
        ) -> c1 (cantidad de clases obligatorias asistidas por cada alumno de una cursada)
        <join>[
            id_alumno,
            id_tipo,
        ]
        <proyección>[
            eca.id_alumno,
            ec.id_tipo,
            cuenta(ec.id_tipo) as clases,
        ](
            <selección>[
                ec.obligatorio=t,
                ec.id_tipo=clase,
            ](B) (lista de clases obligatorias por cada alumno de una cursada)
        ) -> C2 (cantidad de clases por cada alumno de una cursada)
    ) -> C (asistencia por alumno de una cursada)

    C.id_Alumno INTEGER NOT NULL,
    C.id_Tipo INTEGER NOT NULL,
    C.asistencia FLOAT,
    ---------------------------------------------------
    <proyección>[
        eca.id_alumno,
        ec.id_tipo,
        suma(eca.nota)::float / count(eca.nota) as promedio_nota
    ](
        <selección>[ec.id_tipo!=clase](B)
    ) -> D (promedio de eventos de evaluación)

        D.id_Alumno INTEGER NOT NULL,
        D.id_Tipo INTEGER NOT NULL,
        D.promedio_nota FLOAT,
    ---------------------------------------------------
*/
with var (cursada) as (values (1))
select
    c1.id_alumno,
    te.nombre,
    round(cast((c1.clases_asistidas::float / c2.clases)*100 as numeric), 2) as asistencia
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
