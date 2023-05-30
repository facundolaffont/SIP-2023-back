package com.example.helloworld.requests;

import java.io.Serializable;

import lombok.Data;

@Data
public class Calification implements Serializable {

    private Integer studentDossier;
    private String calification;

}