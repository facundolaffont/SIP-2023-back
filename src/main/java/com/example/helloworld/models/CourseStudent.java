package com.example.helloworld.models;

import java.io.Serializable;

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
@Table(name="cursada_alumno")
public class CourseStudent implements Serializable {

    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private long id;

    @JoinColumn(name="id_Cursada")
    @ManyToOne
    private Course idCursada;

    @JoinColumn(name="id_Alumno")
    @ManyToOne
    private Student idAlumno;

    @Column(name="condicion")
    private char condicion;

    @Column(name="recursante")
    private boolean recursante;

    @Column(name="condicion_Final")
    private String condicionFinal;

}
