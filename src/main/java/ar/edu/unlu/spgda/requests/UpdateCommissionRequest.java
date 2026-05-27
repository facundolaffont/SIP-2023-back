package ar.edu.unlu.spgda.requests;

import lombok.Data;

@Data
public class UpdateCommissionRequest {
    private Integer id;
    private Long subjectId;
    private Integer commissionNumber;
}
