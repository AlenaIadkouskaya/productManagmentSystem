package org.example.service.db.orderSearch;

import org.example.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderSearchServiceImpl implements OrderSearchService {
    private final Connection connection;

    public OrderSearchServiceImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Order> searchOrders(String nameFilter, String fromDate, String toDate) throws SQLException {
        List<Order> orders = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM orders WHERE 1=1");

        if (nameFilter != null && !nameFilter.isEmpty()) {
            query.append(" AND LOWER(name) LIKE ?");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            query.append(" AND date >= ?");
        }
        if (toDate != null && !toDate.isEmpty()) {
            query.append(" AND date <= ?");
        }

        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query.toString());

            int paramIndex = 1;
            if (nameFilter != null && !nameFilter.isEmpty()) {
                ps.setString(paramIndex++, "%" + nameFilter.toLowerCase() + "%");
            }
            if (fromDate != null && !fromDate.isEmpty()) {
                ps.setString(paramIndex++, fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                ps.setString(paramIndex++, toDate);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(new Order(
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getDouble("price_in_usd"),
                        rs.getDouble("price_in_pln")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Błąd podczas wykonywania zapytania", e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return orders;
    }
}