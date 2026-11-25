package ui;

import model.ParkingTicket;
import service.ParkingManager;
import util.DateTimeUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Parking History Screen:
 * - Table showing Ticket ID, Vehicle number, Owner name, Vehicle type,
 *   Slot number, Entry time, Exit time, Parking duration, Billed hours, Fee.
 * - Search by Vehicle Number / Ticket ID / Owner name.
 * - Filter by exit date.
 * - Refresh & reset filters.
 */
public class HistoryPanel extends JPanel {

    private final ParkingManager parkingManager;

    private JTable tableHistory;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JTextField txtExitDate;
    private JLabel lblStatusCount;
    private JLabel lblFilteredRevenue;

    private final String[] COLUMN_NAMES = {
            "Ticket ID", "Vehicle No", "Owner Name", "Type", "Slot",
            "Entry Time", "Exit Time", "Duration", "Billed Hrs", "Fee (₹)"
    };

    public HistoryPanel(ParkingManager parkingManager) {
        this.parkingManager = parkingManager;
        initUI();
        refreshHistory();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel topHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        topHeader.setBackground(Theme.BG_DARK);
        JLabel title = new JLabel("Parking History & Audit Logs");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("View completed parking records, search by registration, and filter by exit date");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_MUTED);
        topHeader.add(title);
        topHeader.add(subtitle);
        add(topHeader, BorderLayout.NORTH);

        // Center: Filter Bar on Top, Table in Center
        JPanel centerContainer = new JPanel(new BorderLayout(0, 14));
        centerContainer.setBackground(Theme.BG_DARK);

        // Filters Card
        JPanel filterCard = Theme.createCard();
        filterCard.setLayout(new BorderLayout(12, 8));

        JPanel filterInputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        filterInputs.setBackground(Theme.BG_CARD);

        // Search vehicle
        JLabel lblSearch = new JLabel("Search Vehicle / Ticket:");
        lblSearch.setFont(Theme.FONT_BODY_BOLD);
        lblSearch.setForeground(Theme.TEXT_PRIMARY);
        txtSearch = Theme.createTextField(14);
        txtSearch.addActionListener(e -> applyFilter());

        // Exit Date filter (YYYY-MM-DD)
        JLabel lblDate = new JLabel("Exit Date (YYYY-MM-DD):");
        lblDate.setFont(Theme.FONT_BODY_BOLD);
        lblDate.setForeground(Theme.TEXT_PRIMARY);
        txtExitDate = Theme.createTextField(10);
        txtExitDate.setToolTipText("e.g. 2026-11-24");
        txtExitDate.addActionListener(e -> applyFilter());

        JButton btnToday = Theme.createButton("Today", Theme.BORDER, Theme.TEXT_PRIMARY);
        btnToday.addActionListener(e -> {
            txtExitDate.setText(LocalDate.now().format(DateTimeUtil.DATE_ONLY_FORMATTER));
            applyFilter();
        });

        JButton btnFilter = Theme.createButton("Filter Records", Theme.PRIMARY_BTN, Color.WHITE);
        btnFilter.addActionListener(e -> applyFilter());

        JButton btnReset = Theme.createButton("Reset", Theme.BORDER, Theme.TEXT_MUTED);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            txtExitDate.setText("");
            applyFilter();
        });

        filterInputs.add(lblSearch);
        filterInputs.add(txtSearch);
        filterInputs.add(lblDate);
        filterInputs.add(txtExitDate);
        filterInputs.add(btnToday);
        filterInputs.add(btnFilter);
        filterInputs.add(btnReset);

        filterCard.add(filterInputs, BorderLayout.CENTER);
        centerContainer.add(filterCard, BorderLayout.NORTH);

        // History Table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };

        tableHistory = new JTable(tableModel);
        Theme.styleTable(tableHistory);

        JScrollPane scrollPane = new JScrollPane(tableHistory);
        scrollPane.setBackground(Theme.BG_CARD);
        scrollPane.getViewport().setBackground(Theme.BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        centerContainer.add(scrollPane, BorderLayout.CENTER);

        // Status Footer
        JPanel statusFooter = new JPanel(new BorderLayout());
        statusFooter.setBackground(Theme.BG_DARK);
        lblStatusCount = new JLabel("Total Completed Sessions: 0");
        lblStatusCount.setFont(Theme.FONT_BODY_BOLD);
        lblStatusCount.setForeground(Theme.TEXT_MUTED);

        lblFilteredRevenue = new JLabel("Filtered Revenue: ₹0.00");
        lblFilteredRevenue.setFont(Theme.FONT_HEADER);
        lblFilteredRevenue.setForeground(Theme.SUCCESS);

        statusFooter.add(lblStatusCount, BorderLayout.WEST);
        statusFooter.add(lblFilteredRevenue, BorderLayout.EAST);

        centerContainer.add(statusFooter, BorderLayout.SOUTH);
        add(centerContainer, BorderLayout.CENTER);
    }

    public void refreshHistory() {
        applyFilter();
    }

    private void applyFilter() {
        String vehQuery = txtSearch.getText().trim();
        String dateStr = txtExitDate.getText().trim();

        LocalDate filterDate = null;
        if (!dateStr.isEmpty()) {
            try {
                filterDate = LocalDate.parse(dateStr, DateTimeUtil.DATE_ONLY_FORMATTER);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please use YYYY-MM-DD (e.g. 2026-11-24).",
                        "Date Format Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        List<ParkingTicket> filtered = parkingManager.getFilteredHistory(vehQuery, filterDate);

        tableModel.setRowCount(0);
        double totalRev = 0.0;

        for (ParkingTicket t : filtered) {
            totalRev += t.getFee();
            tableModel.addRow(new Object[]{
                    t.getTicketId(),
                    t.getVehicleNo(),
                    t.getOwnerName(),
                    t.getType().name(),
                    t.getSlotId(),
                    DateTimeUtil.format(t.getEntryTime()),
                    DateTimeUtil.format(t.getExitTime()),
                    DateTimeUtil.formatDuration(t.getDuration()),
                    t.getBilledHours(),
                    String.format("%.2f", t.getFee())
            });
        }

        lblStatusCount.setText(String.format("Showing %d completed record(s)", filtered.size()));
        lblFilteredRevenue.setText(String.format("Filtered Revenue: ₹%.2f", totalRev));
    }
}
