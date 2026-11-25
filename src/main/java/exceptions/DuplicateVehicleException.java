package exceptions;

/**
 * Thrown when attempting to register a vehicle that is already actively parked in the facility.
 */
public class DuplicateVehicleException extends Exception {
    public DuplicateVehicleException(String message) {
        super(message);
    }
}
