package com.example.helloworld.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseEvaluationCriteria;
import com.example.helloworld.repositories.CourseRepository;
import com.example.helloworld.repositories.CourseEvaluationCriteriaRepository;

@Service
public class CourseEvaluationCriteriaService {

    public List<CourseEvaluationCriteria> getCourseEvaluationCriterias(long courseId) {
        
        // Recupero la cursada asociada al ID

        Optional<Course> course = courseRepository.findById(courseId);

        // Recupero los criterios asociados a dicha cursada

        Optional<List<CourseEvaluationCriteria>> courseEvaluationCriterias = courseEvaluationCriteriaRepository.findByCourse(course.get());

        return courseEvaluationCriterias.get();
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
            courseEvaluationCriteriaRepository.save(criteria);
        }
        
        return "Actualizacion exitosa...";
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

