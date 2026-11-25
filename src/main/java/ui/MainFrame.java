package ui;

import service.AutoSaveService;
import service.ParkingManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application frame providing a sidebar navigation inspired by ParkFlow.
 * Handles the 5 core sections:
 * - Dashboard
 * - Park Vehicle
 * - Exit Vehicle
 * - Parking History
 * - Close Application (with confirmation & safe shutdown)
 */
public class MainFrame extends JFrame {

    private final ParkingManager parkingManager;
    private final AutoSaveService autoSaveService;

    private CardLayout cardLayout;
    private JPanel contentContainer;

    private DashboardPanel dashboardPanel;
    private ParkVehiclePanel parkVehiclePanel;
    private ExitVehiclePanel exitVehiclePanel;
    private HistoryPanel historyPanel;

    private JButton btnNavDashboard;
    private JButton btnNavPark;
    private JButton btnNavExit;
    private JButton btnNavHistory;
    private JButton btnNavClose;

    public MainFrame(ParkingManager parkingManager, AutoSaveService autoSaveService) {
        this.parkingManager = parkingManager;
        this.autoSaveService = autoSaveService;
        initUI();
    }

    private void initUI() {
        setTitle("ParkFlow - Vehicle Parking Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_DARK);

        // Main layout with Sidebar on West and Content on Center
        setLayout(new BorderLayout());

        // 1. Sidebar Navigation
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. Content Container (CardLayout)
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(Theme.BG_DARK);

        // Initialize panels
        dashboardPanel = new DashboardPanel(parkingManager);
        parkVehiclePanel = new ParkVehiclePanel(parkingManager, this::onGlobalStateChanged);
        exitVehiclePanel = new ExitVehiclePanel(parkingManager, this::onGlobalStateChanged);
        historyPanel = new HistoryPanel(parkingManager);

        contentContainer.add(dashboardPanel, "DASHBOARD");
        contentContainer.add(parkVehiclePanel, "PARK");
        contentContainer.add(exitVehiclePanel, "EXIT");
        contentContainer.add(historyPanel, "HISTORY");

        add(contentContainer, BorderLayout.CENTER);

        // Window closing hook for safe exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndCloseApplication();
            }
        });

        // Default selection
        selectNav("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(Theme.BG_SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));

        // Brand Logo & Title
        JPanel brandBox = new JPanel(new GridLayout(2, 1, 0, 4));
        brandBox.setBackground(Theme.BG_SIDEBAR);
        brandBox.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel brandLogo = new JLabel("PARKFLOW");
        brandLogo.setFont(Theme.FONT_TITLE);
        brandLogo.setForeground(Theme.ACCENT_BLUE);

        JLabel brandTagline = new JLabel("Parking Management System");
        brandTagline.setFont(Theme.FONT_SMALL);
        brandTagline.setForeground(Theme.TEXT_MUTED);

        brandBox.add(brandLogo);
        brandBox.add(brandTagline);
        sidebar.add(brandBox, BorderLayout.NORTH);

        // Nav Buttons
        JPanel navMenu = new JPanel(new GridLayout(6, 1, 0, 8));
        navMenu.setBackground(Theme.BG_SIDEBAR);
        navMenu.setBorder(new EmptyBorder(10, 16, 10, 16));

        btnNavDashboard = createNavButton("Dashboard");
        btnNavPark = createNavButton("Park Vehicle");
        btnNavExit = createNavButton("Exit Vehicle");
        btnNavHistory = createNavButton("Parking History");
        btnNavClose = createNavButton("Close Application");

        btnNavDashboard.addActionListener(e -> selectNav("DASHBOARD"));
        btnNavPark.addActionListener(e -> selectNav("PARK"));
        btnNavExit.addActionListener(e -> selectNav("EXIT"));
        btnNavHistory.addActionListener(e -> selectNav("HISTORY"));
        btnNavClose.addActionListener(e -> confirmAndCloseApplication());

        navMenu.add(btnNavDashboard);
        navMenu.add(btnNavPark);
        navMenu.add(btnNavExit);
        navMenu.add(btnNavHistory);
        navMenu.add(new JLabel()); // Spacer
        navMenu.add(btnNavClose);

        sidebar.add(navMenu, BorderLayout.CENTER);

        // Footer info
        JPanel sidebarFooter = new JPanel(new GridLayout(2, 1, 0, 2));
        sidebarFooter.setBackground(Theme.BG_SIDEBAR);
        sidebarFooter.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel vLabel = new JLabel("Version 1.0.0 (Java 17+)");
        vLabel.setFont(Theme.FONT_SMALL);
        vLabel.setForeground(Theme.TEXT_MUTED);

        JLabel autoSaveLabel = new JLabel("● Auto-Save: Active (30s)");
        autoSaveLabel.setFont(Theme.FONT_SMALL);
        autoSaveLabel.setForeground(Theme.SUCCESS);

        sidebarFooter.add(vLabel);
        sidebarFooter.add(autoSaveLabel);
        sidebar.add(sidebarFooter, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_SUBHEADER);
        btn.setForeground(Theme.TEXT_PRIMARY);
        btn.setBackground(Theme.BG_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(Theme.BG_CARD)) {
                    btn.setBackground(Theme.BG_CARD_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(Theme.BG_CARD)) {
                    btn.setBackground(Theme.BG_SIDEBAR);
                }
            }
        });

        return btn;
    }

    private void selectNav(String section) {
        // Reset button colors
        btnNavDashboard.setBackground(Theme.BG_SIDEBAR);
        btnNavDashboard.setForeground(Theme.TEXT_PRIMARY);
        btnNavPark.setBackground(Theme.BG_SIDEBAR);
        btnNavPark.setForeground(Theme.TEXT_PRIMARY);
        btnNavExit.setBackground(Theme.BG_SIDEBAR);
        btnNavExit.setForeground(Theme.TEXT_PRIMARY);
        btnNavHistory.setBackground(Theme.BG_SIDEBAR);
        btnNavHistory.setForeground(Theme.TEXT_PRIMARY);

        switch (section) {
            case "DASHBOARD" -> {
                btnNavDashboard.setBackground(Theme.BG_CARD);
                btnNavDashboard.setForeground(Theme.ACCENT_BLUE);
                dashboardPanel.refreshData();
                cardLayout.show(contentContainer, "DASHBOARD");
            }
            case "PARK" -> {
                btnNavPark.setBackground(Theme.BG_CARD);
                btnNavPark.setForeground(Theme.ACCENT_BLUE);
                cardLayout.show(contentContainer, "PARK");
            }
            case "EXIT" -> {
                btnNavExit.setBackground(Theme.BG_CARD);
                btnNavExit.setForeground(Theme.ACCENT_BLUE);
                cardLayout.show(contentContainer, "EXIT");
            }
            case "HISTORY" -> {
                btnNavHistory.setBackground(Theme.BG_CARD);
                btnNavHistory.setForeground(Theme.ACCENT_BLUE);
                historyPanel.refreshHistory();
                cardLayout.show(contentContainer, "HISTORY");
            }
        }
    }

    private void onGlobalStateChanged() {
        dashboardPanel.refreshData();
        historyPanel.refreshHistory();
    }

    /**
     * Proper application exit confirmation dialog.
     * Safely stops background threads, saves pending data, and disposes frame.
     */
    public void confirmAndCloseApplication() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the Vehicle Parking Management System?\n" +
                "All pending records will be saved and background services stopped safely.",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            // Stop background auto save service and perform final save
            if (autoSaveService != null) {
                autoSaveService.stopService();
            }

            dispose();
            System.exit(0);
        }
    }
}
