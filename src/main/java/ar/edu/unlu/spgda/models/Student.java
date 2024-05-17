package ar.edu.unlu.spgda.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="alumno")
public class Student implements Serializable {
    
    @Column(name="legajo")
    @Id
    private int legajo;

    @Column(name="dni")
    private int dni;

    @Column(name="nombre")
    private String nombre;

    @Column(name="apellido")
    private String apellido;

    @Column(name="email")
    private String email;

}
