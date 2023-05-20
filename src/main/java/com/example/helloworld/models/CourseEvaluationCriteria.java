package com.example.helloworld.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de criterios de evaluación de una cursada.
 */
@Data
@Entity
@NoArgsConstructor
public class CourseEvaluationCriteria {
    /**
     * ID de la asignatura.
     */
    @Column(name="asignaturaid")
    @Id
    private int subject_id;

    /**
     * Número de comisión.
     */
    @Column(name="comisionnro")
    @Id
    private int commission;

    /**
     * Año de la cursada.
     */
    @Column(name="aniocursada")
    @Id
    private int year;

    /**
     * ID del criterio.
     */
    @Column(name="criterioid")
    @Id
    private int criteria_id;

    /**
     * Valor (umbral) aplicable en la evaluación del criterio para determinar si
     * el estudiante aplica a regularizar la asignatura.
     */
    @Column(name="valorregular")
    private String value_to_regulate;

    /**
     * Valor (umbral) aplicable en la evaluación del criterio para determinar si
     * el estudiante aplica a promover la asignatura.
     */
    @Column(name="valorpromovido")
    private String value_to_promote;
}
