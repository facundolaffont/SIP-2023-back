package ar.edu.unlu.spgda.requests;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class UpdateEventRequest {
    
    private long eventId;
    private String newName;
    private Timestamp newInitialDate;
    private Timestamp newEndDate;
    private boolean newMandatory;
}
