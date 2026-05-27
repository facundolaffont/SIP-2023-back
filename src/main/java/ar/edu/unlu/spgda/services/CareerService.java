package ar.edu.unlu.spgda.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unlu.spgda.models.Career;
import ar.edu.unlu.spgda.models.Comission;
import ar.edu.unlu.spgda.repositories.CareerRepository;

@Service
public class CareerService {
    @Autowired private CareerRepository careerRepository;

    public List<Career> getAllCareers() {
        return careerRepository.findAll();
    }
}
