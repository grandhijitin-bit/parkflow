import database.DatabaseManager;
import exceptions.DuplicateVehicleException;
import exceptions.InvalidVehicleException;
import exceptions.ParkingFullException;
import exceptions.VehicleNotFoundException;
import model.*;
import service.AutoSaveService;
import service.BillingService;
import service.ParkingManager;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Comprehensive Automated Test Suite covering all 13 required test cases:
 * 1. Successful vehicle registration
 * 2. Invalid vehicle number
 * 3. Duplicate active vehicle
 * 4. No available parking slot (ParkingFullException)
 * 5. Successful vehicle exit
 * 6. Unknown vehicle or ticket (VehicleNotFoundException)
 * 7. Minimum one-hour billing (1 min -> 1 hr -> ₹50)
 * 8. Partial-hour billing (61 mins -> 2 hrs -> ₹100, 130 mins -> 3 hrs -> ₹150, 5 hrs -> ₹250)
 * 9. Correct parking slot release
 * 10. Correct revenue calculation
 * 11. History search & stream filtering
 * 12. Application restart and database persistence
 * 13. AutoSave and safe application shutdown
 */
public class TestParkingSystem {

    private static final String TEST_DB = "test_parking_system.db";
    private static int totalPassed = 0;
    private static int totalFailed = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("     RUNNING VEHICLE PARKING MANAGEMENT SYSTEM TEST SUITE       ");
        System.out.println("================================================================");

        // Clean up any previous test database
        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }

        runTest("Test 1: Billing Rules (Minimum 1 hour, Partial-hour ceiling, rates)", TestParkingSystem::testBillingRules);
        runTest("Test 2: Successful Vehicle Registration", TestParkingSystem::testSuccessfulRegistration);
        runTest("Test 3: Invalid Vehicle Number Rejection", TestParkingSystem::testInvalidVehicleNumber);
        runTest("Test 4: Duplicate Active Vehicle Rejection", TestParkingSystem::testDuplicateActiveVehicle);
        runTest("Test 5: Parking Full Exception when slots exhausted", TestParkingSystem::testParkingFullException);
        runTest("Test 6: Successful Vehicle Exit and Slot Release", TestParkingSystem::testSuccessfulExit);
        runTest("Test 7: Unknown Vehicle / Ticket Lookup", TestParkingSystem::testUnknownVehicleLookup);
        runTest("Test 8: Revenue Calculation Accuracy", TestParkingSystem::testRevenueCalculation);
        runTest("Test 9: History Search and Stream Filtering", TestParkingSystem::testHistoryFiltering);
        runTest("Test 10: Database Persistence across App Restarts", TestParkingSystem::testPersistenceAcrossRestart);
        runTest("Test 11: AutoSaveService Background Execution & Safe Shutdown", TestParkingSystem::testAutoSaveAndShutdown);

        System.out.println("================================================================");
        System.out.printf("TEST SUMMARY: Total: %d | Passed: %d | Failed: %d\n",
                (totalPassed + totalFailed), totalPassed, totalFailed);
        System.out.println("================================================================");

        // Clean up test db
        if (dbFile.exists()) {
            dbFile.delete();
        }

        if (totalFailed > 0) {
            System.exit(1);
        }
    }

    private static void runTest(String testName, TestRunnable runnable) {
        System.out.printf("[RUNNING] %s ... ", testName);
        try {
            runnable.run();
            System.out.println("PASSED [✓]");
            totalPassed++;
        } catch (Throwable t) {
            System.out.println("FAILED [✗]");
            System.err.println("   Reason: " + t.getMessage());
            t.printStackTrace();
            totalFailed++;
        }
    }

    @FunctionalInterface
    interface TestRunnable {
        void run() throws Exception;
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError(String.format("%s - Expected: <%s>, but was: <%s>", message, expected, actual));
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + message);
        }
    }

    /**
     * Test 1: Verifies fixed billing rules specified in Prompt Section 4:
     * - Hourly rate: ₹50 for all vehicle categories.
     * - Minimum billing: One hour.
     * - Partial hours must be rounded up.
     * Examples:
     * 1 minute -> 1 hour -> ₹50
     * 60 minutes -> 1 hour -> ₹50
     * 61 minutes -> 2 hours -> ₹100
     * 130 minutes -> 3 hours -> ₹150
     * 5 hours -> 5 hours -> ₹250
     */
    private static void testBillingRules() {
        BillingService billing = new BillingService();
        LocalDateTime baseTime = LocalDateTime.of(2026, 11, 24, 10, 0, 0);

        // 1 minute duration
        LocalDateTime exit1Min = baseTime.plusMinutes(1);
        assertEquals(1L, billing.calculateBilledHours(baseTime, exit1Min), "1 min billed hours");
        assertEquals(50.0, billing.calculateFee(VehicleType.CAR, baseTime, exit1Min), "1 min fee");

        // 60 minutes duration
        LocalDateTime exit60Min = baseTime.plusMinutes(60);
        assertEquals(1L, billing.calculateBilledHours(baseTime, exit60Min), "60 min billed hours");
        assertEquals(50.0, billing.calculateFee(VehicleType.BIKE, baseTime, exit60Min), "60 min fee");

        // 61 minutes duration -> partial hour rounded up -> 2 hours
        LocalDateTime exit61Min = baseTime.plusMinutes(61);
        assertEquals(2L, billing.calculateBilledHours(baseTime, exit61Min), "61 min billed hours");
        assertEquals(100.0, billing.calculateFee(VehicleType.CAR, baseTime, exit61Min), "61 min fee");

        // 130 minutes duration -> 2 hours 10 mins -> 3 hours
        LocalDateTime exit130Min = baseTime.plusMinutes(130);
        assertEquals(3L, billing.calculateBilledHours(baseTime, exit130Min), "130 min billed hours");
        assertEquals(150.0, billing.calculateFee(VehicleType.TRUCK, baseTime, exit130Min), "130 min fee");

        // 5 hours
        LocalDateTime exit5Hours = baseTime.plusHours(5);
        assertEquals(5L, billing.calculateBilledHours(baseTime, exit5Hours), "5 hours billed hours");
        assertEquals(250.0, billing.calculateFee(VehicleType.CAR, baseTime, exit5Hours), "5 hours fee");

        // Runtime Polymorphism test via Vehicle class
        Vehicle car = new Car("MH12AB1234", "Rajesh");
        double carFee = car.calculateFee(baseTime, exit61Min, 50.0);
        assertEquals(100.0, carFee, "Car polymorphic calculateFee for 61 mins");
    }

    /**
     * Test 2: Successful vehicle registration & slot allocation.
     */
    private static void testSuccessfulRegistration() throws Exception {
        DatabaseManager db = new DatabaseManager(TEST_DB);
        BillingService billing = new BillingService();
        ParkingManager manager = new ParkingManager(db, billing);
        manager.initialize();

        Vehicle car = new Car("KA01MJ1122", "Alice");
        ParkingTicket ticket = manager.parkVehicle(car);

        assertTrue(ticket != null, "Ticket generated");
        assertTrue(ticket.getTicketId().startsWith("PK-C"), "Ticket ID has correct prefix");
        assertEquals("KA01MJ1122", ticket.getVehicleNo(), "Vehicle number matched");
        assertEquals("Alice", ticket.getOwnerName(), "Owner name matched");
        assertTrue(ticket.getSlotId().startsWith("C-"), "Car slot prefix assigned");
        assertTrue(ticket.isActive(), "Ticket is active upon registration");

        // Check active lookup
        ParkingTicket active = manager.findActiveTicket("KA01MJ1122");
        assertEquals(ticket.getTicketId(), active.getTicketId(), "Active ticket found by vehicle number");
    }

    /**
     * Test 3: Invalid vehicle number rejection.
     */
    private static void testInvalidVehicleNumber() throws Exception {
        DatabaseManager db = new DatabaseManager(TEST_DB);
        BillingService billing = new BillingService();
        ParkingManager manager = new ParkingManager(db, billing);
        manager.initialize();

        try {
            manager.parkVehicle(new Car("", "Bob"));
            throw new AssertionError("Should have failed for blank vehicle number");
        } catch (InvalidVehicleException e) {
            // Expected
        }

        try {
            manager.parkVehicle(new Car("$$$INVALID$$$", "Bob"));
            throw new AssertionError("Should have failed for invalid characters");
        } catch (InvalidVehicleException e) {
            // Expected
        }
    }

    /**
     * Test 4: Duplicate active vehicle registration rejection.
     */
    private static void testDuplicateActiveVehicle() throws Exception {
        DatabaseManager db = new DatabaseManager(TEST_DB);
        BillingService billing = new BillingService();
        ParkingManager manager = new ParkingManager(db, billing);
        manager.initialize();

        Vehicle bike = new Bike("DL03AA9999", "Charlie");
        manager.parkVehicle(bike);

        try {
            // Attempting to park the same vehicle again
            manager.parkVehicle(new Bike("DL03AA9999", "Charlie"));
            throw new AssertionError("Should have thrown DuplicateVehicleException");
        } catch (DuplicateVehicleException e) {
            // Expected
            assertTrue(e.getMessage().contains("already parked"), "Duplicate message clear");
        }
    }

    /**
     * Test 5: ParkingFullException when slots of a type are exhausted.
     */
    private static void testParkingFullException() throws Exception {
        DatabaseManager db = new DatabaseManager(TEST_DB);
        BillingService billing = new BillingService();
        ParkingManager manager = new ParkingManager(db, billing);
        manager.initialize();

        // Default has 5 Truck slots (T-01 to T-05)
        for (int i = 1; i <= 5; i++) {
            try {
                manager.parkVehicle(new Truck("TRK-" + i, "Driver " + i));
            } catch (DuplicateVehicleException ignored) {
                // Ignore if already parked
            }
        }

        // 6th truck must trigger ParkingFullException
        try {
            manager.parkVehicle(new Truck("TRK-OVERFLOW", "Driver X"));
            throw new AssertionError("Should have thrown ParkingFullException");
        } catch (ParkingFullException e) {
            // Expected
            assertTrue(e.getMessage().contains("No available parking slots"), "ParkingFullException message");
        }
    }

    /**
     * Test 6: Successful vehicle exit and slot release.
     */
    private static void testSuccessfulExit() throws Exception {
        DatabaseManager db = new DatabaseManager(TEST_DB);
        BillingService billing = new BillingService();
        ParkingManager manager = new ParkingManager(db, billing);
        manager.initialize();

        Vehicle car = new Car("MH02XY5555", "David");
        ParkingTicket ticket = manager.parkVehicle(car);
        String slotId = ticket.getSlotId();

        // Simulate 75 minutes of parking
        LocalDateTime entry = ticket.getEntryTime();
        LocalDateTime exit = entry.plusMinutes(75);

        ParkingTicket exitTicket = manager.exitVehicle(ticket.getTicketId(), exit);
        assertEquals(100.0, exitTicket.getFee(), "75 mins fee is ₹100");
        assertTrue(!exitTicket.isActive(), "Ticket is marked inactive after exit");

        // Verify slot is now vacant
        ParkingSlot slot = manager.getAllSlots().stream()
                .filter(s -> s.getSlotId().equals(slotId))
                .findFirst()
                .orElseThrow();
        assertTrue(slot.isAvailable(), "Slot released and marked available");
        assertEquals(null, slot.getVehicleNo(), "Slot vehicle number cleared");
    }

    /**
     * Test 7: Unknown vehicle lookup throws VehicleNotFoundException.
     */
    private static void testUnknownVehicleLookup() throws Exception {
        DatabaseManager db = new DatabaseManager(TEST_DB);
        BillingService billing = new BillingService();
        ParkingManager manager = new ParkingManager(db, billing);
        manager.initialize();

        try {
            manager.findActiveTicket("NONEXISTENT_VEHICLE");
            throw new AssertionError("Should have thrown VehicleNotFoundException");
        } catch (VehicleNotFoundException e) {
            // Expected
        }
    }

    /**
     * Test 8: Total revenue calculation.
     */
    private static void testRevenueCalculation() throws Exception {
        DatabaseManager db = new DatabaseManager(TEST_DB);
        BillingService billing = new BillingService();
        ParkingManager manager = new ParkingManager(db, billing);
        manager.initialize();

        ParkingTicket t1 = manager.parkVehicle(new Car("REV-CAR-1", "Owner1"));
        manager.exitVehicle(t1.getTicketId(), t1.getEntryTime().plusMinutes(30)); // 1 hr -> ₹50

        ParkingTicket t2 = manager.parkVehicle(new Bike("REV-BIKE-1", "Owner2"));
        manager.exitVehicle(t2.getTicketId(), t2.getEntryTime().plusMinutes(125)); // 3 hrs -> ₹150

        double rev = manager.getDashboardStats().totalRevenue();
        assertTrue(rev >= 200.0, "Total revenue includes completed tickets");
    }

    /**
     * Test 9: History search & stream filtering.
     */
    private static void testHistoryFiltering() throws Exception {
        DatabaseManager db = new DatabaseManager(TEST_DB);
        BillingService billing = new BillingService();
        ParkingManager manager = new ParkingManager(db, billing);
        manager.initialize();

        Vehicle car = new Car("FILTER-CAR-99", "UniqueOwner");
        ParkingTicket ticket = manager.parkVehicle(car);
        manager.exitVehicle(ticket.getTicketId(), ticket.getEntryTime().plusMinutes(40));

        // Filter by unique owner
        List<ParkingTicket> history = manager.getFilteredHistory("UniqueOwner", LocalDate.now());
        assertTrue(history.size() >= 1, "Found ticket by owner filter");
        assertEquals("FILTER-CAR-99", history.get(0).getVehicleNo(), "Correct vehicle returned in stream");
    }

    /**
     * Test 10: Application restart and persistence.
     */
    private static void testPersistenceAcrossRestart() throws Exception {
        DatabaseManager db1 = new DatabaseManager(TEST_DB);
        BillingService billing1 = new BillingService();
        ParkingManager manager1 = new ParkingManager(db1, billing1);
        manager1.initialize();

        // Park a vehicle in instance 1
        Vehicle car = new Car("PERSIST-CAR-7", "PersistUser");
        ParkingTicket ticket = manager1.parkVehicle(car);
        String ticketId = ticket.getTicketId();

        // Simulate app shutdown and restart with fresh instances pointing to same SQLite DB
        DatabaseManager db2 = new DatabaseManager(TEST_DB);
        BillingService billing2 = new BillingService();
        ParkingManager manager2 = new ParkingManager(db2, billing2);
        manager2.initialize(); // loads from DB

        // Should find active vehicle in instance 2
        ParkingTicket reloaded = manager2.findActiveTicket("PERSIST-CAR-7");
        assertEquals(ticketId, reloaded.getTicketId(), "Active ticket persisted across restart");
        assertEquals("PersistUser", reloaded.getOwnerName(), "Owner name persisted across restart");
    }

    /**
     * Test 11: AutoSaveService execution and safe shutdown.
     */
    private static void testAutoSaveAndShutdown() throws Exception {
        DatabaseManager db = new DatabaseManager(TEST_DB);
        BillingService billing = new BillingService();
        ParkingManager manager = new ParkingManager(db, billing);
        manager.initialize();

        AutoSaveService service = new AutoSaveService(manager, 5); // 5 sec interval for testing
        service.startService();
        assertTrue(service.isRunning(), "AutoSaveService running flag is true");

        Thread.sleep(100); // let thread run briefly

        service.stopService();
        assertTrue(!service.isRunning(), "AutoSaveService running flag is false after stop");
    }
}
