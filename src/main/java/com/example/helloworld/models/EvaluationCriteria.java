package com.example.helloworld.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de criterios de evaluación.
 */
@Data
@Entity
@NoArgsConstructor
public class EvaluationCriteria {
    /**
     * ID del criterio.
     */
    @Column(name="idcriterio")
    @Id
    private int id;

    /**
     * Descripción del criterio.
     */
    @Column(name="descripcion")
    private String description;
}
