package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Modern Dark-Themed UI constants, colors, and factory helpers inspired by ParkFlow.
 */
public final class Theme {

    // Background & Surface colors
    public static final Color BG_DARK = new Color(0x0f, 0x17, 0x2a);       // Deep Slate Navy
    public static final Color BG_SIDEBAR = new Color(0x13, 0x1b, 0x2e);    // Sidebar background
    public static final Color BG_CARD = new Color(0x1e, 0x29, 0x3b);       // Card / Panel background
    public static final Color BG_CARD_HOVER = new Color(0x27, 0x35, 0x4d); // Hover state
    public static final Color BG_INPUT = new Color(0x0f, 0x17, 0x2a);      // Form input background
    public static final Color BORDER = new Color(0x33, 0x41, 0x55);        // Subtle borders

    // Text colors
    public static final Color TEXT_PRIMARY = new Color(0xf8, 0xfa, 0xfc);   // High-contrast white
    public static final Color TEXT_MUTED = new Color(0x94, 0xa3, 0xb8);     // Subdued slate gray
    public static final Color TEXT_DARK = new Color(0x0f, 0x17, 0x2a);

    // Accent & Status colors
    public static final Color ACCENT_BLUE = new Color(0x38, 0xbd, 0xf8);    // Sky Blue highlight
    public static final Color PRIMARY_BTN = new Color(0x25, 0x63, 0xeb);    // Royal Blue button
    public static final Color PRIMARY_BTN_HOVER = new Color(0x1d, 0x4e, 0xd8);
    public static final Color SUCCESS = new Color(0x10, 0xb9, 0x81);        // Emerald green
    public static final Color SUCCESS_BG = new Color(0x06, 0x4e, 0x3b);
    public static final Color ERROR = new Color(0xef, 0x44, 0x44);          // Rose red
    public static final Color ERROR_BG = new Color(0x7f, 0x1d, 0x1d);
    public static final Color WARNING = new Color(0xf5, 0x9e, 0x0b);        // Amber
    public static final Color OCCUPIED = new Color(0xe1, 0x1d, 0x48);       // Crimson red for occupied slot
    public static final Color AVAILABLE = new Color(0x10, 0xb9, 0x81);      // Emerald green for available slot

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_NUMBER = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    private Theme() {}

    /**
     * Creates a styled modern card panel with dark background and border.
     */
    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    /**
     * Creates a styled JButton.
     */
    public static JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(FONT_SUBHEADER);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bg.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
            }
        });

        return button;
    }

    /**
     * Creates a styled modern text input field.
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT_BLUE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    /**
     * Applies styling to a JTable for modern dark theme.
     */
    public static void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setGridColor(BORDER);
        table.setSelectionBackground(BG_CARD_HOVER);
        table.setSelectionForeground(ACCENT_BLUE);

        // Header style
        table.getTableHeader().setBackground(BG_SIDEBAR);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setFont(FONT_SUBHEADER);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(BORDER, 1));
        table.getTableHeader().setReorderingAllowed(false);

        // Center aligned cell renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBackground(BG_CARD);
        centerRenderer.setForeground(TEXT_PRIMARY);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}
