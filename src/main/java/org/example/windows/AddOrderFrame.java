package org.example.windows;

import com.toedter.calendar.JDateChooser;
import org.example.model.Order;
import org.example.service.currency.CurrencyConverter;
import org.example.service.db.orderStorage.DatabaseOrderStorageServiceImpl;
import org.example.service.db.orderStorage.OrderStorageService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddOrderFrame extends JFrame {
    private JTextField nameField;
    private JTextField priceInDollarsField;
    private JTextField priceInZlotyField;
    private JDateChooser dateChooser;
    private final CurrencyConverter currencyConverter;
    private final OrderStorageService databaseOrderService;
    private final OrderStorageService xmlOrderService;

    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private List<Order> orders = new ArrayList<>();

    public AddOrderFrame(CurrencyConverter currencyConverter, OrderStorageService databaseOrderService, OrderStorageService xmlOrderService) {
        this.currencyConverter = currencyConverter;
        this.databaseOrderService = databaseOrderService;
        this.xmlOrderService = xmlOrderService;

        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        setTitle("Dodaj zamówienie");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        nameField = new JTextField(10);
        priceInDollarsField = new JTextField(10);
        priceInZlotyField = new JTextField(10);
        priceInZlotyField.setEditable(false);

        priceInDollarsField.addCaretListener(e -> updatePriceInZlotyField());

        dateChooser = new JDateChooser();
        dateChooser.setLocale(new Locale("pl", "PL"));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setPreferredSize(new Dimension(100, 20));

        addComponentsToPanel(panel);

        tableModel = createTableModel();
        ordersTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        panel.add(scrollPane);

        addOrderButtonAction(panel);
        saveAllButtonAction(panel);

        add(panel);
        setVisible(true);
    }

    private void addComponentsToPanel(JPanel panel) {
        panel.add(new JLabel("Nazwa zamówienia:"));
        panel.add(nameField);
        panel.add(new JLabel("Data zamówienia:"));
        panel.add(dateChooser);
        panel.add(new JLabel("Cena w dolarach:"));
        panel.add(priceInDollarsField);
        panel.add(new JLabel("Cena w złotych:"));
        panel.add(priceInZlotyField);
    }

    private DefaultTableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nazwa");
        model.addColumn("Data");
        model.addColumn("Cena w dolarach");
        model.addColumn("Cena w złotych");
        return model;
    }

    private void updatePriceInZlotyField() {
        try {
            String text = priceInDollarsField.getText().replace(",", ".");
            double priceInDollars = Double.parseDouble(text);
            Date date = dateChooser.getDate();
            if (date != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = dateFormat.format(date);

                double priceInZloty = currencyConverter.convertDollarsToZloty(priceInDollars, dateString);
                priceInZlotyField.setText(String.format("%.2f", priceInZloty));
            }
        } catch (NumberFormatException ex) {
            priceInZlotyField.setText("0.00");
        }
    }

    private void addOrderButtonAction(JPanel panel) {
        JButton addOrderButton = new JButton("Dodaj nowe");
        addOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOrder();
            }
        });
        panel.add(addOrderButton);
    }

    private void addOrder() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(AddOrderFrame.this, "Proszę wprowadzić nazwę zamówienia.");
            return;
        }

        Date date = dateChooser.getDate();
        if (date == null) {
            JOptionPane.showMessageDialog(AddOrderFrame.this, "Proszę wybrać poprawną datę.");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(date);

        double priceInDollars = getPriceInDollars();
        if (priceInDollars < 0) return;

        double priceInZloty = currencyConverter.convertDollarsToZloty(priceInDollars, dateString);
        priceInZloty = Math.round(priceInZloty * 100.0) / 100.0;
        tableModel.addRow(new Object[]{name, dateString, priceInDollars, priceInZloty});

        orders.add(new Order(name, dateString, priceInDollars, priceInZloty));

        clearFields();
    }

    private double getPriceInDollars() {
        double priceInDollars = 0;
        try {
            String text = priceInDollarsField.getText().replace(",", ".");
            priceInDollars = Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(AddOrderFrame.this, "Niepoprawna cena w dolarach.");
        }
        return priceInDollars;
    }

    private void clearFields() {
        nameField.setText("");
        priceInDollarsField.setText("");
        priceInZlotyField.setText("");
        dateChooser.setDate(new Date());
    }

    private void saveAllButtonAction(JPanel panel) {
        JButton saveAllButton = new JButton("Zapisz wszystkie");
        saveAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAllOrders();
            }
        });
        panel.add(saveAllButton);
    }

    private void saveAllOrders() {
        boolean isSuccess = true;
        StringBuilder successMessage = new StringBuilder();
        StringBuilder errorMessage = new StringBuilder();
        Connection connection = null;

        try {
            connection = ((DatabaseOrderStorageServiceImpl) databaseOrderService).getConnection();
            connection.setAutoCommit(false);

            String messageDB = databaseOrderService.addOrders(orders);
            successMessage.append(messageDB + "\n");

            String messageXML = xmlOrderService.addOrders(orders);
            successMessage.append("Zapisano do pliku XML:\n" + messageXML);

            orders.clear();

            connection.commit();
        } catch (Exception ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                    errorMessage.append(ex.getMessage());
                }
            } catch (SQLException rollbackEx) {
                errorMessage.append("Błąd przy próbie rollbacku bazy danych: " + rollbackEx.getMessage() + "\n");
            }
            isSuccess = false;
        } finally {
            String message = isSuccess ?
                    "" + successMessage :
                    "Operacja nie powiodła się. Spróbuj ponownie...\n!!!   " + errorMessage;

            JOptionPane.showMessageDialog(AddOrderFrame.this, message);

            if (isSuccess) {
                tableModel.setRowCount(0);
            }
        }
    }
}