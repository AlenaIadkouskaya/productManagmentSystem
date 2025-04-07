package org.example.config;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection connection;
    private static final ConfigReader configReader = new ConfigReader("application.yml");

    public void init() {
        try {
            connection = DriverManager.getConnection(
                    configReader.getUrl(),
                    configReader.getUser(),
                    configReader.getPassword()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się połączyć z bazą danych", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}