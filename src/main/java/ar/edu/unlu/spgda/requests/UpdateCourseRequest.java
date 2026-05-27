package ar.edu.unlu.spgda.requests;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class UpdateCourseRequest {
    private Long id;
    private Integer commissionId;
    private Integer anio;
    private LocalDate initialDate;
    private LocalDate endDate;
    private List<String> professorIds;
}

