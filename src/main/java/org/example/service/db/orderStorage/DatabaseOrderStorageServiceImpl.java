package org.example.service.db.orderStorage;

import org.example.model.Order;
import org.example.service.db.DatabaseService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseOrderStorageServiceImpl implements OrderStorageService, DatabaseService {
    private final Connection connection;

    public DatabaseOrderStorageServiceImpl(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public String addOrders(List<Order> orders) {
        String query = "INSERT INTO orders (name, date, price_in_usd, price_in_pln) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            addOrdersToBatch(orders, preparedStatement);
            executeBatch(preparedStatement);
            return "Zamówienia zostały pomyślnie dodane do bazy danych!";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Błąd podczas dodawania zamówień do bazy danych", e);
        }
    }

    private void addOrdersToBatch(List<Order> orders, PreparedStatement preparedStatement) throws SQLException {
        for (Order order : orders) {
            preparedStatement.setString(1, order.getName());
            preparedStatement.setString(2, order.getDate());
            preparedStatement.setDouble(3, order.getPriceInUSD());
            preparedStatement.setDouble(4, order.getPriceInPLN());
            preparedStatement.addBatch();
        }
    }

    private void executeBatch(PreparedStatement preparedStatement) throws SQLException {
        int[] results = preparedStatement.executeBatch();
    }

    @Override
    public void createOrdersTable() {
        String query = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "date VARCHAR(255), " +
                "price_in_usd DECIMAL(10, 2), " +
                "price_in_pln DECIMAL(10, 2))";

        executeUpdate(query, "Tabela 'orders' została utworzona lub już istnieje.");
    }

    private void executeUpdate(String query, String successMessage) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            System.out.println(successMessage);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Błąd podczas wykonywania zapytania", e);
        }
    }
}