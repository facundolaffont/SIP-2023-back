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
@Table(name="criterio_cursada")
public class CourseEvaluationCriteria implements Serializable {

    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private long id;

    @JoinColumn(name="idCriterio")
    @ManyToOne
    private EvaluationCriteria criteria;

    @JoinColumn(name="idCursada")
    @ManyToOne
    private Course course;

    @Column(name="valorregular")
    private String value_to_regulate;

    @Column(name="valorpromovido")
    private String value_to_promote;

}
