package ar.edu.unlu.spgda.requests;

public class UpdateCourseEvaluationCriteriasOrderRequest {
    private Long id; // El ID del CourseEvaluationCriteria
    private long orden; // El nuevo número de orden

    // Constructores vacíos son buena práctica para que Jackson instancie bien
    public UpdateCourseEvaluationCriteriasOrderRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getOrden() {
        return orden;
    }

    public void setOrden(long orden) {
        this.orden = orden;
    }
}

