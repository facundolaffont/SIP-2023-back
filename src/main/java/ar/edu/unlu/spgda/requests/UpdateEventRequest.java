package ar.edu.unlu.spgda.requests;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class UpdateEventRequest {
    
    private long eventId;
    private boolean newMandatory;
    private Timestamp newInitialDate;
    private Timestamp newEndDate;
}
