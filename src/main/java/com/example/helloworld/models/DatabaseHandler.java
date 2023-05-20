package com.example.helloworld.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseHandler {

    public static DatabaseHandler getInstance() { return instance; }

    public void createConnection() throws SQLException {
        try { connection = DriverManager.getConnection(db_url, db_user, db_password); }
        catch (SQLException e) {
            throw e;
        }
    }

    // Realiza el INSERT en la tabla.
    public void insert(String insertStatement, List<Object> statementAttributes)
        throws SQLException, NotValidAttributeException
    {
        logger.info(String.format(
            "insert(%s, %s)", insertStatement,  statementAttributes
        ));
        logger.info(String.format(
            "statementAttributes.size(): %d", statementAttributes.size()
        ));

        PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
        for (int index = 1; index <= statementAttributes.size(); index++) {
            logger.info(String.format("index: %d", index));

            if (statementAttributes.get(index - 1) instanceof Integer)
                preparedStatement.setInt(index, (Integer) statementAttributes.get(index - 1));
            else if (statementAttributes.get(index - 1) instanceof String)
                preparedStatement.setString(index, (String) statementAttributes.get(index - 1));
            else if (statementAttributes.get(index - 1) instanceof Float)
                preparedStatement.setFloat(index, (Float) statementAttributes.get(index - 1));
            else if (statementAttributes.get(index - 1) instanceof Double)
                preparedStatement.setDouble(index, (Double) statementAttributes.get(index - 1));
            else
                throw new NotValidAttributeException(
                    "El tipo de dato que se quiere insertar no se puede insertar en la BD."
                );
        }
        preparedStatement.executeUpdate();
        logger.info("Se ejecutó un INSERT en la BD.");
    }

    public List<List<String>> select() throws SQLException {
        List<List<String>> datos = new ArrayList<>();
        //String consulta = "SELECT * FROM Cur_Doc";
        String consulta = "SELECT nombre, comisionNro, aniocursada FROM Cur_Doc cd INNER JOIN comision c ON cd.comisionNro=c.numero INNER JOIN Asignatura a ON a.id=c.asignaturaId WHERE legajo=1002";
        PreparedStatement statement = connection.prepareStatement(consulta);
        
        // Ejecutar la consulta y obtener los resultados
        ResultSet resultSet = statement.executeQuery();

            // Acceder a los valores de las columnas
            while (resultSet.next()) {
                List<String> fila = new ArrayList<>();
                // Obtener el valor de la columna y agregarlo a la lista de datos
                String valorColumna1 = resultSet.getString("nombre");
                fila.add(valorColumna1);
                String valorColumna2 = resultSet.getString("comisionNro");
                fila.add(valorColumna2);
                String valorColumna3 = resultSet.getString("anioCursada");
                fila.add(valorColumna3);

                datos.add(fila);
            }
        return datos;
        }


    /* Private */

    private static DatabaseHandler instance = new DatabaseHandler();
    private static final Logger logger = LogManager.getLogger(DatabaseHandler.class);
    private String db_url;
    private String db_user;
    private String db_password;
    private Connection connection;

    private DatabaseHandler() {
        Dotenv dotenv = Dotenv.load();
        db_url = dotenv.get("POSTGRES_URL");
        db_user = dotenv.get("POSTGRES_USER");
        db_password = dotenv.get("POSTGRES_PASSWORD");
    }
}