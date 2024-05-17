package ar.edu.unlu.spgda.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="sede")
public class Campus implements Serializable {
    
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name="nombre")
    private String nombre;

    @Column(name="comision_Desde")
    private int comisionDesde;

    @Column(name="comision_Hasta")
    private int comisionHasta;
}
