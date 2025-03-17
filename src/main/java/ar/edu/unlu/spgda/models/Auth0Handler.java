package ar.edu.unlu.spgda.models;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.auth0.net.Request;
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
        
        logger.debug(String.format("Se ejecuta el método createProfessor. [newUser = %s]", newUser));

        initializeManagementApiHandler();

        Response<User> responseUser = managementAPI
            .users()
            .create(newUser)
            .execute(); // TODO: Arroja APIException porque el token no tiene el permiso create:users.
        int statusCode = responseUser.getStatusCode();
        logger.info(String.format("Status code: %d.", statusCode));

        if (statusCode >= 200 && statusCode < 300)
            logger.debug("Usuario creado.");

        assignProfessorRole(responseUser);

    }

    // Obtiene los roles del usuario.
    public String getUserRoles(User user) throws Auth0Exception {

        initializeManagementApiHandler();

        //...

        return null;
    }

    public String getUserIdByEmail(String email) throws Auth0Exception {
        UserFilter filter = new UserFilter();
        filter.withQuery("email:" + email);
        
        Request<UsersPage> request = managementAPI.users().list(filter);
        Response<UsersPage> response = request.execute();
        UsersPage usersPage = response.getBody();
        
        if (usersPage.getItems().size() > 0) {
            User user = usersPage.getItems().get(0);
            return user.getId();
        }
        
        return null;
    }

    
    /* Private */

    private static Auth0Handler instance = null;
    private static final Logger logger = LoggerFactory.getLogger(Auth0Handler.class);
    private Dotenv dotenv;
    private ManagementAPI managementAPI;
    private AuthAPI authAPI;

    private Auth0Handler() throws Auth0Exception {
        
        dotenv = Dotenv.load();

        authAPI = AuthAPI.newBuilder(
            dotenv.get("AUTH0_DOMAIN"),
            dotenv.get("AUTH0_APP_CLIENT_ID"),
            dotenv.get("AUTH0_APP_SECRET")
        ).build();

    }

    /**
     * Configura el manejador de la API de administración de Auth0 utilizando
     * el token de acceso del usuario.
     * 
     * @throws Auth0Exception
     */
    private void initializeManagementApiHandler() throws Auth0Exception {

        String accessToken = getToken();
        managementAPI = ManagementAPI
            .newBuilder(
                dotenv.get("AUTH0_DOMAIN"),
                accessToken
            ).build();

    }

    // Obtiene token Auth0.
    private String getToken() throws Auth0Exception {
        
        TokenRequest tokenRequest = authAPI.requestToken(
            "https://%s/api/v2/".formatted(
                dotenv.get("AUTH0_DOMAIN")
            )
        );
        logger.debug("Pedido de obtención de token Auth0...");
        TokenHolder holder = tokenRequest.setScope("create:client_grants create:users").execute().getBody();
        String accessToken = holder.getAccessToken();
        logger.debug("Token Auth0 obtenido.");

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
            logger.debug("Rol asignado.");
        
    }

}