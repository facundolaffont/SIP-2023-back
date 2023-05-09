package com.example.helloworld.services;

import org.springframework.stereotype.Service;

import com.example.helloworld.models.Professor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import io.github.cdimascio.dotenv.Dotenv;


@Service
public class ProfessorService {
    public Professor create(String email, String first_name, String last_name, int legajo, String password, String role) throws SQLException{
        // crear profesor y guardar en la BD.  si falla, aborta.

        Dotenv dotenv = Dotenv.load();
        String db_url = dotenv.get("POSTGRES_URL");
        String db_user = dotenv.get("POSTGRES_USER");
        String db_password = dotenv.get("POSTGRES_PASSWORD");

        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "example");
            String query = "INSERT INTO usuario (legajo, nombre, apellido, email, rol) VALUES (?, ?, ?, ?, ?)";
            statement = conn.prepareStatement(query);
            statement.setInt(1, legajo);
            statement.setString(2, first_name);
            statement.setString(3, last_name);
            statement.setString(4, email);
            statement.setString(5, role);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {
            // Cerrar la conexión después de usarla
            System.out.println("llegue hasta aca");
            if (statement != null) {
                statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    
        
    
        // si ok: petición a Auth0 para crear el usuario.    si falla, rollback de lo anterior.
        // si ok: return Professor.
        return new Professor(email, first_name, last_name, legajo);
    }
}
