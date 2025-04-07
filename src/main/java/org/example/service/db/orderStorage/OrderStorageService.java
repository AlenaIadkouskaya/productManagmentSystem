package org.example.service.db.orderStorage;

import org.example.model.Order;

import java.util.List;

public interface OrderStorageService {
    String addOrders(List<Order> orders);
}