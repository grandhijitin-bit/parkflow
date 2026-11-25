# VEHICLE PARKING MANAGEMENT SYSTEM (PARKFLOW)
### Standalone Desktop Application using Advanced Java, Swing & SQLite

**Course:** CSE 213 â€“ Advanced Java Programming  
**Degree:** Bachelor of Technology in Computer Science and Engineering  
**School of Engineering and Sciences**  
**Institution:** SRM University â€“ AP, Andhra Pradesh  
**Academic Year:** 2026 â€“ 2027 (Odd Semester)  

---

### Project Submitted By:
- **Chandrala Rishit Prem** â€” `AP25110011030` *(Lead Architect: System Architecture & OOP Framework)*
- **M. Hiranya Kumar** â€” `AP25110011019` *(Core Services, Concurrency & Billing Engine)*
- **K. Komal** â€” `AP25110011040` *(Java Swing UI, Slot Matrix & Database Design)*
- **G.V.N.S.S. Jitin** â€” `AP25110010958` *(Automated Testing & Documentation)*

### Project Supervisor:
**Dr. Prakash Ankur / Dr. Prakash Ankur**  
Department of Computer Science and Engineering  
School of Engineering and Sciences, SRM University â€“ AP  

---

## CERTIFICATE

This is to certify that the project report entitled **â€œVehicle Parking Management System (ParkFlow)â€** is submitted by **Chandrala Rishit Prem (AP25110011030)**, **M. Hiranya Kumar (AP25110011019)**, **K. Komal (AP25110011040)**, and **G.V.N.S.S. Jitin (AP25110010958)**, students of the Department of Computer Science and Engineering, SRM University â€“ AP, in partial fulfilment of the requirements for the award of the degree of **Bachelor of Technology in Computer Science and Engineering**, carried out by them during the academic year 2026â€“2027 as part of the course **CSE 213 â€“ Advanced Java Programming**.

---

## ACKNOWLEDGEMENTS

We express our sincere gratitude to **SRM University â€“ AP** for providing the infrastructure and resources necessary to successfully develop this project. We extend our thanks to the Vice Chancellor and the Dean of the School of Engineering and Sciences for fostering an environment oriented toward practical software engineering.

We convey our deep gratitude to our Faculty Supervisor for their guidance, technical reviews, and encouragement throughout the software design and implementation phases. Finally, we thank our peers and families for their continuous support.

---

## ABSTRACT

The **Vehicle Parking Management System (ParkFlow)** is an independent, standalone Java desktop application developed to demonstrate Advanced Java Programming concepts through a real-world parking automation workflow. Designed for deployment on counter terminals with as little as 4 GB RAM, the system eliminates paper logs, prevents slot collisions, automates billing calculations, and provides durable persistence without relying on external web servers or heavyweight enterprise frameworks.

The software is structured across seven cohesive packages. It models parking domains using an interface-driven object-oriented hierarchy: a `Billable` interface contract, an abstract base class `Vehicle`, and concrete subclasses `Bike`, `Car`, and `Truck` exhibiting runtime polymorphism. In-memory state is managed through high-performance collections: `HashMap` for O(1) constant-time active vehicle retrieval, `EnumMap` for category tariffs, and `ArrayList` for record management. Java Streams and Lambdas process multi-criteria audit filtering and real-time revenue aggregation. Data durability is guaranteed through an embedded **SQLite database via JDBC**, utilizing ACID transactions, prepared statements, and auto-seeding. A background daemon thread (`AutoSaveService`) periodically flushes state to disk every 30 seconds using thread synchronization and a `volatile` control flag. An automated test suite (`TestParkingSystem.java`) validates eleven test specifications covering edge-case durations, slot capacity exhaustion, and persistence recovery with a 100% pass rate.

---

## TABLE OF CONTENTS
1. Introduction & Objectives
2. Literature Survey & Problem Motivation
3. Software Architecture & 7-Layer Packages
4. Object-Oriented Programming (OOP) Implementation
5. Collections, Streams & Modern Java Features
6. Deterministic Billing Engine & Rules
7. Database Design & SQLite JDBC Persistence
8. Multithreading, Concurrency & Data Safety
9. User Interface Design & Five Core Modules
10. Automated Testing & Verification
11. Conclusions & Future Scope
12. Appendix: Complete Java Source Code
13. References

---

## 1. INTRODUCTION & OBJECTIVES

Urbanization, commercial complexes, and academic campuses face acute traffic bottlenecks caused by inefficient parking management. Manual parking facilities depend on physical receipts and paper logbooks, leading to common operational failures:
1. **Slot Double-Allocation:** Attendants inadvertently allot occupied slots due to a lack of real-time visual tracking.
2. **Billing Inaccuracies:** Manual calculation of parking durations and arbitrary rounding lead to revenue leakage.
3. **Retrieval Latency:** Searching physical logbooks or unstructured records during checkout scales linearly (O(N)), causing exit queues.
4. **Data Loss:** Spills, lost tickets, or terminal crashes result in unrecoverable audit records.

As part of **CSE 213 â€“ Advanced Java Programming**, this project addresses these challenges by delivering an independent, desktop-based Vehicle Parking Management System. Inspired by modern dark-themed user experiences, the application operates independently of web services, running natively on JDK 17+ with Java Swing and SQLite.

### Objectives of the Project
- **Architectural Separation:** Decouple user interface, business services, and database persistence into a seven-package modular structure.
- **Advanced OOP Compliance:** Implement interfaces (`Billable`), abstract base classes (`Vehicle`), runtime polymorphism (`Bike`, `Car`, `Truck`), and private encapsulation across all models.
- **Deterministic Billing Engine:** Enforce fixed â‚¹50.00/hour tariffs, minimum 1-hour charges, and ceiling round-up rules for partial hours using `java.time.Duration`.
- **High-Performance Collections:** Utilize `HashMap` for O(1) active vehicle lookups, `EnumMap` for category tariffs, and Java Streams for dynamic history queries.
- **Multithreaded Data Safety:** Implement a background auto-save daemon thread that flushes memory state to SQLite every 30 seconds without blocking the Swing Event Dispatch Thread (EDT).
- **Embedded Persistence:** Guarantee zero-data-loss durability across power cycles using SQLite JDBC, prepared statements, and automatic schema provisioning.

---

## 2. SOFTWARE ARCHITECTURE & 7-LAYER PACKAGES

The application is structured into seven distinct packages:

| Package | Responsibility | Primary Classes |
|---|---|---|
| `app` | Application bootstrap, L&F initialization, Swing EDT launch | `Main.java` |
| `model` | Core domain entities, encapsulation, polymorphism | `Billable`, `Vehicle`, `Bike`, `Car`, `Truck`, `ParkingSlot`, `ParkingTicket`, `VehicleType`, `SlotStatus` |
| `service` | Business logic, tariff calculations, background auto-save | `ParkingManager`, `BillingService`, `AutoSaveService` |
| `database` | SQLite JDBC management, schema initialization, prepared statements | `DatabaseManager.java` |
| `exceptions` | Custom domain exceptions for input, duplicate, and capacity errors | `InvalidVehicleException`, `DuplicateVehicleException`, `ParkingFullException`, `VehicleNotFoundException` |
| `ui` | Dark-themed Swing desktop interface, cards, tables, modals | `Theme`, `MainFrame`, `DashboardPanel`, `ParkVehiclePanel`, `ExitVehiclePanel`, `HistoryPanel`, `ReceiptDialog` |
| `util` | Date/Time formatting and registration regex sanitization | `DateTimeUtil`, `ValidationUtil` |

---

## 3. OBJECT-ORIENTED PROGRAMMING (OOP) IMPLEMENTATION

### 3.1 Interface-Driven Design: `Billable`
The `Billable` interface establishes a standardized billing contract for all entities capable of incurring parking charges:
```java
public interface Billable {
    double calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, double hourlyRate);
}
```

### 3.2 Abstract Base Class: `Vehicle`
The abstract `Vehicle` class encapsulates common state and behavior while enforcing category-specific specialization through an abstract method:
- **Encapsulated Fields:** `private final String vehicleNumber`, `private final String ownerName`, `private final VehicleType type`.
- **Abstract Method for Polymorphism:** `public abstract String getVehicleCategoryDescription()`.
- **Default Billing Contract:** Evaluates duration in seconds, applies ceiling rounding, and computes total tariff.
- **Identity Contracts:** Overrides `equals()` and `hashCode()` using normalized uppercase registration numbers.

### 3.3 Runtime Polymorphism: `Bike`, `Car`, `Truck`
Concrete subclasses specialize descriptions and domain details:
- **`Bike`:** Overrides description returning `"Two-Wheeler (Motorcycle / Scooter / Moped)"`.
- **`Car`:** Overrides description returning `"Four-Wheeler (Sedan / SUV / Hatchback)"`.
- **`Truck`:** Overrides description returning `"Heavy Vehicle (Commercial Truck / Bus / Van)"`.
Polymorphic binding ensures the check-in form instantiates the appropriate subclass dynamically at runtime.

---

## 4. COLLECTIONS, STREAMS & MODERN JAVA FEATURES

1. **`HashMap` for O(1) Lookups:**
   - `activeTicketsByVehicle`: Maps vehicle registration numbers to active tickets for instant duplicate checks and exit lookups.
   - `activeTicketsById`: Enables instant ticket retrieval via barcode or ticket ID.
   - `slotsById`: Provides instant slot state lookups without database roundtrips.
2. **`EnumMap` for Category Tariffs:**
   - `EnumMap<VehicleType, Double>` in `BillingService` provides type-safe, array-backed memory performance superior to standard HashMaps.
3. **Java Streams and Lambdas:**
   - **Slot Allocation:** `slots.stream().filter(s -> s.getType() == type && s.isAvailable()).findFirst()`
   - **Dynamic History Filter:** Multi-criteria stream pipeline filtering completed sessions by vehicle number, driver name, and exit date.
   - **Revenue Aggregation:** `allTickets.stream().filter(t -> !t.isActive()).mapToDouble(ParkingTicket::getFee).sum()`
4. **Java Date/Time API (`java.time`):**
   - Eliminates deprecated `java.util.Date` bugs. `LocalDateTime` and `Duration` calculate exact elapsed time down to the second for deterministic ceiling rounding.

---

## 5. DETERMINISTIC BILLING ENGINE & RULES

The billing engine in `BillingService.java` enforces fixed rules:
- **Hourly Tariff:** Fixed at â‚¹50.00 / hour for all vehicle categories (stored in database `rates` table).
- **Minimum Charge:** Minimum 1 hour (â‚¹50.00), even for stays under one minute.
- **Ceiling Round-Up Rule:** Partial hours are rounded up to the next whole hour.
- **Transaction Safety:** Incomplete or aborted parking operations are never charged.

### Billing Verification Matrix
| Actual Parking Stay | Billed Hours | Total Fee (INR) | Business Rule Applied |
|---|:---:|:---:|---|
| 1 minute | 1 hour | â‚¹50.00 | Minimum 1-hour charge |
| 60 minutes | 1 hour | â‚¹50.00 | Exact hour boundary |
| 61 minutes | 2 hours | â‚¹100.00 | Ceiling rule on partial hour |
| 130 minutes (2h 10m) | 3 hours | â‚¹150.00 | Ceiling rule on partial hour |
| 5 hours | 5 hours | â‚¹250.00 | Exact multi-hour calculation |

---

## 6. DATABASE DESIGN & SQLite JDBC PERSISTENCE

The application uses an embedded **SQLite** database via JDBC (`parking_system.db`). On first launch, `DatabaseManager.java` initializes the schema and auto-seeds initial data.

### 6.1 Relational Schema
```sql
-- 1. Parking Slots Table
CREATE TABLE IF NOT EXISTS slots (
    id TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    occupied INTEGER NOT NULL DEFAULT 0,
    vehicle_no TEXT
);

-- 2. Parking Tickets Table
CREATE TABLE IF NOT EXISTS tickets (
    id TEXT PRIMARY KEY,
    vehicle_no TEXT NOT NULL,
    owner_name TEXT,
    type TEXT NOT NULL,
    slot_id TEXT NOT NULL,
    entry_time TEXT NOT NULL,
    exit_time TEXT,
    fee REAL NOT NULL DEFAULT 0.0
);

-- 3. Tariff Rates Table
CREATE TABLE IF NOT EXISTS rates (
    type TEXT PRIMARY KEY,
    rate REAL NOT NULL
);
```

### 6.2 Initial Capacity Seeding
The database auto-seeds **30 slots** on first startup:
- **10 Bike Slots:** `B-01` to `B-10`
- **15 Car Slots:** `C-01` to `C-15`
- **5 Truck Slots:** `T-01` to `T-05`
- Active tickets maintain `exit_time IS NULL`. When a vehicle checks out, `exit_time` and `fee` are updated in a single atomic prepared statement.

---

## 7. MULTITHREADING, CONCURRENCY & DATA SAFETY

Data safety across concurrent user actions and background disk writes is guaranteed through `AutoSaveService.java`.

### 7.1 Concurrency Architecture
- **Dedicated Daemon Thread:** `AutoSaveService` implements `Runnable` and runs as a daemon thread named `ParkingSystem-AutoSave-Thread`.
- **Periodic 30-Second Flush:** Calls `ParkingManager.savePendingData()` every 30 seconds to persist memory snapshots to SQLite.
- **Volatile Execution Flag:** A `volatile boolean running` flag guarantees immediate visibility of shutdown signals across threads.
- **Critical Section Synchronization:** Data mutation methods in `ParkingManager` (`parkVehicle()`, `exitVehicle()`, `savePendingData()`) are declared `synchronized` to eliminate race conditions between user check-ins and auto-save disk writes.
- **EDT Isolation:** Disk I/O runs exclusively on background threads, keeping the Swing Event Dispatch Thread (EDT) responsive. GUI updates use `SwingUtilities.invokeLater()`.

---

## 8. USER INTERFACE DESIGN & FIVE CORE MODULES

The interface features a dark-themed charcoal/navy palette (`#0F172A`, `#1E293B`) with high-contrast typography, styled tables, and interactive card layouts.

### 8.1 Module Breakdown
1. **Dashboard:**
   - Displays six real-time metric cards: Total Slots (30), Occupied Slots, Available Slots, Today's Entries, Today's Exits, and Total Revenue.
   - Includes a live system clock updated every second via `javax.swing.Timer`.
   - Features an interactive **30-Slot Grid Matrix** where vacant slots appear in emerald green and occupied slots appear in crimson red with hover tooltips displaying vehicle registration numbers.
2. **Park Vehicle (Check-In):**
   - Validates vehicle registration numbers using regex (`^[A-Z0-9 -]+$`) and sanitizes owner names.
   - Provides category selection (`Bike`, `Car`, `Truck`) and dynamically displays tariff hints.
   - Automatically allocates the next available matching slot, generates a unique ticket ID (e.g. `PK-C204918`), inserts records into SQLite, and displays an on-screen confirmation card.
3. **Exit Vehicle (Check-Out):**
   - Attendant searches active vehicles by **Vehicle Number** OR **Ticket ID** in O(1) time.
   - Displays a billing summary card showing vehicle details, entry timestamp, exit timestamp, actual duration, ceiling billed hours, and total fee.
   - Prompting confirmation releases the slot, records the exit in SQLite, and displays an **Official Tax Invoice / Receipt Modal** (`ReceiptDialog.java`).
4. **Parking History (Audit Logs):**
   - Full audit trail table displaying Ticket ID, Vehicle No, Owner, Category, Slot, Entry Time, Exit Time, Duration, Billed Hours, and Fee.
   - Substring searching by registration number, ticket ID, or owner name.
   - Date filtering by exact exit date (`YYYY-MM-DD`) or a one-click "Today" filter.
   - Live summary footer aggregating record counts and filtered revenue via Java Streams.
5. **Close Application:**
   - Exit confirmation prompt that halts `AutoSaveService`, interrupts daemon sleep, flushes all memory state to SQLite, and closes database connections safely.

---

## 9. AUTOMATED TESTING & VERIFICATION

Testing was conducted using a dedicated automated test suite (`TestParkingSystem.java` / `test.bat`) verifying all 11 test suites and 13 functional cases:

| Suite | Test Name | Business Rule / Concept Verified | Status |
|:---:|---|---|:---:|
| **T01** | `testBillingRules` | Verifies fixed â‚¹50/hr rate, 1-hr minimum, and ceiling rule (1 min = â‚¹50, 60 min = â‚¹50, 61 min = â‚¹100, 130 min = â‚¹150, 5 hrs = â‚¹250). | **PASSED [âœ“]** |
| **T02** | `testSuccessfulRegistration` | Validates ticket generation, slot prefix allocation (`PK-C...` to `C-01`), and active state. | **PASSED [âœ“]** |
| **T03** | `testInvalidVehicleNumber` | Verifies rejection of blank strings, special characters, and length limits (`InvalidVehicleException`). | **PASSED [âœ“]** |
| **T04** | `testDuplicateActiveVehicle` | Verifies rejection when parking a car that is already parked (`DuplicateVehicleException`). | **PASSED [âœ“]** |
| **T05** | `testParkingFullException` | Fills all 5 truck slots (`T-01` to `T-05`) and verifies the 6th truck triggers `ParkingFullException`. | **PASSED [âœ“]** |
| **T06** | `testSuccessfulExit` | Verifies exit time, slot release (`isAvailable() == true`), fee calculation, and active flag clearing. | **PASSED [âœ“]** |
| **T07** | `testUnknownVehicleLookup` | Verifies `VehicleNotFoundException` when querying a vehicle or ticket ID that does not exist. | **PASSED [âœ“]** |
| **T08** | `testRevenueCalculation` | Verifies cumulative revenue accuracy across multiple vehicle types using `Stream.mapToDouble()`. | **PASSED [âœ“]** |
| **T09** | `testHistoryFiltering` | Verifies Streams and Lambdas correctly filter by vehicle number, owner, and exit date. | **PASSED [âœ“]** |
| **T10** | `testPersistenceAcrossRestart` | Creates state in Instance 1, shuts down, launches Instance 2 on same DB, and verifies all records persist. | **PASSED [âœ“]** |
| **T11** | `testAutoSaveAndShutdown` | Verifies `AutoSaveService` background thread execution, `volatile` running flag, and safe shutdown. | **PASSED [âœ“]** |

**Summary: 11 Test Suites Executed Â· 11 Passed Â· 0 Failed (100% Pass Rate)**

---

## 10. CONCLUSIONS & FUTURE SCOPE

The **Vehicle Parking Management System** bridges academic Advanced Java concepts with a functional, desktop software design:
1. **Object-Oriented Design:** Clear separation of interfaces, abstract classes, and polymorphic implementations.
2. **Modern APIs:** Replaced legacy classes with `java.time.LocalDateTime`, `Duration`, Streams, and Lambdas.
3. **Data Safety:** Atomic SQLite operations combined with background multithreaded auto-save.
4. **Lightweight Footprint:** Operates within 4 GB RAM machines without heavy frameworks.

### Future Roadmap
- **Computer Vision (ANPR):** Automated number plate recognition using camera feeds.
- **Hardware Integration:** IoT ultrasonic sensors to operate gate barrier arms.
- **Digital Payments:** Dynamic UPI QR code generation on checkout receipts.
- **Multi-Counter Sync:** Transitioning embedded SQLite to networked MySQL for multi-gate facilities.

---

## 11. REFERENCES

1. Schildt, H., *Java: The Complete Reference*, 12th Edition, McGraw-Hill / Oracle Press (2021).
2. Bloch, J., *Effective Java*, 3rd Edition, Addison-Wesley (2018).
3. Horstmann, C. S., *Core Java Volume I â€“ Fundamentals*, 12th Edition, Pearson (2022).
4. Oracle, *Java SE Documentation & Tutorials (JDBC, Swing, Concurrency)*, docs.oracle.com (2026).
5. SQLite Consortium, *SQLite Database Documentation & Engine Specification*, sqlite.org (2026).
6. Xerial, *SQLite JDBC Driver*, github.com/xerial/sqlite-jdbc (2026).

