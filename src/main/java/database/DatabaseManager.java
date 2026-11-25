package database;

import model.ParkingSlot;
import model.ParkingTicket;
import model.SlotStatus;
import model.VehicleType;
import util.DateTimeUtil;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * SQLite Database Manager handling database initialization, prepared statements,
 * schema migration, transaction handling, and thread-safe persistence.
 */
public class DatabaseManager {

    private static final String DEFAULT_DB_FILE = "parking_system.db";
    private final String dbUrl;

    public DatabaseManager() {
        this(DEFAULT_DB_FILE);
    }

    public DatabaseManager(String dbFileName) {
        this.dbUrl = "jdbc:sqlite:" + dbFileName;
    }

    /**
     * Obtains a new database connection.
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            // driver might be pre-registered by SPI in modern JDBC
        }
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * Initializes the database schema and seeds initial data if tables are empty.
     */
    public synchronized void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Enable foreign keys and WAL mode for better concurrency
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("PRAGMA journal_mode = WAL;");

            // 1. slots table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS slots (
                    id TEXT PRIMARY KEY,
                    type TEXT NOT NULL,
                    occupied INTEGER NOT NULL DEFAULT 0,
                    vehicle_no TEXT
                );
            """);

            // 2. tickets table
            stmt.execute("""
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
            """);

            // 3. rates table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS rates (
                    type TEXT PRIMARY KEY,
                    rate REAL NOT NULL
                );
            """);

            // Seed default rates if empty
            seedDefaultRates(conn);

            // Seed default slots if empty
            seedDefaultSlots(conn);
        }
    }

    private void seedDefaultRates(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM rates";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO rates (type, rate) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    for (VehicleType vt : VehicleType.values()) {
                        ps.setString(1, vt.name());
                        ps.setDouble(2, 50.0); // Fixed 50/hour rule
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }
    }

    private void seedDefaultSlots(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM slots";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO slots (id, type, occupied, vehicle_no) VALUES (?, ?, 0, NULL)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    // Seed 10 Bike slots: B-01 to B-10
                    for (int i = 1; i <= 10; i++) {
                        ps.setString(1, String.format("B-%02d", i));
                        ps.setString(2, VehicleType.BIKE.name());
                        ps.addBatch();
                    }
                    // Seed 15 Car slots: C-01 to C-15
                    for (int i = 1; i <= 15; i++) {
                        ps.setString(1, String.format("C-%02d", i));
                        ps.setString(2, VehicleType.CAR.name());
                        ps.addBatch();
                    }
                    // Seed 5 Truck slots: T-01 to T-05
                    for (int i = 1; i <= 5; i++) {
                        ps.setString(1, String.format("T-%02d", i));
                        ps.setString(2, VehicleType.TRUCK.name());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }
    }

    /**
     * Loads all parking slots from the database.
     */
    public synchronized List<ParkingSlot> loadAllSlots() throws SQLException {
        List<ParkingSlot> slots = new ArrayList<>();
        String sql = "SELECT id, type, occupied, vehicle_no FROM slots ORDER BY id ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("id");
                VehicleType type = VehicleType.fromString(rs.getString("type"));
                boolean occupied = rs.getInt("occupied") == 1;
                String vehicleNo = rs.getString("vehicle_no");
                SlotStatus status = occupied ? SlotStatus.OCCUPIED : SlotStatus.AVAILABLE;
                slots.add(new ParkingSlot(id, type, status, vehicleNo));
            }
        }
        return slots;
    }

    /**
     * Updates all slots in a single transaction.
     */
    public synchronized void updateAllSlots(List<ParkingSlot> slots) throws SQLException {
        String sql = "UPDATE slots SET occupied = ?, vehicle_no = ? WHERE id = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (ParkingSlot slot : slots) {
                    boolean occupied = slot.getStatus() == SlotStatus.OCCUPIED && slot.getVehicleNo() != null;
                    ps.setInt(1, occupied ? 1 : 0);
                    ps.setString(2, occupied ? slot.getVehicleNo() : null);
                    ps.setString(3, slot.getSlotId());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Updates an individual slot.
     */
    public synchronized void updateSlot(ParkingSlot slot) throws SQLException {
        String sql = "UPDATE slots SET occupied = ?, vehicle_no = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            boolean occupied = slot.getStatus() == SlotStatus.OCCUPIED && slot.getVehicleNo() != null;
            ps.setInt(1, occupied ? 1 : 0);
            ps.setString(2, occupied ? slot.getVehicleNo() : null);
            ps.setString(3, slot.getSlotId());
            ps.executeUpdate();
        }
    }

    /**
     * Inserts a new parking ticket into the database.
     */
    public synchronized void insertTicket(ParkingTicket ticket) throws SQLException {
        String sql = """
            INSERT INTO tickets (id, vehicle_no, owner_name, type, slot_id, entry_time, exit_time, fee)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticket.getTicketId());
            ps.setString(2, ticket.getVehicleNo());
            ps.setString(3, ticket.getOwnerName());
            ps.setString(4, ticket.getType().name());
            ps.setString(5, ticket.getSlotId());
            ps.setString(6, DateTimeUtil.formatIso(ticket.getEntryTime()));
            ps.setString(7, DateTimeUtil.formatIso(ticket.getExitTime()));
            ps.setDouble(8, ticket.getFee());
            ps.executeUpdate();
        }
    }

    /**
     * Updates a ticket upon vehicle exit (setting exit_time and fee).
     */
    public synchronized void updateTicketExit(ParkingTicket ticket) throws SQLException {
        String sql = "UPDATE tickets SET exit_time = ?, fee = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, DateTimeUtil.formatIso(ticket.getExitTime()));
            ps.setDouble(2, ticket.getFee());
            ps.setString(3, ticket.getTicketId());
            ps.executeUpdate();
        }
    }

    /**
     * Loads all active tickets (where exit_time IS NULL).
     */
    public synchronized List<ParkingTicket> loadActiveTickets() throws SQLException {
        List<ParkingTicket> activeTickets = new ArrayList<>();
        String sql = "SELECT id, vehicle_no, owner_name, type, slot_id, entry_time, exit_time, fee " +
                     "FROM tickets WHERE exit_time IS NULL OR exit_time = '' ORDER BY entry_time DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                activeTickets.add(mapResultSetToTicket(rs));
            }
        }
        return activeTickets;
    }

    /**
     * Loads all parking tickets (active and completed history).
     */
    public synchronized List<ParkingTicket> loadAllTickets() throws SQLException {
        List<ParkingTicket> tickets = new ArrayList<>();
        String sql = "SELECT id, vehicle_no, owner_name, type, slot_id, entry_time, exit_time, fee " +
                     "FROM tickets ORDER BY entry_time DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        }
        return tickets;
    }

    /**
     * Loads rates from database.
     */
    public synchronized Map<VehicleType, Double> loadRates() throws SQLException {
        Map<VehicleType, Double> map = new EnumMap<>(VehicleType.class);
        String sql = "SELECT type, rate FROM rates";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                VehicleType vt = VehicleType.fromString(rs.getString("type"));
                double rate = rs.getDouble("rate");
                map.put(vt, rate);
            }
        }
        return map;
    }

    /**
     * Saves or updates a rate.
     */
    public synchronized void saveRate(VehicleType type, double rate) throws SQLException {
        String sql = "INSERT INTO rates (type, rate) VALUES (?, ?) ON CONFLICT(type) DO UPDATE SET rate = excluded.rate";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            ps.setDouble(2, rate);
            ps.executeUpdate();
        }
    }

    private ParkingTicket mapResultSetToTicket(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String vehicleNo = rs.getString("vehicle_no");
        String ownerName = rs.getString("owner_name");
        VehicleType type = VehicleType.fromString(rs.getString("type"));
        String slotId = rs.getString("slot_id");
        LocalDateTime entryTime = DateTimeUtil.parseIso(rs.getString("entry_time"));
        LocalDateTime exitTime = DateTimeUtil.parseIso(rs.getString("exit_time"));
        double fee = rs.getDouble("fee");

        return new ParkingTicket(id, vehicleNo, ownerName, type, slotId, entryTime, exitTime, fee);
    }
}
