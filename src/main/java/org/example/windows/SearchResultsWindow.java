package org.example.windows;

import com.toedter.calendar.JDateChooser;
import org.example.model.Order;
import org.example.service.db.orderSearch.OrderSearchService;
import org.example.service.db.orderSearch.OrderSearchServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchResultsWindow extends JFrame {
    private final JTextField nameFilterField;
    private final JDateChooser fromDateChooser;
    private final JDateChooser toDateChooser;
    private final DefaultTableModel model;
    private final OrderSearchService orderSearchService;

    public SearchResultsWindow(Connection connection) {
        this.orderSearchService = new OrderSearchServiceImpl(connection);
        setTitle("Wyniki wyszukiwania");
        setMinimumSize(new Dimension(700, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        nameFilterField = new JTextField(20);
        fromDateChooser = new JDateChooser();
        toDateChooser = new JDateChooser();
        model = createTableModel();
        setupLayout();
        pack();
    }

    private void setupLayout() {
        JPanel filterPanel = createFilterPanel();
        JTable table = createTable();
        JScrollPane scrollPane = new JScrollPane(table);
        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        addNameFilterPanel(filterPanel, gbc);
        addDateFilterPanel(filterPanel, gbc);
        addSearchButtonPanel(filterPanel, gbc);
        return filterPanel;
    }

    private void addNameFilterPanel(JPanel filterPanel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(createNameFilterPanel(), gbc);
    }

    private JPanel createNameFilterPanel() {
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new JLabel("Filtr po nazwie: "));
        namePanel.add(nameFilterField);
        JButton clearNameFilterButton = new JButton("X");
        clearNameFilterButton.addActionListener(this::clearNameFilter);
        namePanel.add(clearNameFilterButton);
        return namePanel;
    }

    private void addDateFilterPanel(JPanel filterPanel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        filterPanel.add(createDateFilterPanel(), gbc);
    }

    private JPanel createDateFilterPanel() {
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setupDateChoosers();
        datePanel.add(new JLabel("Data od:"));
        datePanel.add(fromDateChooser);
        datePanel.add(new JLabel("do:"));
        datePanel.add(toDateChooser);
        addClearDateFilterButton(datePanel);
        return datePanel;
    }

    private void setupDateChoosers() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        fromDateChooser.setLocale(new Locale("pl", "PL"));
        fromDateChooser.setDateFormatString("yyyy-MM-dd");
        toDateChooser.setLocale(new Locale("pl", "PL"));
        toDateChooser.setDateFormatString("yyyy-MM-dd");
        fromDateChooser.setPreferredSize(new Dimension(150, fromDateChooser.getPreferredSize().height));
        toDateChooser.setPreferredSize(new Dimension(150, toDateChooser.getPreferredSize().height));
    }

    private void addClearDateFilterButton(JPanel datePanel) {
        JButton clearDateFilterButton = new JButton("X");
        clearDateFilterButton.addActionListener(this::clearDateFilter);
        datePanel.add(clearDateFilterButton);
    }

    private void addSearchButtonPanel(JPanel filterPanel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 2;
        filterPanel.add(createSearchButtonPanel(), gbc);
    }

    private JPanel createSearchButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton filterButton = new JButton("Szukaj");
        filterButton.addActionListener(this::onSearchButtonClick);
        buttonPanel.add(filterButton);
        return buttonPanel;
    }

    private JTable createTable() {
        JTable table = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        sorter.setSortable(2, false);
        sorter.setSortable(3, false);
        return table;
    }

    private void onSearchButtonClick(ActionEvent e) {
        String nameFilter = nameFilterField.getText().trim();
        String fromDateString = getFormattedDate(fromDateChooser.getDate());
        String toDateString = getFormattedDate(toDateChooser.getDate());
        model.setRowCount(0);
        loadOrders(nameFilter, fromDateString, toDateString);
    }

    private String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return (date != null) ? sdf.format(date) : "";
    }

    private void loadOrders(String nameFilter, String fromDate, String toDate) {
        try {
            List<Order> orders = orderSearchService.searchOrders(nameFilter, fromDate, toDate);
            if (orders.isEmpty()) {
                showMessage("Nie znaleziono żadnych zamówień.");
            } else {
                updateTableWithOrders(orders);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Błąd ładowania danych");
        }
    }

    private void updateTableWithOrders(List<Order> orders) {
        model.setRowCount(0);
        for (Order order : orders) {
            model.addRow(new Object[]{
                    order.getName(),
                    order.getDate(),
                    order.getPriceInUSD(),
                    order.getPriceInPLN()
            });
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void clearNameFilter(ActionEvent e) {
        nameFilterField.setText("");
        loadOrders("", "", "");
    }

    private void clearDateFilter(ActionEvent e) {
        fromDateChooser.setDate(null);
        toDateChooser.setDate(null);
        loadOrders(nameFilterField.getText().trim(), "", "");
    }

    private DefaultTableModel createTableModel() {
        String[] columnNames = {"Imię", "Data", "Cena w USD", "Cena w PLN"};
        return new DefaultTableModel(columnNames, 0);
    }
}