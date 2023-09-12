package com.example.helloworld.requests;

import java.util.List;
import lombok.Data;

@Data public class AttendanceRegistrationRequest {

    @Data public static class Attendance {
        private Integer dossier;
        private boolean attendance;
    }
    private Long eventId;
    private List<Attendance> attendanceList;

}
