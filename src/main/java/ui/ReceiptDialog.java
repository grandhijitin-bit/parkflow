package ui;

import model.ParkingTicket;
import util.DateTimeUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Receipt Dialog displaying a professional parking invoice / receipt upon exit.
 */
public class ReceiptDialog extends JDialog {

    public ReceiptDialog(Frame parent, ParkingTicket ticket, double hourlyRate) {
        super(parent, "Parking Receipt - " + ticket.getTicketId(), true);
        initUI(ticket, hourlyRate);
    }

    private void initUI(ParkingTicket ticket, double hourlyRate) {
        setSize(480, 560);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        // Receipt Header
        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        headerPanel.setBackground(Theme.BG_CARD);

        JLabel brand = new JLabel("PARKFLOW PARKING SERVICES", SwingConstants.CENTER);
        brand.setFont(Theme.FONT_TITLE);
        brand.setForeground(Theme.ACCENT_BLUE);

        JLabel subBrand = new JLabel("Official Parking Fee Receipt & Tax Invoice", SwingConstants.CENTER);
        subBrand.setFont(Theme.FONT_BODY);
        subBrand.setForeground(Theme.TEXT_MUTED);

        JLabel divider = new JLabel("----------------------------------------------------------------", SwingConstants.CENTER);
        divider.setFont(Theme.FONT_MONO);
        divider.setForeground(Theme.BORDER);

        headerPanel.add(brand);
        headerPanel.add(subBrand);
        headerPanel.add(divider);
        card.add(headerPanel, BorderLayout.NORTH);

        // Receipt Details
        JPanel bodyPanel = new JPanel(new GridLayout(10, 1, 0, 8));
        bodyPanel.setBackground(Theme.BG_CARD);

        addRow(bodyPanel, "Receipt / Ticket ID:", ticket.getTicketId());
        addRow(bodyPanel, "Vehicle Number:", ticket.getVehicleNo());
        addRow(bodyPanel, "Owner / Driver:", ticket.getOwnerName());
        addRow(bodyPanel, "Vehicle Category:", ticket.getType().getDisplayName());
        addRow(bodyPanel, "Assigned Slot:", ticket.getSlotId());
        addRow(bodyPanel, "Entry Time:", DateTimeUtil.format(ticket.getEntryTime()));
        addRow(bodyPanel, "Exit Time:", DateTimeUtil.format(ticket.getExitTime()));
        addRow(bodyPanel, "Actual Duration:", DateTimeUtil.formatDuration(ticket.getDuration()));
        addRow(bodyPanel, "Billed Units:", ticket.getBilledHours() + " Hour(s) @ ₹" + String.format("%.2f", hourlyRate) + "/hr");
        addRow(bodyPanel, "TOTAL PAID:", "₹ " + String.format("%.2f", ticket.getFee()), Theme.SUCCESS, Theme.FONT_HEADER);

        card.add(bodyPanel, BorderLayout.CENTER);

        // Receipt Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setBackground(Theme.BG_CARD);

        JButton btnClose = Theme.createButton("Close Receipt", Theme.PRIMARY_BTN, Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        footer.add(btnClose);

        card.add(footer, BorderLayout.SOUTH);
        add(card);
    }

    private void addRow(JPanel parent, String label, String value) {
        addRow(parent, label, value, Theme.TEXT_PRIMARY, Theme.FONT_BODY_BOLD);
    }

    private void addRow(JPanel parent, String label, String value, Color valueColor, Font valueFont) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Theme.BG_CARD);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_BODY);
        lbl.setForeground(Theme.TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(valueFont);
        val.setForeground(valueColor);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        parent.add(row);
    }
}
