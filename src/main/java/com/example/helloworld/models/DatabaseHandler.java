package com.example.helloworld.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseHandler {

    static public DatabaseHandler getInstance() throws SQLException {
        if (instance == null) new DatabaseHandler();
        return instance;
    }

    // Ejecuta una sentencia en la tabla.
    public void executeStatement(String statement, List<Object> statementAttributes)
        throws SQLException, NotValidAttributeException
    {
        logger.info(String.format( // logger.debug
            "executeStatement(%s, %s)", statement,  statementAttributes
        ));
        logger.info(String.format( // logger.debug
            "statementAttributes.size(): %d", statementAttributes.size()
        ));

        PreparedStatement preparedStatement = connection.prepareStatement(statement);
        for (int index = 1; index <= statementAttributes.size(); index++) {
            logger.info(String.format("index: %d", index)); // logger.debug

            if (statementAttributes.get(index - 1) instanceof Integer)
                preparedStatement.setInt(index, (Integer) statementAttributes.get(index - 1));
            else if (statementAttributes.get(index - 1) instanceof String)
                preparedStatement.setString(index, (String) statementAttributes.get(index - 1));
            else if (statementAttributes.get(index - 1) instanceof Float)
                preparedStatement.setFloat(index, (Float) statementAttributes.get(index - 1));
            else if (statementAttributes.get(index - 1) instanceof Double)
                preparedStatement.setDouble(index, (Double) statementAttributes.get(index - 1));
            else if (statementAttributes.get(index - 1) instanceof Date)
                preparedStatement.setDate(index, (Date) statementAttributes.get(index - 1));
            else
                throw new NotValidAttributeException(
                    "El tipo de dato que se quiere agregar a la sentencia SQL no es válido."
                );
        }
        preparedStatement.executeUpdate();
        logger.info("Sentencia ejecutada."); // logger.debug
    }

    // Ejecuta una consulta en la tabla.
    public ResultSet executeQuery(String query)
        throws SQLException, NotValidAttributeException
    {
        logger.info(String.format( // logger.debug
            "executeQuery(%s)", query
        ));

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        logger.info("Query ejecutado."); // logger.debug

        return resultSet;
    }


    /* Private */

    private static DatabaseHandler instance;
    private static final Logger logger = LogManager.getLogger(DatabaseHandler.class);
    private String db_url;
    private String db_user;
    private String db_password;
    private Connection connection;

    private DatabaseHandler() throws SQLException {
        Dotenv dotenv = Dotenv.load();
        db_url = dotenv.get("POSTGRES_URL");
        db_user = dotenv.get("POSTGRES_USER");
        db_password = dotenv.get("POSTGRES_PASSWORD");

        connection = DriverManager.getConnection(db_url, db_user, db_password);
    }
}