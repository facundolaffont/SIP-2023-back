package ar.edu.unlu.spgda.requests;

import java.io.Serializable;

import lombok.Data;

@Data
public class Calification implements Serializable {

    private Integer studentDossier;
    private String calification;

}