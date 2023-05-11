package com.example.helloworld.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.auth0.client.auth.AuthAPI;
import com.example.helloworld.models.Professor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

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
    ) throws SQLException {

        // Loguea los datos que se quieren insertar.
        logger.info(
            String.format(
                "create(email: %s, first_name: %s, last_name: %s, legajo: %s, password: %s, role: %s)",
                email,
                first_name,
                last_name,
                String.valueOf(legajo),
                password,
                role
            )
        );

        // TODO: Validar atributo.

        PreparedStatement statement = null;
        Connection conn = null;
        Dotenv dotenv;
        try {
            dotenv = Dotenv.load();
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

            logger.info("INSERT realizado.");

            // TODO: Si está OK, realizar petición a Auth0 para crear el usuario. Si falla, se hace rollback de lo anterior y arroja excepción.
        }
        catch (SQLException ex) { System.out.println(ex); }
        finally {
            // Cerrar la conexión después de usarla
            if (statement != null) statement.close();
            if (conn != null) conn.close();
        }

        return new Professor(email, first_name, last_name, legajo);
    }


    /* Private */

    private static final Logger logger = LogManager.getLogger(ProfessorService.class);
}
