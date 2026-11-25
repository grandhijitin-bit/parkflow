package model;

/**
 * Supported vehicle categories in the parking system.
 */
public enum VehicleType {
    BIKE("Two-Wheeler / Bike", "B"),
    CAR("Four-Wheeler / Car", "C"),
    TRUCK("Heavy Vehicle / Truck", "T");

    private final String displayName;
    private final String slotPrefix;

    VehicleType(String displayName, String slotPrefix) {
        this.displayName = displayName;
        this.slotPrefix = slotPrefix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSlotPrefix() {
        return slotPrefix;
    }

    public static VehicleType fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return CAR;
        }
        String clean = text.trim().toUpperCase();
        for (VehicleType vt : values()) {
            if (vt.name().equalsIgnoreCase(clean) || vt.getDisplayName().toUpperCase().contains(clean)) {
                return vt;
            }
        }
        return CAR;
    }
}
