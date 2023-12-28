package com.example.helloworld.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.helloworld.models.Userr;

public interface UserRepository extends JpaRepository<Userr, String> {
    Userr getById(String id);
}
