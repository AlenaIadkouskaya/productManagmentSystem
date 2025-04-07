package org.example;

import org.example.config.DatabaseConnection;
import org.example.service.db.orderStorage.DatabaseOrderStorageServiceImpl;
import org.example.windows.MainAppGUI;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                databaseConnection.init();
                Connection connection = databaseConnection.getConnection();

                DatabaseOrderStorageServiceImpl storageService = new DatabaseOrderStorageServiceImpl(connection);
                storageService.createOrdersTable();

                new MainAppGUI(connection).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Błąd podczas uruchamiania aplikacji!");
            }
        });
    }
}