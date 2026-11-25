# Vehicle Parking Management System (ParkFlow)

A standalone desktop application built in **Java Swing** and **SQLite** demonstrating Advanced Java Programming principles, modern UI design, and robust multithreading.

Inspired by the design of ParkFlow, this application provides an independent parking-management workflow with fixed billing rules, real-time dashboard analytics, slot matrix visualization, receipt generation, and background synchronization.

---

## 1. Key Features

- **Modern Dark-Themed GUI**: Charcoal/deep navy theme (`#0f172a`, `#1e293b`) with high-contrast statistics cards, slot matrix visualization, and table styling.
- **Complete Workflow**:
  1. **Dashboard**: Live counter cards (Total Slots, Occupied, Vacant, Today's Entries, Today's Exits, Total Revenue), live updating system clock, and visual slot status grid.
  2. **Park Vehicle (Check-In)**: Registration number validation, vehicle category selection (`Bike`, `Car`, `Truck`), automatic slot allocation, unique ticket generation, and parking confirmation.
  3. **Exit Vehicle (Check-Out)**: Search by Vehicle Number or Ticket ID, duration calculation, ceiling billed hours, instant fee assessment, confirmation, slot release, and receipt display.
  4. **Parking History**: Audit trail table with instant search and date-based filtering powered by Java Streams.
  5. **Close Application**: Confirmation prompt with graceful shutdown of background threads and data synchronization.
- **Fixed Billing Engine**:
  - Hourly rate: ₹50 for all vehicle types.
  - Minimum charge: 1 hour (₹50).
  - Ceiling rule: Partial hours rounded up (e.g. 1 min → 1 hr → ₹50; 61 min → 2 hrs → ₹100; 130 min → 3 hrs → ₹150).
- **Background Auto-Save**: Thread-safe daemon thread running every 30 seconds with volatile control flags and synchronized persistence.
- **Zero-Friction Execution**: Includes pre-configured `pom.xml`, Maven wrapper support, and standalone Windows batch files (`build.bat`, `run.bat`, `test.bat`).

---

## 2. Advanced Java Concepts Demonstrated

| Concept | Implementation in Project |
|---|---|
| **Interface** | `model.Billable`: Defines the fee calculation contract. |
| **Abstract Class** | `model.Vehicle`: Encapsulates vehicle number, owner name, category, and common behavior. |
| **Runtime Polymorphism** | Subclasses `Bike`, `Car`, and `Truck` override category descriptions and polymorphic calculations. |
| **Encapsulation** | Private fields with explicit getters, setters, and immutability where applicable across all models. |
| **Custom Exceptions** | `InvalidVehicleException`, `DuplicateVehicleException`, `ParkingFullException`, `VehicleNotFoundException`. |
| **Collections** | `ArrayList` for records, `HashMap` for $O(1)$ active ticket and slot lookups, `EnumMap` for vehicle tariff rates. |
| **Java Streams & Lambdas** | Used in `ParkingManager.getFilteredHistory()`, `getDashboardStats()`, and slot allocation filters. |
| **Java Date/Time API** | `LocalDateTime`, `Duration`, `LocalDate`, `DateTimeFormatter` for robust time tracking without legacy `java.util.Date`. |
| **Multithreading** | `service.AutoSaveService` running as a background daemon thread with `volatile boolean running` flag and synchronized critical sections. |
| **Database & JDBC** | `database.DatabaseManager` using SQLite with prepared statements, connection isolation, and schema auto-initialization. |

---

## 3. Database Schema (SQLite)

The system automatically initializes `parking_system.db` on first run with three primary tables:

```sql
-- 1. Parking Slots
CREATE TABLE slots (
    id TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    occupied INTEGER NOT NULL DEFAULT 0,
    vehicle_no TEXT
);

-- 2. Parking Tickets
CREATE TABLE tickets (
    id TEXT PRIMARY KEY,
    vehicle_no TEXT NOT NULL,
    owner_name TEXT,
    type TEXT NOT NULL,
    slot_id TEXT NOT NULL,
    entry_time TEXT NOT NULL,
    exit_time TEXT,
    fee REAL NOT NULL DEFAULT 0.0
);

-- 3. Tariff Rates
CREATE TABLE rates (
    type TEXT PRIMARY KEY,
    rate REAL NOT NULL
);
```
- Active tickets maintain `exit_time IS NULL`.
- Default configuration seeds **30 slots** (10 Bikes: `B-01` to `B-10`, 15 Cars: `C-01` to `C-15`, 5 Trucks: `T-01` to `T-05`) and standard ₹50 rates.

---

## 4. Project Directory Structure

```
├── lib/
│   ├── sqlite-jdbc-3.46.1.0.jar
│   └── slf4j-api-1.7.36.jar
├── pom.xml
├── build.bat
├── run.bat
├── test.bat
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       ├── app/
    │       │   └── Main.java
    │       ├── model/
    │       │   ├── Billable.java
    │       │   ├── VehicleType.java
    │       │   ├── SlotStatus.java
    │       │   ├── Vehicle.java
    │       │   ├── Bike.java
    │       │   ├── Car.java
    │       │   ├── Truck.java
    │       │   ├── ParkingSlot.java
    │       │   └── ParkingTicket.java
    │       ├── service/
    │       │   ├── BillingService.java
    │       │   ├── ParkingManager.java
    │       │   └── AutoSaveService.java
    │       ├── database/
    │       │   └── DatabaseManager.java
    │       ├── exceptions/
    │       │   ├── InvalidVehicleException.java
    │       │   ├── DuplicateVehicleException.java
    │       │   ├── ParkingFullException.java
    │       │   └── VehicleNotFoundException.java
    │       ├── ui/
    │       │   ├── Theme.java
    │       │   ├── MainFrame.java
    │       │   ├── DashboardPanel.java
    │       │   ├── ParkVehiclePanel.java
    │       │   ├── ExitVehiclePanel.java
    │       │   ├── HistoryPanel.java
    │       │   └── ReceiptDialog.java
    │       └── util/
    │           ├── DateTimeUtil.java
    │           └── ValidationUtil.java
    └── test/
        └── java/
            └── TestParkingSystem.java
```

---

## 5. Prerequisites

- **Java Development Kit (JDK)**: JDK 17 or newer (tested with JDK 26 / OpenJDK 17+).
- **RAM**: Lightweight, runs efficiently on 4 GB RAM systems.
- **Operating System**: Windows, macOS, or Linux.

---

## 6. How to Build and Run

### Method A: Using the Provided Scripts (Fastest & Simplest)

1. **Compile**:
   Double click `build.bat` or run:
   ```cmd
   build.bat
   ```
2. **Run Application**:
   Double click `run.bat` or run:
   ```cmd
   run.bat
   ```
3. **Run Automated Test Suite**:
   Double click `test.bat` or run:
   ```cmd
   test.bat
   ```

### Method B: Using Apache Maven

If Maven is installed on your system:
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package executable JAR
mvn package

# Launch application
java -jar target/vehicle-parking-system-1.0.0.jar
```

---

## 7. Automated Test Suite Results

The automated test runner (`TestParkingSystem.java` / `test.bat`) verifies all business rules and edge cases:

```
================================================================
     RUNNING VEHICLE PARKING MANAGEMENT SYSTEM TEST SUITE       
================================================================
[RUNNING] Test 1: Billing Rules (Minimum 1 hour, Partial-hour ceiling, rates) ... PASSED [✓]
[RUNNING] Test 2: Successful Vehicle Registration ... PASSED [✓]
[RUNNING] Test 3: Invalid Vehicle Number Rejection ... PASSED [✓]
[RUNNING] Test 4: Duplicate Active Vehicle Rejection ... PASSED [✓]
[RUNNING] Test 5: Parking Full Exception when slots exhausted ... PASSED [✓]
[RUNNING] Test 6: Successful Vehicle Exit and Slot Release ... PASSED [✓]
[RUNNING] Test 7: Unknown Vehicle / Ticket Lookup ... PASSED [✓]
[RUNNING] Test 8: Revenue Calculation Accuracy ... PASSED [✓]
[RUNNING] Test 9: History Search and Stream Filtering ... PASSED [✓]
[RUNNING] Test 10: Database Persistence across App Restarts ... PASSED [✓]
[RUNNING] Test 11: AutoSaveService Background Execution & Safe Shutdown ... PASSED [✓]
================================================================
TEST SUMMARY: Total: 11 | Passed: 11 | Failed: 0
================================================================
```

---

## 8. Verification Workflow

1. **Launch App**: Click `run.bat`. The dark-themed GUI opens displaying the Live Dashboard with 30 total slots (10 Bike, 15 Car, 5 Truck).
2. **Park Vehicle**: Navigate to **Park Vehicle**, enter `MH12AB1234`, Owner `Rajesh Sharma`, select `Four-Wheeler / Car`. Click **Park Vehicle & Issue Ticket**.
3. **Verify Dashboard**: Switch to **Dashboard**; Occupied slots increments to 1, Available slots becomes 29, and slot `C-01` turns red with vehicle tooltip.
4. **Exit Vehicle**: Navigate to **Exit Vehicle**, enter `MH12AB1234` or the generated Ticket ID, click **Locate Vehicle**. The duration, ceiling billed hours, and fee (₹50/hour) are displayed.
5. **Confirm Exit**: Click **Confirm Exit & Pay**. A formal invoice receipt dialog opens, and slot `C-01` is released.
6. **Check History**: Navigate to **Parking History**. The completed session appears with all audit details and revenue totals. Filter by vehicle or click **Today**.
7. **Safe Exit**: Click **Close Application**. A confirmation dialog safely terminates the auto-save thread, commits all state, and closes the application.
