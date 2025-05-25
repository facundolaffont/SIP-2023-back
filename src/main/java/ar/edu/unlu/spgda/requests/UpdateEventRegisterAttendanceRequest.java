package ar.edu.unlu.spgda.requests;

import lombok.Data;

@Data
public class UpdateEventRegisterAttendanceRequest {
    
    private long studentCourseEventRegisterId;
    private Boolean newAttendanceValue;

}
