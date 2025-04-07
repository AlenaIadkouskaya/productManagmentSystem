package org.example.service.db.orderSearch;

import org.example.model.Order;

import java.sql.SQLException;
import java.util.List;

public interface OrderSearchService {
    List<Order> searchOrders(String nameFilter, String fromDate, String toDate) throws SQLException;
}