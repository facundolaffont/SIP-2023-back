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
@Table(name="usuario")
public class Userr implements Serializable {
    
    @Column(name="id")
    @Id
    private String id;

    @Column(name="legajo")
    private int legajo;

    @Column(name="rol")
    private String rol;

    @Column(name="nombre")
    private String nombre;

    @Column(name="email")
    private String email;

}
