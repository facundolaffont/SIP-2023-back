package com.example.helloworld.requests;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class NewDossiersCheckRequest implements Serializable {

    private List<Integer> dossiersList;
    
}