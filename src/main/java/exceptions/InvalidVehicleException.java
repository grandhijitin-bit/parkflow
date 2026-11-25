package exceptions;

/**
 * Thrown when vehicle registration input (e.g. registration number, owner name) is malformed or invalid.
 */
public class InvalidVehicleException extends Exception {
    public InvalidVehicleException(String message) {
        super(message);
    }
}
