package com.example.helloworld.models;

import java.io.Serializable;
import java.security.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="evento_cursada")
public class CourseEvent implements Serializable {

    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private long id;

    @JoinColumn(name="id_Tipo")
    @ManyToOne
    private EventType idTipo;

    @JoinColumn(name="id_Cursada")
    @ManyToOne
    private Course idCursada;

    @Column(name="obligatorio")
    private boolean obligatorio;

    @Column(name="fecha_Hora_Inicio")
    private Timestamp fechaHoraInicio;

    @Column(name="fecha_Hora_Fin")
    private Timestamp fechaHoraFin;

}
