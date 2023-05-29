package com.example.helloworld.models;

import java.io.Serializable;
import java.sql.Date;

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
@Table(name="cursada")
public class Course implements Serializable {

    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private long id;

    @JoinColumn(name="idComision")
    @ManyToOne
    private Comission idComision;

    @Column(name="anio")
    private int anio;

    @Column(name="fechaInicio")
    private Date fechaInicio;

    @Column(name="fechaFin")
    private Date fechaFin;

}
