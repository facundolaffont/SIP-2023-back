/*** Consultas
    1) Consulta los resultados de los eventos de cursada de un alumno específico.
    2) Consulta los eventos de un tipo de evento específico.
    3) Consulta los criterios de evaluación de un tipo de evento de cursada específico.
***/

-- 1) Consulta los resultados de los eventos de cursada de un alumno específico.
select
    *
from
    evento_cursada_alumno as eca
    inner join evento_cursada as ec on ec.id = eca.id_evento
    inner join tipo_evento as te on te.id = ec.id_tipo
where
    eca.id_alumno = 150001
;

-- 2) Consulta los eventos de un tipo de evento específico.
select
    *
from
    evento_cursada as ec
    inner join tipo_evento as te on te.id = ec.id_tipo
where
    te.nombre = 'Trabajo práctico'
;

-- 3) Consulta los criterios de evaluación de una cursada específica.
select
    *
from
    criterio_evaluacion as ce
    inner join criterio_cursada as cc on cc.id_criterio = ce.id
where
    cc.id_cursada = 1
;