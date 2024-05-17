package ar.edu.unlu.spgda.requests;

import java.util.List;
import lombok.Data;

@Data public class CalificationRegistrationRequest {

    @Data public static class Calification {
        private Integer dossier;
        private String calification;
    }
    private Long eventId;
    private List<Calification> calificationList;

}
