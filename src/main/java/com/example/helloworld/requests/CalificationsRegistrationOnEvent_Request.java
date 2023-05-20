package com.example.helloworld.requests;

import java.util.List;
import com.example.helloworld.models.Calification;
import lombok.Data;

@Data
public class CalificationsRegistrationOnEvent_Request {

    // PK de tabla Evento_Cursada.
    private Integer idAsignatura;
    private Integer commissionNumber;
    private Integer classYear;
    private Integer eventID;

    private List<Calification> califications;

}
