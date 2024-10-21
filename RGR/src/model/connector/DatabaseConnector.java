package model.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class ConnectionProperties {
    public static final String USER = "postgres";
    public static final String PASSWORD = "root";
}

public class DatabaseConnector {
    private Connection psqlConnection;

    public Connection connect() throws IllegalStateException
    {
        if (psqlConnection != null)
            return psqlConnection;

        try {
            DriverManager.registerDriver(new org.postgresql.Driver());

            Properties properties = new Properties();
            properties.setProperty("user", ConnectionProperties.USER);
            properties.setProperty("password", ConnectionProperties.PASSWORD);

            String url = "jdbc:postgresql://localhost:5432/postgres";
            psqlConnection = DriverManager.getConnection(url, properties);
            System.out.println("Successfully connected to the database.");

            return psqlConnection;

        } catch (NullPointerException e) {
            throw new IllegalStateException("Failed to load PostgreSQL JDBC driver.");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to connect to the database.");
        }
    }
}
