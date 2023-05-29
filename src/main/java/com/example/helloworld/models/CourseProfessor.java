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
@Table(name="cursada_docente")
public class CourseProfessor implements Serializable {

    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private long id;

    @JoinColumn(name="idCursada")
    @ManyToOne
    private Course idCursada;

    @JoinColumn(name="idDocente")
    @ManyToOne
    private User idDocente;

    @Column(name="nivelPermiso")
    private int nivelPermiso;

}
