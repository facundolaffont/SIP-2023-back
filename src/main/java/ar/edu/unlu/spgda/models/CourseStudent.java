package ar.edu.unlu.spgda.models;

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
    private Course cursada;

    @JoinColumn(name="id_Alumno")
    @ManyToOne
    private Student alumno;
    
    @Column(name="previous_subjects_approved")
    private boolean previousSubjectsApproved;

    @Column(name="studied_previously")
    private boolean recursante;

    @Column(name="condicion_Final")
    private String condicionFinal;

}
