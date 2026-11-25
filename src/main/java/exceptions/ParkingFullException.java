package exceptions;

/**
 * Thrown when no available parking slot can be allocated for the specified vehicle type.
 */
public class ParkingFullException extends Exception {
    public ParkingFullException(String message) {
        super(message);
    }
}
