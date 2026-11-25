package service;

import database.DatabaseManager;
import exceptions.DuplicateVehicleException;
import exceptions.InvalidVehicleException;
import exceptions.ParkingFullException;
import exceptions.VehicleNotFoundException;
import model.*;
import util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Core business coordinator managing parking operations, slot allocations,
 * active vehicle lookups, history queries, and data persistence.
 *
 * Demonstrates:
 * - ArrayList for storing parking tickets and slots.
 * - HashMap for O(1) active vehicle and ticket lookups.
 * - Java Streams and Lambdas for data filtering, processing, and statistics.
 * - Thread-safe synchronization for concurrent access.
 */
public class ParkingManager {

    private final DatabaseManager databaseManager;
    private final BillingService billingService;

    // ArrayList collections for parking records
    private final List<ParkingSlot> slots = new ArrayList<>();
    private final List<ParkingTicket> allTickets = new ArrayList<>();

    // HashMaps for fast O(1) lookups of active parking sessions
    private final Map<String, ParkingTicket> activeTicketsByVehicle = new HashMap<>();
    private final Map<String, ParkingTicket> activeTicketsById = new HashMap<>();
    private final Map<String, ParkingSlot> slotsById = new HashMap<>();

    public ParkingManager(DatabaseManager databaseManager, BillingService billingService) {
        this.databaseManager = Objects.requireNonNull(databaseManager, "DatabaseManager cannot be null");
        this.billingService = Objects.requireNonNull(billingService, "BillingService cannot be null");
    }

    /**
     * Initializes state by loading persisted slots, rates, and active tickets from SQLite.
     */
    public synchronized void initialize() throws SQLException {
        databaseManager.initializeDatabase();

        // 1. Load rates
        Map<VehicleType, Double> savedRates = databaseManager.loadRates();
        savedRates.forEach(billingService::setRate);

        // 2. Load slots
        slots.clear();
        slotsById.clear();
        List<ParkingSlot> loadedSlots = databaseManager.loadAllSlots();
        slots.addAll(loadedSlots);
        for (ParkingSlot s : slots) {
            slotsById.put(s.getSlotId(), s);
        }

        // 3. Load all tickets (active + completed)
        allTickets.clear();
        activeTicketsByVehicle.clear();
        activeTicketsById.clear();
        List<ParkingTicket> loadedTickets = databaseManager.loadAllTickets();
        allTickets.addAll(loadedTickets);

        // Populate active maps using Streams and Lambdas
        allTickets.stream()
                .filter(ParkingTicket::isActive)
                .forEach(ticket -> {
                    activeTicketsByVehicle.put(ticket.getVehicleNo().toUpperCase(), ticket);
                    activeTicketsById.put(ticket.getTicketId().toUpperCase(), ticket);
                });
    }

    /**
     * Parks a vehicle into an available slot matching its category.
     *
     * @param vehicle the vehicle to park
     * @return generated ParkingTicket
     * @throws InvalidVehicleException if vehicle data is invalid
     * @throws DuplicateVehicleException if vehicle is already actively parked
     * @throws ParkingFullException if no slots are available
     * @throws SQLException if database error occurs
     */
    public synchronized ParkingTicket parkVehicle(Vehicle vehicle)
            throws InvalidVehicleException, DuplicateVehicleException, ParkingFullException, SQLException {

        if (vehicle == null) {
            throw new InvalidVehicleException("Vehicle information cannot be null.");
        }

        String validVehicleNo = ValidationUtil.validateVehicleNumber(vehicle.getVehicleNumber());
        String validOwnerName = ValidationUtil.validateOwnerName(vehicle.getOwnerName());

        // Check for duplicate active registration using HashMap
        if (activeTicketsByVehicle.containsKey(validVehicleNo)) {
            ParkingTicket existing = activeTicketsByVehicle.get(validVehicleNo);
            throw new DuplicateVehicleException(
                    String.format("Vehicle '%s' is already parked in Slot %s (Ticket: %s).",
                            validVehicleNo, existing.getSlotId(), existing.getTicketId()));
        }

        // Allocate parking slot using Streams and Lambdas
        ParkingSlot allocatedSlot = slots.stream()
                .filter(slot -> slot.getType() == vehicle.getType() && slot.isAvailable())
                .findFirst()
                .orElse(null);

        // If specific slot type is full, check for generic availability if allowed, or throw
        if (allocatedSlot == null) {
            throw new ParkingFullException(
                    String.format("No available parking slots for vehicle type: %s.",
                            vehicle.getType().getDisplayName()));
        }

        // Occupy the slot
        allocatedSlot.occupy(validVehicleNo);

        // Generate unique ticket
        String ticketId = generateUniqueTicketId(vehicle.getType());
        LocalDateTime entryTime = LocalDateTime.now();

        ParkingTicket ticket = new ParkingTicket(
                ticketId, validVehicleNo, validOwnerName,
                vehicle.getType(), allocatedSlot.getSlotId(), entryTime);

        // Persist to database
        databaseManager.insertTicket(ticket);
        databaseManager.updateSlot(allocatedSlot);

        // Update in-memory collections
        allTickets.add(ticket);
        activeTicketsByVehicle.put(validVehicleNo, ticket);
        activeTicketsById.put(ticketId.toUpperCase(), ticket);

        return ticket;
    }

    /**
     * Finds an active parking ticket by vehicle number or ticket ID.
     */
    public synchronized ParkingTicket findActiveTicket(String query) throws VehicleNotFoundException {
        if (query == null || query.trim().isEmpty()) {
            throw new VehicleNotFoundException("Please enter a vehicle number or ticket ID.");
        }
        String q = query.trim().toUpperCase();

        // Check by ticket ID first
        if (activeTicketsById.containsKey(q)) {
            return activeTicketsById.get(q);
        }
        // Check by vehicle number
        if (activeTicketsByVehicle.containsKey(q)) {
            return activeTicketsByVehicle.get(q);
        }

        // Try case-insensitive matching through active tickets stream
        return activeTicketsByVehicle.values().stream()
                .filter(t -> t.getVehicleNo().equalsIgnoreCase(q) || t.getTicketId().equalsIgnoreCase(q))
                .findFirst()
                .orElseThrow(() -> new VehicleNotFoundException(
                        "No active vehicle found matching: " + query));
    }

    /**
     * Exits a vehicle, calculates the fee, releases its slot, and updates records.
     *
     * @param query vehicle number or ticket ID
     * @param exitTimestamp custom exit time (or null for current time)
     * @return completed ParkingTicket
     */
    public synchronized ParkingTicket exitVehicle(String query, LocalDateTime exitTimestamp)
            throws VehicleNotFoundException, SQLException {

        ParkingTicket ticket = findActiveTicket(query);
        LocalDateTime exitTime = (exitTimestamp != null) ? exitTimestamp : LocalDateTime.now();

        if (exitTime.isBefore(ticket.getEntryTime())) {
            exitTime = ticket.getEntryTime().plusMinutes(1);
        }

        // Calculate fee using BillingService
        double fee = billingService.calculateFee(ticket.getType(), ticket.getEntryTime(), exitTime);
        ticket.setExitTime(exitTime);
        ticket.setFee(fee);

        // Release slot
        ParkingSlot slot = slotsById.get(ticket.getSlotId());
        if (slot != null) {
            slot.release();
            databaseManager.updateSlot(slot);
        }

        // Persist ticket completion
        databaseManager.updateTicketExit(ticket);

        // Remove from active lookup maps
        activeTicketsByVehicle.remove(ticket.getVehicleNo().toUpperCase());
        activeTicketsById.remove(ticket.getTicketId().toUpperCase());

        return ticket;
    }

    /**
     * Searches and filters parking history using Java Streams and Lambdas.
     *
     * @param vehicleQuery partial or full vehicle number (optional)
     * @param exitDate filter by date of exit (optional)
     * @return filtered list of completed tickets
     */
    public synchronized List<ParkingTicket> getFilteredHistory(String vehicleQuery, LocalDate exitDate) {
        return allTickets.stream()
                .filter(t -> !t.isActive()) // Completed tickets only for history
                .filter(t -> {
                    if (vehicleQuery == null || vehicleQuery.trim().isEmpty()) {
                        return true;
                    }
                    String q = vehicleQuery.trim().toUpperCase();
                    return t.getVehicleNo().toUpperCase().contains(q) ||
                           t.getTicketId().toUpperCase().contains(q) ||
                           t.getOwnerName().toUpperCase().contains(q);
                })
                .filter(t -> {
                    if (exitDate == null) {
                        return true;
                    }
                    return t.getExitTime() != null && t.getExitTime().toLocalDate().equals(exitDate);
                })
                .sorted((t1, t2) -> t2.getExitTime().compareTo(t1.getExitTime())) // Recent exits first
                .collect(Collectors.toList());
    }

    /**
     * Returns all tickets (active and completed).
     */
    public synchronized List<ParkingTicket> getAllTickets() {
        return new ArrayList<>(allTickets);
    }

    /**
     * Returns a snapshot of all parking slots.
     */
    public synchronized List<ParkingSlot> getAllSlots() {
        return new ArrayList<>(slots);
    }

    /**
     * Returns active tickets.
     */
    public synchronized List<ParkingTicket> getActiveTickets() {
        return new ArrayList<>(activeTicketsByVehicle.values());
    }

    /**
     * Calculates dashboard statistics using Streams.
     */
    public synchronized DashboardStats getDashboardStats() {
        LocalDate today = LocalDate.now();

        long totalSlots = slots.size();
        long occupiedSlots = slots.stream().filter(s -> !s.isAvailable()).count();
        long availableSlots = totalSlots - occupiedSlots;

        // Today's entries
        long todayEntries = allTickets.stream()
                .filter(t -> t.getEntryTime() != null && t.getEntryTime().toLocalDate().equals(today))
                .count();

        // Today's exits
        long todayExits = allTickets.stream()
                .filter(t -> !t.isActive() && t.getExitTime() != null && t.getExitTime().toLocalDate().equals(today))
                .count();

        // Total revenue across all completed tickets
        double totalRevenue = allTickets.stream()
                .filter(t -> !t.isActive())
                .mapToDouble(ParkingTicket::getFee)
                .sum();

        return new DashboardStats(totalSlots, occupiedSlots, availableSlots, todayEntries, todayExits, totalRevenue);
    }

    /**
     * Saves all current memory state to database (used by AutoSaveService and shutdown).
     */
    public synchronized void savePendingData() throws SQLException {
        databaseManager.updateAllSlots(slots);
        // Ensure all completed tickets are updated
        for (ParkingTicket ticket : allTickets) {
            if (!ticket.isActive()) {
                databaseManager.updateTicketExit(ticket);
            }
        }
    }

    private String generateUniqueTicketId(VehicleType type) {
        long timestamp = System.currentTimeMillis() % 1000000;
        int random = 100 + new Random().nextInt(900);
        return String.format("PK-%s%d%d", type.getSlotPrefix(), timestamp, random);
    }

    public BillingService getBillingService() {
        return billingService;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * Value object for dashboard stats.
     */
    public record DashboardStats(
            long totalSlots,
            long occupiedSlots,
            long availableSlots,
            long todayEntries,
            long todayExits,
            double totalRevenue
    ) {}
}
