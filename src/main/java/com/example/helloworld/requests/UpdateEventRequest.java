package com.example.helloworld.requests;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class UpdateEventRequest {
    
    private long eventId;
    private boolean newMandatory;
    private Timestamp initialDate;
    private Timestamp endDate;
}
