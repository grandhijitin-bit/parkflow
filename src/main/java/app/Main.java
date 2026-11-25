package app;

import database.DatabaseManager;
import service.AutoSaveService;
import service.BillingService;
import service.ParkingManager;
import ui.MainFrame;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Main application bootstrap class.
 * Initializes Database, Billing Service, Parking Manager, Auto-Save Service,
 * and launches the Swing GUI safely on the Event Dispatch Thread (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // Set system look and feel / properties
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Initialize backend services
        DatabaseManager dbManager = new DatabaseManager();
        BillingService billingService = new BillingService();
        ParkingManager parkingManager = new ParkingManager(dbManager, billingService);

        try {
            // Database and collections initialization
            parkingManager.initialize();
            System.out.println("[Main] Database and parking state successfully initialized.");
        } catch (SQLException e) {
            System.err.println("[Main] Failed to initialize database: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Failed to initialize parking database: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Initialize AutoSaveService (background thread every 30 seconds)
        AutoSaveService autoSaveService = new AutoSaveService(parkingManager, 30);
        autoSaveService.startService();
        System.out.println("[Main] AutoSaveService started in background (30s interval).");

        // Launch Swing GUI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(parkingManager, autoSaveService);
            mainFrame.setVisible(true);
        });
    }
}
