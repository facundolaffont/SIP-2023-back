package ar.edu.unlu.spgda.requests;

import lombok.Data;

@Data
public class UpdateSubjectRequest {
    private Long id;
    private Long careerId;
    private Integer subjectCode;
    private String subjectName;
}