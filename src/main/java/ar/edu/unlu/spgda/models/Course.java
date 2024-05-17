package ar.edu.unlu.spgda.models;

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

    @ManyToOne
    @JoinColumn(name="id_Comision")
    private Comission comision;

    @Column(name="anio")
    private int anio;

    @Column(name="fecha_Inicio")
    private Date fechaInicio;

    @Column(name="fecha_Fin")
    private Date fechaFin;

}
