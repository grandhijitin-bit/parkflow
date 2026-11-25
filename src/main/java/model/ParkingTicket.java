package model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents a parking ticket record for an active or completed parking session.
 */
public class ParkingTicket {
    private final String ticketId;
    private final String vehicleNo;
    private String ownerName;
    private final VehicleType type;
    private final String slotId;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;

    public ParkingTicket(String ticketId, String vehicleNo, String ownerName,
                         VehicleType type, String slotId, LocalDateTime entryTime,
                         LocalDateTime exitTime, double fee) {
        this.ticketId = ticketId;
        this.vehicleNo = vehicleNo;
        this.ownerName = (ownerName != null && !ownerName.trim().isEmpty()) ? ownerName.trim() : "Guest";
        this.type = type;
        this.slotId = slotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.fee = fee;
    }

    public ParkingTicket(String ticketId, String vehicleNo, String ownerName,
                         VehicleType type, String slotId, LocalDateTime entryTime) {
        this(ticketId, vehicleNo, ownerName, type, slotId, entryTime, null, 0.0);
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public VehicleType getType() {
        return type;
    }

    public String getSlotId() {
        return slotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public boolean isActive() {
        return exitTime == null;
    }

    public Duration getDuration() {
        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        if (entryTime == null || end.isBefore(entryTime)) {
            return Duration.ZERO;
        }
        return Duration.between(entryTime, end);
    }

    public long getBilledHours() {
        Duration duration = getDuration();
        long totalSeconds = duration.getSeconds();
        if (totalSeconds <= 0) {
            return 1;
        }
        long hours = (long) Math.ceil(totalSeconds / 3600.0);
        return Math.max(1, hours);
    }
}
