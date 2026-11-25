package model;

/**
 * Status representation of a parking slot.
 */
public enum SlotStatus {
    AVAILABLE("Available"),
    OCCUPIED("Occupied");

    private final String label;

    SlotStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
