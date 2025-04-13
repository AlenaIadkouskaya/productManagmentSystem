package org.example.service.db.orderSearch;

import org.example.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderSearchServiceImplTest {
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private OrderSearchServiceImpl orderSearchService;

    @BeforeEach
    void setUp() throws SQLException {

        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        orderSearchService = new OrderSearchServiceImpl(mockConnection);
    }

    @Test
    void should_return_orders_when_valid_filters_are_provided() throws SQLException {
        // given
        String nameFilter = "order";
        String fromDate = "2024-01-01";
        String toDate = "2024-12-31";

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("name")).thenReturn("Test Order");
        when(mockResultSet.getString("date")).thenReturn("2024-05-01");
        when(mockResultSet.getDouble("price_in_usd")).thenReturn(100.0);
        when(mockResultSet.getDouble("price_in_pln")).thenReturn(450.0);

        // when
        List<Order> orders = orderSearchService.searchOrders(nameFilter, fromDate, toDate);

        // then
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getName()).isEqualTo("Test Order");
        assertThat(orders.get(0).getDate()).isEqualTo("2024-05-01");
        assertThat(orders.get(0).getPriceInUSD()).isEqualTo(100.0);
        assertThat(orders.get(0).getPriceInPLN()).isEqualTo(450.0);
    }

    @Test
    void should_return_empty_list_when_no_orders_found() throws SQLException {
        // given
        String nameFilter = "nonexistent";
        String fromDate = "2024-01-01";
        String toDate = "2024-12-31";

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // when
        List<Order> orders = orderSearchService.searchOrders(nameFilter, fromDate, toDate);

        // then
        assertThat(orders).isEmpty();
    }

    @Test
    void should_throw_exception_when_sql_exception_occurs() throws SQLException {
        // given
        String nameFilter = "order";
        String fromDate = "2024-01-01";
        String toDate = "2024-12-31";

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // when
        Executable e = () -> orderSearchService.searchOrders(nameFilter, fromDate, toDate);

        // then
        assertThrows(SQLException.class, e);
    }

    @Test
    void should_return_orders_when_only_name_filter_is_provided() throws SQLException {
        // given
        String nameFilter = "Test Order";
        String fromDate = null;
        String toDate = null;

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("name")).thenReturn("Test Order");
        when(mockResultSet.getString("date")).thenReturn("2024-05-01");
        when(mockResultSet.getDouble("price_in_usd")).thenReturn(100.0);
        when(mockResultSet.getDouble("price_in_pln")).thenReturn(450.0);

        // when
        List<Order> orders = orderSearchService.searchOrders(nameFilter, fromDate, toDate);

        // then
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getName()).isEqualTo("Test Order");
    }

    @Test
    void should_return_orders_when_only_date_filter_is_provided() throws SQLException {
        // given
        String nameFilter = null;
        String fromDate = "2024-01-01";
        String toDate = "2024-12-31";

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("name")).thenReturn("Test Order");
        when(mockResultSet.getString("date")).thenReturn("2024-05-01");
        when(mockResultSet.getDouble("price_in_usd")).thenReturn(100.0);
        when(mockResultSet.getDouble("price_in_pln")).thenReturn(450.0);

        // when
        List<Order> orders = orderSearchService.searchOrders(nameFilter, fromDate, toDate);

        // then
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getName()).isEqualTo("Test Order");
    }
}