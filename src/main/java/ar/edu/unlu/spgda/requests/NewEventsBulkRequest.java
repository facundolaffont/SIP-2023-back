package ar.edu.unlu.spgda.requests;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class NewEventsBulkRequest {

    @Data public static class Event {
        private Integer eventTempId;
        private Long eventTypeId;
        private LocalDateTime initialDatetime;
        private LocalDateTime endDatetime;
        private Boolean obligatory;
    }

    private Long courseId;
    private List<Event> eventsList;

}