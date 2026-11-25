package model;

/**
 * Subclass representing a Four-Wheeler / Car.
 */
public class Car extends Vehicle {

    public Car(String vehicleNumber, String ownerName) {
        super(vehicleNumber, ownerName, VehicleType.CAR);
    }

    @Override
    public String getVehicleCategoryDescription() {
        return "Four-Wheeler (Sedan / SUV / Hatchback)";
    }
}
