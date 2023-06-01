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

    @JoinColumn(name="id_Criterio")
    @ManyToOne
    private EvaluationCriteria criteria;

    @JoinColumn(name="id_Cursada")
    @ManyToOne
    private Course course;

    @Column(name="valor_Regular")
    private long value_to_regulate;

    @Column(name="valor_Promovido")
    private long value_to_promote;

}
