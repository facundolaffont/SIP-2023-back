-- Limpiar BD.sql --


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