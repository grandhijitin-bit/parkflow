package ui;

import exceptions.DuplicateVehicleException;
import exceptions.InvalidVehicleException;
import exceptions.ParkingFullException;
import model.*;
import service.ParkingManager;
import util.DateTimeUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Park Vehicle Screen:
 * - Vehicle registration number input
 * - Owner name input
 * - Vehicle type selection (Bike, Car, Truck)
 * - Automatic parking slot allocation
 * - Unique ticket generation
 * - Entry timestamp
 * - Successful parking confirmation card
 * - Validation & error handling
 */
public class ParkVehiclePanel extends JPanel {

    private final ParkingManager parkingManager;
    private final Runnable onStateChanged;

    private JTextField txtVehicleNo;
    private JTextField txtOwnerName;
    private JComboBox<VehicleType> cmbVehicleType;
    private JLabel lblRateHint;

    private JPanel confirmationCard;
    private JLabel lblConfTicketId;
    private JLabel lblConfVehicleNo;
    private JLabel lblConfOwner;
    private JLabel lblConfType;
    private JLabel lblConfSlot;
    private JLabel lblConfEntryTime;
    private JLabel lblConfRate;

    public ParkVehiclePanel(ParkingManager parkingManager, Runnable onStateChanged) {
        this.parkingManager = parkingManager;
        this.onStateChanged = onStateChanged;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel topHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        topHeader.setBackground(Theme.BG_DARK);
        JLabel title = new JLabel("Park Vehicle (Check-In)");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Register incoming vehicle, allocate parking slot, and issue digital parking ticket");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_MUTED);
        topHeader.add(title);
        topHeader.add(subtitle);
        add(topHeader, BorderLayout.NORTH);

        // Center split: Form on Left, Confirmation / Ticket Preview on Right
        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 24, 0));
        contentGrid.setBackground(Theme.BG_DARK);

        // Form Card
        JPanel formCard = Theme.createCard();
        formCard.setLayout(new BorderLayout(0, 16));

        JLabel formTitle = new JLabel("Vehicle Entry Form");
        formTitle.setFont(Theme.FONT_HEADER);
        formTitle.setForeground(Theme.TEXT_PRIMARY);
        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(4, 1, 0, 14));
        fieldsPanel.setBackground(Theme.BG_CARD);

        // 1. Vehicle Number
        JPanel pnlVehNo = new JPanel(new BorderLayout(0, 6));
        pnlVehNo.setBackground(Theme.BG_CARD);
        JLabel lblVehNo = new JLabel("Vehicle Registration Number * (e.g. MH-12-AB-1234)");
        lblVehNo.setFont(Theme.FONT_BODY_BOLD);
        lblVehNo.setForeground(Theme.TEXT_PRIMARY);
        txtVehicleNo = Theme.createTextField(15);
        pnlVehNo.add(lblVehNo, BorderLayout.NORTH);
        pnlVehNo.add(txtVehicleNo, BorderLayout.CENTER);
        fieldsPanel.add(pnlVehNo);

        // 2. Owner Name
        JPanel pnlOwner = new JPanel(new BorderLayout(0, 6));
        pnlOwner.setBackground(Theme.BG_CARD);
        JLabel lblOwner = new JLabel("Vehicle Owner / Driver Name *");
        lblOwner.setFont(Theme.FONT_BODY_BOLD);
        lblOwner.setForeground(Theme.TEXT_PRIMARY);
        txtOwnerName = Theme.createTextField(15);
        pnlOwner.add(lblOwner, BorderLayout.NORTH);
        pnlOwner.add(txtOwnerName, BorderLayout.CENTER);
        fieldsPanel.add(pnlOwner);

        // 3. Vehicle Type Selection
        JPanel pnlType = new JPanel(new BorderLayout(0, 6));
        pnlType.setBackground(Theme.BG_CARD);
        JLabel lblType = new JLabel("Vehicle Category *");
        lblType.setFont(Theme.FONT_BODY_BOLD);
        lblType.setForeground(Theme.TEXT_PRIMARY);

        cmbVehicleType = new JComboBox<>(VehicleType.values());
        cmbVehicleType.setFont(Theme.FONT_BODY);
        cmbVehicleType.setBackground(Theme.BG_INPUT);
        cmbVehicleType.setForeground(Theme.TEXT_PRIMARY);
        cmbVehicleType.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof VehicleType vt) {
                    l.setText(vt.getDisplayName());
                }
                l.setBackground(isSelected ? Theme.BG_CARD_HOVER : Theme.BG_INPUT);
                l.setForeground(isSelected ? Theme.ACCENT_BLUE : Theme.TEXT_PRIMARY);
                l.setBorder(new EmptyBorder(6, 10, 6, 10));
                return l;
            }
        });
        cmbVehicleType.addActionListener(e -> updateRateHint());
        pnlType.add(lblType, BorderLayout.NORTH);
        pnlType.add(cmbVehicleType, BorderLayout.CENTER);
        fieldsPanel.add(pnlType);

        // 4. Rate Hint
        lblRateHint = new JLabel("Standard Tariff: ₹50.00 / hour (Minimum 1 hour)");
        lblRateHint.setFont(Theme.FONT_SMALL);
        lblRateHint.setForeground(Theme.ACCENT_BLUE);
        fieldsPanel.add(lblRateHint);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionPanel.setBackground(Theme.BG_CARD);

        JButton btnClear = Theme.createButton("Clear Form", Theme.BORDER, Theme.TEXT_PRIMARY);
        btnClear.addActionListener(e -> clearForm());

        JButton btnPark = Theme.createButton("Park Vehicle & Issue Ticket", Theme.PRIMARY_BTN, Color.WHITE);
        btnPark.addActionListener(e -> handleParkVehicle());

        actionPanel.add(btnClear);
        actionPanel.add(btnPark);
        formCard.add(actionPanel, BorderLayout.SOUTH);

        contentGrid.add(formCard);

        // Right side: Confirmation / Ticket Issue Preview
        confirmationCard = Theme.createCard();
        confirmationCard.setLayout(new BorderLayout(0, 14));

        JLabel confTitle = new JLabel("Active Parking Ticket Confirmation");
        confTitle.setFont(Theme.FONT_HEADER);
        confTitle.setForeground(Theme.SUCCESS);
        confirmationCard.add(confTitle, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridLayout(7, 1, 0, 10));
        detailsPanel.setBackground(Theme.BG_CARD);

        lblConfTicketId = createDetailRow(detailsPanel, "Ticket ID:", "—");
        lblConfVehicleNo = createDetailRow(detailsPanel, "Vehicle Number:", "—");
        lblConfOwner = createDetailRow(detailsPanel, "Owner Name:", "—");
        lblConfType = createDetailRow(detailsPanel, "Vehicle Type:", "—");
        lblConfSlot = createDetailRow(detailsPanel, "Allocated Slot:", "—");
        lblConfEntryTime = createDetailRow(detailsPanel, "Entry Timestamp:", "—");
        lblConfRate = createDetailRow(detailsPanel, "Hourly Rate:", "₹50.00 / hour");

        confirmationCard.add(detailsPanel, BorderLayout.CENTER);

        JLabel hintFooter = new JLabel("Please present this ticket ID or vehicle number at the exit counter.", SwingConstants.CENTER);
        hintFooter.setFont(Theme.FONT_SMALL);
        hintFooter.setForeground(Theme.TEXT_MUTED);
        confirmationCard.add(hintFooter, BorderLayout.SOUTH);

        contentGrid.add(confirmationCard);
        add(contentGrid, BorderLayout.CENTER);

        updateRateHint();
    }

    private JLabel createDetailRow(JPanel parent, String labelText, String defaultValue) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Theme.BG_CARD);
        JLabel lblName = new JLabel(labelText);
        lblName.setFont(Theme.FONT_BODY_BOLD);
        lblName.setForeground(Theme.TEXT_MUTED);

        JLabel lblVal = new JLabel(defaultValue);
        lblVal.setFont(Theme.FONT_BODY_BOLD);
        lblVal.setForeground(Theme.TEXT_PRIMARY);

        row.add(lblName, BorderLayout.WEST);
        row.add(lblVal, BorderLayout.EAST);
        parent.add(row);
        return lblVal;
    }

    private void updateRateHint() {
        VehicleType type = (VehicleType) cmbVehicleType.getSelectedItem();
        double rate = parkingManager.getBillingService().getRate(type);
        lblRateHint.setText(String.format("Tariff for %s: ₹%.2f / hour (Minimum 1 hour)",
                (type != null ? type.getDisplayName() : "All"), rate));
    }

    private void handleParkVehicle() {
        String vehNo = txtVehicleNo.getText().trim();
        String owner = txtOwnerName.getText().trim();
        VehicleType type = (VehicleType) cmbVehicleType.getSelectedItem();

        // Polymorphic Vehicle creation
        Vehicle vehicle;
        switch (type) {
            case BIKE -> vehicle = new Bike(vehNo, owner);
            case TRUCK -> vehicle = new Truck(vehNo, owner);
            case CAR -> vehicle = new Car(vehNo, owner);
            default -> vehicle = new Car(vehNo, owner);
        }

        try {
            ParkingTicket ticket = parkingManager.parkVehicle(vehicle);

            // Update confirmation preview
            lblConfTicketId.setText(ticket.getTicketId());
            lblConfVehicleNo.setText(ticket.getVehicleNo());
            lblConfOwner.setText(ticket.getOwnerName());
            lblConfType.setText(ticket.getType().getDisplayName());
            lblConfSlot.setText(ticket.getSlotId());
            lblConfEntryTime.setText(DateTimeUtil.format(ticket.getEntryTime()));
            lblConfRate.setText(String.format("₹%.2f / hour", parkingManager.getBillingService().getRate(ticket.getType())));

            JOptionPane.showMessageDialog(this,
                    String.format("Vehicle successfully parked!\n\nTicket ID: %s\nSlot: %s\nVehicle: %s",
                            ticket.getTicketId(), ticket.getSlotId(), ticket.getVehicleNo()),
                    "Parking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            if (onStateChanged != null) {
                onStateChanged.run();
            }

        } catch (InvalidVehicleException | DuplicateVehicleException | ParkingFullException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Parking Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while parking vehicle: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtVehicleNo.setText("");
        txtOwnerName.setText("");
        cmbVehicleType.setSelectedIndex(0);
        updateRateHint();
    }
}
