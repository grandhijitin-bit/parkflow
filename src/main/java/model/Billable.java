package model;

import java.time.LocalDateTime;

/**
 * Interface defining the billing-related contract for parking operations.
 */
public interface Billable {
    /**
     * Calculates the parking fee based on entry and exit timestamps.
     *
     * @param entryTime The vehicle's entry timestamp
     * @param exitTime  The vehicle's exit timestamp
     * @param hourlyRate The hourly rate for the vehicle
     * @return Calculated fee in currency units (INR)
     */
    double calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, double hourlyRate);
}
