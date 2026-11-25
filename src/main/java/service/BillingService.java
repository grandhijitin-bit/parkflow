package service;

import model.VehicleType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Reusable billing service adhering to the specified parking rules:
 * - Hourly rate: ₹50 for all vehicle categories (configurable via EnumMap / DB).
 * - Minimum billing: One hour.
 * - Partial hours must be rounded up (ceiling).
 * - Calculate duration using actual entry and exit timestamps.
 * - Never charge for an incomplete or failed parking operation.
 */
public class BillingService {

    public static final double DEFAULT_HOURLY_RATE = 50.0;

    // Demonstrating EnumMap for category-based rates
    private final EnumMap<VehicleType, Double> rates;

    public BillingService() {
        this.rates = new EnumMap<>(VehicleType.class);
        // Initialize default rates
        for (VehicleType type : VehicleType.values()) {
            this.rates.put(type, DEFAULT_HOURLY_RATE);
        }
    }

    public synchronized double getRate(VehicleType type) {
        if (type == null) {
            return DEFAULT_HOURLY_RATE;
        }
        return rates.getOrDefault(type, DEFAULT_HOURLY_RATE);
    }

    public synchronized void setRate(VehicleType type, double rate) {
        if (type != null && rate >= 0) {
            rates.put(type, rate);
        }
    }

    public synchronized Map<VehicleType, Double> getAllRates() {
        return Collections.unmodifiableMap(new EnumMap<>(rates));
    }

    /**
     * Calculates billed hours based on entry and exit time.
     * Minimum 1 hour; partial hours rounded up.
     */
    public long calculateBilledHours(LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null || exitTime.isBefore(entryTime)) {
            return 0;
        }
        Duration duration = Duration.between(entryTime, exitTime);
        long totalSeconds = duration.getSeconds();
        if (totalSeconds <= 0) {
            return 1;
        }
        // Round up partial hours
        long hours = (long) Math.ceil(totalSeconds / 3600.0);
        return Math.max(1, hours);
    }

    /**
     * Calculates the total parking fee.
     *
     * @param type vehicle category
     * @param entryTime entry timestamp
     * @param exitTime exit timestamp
     * @return fee in INR
     */
    public double calculateFee(VehicleType type, LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null || exitTime.isBefore(entryTime)) {
            return 0.0;
        }
        long billedHours = calculateBilledHours(entryTime, exitTime);
        double rate = getRate(type);
        return billedHours * rate;
    }
}
