package org.example.windows;

import org.example.service.db.orderStorage.DatabaseOrderStorageServiceImpl;
import org.example.service.db.orderStorage.OrderStorageService;
import org.example.service.db.orderStorage.XmlOrderStorageServiceImpl;
import org.example.service.currency.CurrencyConverter;
import org.example.service.currency.CurrencyService;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class MainAppGUI extends JFrame {

    private final CurrencyConverter currencyConverter = new CurrencyConverter(new CurrencyService());
    private final OrderStorageService databaseOrderService;
    private final OrderStorageService xmlOrderService;
    private final Connection connection;

    public MainAppGUI(Connection connection) {
        this.databaseOrderService = new DatabaseOrderStorageServiceImpl(connection);
        this.xmlOrderService = new XmlOrderStorageServiceImpl();
        this.connection = connection;

        setTitle("Aplikacja do składania zamówienia");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton addOrderButton = new JButton("Dodaj zamówienie");
        JButton showOrdersButton = new JButton("Pokaż zamówienia");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(addOrderButton);
        panel.add(showOrdersButton);

        add(panel, BorderLayout.NORTH);

        addOrderButton.addActionListener(e -> openAddOrderFrame());

        showOrdersButton.addActionListener(e -> showOrders());
    }

    private void openAddOrderFrame() {
        new AddOrderFrame(currencyConverter, databaseOrderService, xmlOrderService);
    }

    private void showOrders() {
        new SearchResultsWindow(connection).setVisible(true);
    }
}