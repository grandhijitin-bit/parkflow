package util;

import exceptions.InvalidVehicleException;
import java.util.regex.Pattern;

/**
 * Validation utilities for vehicle registration numbers and user input.
 */
public final class ValidationUtil {

    // Regex accepting formats such as: MH12AB1234, DL-01-A-1234, KA 05 MC 4567, or any 4-15 alphanumeric format
    private static final Pattern VEHICLE_NO_PATTERN =
            Pattern.compile("^[A-Z0-9]{2}[ -]?[0-9]{1,2}[ -]?[A-Z]{0,3}[ -]?[0-9]{1,4}$", Pattern.CASE_INSENSITIVE);

    private ValidationUtil() {
        // Utility
    }

    /**
     * Validates and standardizes a vehicle registration number.
     *
     * @param vehicleNo raw input string
     * @return cleaned, uppercase registration number
     * @throws InvalidVehicleException if invalid
     */
    public static String validateVehicleNumber(String vehicleNo) throws InvalidVehicleException {
        if (vehicleNo == null || vehicleNo.trim().isEmpty()) {
            throw new InvalidVehicleException("Vehicle registration number cannot be blank.");
        }
        String cleaned = vehicleNo.trim().toUpperCase().replaceAll("\\s+", " ");
        if (cleaned.length() < 4 || cleaned.length() > 16) {
            throw new InvalidVehicleException("Vehicle registration number must be between 4 and 16 characters.");
        }
        // Basic alphanumeric and hyphen check
        if (!cleaned.matches("^[A-Z0-9 -]+$")) {
            throw new InvalidVehicleException("Vehicle registration number contains invalid characters.");
        }
        return cleaned;
    }

    /**
     * Validates owner name.
     */
    public static String validateOwnerName(String name) throws InvalidVehicleException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidVehicleException("Owner name cannot be blank.");
        }
        String cleaned = name.trim();
        if (cleaned.length() < 2 || cleaned.length() > 50) {
            throw new InvalidVehicleException("Owner name must be between 2 and 50 characters.");
        }
        return cleaned;
    }
}
