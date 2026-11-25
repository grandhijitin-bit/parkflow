package model;

/**
 * Subclass representing a Heavy Vehicle / Truck.
 */
public class Truck extends Vehicle {

    public Truck(String vehicleNumber, String ownerName) {
        super(vehicleNumber, ownerName, VehicleType.TRUCK);
    }

    @Override
    public String getVehicleCategoryDescription() {
        return "Heavy Vehicle (Commercial Truck / Bus / Van)";
    }
}
