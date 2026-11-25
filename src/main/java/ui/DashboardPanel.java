package ui;

import service.ParkingManager;
import service.ParkingManager.DashboardStats;
import model.ParkingSlot;
import model.SlotStatus;
import util.DateTimeUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dashboard screen displaying:
 * - Total parking slots, occupied, available
 * - Today's entries, today's exits, total revenue
 * - Live updating date and time
 * - Real-time parking slot visual status map
 */
public class DashboardPanel extends JPanel {

    private final ParkingManager parkingManager;

    private JLabel lblTotalSlots;
    private JLabel lblOccupiedSlots;
    private JLabel lblAvailableSlots;
    private JLabel lblTodayEntries;
    private JLabel lblTodayExits;
    private JLabel lblTotalRevenue;
    private JLabel lblLiveClock;

    private JPanel slotsGridPanel;

    public DashboardPanel(ParkingManager parkingManager) {
        this.parkingManager = parkingManager;
        initUI();
        startClockTimer();
        refreshData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Top Header with Title and Live Clock
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(Theme.BG_DARK);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setBackground(Theme.BG_DARK);
        JLabel title = new JLabel("System Dashboard");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Real-time parking occupancy, vehicle movement, and revenue overview");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_MUTED);
        titleBox.add(title);
        titleBox.add(subtitle);

        lblLiveClock = new JLabel(DateTimeUtil.format(LocalDateTime.now()));
        lblLiveClock.setFont(Theme.FONT_SUBHEADER);
        lblLiveClock.setForeground(Theme.ACCENT_BLUE);
        lblLiveClock.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));

        topHeader.add(titleBox, BorderLayout.WEST);
        topHeader.add(lblLiveClock, BorderLayout.EAST);
        add(topHeader, BorderLayout.NORTH);

        // Center Content: Stats Cards + Live Slot Grid
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(Theme.BG_DARK);

        // 6 Dashboard Stat Cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 16, 16));
        statsPanel.setBackground(Theme.BG_DARK);

        lblTotalSlots = new JLabel("0");
        lblOccupiedSlots = new JLabel("0");
        lblAvailableSlots = new JLabel("0");
        lblTodayEntries = new JLabel("0");
        lblTodayExits = new JLabel("0");
        lblTotalRevenue = new JLabel("₹0.00");

        statsPanel.add(createStatCard("Total Parking Slots", lblTotalSlots, "Overall capacity", Theme.ACCENT_BLUE));
        statsPanel.add(createStatCard("Occupied Slots", lblOccupiedSlots, "Active vehicles", Theme.OCCUPIED));
        statsPanel.add(createStatCard("Available Slots", lblAvailableSlots, "Ready to park", Theme.AVAILABLE));
        statsPanel.add(createStatCard("Today's Entries", lblTodayEntries, "Vehicles entered today", Theme.ACCENT_BLUE));
        statsPanel.add(createStatCard("Today's Exits", lblTodayExits, "Vehicles departed today", Theme.WARNING));
        statsPanel.add(createStatCard("Total Revenue", lblTotalRevenue, "Cumulative collections", Theme.SUCCESS));

        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Slot Visualization Card
        JPanel slotMapCard = Theme.createCard();
        slotMapCard.setLayout(new BorderLayout(0, 12));

        JPanel slotMapHeader = new JPanel(new BorderLayout());
        slotMapHeader.setBackground(Theme.BG_CARD);
        JLabel slotMapTitle = new JLabel("Live Parking Slots Matrix");
        slotMapTitle.setFont(Theme.FONT_HEADER);
        slotMapTitle.setForeground(Theme.TEXT_PRIMARY);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        legend.setBackground(Theme.BG_CARD);
        legend.add(createLegendItem("Available", Theme.AVAILABLE));
        legend.add(createLegendItem("Occupied", Theme.OCCUPIED));
        slotMapHeader.add(slotMapTitle, BorderLayout.WEST);
        slotMapHeader.add(legend, BorderLayout.EAST);

        slotMapCard.add(slotMapHeader, BorderLayout.NORTH);

        // Slots Grid Panel with Scroll Pane
        slotsGridPanel = new JPanel(new GridLayout(0, 6, 10, 10));
        slotsGridPanel.setBackground(Theme.BG_CARD);

        JScrollPane scrollPane = new JScrollPane(slotsGridPanel);
        scrollPane.setBackground(Theme.BG_CARD);
        scrollPane.getViewport().setBackground(Theme.BG_CARD);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        slotMapCard.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(slotMapCard, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String labelText, JLabel valueLabel, String subtext, Color accent) {
        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout(0, 8));

        JLabel lblTitle = new JLabel(labelText.toUpperCase());
        lblTitle.setFont(Theme.FONT_SMALL);
        lblTitle.setForeground(Theme.TEXT_MUTED);

        valueLabel.setFont(Theme.FONT_NUMBER);
        valueLabel.setForeground(accent);

        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(Theme.FONT_SMALL);
        lblSub.setForeground(Theme.TEXT_MUTED);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setBackground(Theme.BG_CARD);
        JLabel dot = new JLabel("●");
        dot.setForeground(color);
        dot.setFont(Theme.FONT_HEADER);
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_MUTED);
        p.add(dot);
        p.add(lbl);
        return p;
    }

    /**
     * Refreshes the dashboard stats and slot matrix from the parking manager.
     */
    public void refreshData() {
        DashboardStats stats = parkingManager.getDashboardStats();

        lblTotalSlots.setText(String.valueOf(stats.totalSlots()));
        lblOccupiedSlots.setText(String.valueOf(stats.occupiedSlots()));
        lblAvailableSlots.setText(String.valueOf(stats.availableSlots()));
        lblTodayEntries.setText(String.valueOf(stats.todayEntries()));
        lblTodayExits.setText(String.valueOf(stats.todayExits()));
        lblTotalRevenue.setText(String.format("₹%.2f", stats.totalRevenue()));

        // Refresh slots grid
        slotsGridPanel.removeAll();
        List<ParkingSlot> slots = parkingManager.getAllSlots();
        for (ParkingSlot slot : slots) {
            slotsGridPanel.add(createSlotBox(slot));
        }
        slotsGridPanel.revalidate();
        slotsGridPanel.repaint();
    }

    private JPanel createSlotBox(ParkingSlot slot) {
        JPanel box = new JPanel(new BorderLayout(0, 2));
        boolean isOccupied = slot.getStatus() == SlotStatus.OCCUPIED;
        Color statusColor = isOccupied ? Theme.OCCUPIED : Theme.AVAILABLE;

        box.setBackground(Theme.BG_INPUT);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(statusColor, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel lblId = new JLabel(slot.getSlotId(), SwingConstants.CENTER);
        lblId.setFont(Theme.FONT_SUBHEADER);
        lblId.setForeground(Theme.TEXT_PRIMARY);

        JLabel lblType = new JLabel(slot.getType().name(), SwingConstants.CENTER);
        lblType.setFont(Theme.FONT_SMALL);
        lblType.setForeground(Theme.TEXT_MUTED);

        JLabel lblStatus = new JLabel(isOccupied ? "OCCUPIED" : "VACANT", SwingConstants.CENTER);
        lblStatus.setFont(Theme.FONT_SMALL);
        lblStatus.setForeground(statusColor);

        box.add(lblId, BorderLayout.NORTH);
        box.add(lblType, BorderLayout.CENTER);
        box.add(lblStatus, BorderLayout.SOUTH);

        if (isOccupied && slot.getVehicleNo() != null) {
            box.setToolTipText(String.format("Slot %s | Vehicle: %s", slot.getSlotId(), slot.getVehicleNo()));
        } else {
            box.setToolTipText(String.format("Slot %s is Available for %s", slot.getSlotId(), slot.getType().getDisplayName()));
        }

        return box;
    }

    private void startClockTimer() {
        Timer timer = new Timer(1000, e -> {
            lblLiveClock.setText(DateTimeUtil.format(LocalDateTime.now()));
        });
        timer.start();
    }
}
