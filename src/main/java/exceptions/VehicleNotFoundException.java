package exceptions;

/**
 * Thrown when searching for an active or completed vehicle record that does not exist.
 */
public class VehicleNotFoundException extends Exception {
    public VehicleNotFoundException(String message) {
        super(message);
    }
}
