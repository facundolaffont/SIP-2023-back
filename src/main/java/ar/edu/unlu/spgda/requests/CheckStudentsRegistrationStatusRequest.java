package ar.edu.unlu.spgda.requests;

import java.util.List;
import lombok.Data;

@Data
public class CheckStudentsRegistrationStatusRequest {

    Long courseId;
    List<Integer> dossierList;

}
