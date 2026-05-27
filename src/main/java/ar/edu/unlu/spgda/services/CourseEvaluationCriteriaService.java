package ar.edu.unlu.spgda.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.CourseEvaluationCriteria;
import ar.edu.unlu.spgda.repositories.CourseEvaluationCriteriaRepository;
import ar.edu.unlu.spgda.repositories.CourseRepository;
import ar.edu.unlu.spgda.requests.UpdateCourseEvaluationCriteriasOrderRequest;

@Service
public class CourseEvaluationCriteriaService {

    public List<CourseEvaluationCriteria> getCourseEvaluationCriterias(long courseId) {
        
        // Recupero la cursada asociada al ID

        Optional<Course> course = courseRepository.findById(courseId);

        // Recupero los criterios asociados a dicha cursada

        List<CourseEvaluationCriteria> courseEvaluationCriterias = courseEvaluationCriteriaRepository.findByCourseOrderByOrdenAsc(course.get());

        return courseEvaluationCriterias;
    }

    @Autowired private CourseRepository courseRepository;
    @Autowired private CourseEvaluationCriteriaRepository courseEvaluationCriteriaRepository;
    public String save(CourseEvaluationCriteria criteria) {

        // Verifico si el criterio existe

        Optional<List<CourseEvaluationCriteria>> listCriterias = courseEvaluationCriteriaRepository.findByCourseAndCriteria(criteria.getCourse(), criteria.getCriteria());
        
        System.out.println("LIST CRITERIAAAAAA" + listCriterias.get().toString());

        // Si existe lo actualizamos

        if (listCriterias.isPresent() && !listCriterias.get().isEmpty()) {
            System.out.println("llegue1-------------------");
            CourseEvaluationCriteria courseEvaluationCriteria = listCriterias.get().get(0);
            System.out.println("llegue2-------------------");
            courseEvaluationCriteria.setCriteria(criteria.getCriteria());
            System.out.println("VALUE TO PROMOTEEEE" + criteria.getValue_to_promote());
            courseEvaluationCriteria.setValue_to_regulate(criteria.getValue_to_regulate());
            courseEvaluationCriteria.setValue_to_promote(criteria.getValue_to_promote());
            System.out.println("VALUE TO PROMOTE:" + courseEvaluationCriteria.getValue_to_promote());
            System.out.println("llegue3-------------------");
            courseEvaluationCriteriaRepository.save(courseEvaluationCriteria);
        } else { 
            System.out.println("CRITERIOOOOOOO" + criteria.toString());

            // 1. Traemos todos los criterios de la cursada para ver cuántos hay
            List<CourseEvaluationCriteria> criteriosExistentes = 
                courseEvaluationCriteriaRepository.findByCourseOrderByOrdenAsc(criteria.getCourse());

            // 2. Calculamos el orden máximo actual
            long nuevoOrden = 0;
            if (criteriosExistentes != null && !criteriosExistentes.isEmpty()) {
                // Obtenemos el último elemento de la lista (el de mayor orden)
                int ultimoIndice = criteriosExistentes.size() - 1;
                long maxOrden = criteriosExistentes.get(ultimoIndice).getOrden();
                nuevoOrden = maxOrden + 1; // Lo ponemos al final
            }

            // 3. Le seteamos el nuevo orden al criterio antes de guardarlo
            criteria.setOrden(nuevoOrden);

            // 4. Guardamos
            courseEvaluationCriteriaRepository.save(criteria);
        }
        
        return "Actualizacion exitosa...";
    }

    public void updateOrder(List<UpdateCourseEvaluationCriteriasOrderRequest> orderList) {
        // Creamos una lista para ir guardando los criterios modificados
        List<CourseEvaluationCriteria> criteriosAActualizar = new ArrayList<>();

        for (UpdateCourseEvaluationCriteriasOrderRequest dto : orderList) {
            
            // Usamos el findById nativo de Spring (que devuelve un solo Optional)
            // Nota: Si el ID de tu DTO es Long y el Repo usa Integer, agregamos .intValue()
            Optional<CourseEvaluationCriteria> optionalCriteria = courseEvaluationCriteriaRepository.findById(dto.getId());
            
            // Si lo encontró...
            if (optionalCriteria.isPresent()) {
                // Lo sacamos directamente
                CourseEvaluationCriteria criteria = optionalCriteria.get();
                
                // Le pisamos EXCLUSIVAMENTE el orden
                criteria.setOrden(dto.getOrden());
                
                // Lo agregamos a nuestra lista para guardar
                criteriosAActualizar.add(criteria);
            }
        }

        // Guardamos todos los cambios juntos de una sola vez
        courseEvaluationCriteriaRepository.saveAll(criteriosAActualizar);
    }

    public String delete(CourseEvaluationCriteria criteria) {

        // Verifico si el criterio existe

        Optional<List<CourseEvaluationCriteria>> listCriterias = courseEvaluationCriteriaRepository.findByCourseAndCriteria(criteria.getCourse(), criteria.getCriteria());

        // Si existe lo borramos

         if (listCriterias.isPresent() && !listCriterias.get().isEmpty()) {
            System.out.println("llegue1-------------------");
            courseEvaluationCriteriaRepository.delete(criteria);
            System.out.println("llegue2-------------------");
        } 

        return "Actualizacion exitosa...";
    }

}

