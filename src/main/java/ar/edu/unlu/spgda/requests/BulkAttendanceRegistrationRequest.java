package ar.edu.unlu.spgda.requests;

import java.util.List;
import lombok.Data;

@Data
public class BulkAttendanceRegistrationRequest {

    @Data
    public static class EventAttendance {
        private Long eventId;
        private List<AttendanceEntry> attendanceList;
    }

    @Data
    public static class AttendanceEntry {
        private Integer dossier;
        private boolean attendance;
    }

    private List<EventAttendance> attendanceByEvent;
}
