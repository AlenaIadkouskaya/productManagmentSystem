package org.example.service.db.orderStorage;

import org.example.model.Order;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

public class DatabaseOrderStorageServiceImplTest {
    private DatabaseOrderStorageServiceImpl service;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private Statement mockStatement;

    @BeforeEach
    void setUp() throws SQLException {

        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockStatement = mock(Statement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);

        service = new DatabaseOrderStorageServiceImpl(mockConnection);
    }

    @Test
    void should_add_orders_successfully() throws SQLException {
        // given
        Order order1 = new Order("Order 1", "2024-05-01", 100.0, 450.0);
        Order order2 = new Order("Order 2", "2024-06-01", 120.0, 540.0);
        List<Order> orders = Arrays.asList(order1, order2);

        // when
        String result = service.addOrders(orders);

        // then
        assertEquals("Zamówienia zostały pomyślnie dodane do bazy danych!", result);
        verify(mockPreparedStatement, times(2)).setString(eq(1), anyString());
        verify(mockPreparedStatement, times(2)).setString(eq(2), anyString());
        verify(mockPreparedStatement, times(2)).setDouble(eq(3), anyDouble());
        verify(mockPreparedStatement, times(2)).setDouble(eq(4), anyDouble());
        verify(mockPreparedStatement, times(2)).addBatch();
        verify(mockPreparedStatement).executeBatch();
    }

    @Test
    void should_handle_sql_exception_when_adding_orders() throws SQLException {
        // given
        Order order = new Order("Order 1", "2024-05-01", 100.0, 450.0);
        List<Order> orders = Arrays.asList(order);

        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.addOrders(orders));

        // then
        assertTrue(exception.getMessage().contains("Błąd podczas dodawania zamówień do bazy danych"));
    }

    @Test
    void should_create_orders_table_if_not_exists() throws SQLException {
        // when
        service.createOrdersTable();

        // then
        verify(mockStatement).execute("CREATE TABLE IF NOT EXISTS orders (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "date VARCHAR(255), " +
                "price_in_usd DECIMAL(10, 2), " +
                "price_in_pln DECIMAL(10, 2))");
    }

    @Test
    void should_handle_sql_exception_when_creating_table() throws SQLException {
        when(mockConnection.createStatement()).thenThrow(SQLException.class);

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.createOrdersTable());

        // then
        assertTrue(exception.getMessage().contains("Błąd podczas wykonywania zapytania"));
    }
}