package com.example.helloworld.requests;

import java.io.Serializable;
import lombok.Data;

@Data
public class Attendance implements Serializable {

    private Integer studentDossier;
    private Boolean attendance;

}