package model;

/**
 * Subclass representing a Two-Wheeler / Bike.
 */
public class Bike extends Vehicle {

    public Bike(String vehicleNumber, String ownerName) {
        super(vehicleNumber, ownerName, VehicleType.BIKE);
    }

    @Override
    public String getVehicleCategoryDescription() {
        return "Two-Wheeler (Motorcycle / Scooter / Moped)";
    }
}
