package ar.edu.unlu.spgda.models;

import java.sql.Timestamp;

import lombok.Getter;

public class Event {
    @Getter private int id;
    @Getter private String tipo;
    @Getter private Timestamp fecha_inicio;
    @Getter private Timestamp fecha_fin;

    public Event (int id, String tipo, Timestamp fecha_inicio, Timestamp fecha_fin) {
        this.id = id;
        this.tipo = tipo;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
    }
}
