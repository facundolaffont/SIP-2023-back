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
@Table(name="evento_cursada_alumno")
public class StudentCourseEvent implements Serializable {

    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private long id;

    @JoinColumn(name="id_Evento")
    @ManyToOne
    private CourseEvent eventoCursada;

    @JoinColumn(name="id_Alumno")
    @ManyToOne
    private Student alumno;

    @Column(name="asistencia")
    private Boolean asistencia;

    @Column(name="nota")
    private String nota;

}
