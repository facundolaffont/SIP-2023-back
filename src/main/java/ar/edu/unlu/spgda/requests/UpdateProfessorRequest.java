package ar.edu.unlu.spgda.requests;

import lombok.Data;

@Data
public class UpdateProfessorRequest {
    private String id;
    private String email;
    private String nombre;
    private String apellido;
    private Integer legajo;
    // Por el momento no se incluyen ni rol ni password para la modificación
}
