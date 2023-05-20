package com.example.helloworld.services;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.helloworld.models.DatabaseHandler;

@Service
public class CourseService {

    public List<List<String>> getProfessor() throws SQLException {
        return DatabaseHandler.getInstance().select();
    }
    
}
