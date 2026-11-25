package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract class representing a generic vehicle.
 * Implements encapsulation, provides common vehicle attributes and behavior,
 * and defines an abstract method for vehicle-specific details.
 */
public abstract class Vehicle implements Billable {
    private final String vehicleNumber;
    private final String ownerName;
    private final VehicleType type;

    public Vehicle(String vehicleNumber, String ownerName, VehicleType type) {
        this.vehicleNumber = (vehicleNumber != null) ? vehicleNumber.trim().toUpperCase() : "";
        this.ownerName = (ownerName != null) ? ownerName.trim() : "Unknown Owner";
        this.type = Objects.requireNonNull(type, "VehicleType cannot be null");
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public VehicleType getType() {
        return type;
    }

    /**
     * Vehicle-specific description demonstrating runtime polymorphism.
     */
    public abstract String getVehicleCategoryDescription();

    /**
     * Standard implementation of billing calculation conforming to the Billable contract.
     * Minimum 1 hour; partial hours rounded up.
     */
    @Override
    public double calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, double hourlyRate) {
        if (entryTime == null || exitTime == null || exitTime.isBefore(entryTime)) {
            return 0.0;
        }
        Duration duration = Duration.between(entryTime, exitTime);
        long totalSeconds = duration.getSeconds();
        if (totalSeconds <= 0) {
            return hourlyRate; // Minimum 1 hour
        }
        // Round up partial hours (ceiling division)
        long hours = (long) Math.ceil(totalSeconds / 3600.0);
        if (hours < 1) {
            hours = 1;
        }
        return hours * hourlyRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return vehicleNumber.equalsIgnoreCase(vehicle.vehicleNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleNumber.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Owner: %s)", type, vehicleNumber, ownerName);
    }
}
