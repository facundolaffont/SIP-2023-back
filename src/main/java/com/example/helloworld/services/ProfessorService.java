package com.example.helloworld.services;

import org.springframework.stereotype.Service;
import com.example.helloworld.models.Professor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;

@Log4j2 // Agregar un logger llamado log.
@Service
public class ProfessorService {

    // crear profesor y guardar en la BD. si falla, aborta.
    public Professor create(
        String email,
        String first_name,
        String last_name,
        int legajo,
        String password,
        String role
    ) throws SQLException{

        // Loguea los datos que se quieren insertar.
        log.debug("email: " + email);
        log.debug("first_name: " + first_name);
        log.debug("last_name: " + last_name);
        log.debug("legajo: " + legajo);
        log.debug("role: " + role);

        PreparedStatement statement = null;
        Connection conn = null;
        try {
            Dotenv dotenv = Dotenv.load();
            String db_url = dotenv.get("POSTGRES_URL");
            String db_user = dotenv.get("POSTGRES_USER");
            String db_password = dotenv.get("POSTGRES_PASSWORD");
            conn = DriverManager.getConnection(db_url, db_user, db_password);
            
            String query = "INSERT INTO usuario (legajo, nombre, apellido, email, rol) VALUES (?, ?, ?, ?, ?)";
            statement = conn.prepareStatement(query);
            statement.setInt(1, legajo);
            statement.setString(2, first_name);
            statement.setString(3, last_name);
            statement.setString(4, email);
            statement.setString(5, role);
            statement.executeUpdate();

            log.debug("INSERT realizado.");
        }
        catch (SQLException ex) { System.out.println(ex); }
        finally {
            // Cerrar la conexión después de usarla
            if (statement != null) statement.close();
            if (conn != null) conn.close();
        }
    
        // TODO: Si está OK, realizar petición a Auth0 para crear el usuario. Si falla, se hace rollback de lo anterior y arroja excepción.

        return new Professor(email, first_name, last_name, legajo);
    }
}
