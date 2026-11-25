package ui;

import exceptions.VehicleNotFoundException;
import model.ParkingTicket;
import service.ParkingManager;
import util.DateTimeUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Exit Vehicle Screen:
 * - Search by Vehicle Number or Ticket ID
 * - Displays vehicle details, entry/exit timestamp, duration, billed hours, fee
 * - Exit confirmation & slot release
 * - Receipt generation
 */
public class ExitVehiclePanel extends JPanel {

    private final ParkingManager parkingManager;
    private final Runnable onStateChanged;

    private JTextField txtSearch;
    private JPanel detailsCard;
    private JLabel lblTicketId;
    private JLabel lblVehicleNo;
    private JLabel lblOwnerName;
    private JLabel lblVehicleType;
    private JLabel lblSlotId;
    private JLabel lblEntryTime;
    private JLabel lblExitTime;
    private JLabel lblDuration;
    private JLabel lblBilledHours;
    private JLabel lblTotalFee;

    private JButton btnConfirmExit;
    private ParkingTicket currentActiveTicket = null;

    public ExitVehiclePanel(ParkingManager parkingManager, Runnable onStateChanged) {
        this.parkingManager = parkingManager;
        this.onStateChanged = onStateChanged;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Top Header
        JPanel topHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        topHeader.setBackground(Theme.BG_DARK);
        JLabel title = new JLabel("Exit Vehicle (Check-Out)");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Locate active vehicle, calculate parking duration & fee, release slot and generate receipt");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_MUTED);
        topHeader.add(title);
        topHeader.add(subtitle);
        add(topHeader, BorderLayout.NORTH);

        // Center Split: Search & Actions on Top, Details Card in Center
        JPanel mainContainer = new JPanel(new BorderLayout(0, 20));
        mainContainer.setBackground(Theme.BG_DARK);

        // Search Bar Card
        JPanel searchCard = Theme.createCard();
        searchCard.setLayout(new BorderLayout(16, 0));

        JPanel searchInputBox = new JPanel(new BorderLayout(8, 0));
        searchInputBox.setBackground(Theme.BG_CARD);

        JLabel lblSearchPrompt = new JLabel("Enter Vehicle Registration Number or Ticket ID: ");
        lblSearchPrompt.setFont(Theme.FONT_BODY_BOLD);
        lblSearchPrompt.setForeground(Theme.TEXT_PRIMARY);

        txtSearch = Theme.createTextField(20);
        txtSearch.addActionListener(e -> searchActiveVehicle());

        JButton btnSearch = Theme.createButton("Locate Vehicle", Theme.PRIMARY_BTN, Color.WHITE);
        btnSearch.addActionListener(e -> searchActiveVehicle());

        searchInputBox.add(lblSearchPrompt, BorderLayout.WEST);
        searchInputBox.add(txtSearch, BorderLayout.CENTER);
        searchInputBox.add(btnSearch, BorderLayout.EAST);

        searchCard.add(searchInputBox, BorderLayout.CENTER);
        mainContainer.add(searchCard, BorderLayout.NORTH);

        // Details & Billing Card
        detailsCard = Theme.createCard();
        detailsCard.setLayout(new BorderLayout(0, 16));

        JLabel detailsTitle = new JLabel("Billing & Parking Summary");
        detailsTitle.setFont(Theme.FONT_HEADER);
        detailsTitle.setForeground(Theme.TEXT_PRIMARY);
        detailsCard.add(detailsTitle, BorderLayout.NORTH);

        JPanel infoGrid = new JPanel(new GridLayout(5, 2, 20, 12));
        infoGrid.setBackground(Theme.BG_CARD);

        lblTicketId = createSummaryItem(infoGrid, "Ticket ID:", "—");
        lblVehicleNo = createSummaryItem(infoGrid, "Vehicle Number:", "—");
        lblOwnerName = createSummaryItem(infoGrid, "Owner / Driver:", "—");
        lblVehicleType = createSummaryItem(infoGrid, "Vehicle Category:", "—");
        lblSlotId = createSummaryItem(infoGrid, "Occupied Slot:", "—");
        lblEntryTime = createSummaryItem(infoGrid, "Entry Time:", "—");
        lblExitTime = createSummaryItem(infoGrid, "Exit Time (Now):", "—");
        lblDuration = createSummaryItem(infoGrid, "Actual Duration:", "—");
        lblBilledHours = createSummaryItem(infoGrid, "Billed Hours:", "—");
        lblTotalFee = createSummaryItem(infoGrid, "Total Amount Due:", "₹0.00");
        lblTotalFee.setFont(Theme.FONT_HEADER);
        lblTotalFee.setForeground(Theme.SUCCESS);

        detailsCard.add(infoGrid, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottomAction.setBackground(Theme.BG_CARD);

        btnConfirmExit = Theme.createButton("Confirm Exit & Pay", Theme.SUCCESS, Color.WHITE);
        btnConfirmExit.setEnabled(false);
        btnConfirmExit.addActionListener(e -> handleConfirmExit());

        bottomAction.add(btnConfirmExit);
        detailsCard.add(bottomAction, BorderLayout.SOUTH);

        mainContainer.add(detailsCard, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);
    }

    private JLabel createSummaryItem(JPanel parent, String labelText, String initialValue) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(Theme.BG_CARD);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.FONT_BODY_BOLD);
        lbl.setForeground(Theme.TEXT_MUTED);

        JLabel val = new JLabel(initialValue);
        val.setFont(Theme.FONT_BODY_BOLD);
        val.setForeground(Theme.TEXT_PRIMARY);

        p.add(lbl, BorderLayout.WEST);
        p.add(val, BorderLayout.EAST);
        parent.add(p);
        return val;
    }

    private void searchActiveVehicle() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a vehicle registration number or ticket ID to search.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            currentActiveTicket = parkingManager.findActiveTicket(query);
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(currentActiveTicket.getEntryTime(), now);
            long billedHours = parkingManager.getBillingService().calculateBilledHours(currentActiveTicket.getEntryTime(), now);
            double fee = parkingManager.getBillingService().calculateFee(currentActiveTicket.getType(), currentActiveTicket.getEntryTime(), now);

            lblTicketId.setText(currentActiveTicket.getTicketId());
            lblVehicleNo.setText(currentActiveTicket.getVehicleNo());
            lblOwnerName.setText(currentActiveTicket.getOwnerName());
            lblVehicleType.setText(currentActiveTicket.getType().getDisplayName());
            lblSlotId.setText(currentActiveTicket.getSlotId());
            lblEntryTime.setText(DateTimeUtil.format(currentActiveTicket.getEntryTime()));
            lblExitTime.setText(DateTimeUtil.format(now));
            lblDuration.setText(DateTimeUtil.formatDuration(duration));
            lblBilledHours.setText(billedHours + (billedHours == 1 ? " hour" : " hours") + " (Min 1 hr, ceiling)");
            lblTotalFee.setText(String.format("₹%.2f", fee));

            btnConfirmExit.setEnabled(true);

        } catch (VehicleNotFoundException ex) {
            btnConfirmExit.setEnabled(false);
            resetDisplay();
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Vehicle Not Found",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleConfirmExit() {
        if (currentActiveTicket == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                String.format("Confirm vehicle exit for %s (Ticket: %s)?\nTotal Fee: %s\nSlot %s will be released.",
                        currentActiveTicket.getVehicleNo(), currentActiveTicket.getTicketId(),
                        lblTotalFee.getText(), currentActiveTicket.getSlotId()),
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                LocalDateTime exitTime = LocalDateTime.now();
                ParkingTicket completedTicket = parkingManager.exitVehicle(currentActiveTicket.getTicketId(), exitTime);

                // Show receipt dialog
                ReceiptDialog receiptDialog = new ReceiptDialog(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        completedTicket,
                        parkingManager.getBillingService().getRate(completedTicket.getType())
                );
                receiptDialog.setVisible(true);

                // Reset and notify
                resetDisplay();
                txtSearch.setText("");
                currentActiveTicket = null;
                btnConfirmExit.setEnabled(false);

                if (onStateChanged != null) {
                    onStateChanged.run();
                }

            } catch (VehicleNotFoundException | SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error processing vehicle exit: " + ex.getMessage(),
                        "Exit Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetDisplay() {
        lblTicketId.setText("—");
        lblVehicleNo.setText("—");
        lblOwnerName.setText("—");
        lblVehicleType.setText("—");
        lblSlotId.setText("—");
        lblEntryTime.setText("—");
        lblExitTime.setText("—");
        lblDuration.setText("—");
        lblBilledHours.setText("—");
        lblTotalFee.setText("₹0.00");
    }
}
