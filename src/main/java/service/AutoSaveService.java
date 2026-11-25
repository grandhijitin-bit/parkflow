package service;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Background auto-save service meeting the project specifications:
 * - Runs in a separate background thread.
 * - Saves application data every 30 seconds.
 * - Uses a volatile boolean flag to control execution.
 * - Uses synchronized execution to protect shared state.
 * - Stops safely when the application closes.
 */
public class AutoSaveService implements Runnable {

    private final ParkingManager parkingManager;
    private final int intervalSeconds;

    // Volatile boolean flag to control thread execution across threads
    private volatile boolean running = false;
    private Thread workerThread;

    public AutoSaveService(ParkingManager parkingManager) {
        this(parkingManager, 30);
    }

    public AutoSaveService(ParkingManager parkingManager, int intervalSeconds) {
        this.parkingManager = Objects.requireNonNull(parkingManager, "ParkingManager cannot be null");
        this.intervalSeconds = Math.max(5, intervalSeconds);
    }

    /**
     * Starts the background auto-save thread.
     */
    public synchronized void startService() {
        if (running) {
            return;
        }
        running = true;
        workerThread = new Thread(this, "ParkingSystem-AutoSave-Thread");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    /**
     * Stops the auto-save thread safely and performs an immediate final save.
     */
    public synchronized void stopService() {
        if (!running) {
            return;
        }
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
        // Final synchronized save
        try {
            parkingManager.savePendingData();
        } catch (SQLException e) {
            System.err.println("[AutoSaveService] Error during shutdown save: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(intervalSeconds * 1000L);
                if (running) {
                    performAutoSave();
                }
            } catch (InterruptedException e) {
                // Thread interrupted on shutdown
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void performAutoSave() {
        try {
            parkingManager.savePendingData();
        } catch (SQLException e) {
            System.err.println("[AutoSaveService] Auto-save failed: " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return running;
    }
}
