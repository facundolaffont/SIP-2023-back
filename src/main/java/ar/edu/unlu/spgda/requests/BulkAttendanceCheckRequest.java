package ar.edu.unlu.spgda.requests;

import java.util.List;
import lombok.Data;

@Data
public class BulkAttendanceCheckRequest {
    private Long courseId;
    private List<Integer> dossiersList;
}
