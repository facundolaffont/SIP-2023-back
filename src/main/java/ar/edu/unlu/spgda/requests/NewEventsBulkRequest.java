package ar.edu.unlu.spgda.requests;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewEventsBulkRequest {

    @Data public static class Event {
        private Integer eventTempId;

        @NotBlank(message = "El nombre del evento es obligatorio")
        private String eventName;

        @NotNull(message = "El tipo de evento es obligatorio")
        private Long eventTypeId;

        @NotNull(message = "La fecha de inicio es obligatoria")
        private LocalDateTime initialDatetime;

        @NotNull(message = "La fecha de fin es obligatoria")
        private LocalDateTime endDatetime;

        private Boolean obligatory;
    }

    @NotNull(message = "El ID de cursada es obligatorio")
    private Long courseId;

    @Valid
    @NotNull(message = "La lista de eventos es obligatoria")
    private List<Event> eventsList;

}