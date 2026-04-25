package ar.edu.unlu.spgda.responses;

import ar.edu.unlu.spgda.models.Userr;

public record UserResponse(
    String id,
    Integer legajo,
    String rol,
    String nombre,
    String apellido,
    String email
) {
    public static UserResponse fromEntity(Userr u) {
        return new UserResponse(
            u.getId(),
            u.getLegajo(),
            u.getRol(),
            u.getNombre(),
            u.getApellido(),
            u.getEmail()
        );
    }
}