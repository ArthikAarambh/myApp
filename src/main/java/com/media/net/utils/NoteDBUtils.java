package com.media.net.utils;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class NoteDBUtils {

    private static final String DEVICE_DB_DIR = "/data/data/com.example.myapp/databases/";
    private static final String DB_NAME = "note_database";
    private static final String LOCAL_DB_PATH = DB_NAME;
    private static final String JDBC_URL = "jdbc:sqlite:" + LOCAL_DB_PATH;

    /**
     * Pulls note_database and WAL files from the Android device to local project directory.
     */
    public static void pullDatabaseFromDevice(String deviceId) {
        try {
            System.out.println("📥 Pulling DB and WAL files...");

            // Stop the app first to flush DB
            new ProcessBuilder("adb", "-s", deviceId, "shell", "am", "force-stop", "com.example.myapp")
                    .start().waitFor();

            // Pull main DB + WAL + SHM
            pullFile(deviceId, DB_NAME);
            pullFile(deviceId, DB_NAME + "-wal");
            pullFile(deviceId, DB_NAME + "-shm");

            System.out.println("✅ Pulled: " + DB_NAME + ", " + DB_NAME + "-wal, " + DB_NAME + "-shm");

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to pull database from device", e);
        }
    }

    private static void pullFile(String deviceId, String fileName) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(
                "adb", "-s", deviceId, "pull",
                DEVICE_DB_DIR + fileName,
                fileName
        );
        Process process = builder.start();
        process.waitFor();
    }

    /**
     * Deletes a note by title.
     */
    public static void deleteNoteByTitle(String title) {
        String sql = "DELETE FROM notes WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            int rows = stmt.executeUpdate();
            System.out.println("🗑️ Deleted " + rows + " note(s) with title: " + title);

        } catch (SQLException e) {
            listTablesForDebug(); // Help you debug missing table errors
            throw new RuntimeException("❌ Failed to delete note by title", e);
        }
    }

    /**
     * Deletes a note by noteId.
     */
    public static void deleteNoteById(int noteId) {
        String sql = "DELETE FROM notes WHERE noteId = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, noteId);
            int rows = stmt.executeUpdate();
            System.out.println("🗑️ Deleted " + rows + " note(s) with ID: " + noteId);

        } catch (SQLException e) {
            listTablesForDebug();
            throw new RuntimeException("❌ Failed to delete note by ID", e);
        }
    }

    /**
     * Helper method to list all tables — useful for debugging.
     */
    private static void listTablesForDebug() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'")) {

            System.out.println("📋 Tables in the database:");
            while (rs.next()) {
                System.out.println("➡️ " + rs.getString("name"));
            }

        } catch (SQLException ex) {
            System.err.println("⚠️ Failed to list tables: " + ex.getMessage());
        }
    }
}

