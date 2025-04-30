package ar.edu.unlu.spgda.requests;

import lombok.Data;

@Data
public class UpdateEventRegisterNoteRequest {

    private long studentCourseEventRegisterId;
    private String newNoteValue;

}
