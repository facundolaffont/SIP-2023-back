package com.example.helloworld.models;

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Response;
import com.auth0.net.TokenRequest;
import io.github.cdimascio.dotenv.Dotenv;

public class Auth0Handler {

    static public Auth0Handler getInstance() throws Auth0Exception {
        if (instance == null) instance = new Auth0Handler();

        return instance;
    }

    // Realiza petición a la API de Auth0 para crear el usuario.
    // Arroja una excepción si no se pudo.
    public void createProfessor(User newUser) throws Auth0Exception {
        
        logger.info("Pedido de creación de usuario...");
        Response<User> responseUser = managementAPI.users().create(newUser).execute(); // Arroja APIException.
        int statusCode = responseUser.getStatusCode();
        logger.info(String.format("Status code: %d.", statusCode));

        if (statusCode >= 200 && statusCode < 300)
            logger.info("Usuario creado."); // logger.debug

        assignProfessorRole(responseUser);

    }

    
    /* Private */

    private static Auth0Handler instance = null;
    private static final Logger logger = LogManager.getLogger(Auth0Handler.class);
    private Dotenv dotenv;
    private ManagementAPI managementAPI;
    private AuthAPI authAPI;

    private Auth0Handler() throws Auth0Exception {
        dotenv = Dotenv.load();

        String accessToken = getToken();
        managementAPI = ManagementAPI
            .newBuilder(
                dotenv.get("AUTH0_DOMAIN"),
                accessToken
            ).build();
        
        authAPI = AuthAPI.newBuilder(
            dotenv.get("AUTH0_DOMAIN"),
            dotenv.get("AUTH0_APP_CLIENT_ID"),
            dotenv.get("AUTH0_APP_SECRET")
        ).build();
    }

    // Obtiene token Auth0.
    private String getToken() throws Auth0Exception {
        
        TokenRequest tokenRequest = authAPI.requestToken(
            String.format(
                "https://%s/api/v2/",
                dotenv.get("AUTH0_DOMAIN")
            )
        );
        logger.info("Pedido de obtención de token Auth0..."); // logger.debug
        TokenHolder holder = tokenRequest.execute().getBody();
        String accessToken = holder.getAccessToken();
        logger.info("Token Auth0 obtenido."); // logger.debug

        return accessToken;

    }

    // Le asigna el rol de docente.
    private void assignProfessorRole(Response<User> responseUser)
        throws Auth0Exception
    {
        var listaRoles = new ArrayList<String>();
        listaRoles.add(dotenv.get("AUTH0_ROLID_DOCENTE"));
        logger.info("Pedido de asignación de rol a usuario...");
        Response<Void> responseVoid = managementAPI
            .users()
            .addRoles(
                responseUser.getBody().getId(),
                listaRoles
            ).execute();
        int statusCode = responseVoid.getStatusCode();
        logger.info(String.format("Status code: %d.", statusCode));

        if (statusCode >= 200 && statusCode < 300)
            logger.info("Rol asignado."); // logger.debug
        
    }

}