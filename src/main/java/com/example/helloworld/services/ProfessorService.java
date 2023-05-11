package com.example.helloworld.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.roles.Role;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Response;
import com.auth0.net.TokenRequest;
import com.example.helloworld.models.Professor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

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

        // TODO: Validar atributos.

        PreparedStatement statement = null;
        Connection conn = null;
        Dotenv dotenv;
        boolean error = false;
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

            /* TODO: Realizar petición a Auth0 para crear el usuario.
             * Si falla, se hace rollback de lo anterior y arroja excepción.
             */

            // Obtener token.
            AuthAPI authAPI = AuthAPI.newBuilder(
                dotenv.get("AUTH0_DOMAIN"),
                dotenv.get("AUTH0_APP_CLIENT_ID"),
                dotenv.get("AUTH0_APP_SECRET")
            ).build();
            TokenRequest tokenRequest = authAPI.requestToken(
                String.format(
                    "https://%s/api/v2/",
                    dotenv.get("AUTH0_DOMAIN")
                )
            );
            logger.info("Pedido de obtención de token Auth0.");
            TokenHolder holder = tokenRequest.execute().getBody();
            String accessToken = holder.getAccessToken();
            logger.info("Token Auth0 obtenido.");

            // Configuración de los datos del usuario que se quiere crear en Auth0.
            User newUser = new User(dotenv.get("AUTH0_DB_CONNECTION"));
            newUser.setEmail(email);
            newUser.setName(first_name);
            newUser.setFamilyName(last_name);
            newUser.setPassword(password.toCharArray());

            // Realiza petición a la API de Auth0 para crear el usuario.
            ManagementAPI mgmt = ManagementAPI
                .newBuilder(
                    dotenv.get("AUTH0_DOMAIN"),
                    accessToken
                ).build();
            logger.info("Pedido de creación de usuario...");
            Response<User> responseUser = mgmt.users().create(newUser).execute();
            int statusCode = responseUser.getStatusCode();
            logger.info(String.format("Status code: %d.", statusCode));

            // Le asigna el rol de docente.
            var listaRoles = new ArrayList<String>();
            listaRoles.add(dotenv.get("AUTH0_ROLID_DOCENTE"));
            logger.info("Pedido de asignación de rol a usuario...");
            Response<Void> responseVoid = mgmt
                .users()
                .addRoles(
                    responseUser.getBody().getId(),
                    listaRoles
                ).execute();
            statusCode = responseVoid.getStatusCode();
            logger.info(String.format("Status code: %d.", statusCode));
        }
        catch (SQLException e) {
            error = true;
            logger.error("SQLException: " + e.getMessage());
        }
        catch (APIException e) {
            error = true;
            logger.error("APIException: " + e.getMessage());
        }
        catch (Auth0Exception e) {
            error = true;
            logger.error("Auth0Exception: " + e.getMessage());
        }
        catch (Exception e) {
            error = true;
            logger.error("Exception: " + e.getMessage());
        }
        finally {
            // Si hubo error, realiza un rollback del INSERT.
            if (error) {
                // TODO: realizar rollback y generar Throw.
            }
            
            // Cierra la conexión.
            if (statement != null) statement.close();
            if (conn != null) conn.close();
        }

        // Todo salió OK; se devuelve el docente creado.
        return new Professor(email, first_name, last_name, legajo);
    }


    /* Private */

    private static final Logger logger = LogManager.getLogger(ProfessorService.class);
}
