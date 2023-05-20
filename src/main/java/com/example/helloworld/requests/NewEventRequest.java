package com.example.helloworld.requests;

import lombok.Getter;

public class NewEventRequest {
    @Getter private int id;
    @Getter private String tipo;
    @Getter private String fecha_inicio;
    @Getter private String fecha_fin;

    public NewEventRequest (int id, String tipo, String fecha_inicio, String fecha_fin) {
        this.id = id;
        this.tipo = tipo;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
    }
}
