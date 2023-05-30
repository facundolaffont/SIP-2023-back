package com.example.helloworld.requests;

import lombok.Data;

@Data
public class NewEventRequest {

    private int id;
    private String tipo;
    private String fecha_inicio;
    private String fecha_fin;

}