package org.example;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class makes the connection between the application and the DataBase
 */

public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(
            ConnectionFactory.class.getName()
    );
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = getEnv("DB_URL", "jdbc:postgresql://localhost:5432/postgres?currentSchema=schema_ass3");
    private static final String USER = getEnv("DB_USER", "postgres");
    private static final String PASS = getEnv("DB_PASSWORD", "postgres");

    private static String getEnv(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isEmpty()) ? fallback : value;
    }

    private static ConnectionFactory singleInstance = new ConnectionFactory();

    private ConnectionFactory() {
        try{
            Class.forName(DRIVER);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "An error occured while trying to connect to the database");
            e.printStackTrace();
        }
        return connection;

    }
    public static Connection getConnection() {
        return singleInstance.createConnection();
    }
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "An error occured while trying to close the connection");
            }
        }
    }
    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "An error occured while trying to close the statement");
            }
        }
    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "An error occured while trying to close the ResultSet");
            }
        }
    }
}
