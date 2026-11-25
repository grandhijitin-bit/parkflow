package model;

import java.util.Objects;

/**
 * Represents a physical parking slot.
 */
public class ParkingSlot {
    private final String slotId;
    private final VehicleType type;
    private SlotStatus status;
    private String vehicleNo;

    public ParkingSlot(String slotId, VehicleType type, SlotStatus status, String vehicleNo) {
        this.slotId = Objects.requireNonNull(slotId, "slotId cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.status = (status != null) ? status : SlotStatus.AVAILABLE;
        this.vehicleNo = vehicleNo;
    }

    public ParkingSlot(String slotId, VehicleType type) {
        this(slotId, type, SlotStatus.AVAILABLE, null);
    }

    public String getSlotId() {
        return slotId;
    }

    public VehicleType getType() {
        return type;
    }

    public synchronized SlotStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(SlotStatus status) {
        this.status = status;
    }

    public synchronized String getVehicleNo() {
        return vehicleNo;
    }

    public synchronized void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public synchronized boolean isAvailable() {
        return this.status == SlotStatus.AVAILABLE && (vehicleNo == null || vehicleNo.isEmpty());
    }

    public synchronized void occupy(String vehicleNumber) {
        this.status = SlotStatus.OCCUPIED;
        this.vehicleNo = vehicleNumber;
    }

    public synchronized void release() {
        this.status = SlotStatus.AVAILABLE;
        this.vehicleNo = null;
    }

    @Override
    public String toString() {
        return String.format("Slot %s [%s] - %s%s", slotId, type, status,
                (vehicleNo != null ? " (" + vehicleNo + ")" : ""));
    }
}
