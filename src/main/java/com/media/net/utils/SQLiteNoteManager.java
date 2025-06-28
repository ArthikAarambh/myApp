package com.media.net.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for managing notes in SQLite database on Android device (non-rooted)
 * Supports deleting notes by ID or name through ADB commands
 */
public class SQLiteNoteManager {

    private final String packageName;
    private final String databaseName;
    private final String tempDir;

    /**
     * Constructor
     * @param packageName Android app package name (e.g., "com.example.myapp")
     * @param databaseName Database file name (e.g., "note_database")
     */
    public SQLiteNoteManager(String packageName, String databaseName) {
        this.packageName = packageName;
        this.databaseName = databaseName;
        // Use current working directory instead of system temp for better control
        this.tempDir = System.getProperty("user.dir");
        System.out.println("Working directory: " + this.tempDir);
        System.out.println("Package: " + this.packageName);
        System.out.println("Database: " + this.databaseName);
    }

    /**
     * Delete note by ID
     * @param noteId The ID of the note to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteNoteById(int noteId) {
        String deleteQuery = String.format("DELETE FROM notes WHERE noteId = %d;", noteId);
        return executeDeleteOperation(deleteQuery, "noteId = " + noteId);
    }

    /**
     * Delete note by name
     * @param noteName The name of the note to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteNoteByName(String noteName) {
        String deleteQuery = String.format("DELETE FROM notes WHERE name = '%s';", noteName.replace("'", "''"));
        return executeDeleteOperation(deleteQuery, "name = '" + noteName + "'");
    }

    /**
     * Delete multiple notes by IDs
     * @param noteIds List of note IDs to delete
     * @return true if all deletions were successful, false otherwise
     */
    public boolean deleteNotesByIds(List<Integer> noteIds) {
        if (noteIds == null || noteIds.isEmpty()) {
            System.out.println("No note IDs provided for deletion");
            return false;
        }

        StringBuilder deleteQuery = new StringBuilder("DELETE FROM notes WHERE noteId IN (");
        for (int i = 0; i < noteIds.size(); i++) {
            deleteQuery.append(noteIds.get(i));
            if (i < noteIds.size() - 1) {
                deleteQuery.append(", ");
            }
        }
        deleteQuery.append(");");

        return executeDeleteOperation(deleteQuery.toString(), "noteIds = " + noteIds.toString());
    }

    /**
     * Get all notes from database
     * @return List of note information strings
     */
    public List<String> getAllNotes() {
        List<String> notes = new ArrayList<>();

        try {
            // Extract database files
            if (!extractDatabaseFiles()) {
                return notes;
            }

            // Query all notes
            String[] queryCommand = {
                    "sqlite3",
                    tempDir + File.separator + "note_database.db",
                    "SELECT noteId, name FROM notes;"
            };

            ProcessBuilder pb = new ProcessBuilder(queryCommand);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                notes.add(line);
            }

            process.waitFor();
            reader.close();

        } catch (Exception e) {
            System.err.println("Error getting notes: " + e.getMessage());
            e.printStackTrace();
        }

        return notes;
    }

    /**
     * Core method to execute delete operations
     */
    private boolean executeDeleteOperation(String deleteQuery, String logInfo) {
        try {
            System.out.println("Starting deletion process for: " + logInfo);

            // Step 1: Extract database files
            if (!extractDatabaseFiles()) {
                return false;
            }

            // Step 2: Execute delete query
            if (!executeDeleteQuery(deleteQuery)) {
                return false;
            }

            // Step 3: Push modified database back
            if (!pushDatabaseBack()) {
                return false;
            }

            // Step 4: Clean up and refresh app
            if (!cleanupAndRefreshApp()) {
                return false;
            }

            System.out.println("✅ Successfully deleted note(s): " + logInfo);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error during deletion process: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Step 1: Extract database files from device
     */
    private boolean extractDatabaseFiles() {
        try {
            System.out.println("📱 Extracting database files from device...");

            // First, create temp directory in app's cache
            String createTempDir = String.format("adb shell run-as %s mkdir -p cache/temp", packageName);
            executeCommand(createTempDir);

            // Copy database files to app's cache
            String[] commands = {
                    String.format("adb shell run-as %s cp databases/%s cache/temp/", packageName, databaseName),
                    String.format("adb shell run-as %s cp databases/%s-wal cache/temp/", packageName, databaseName),
                    String.format("adb shell run-as %s cp databases/%s-shm cache/temp/", packageName, databaseName)
            };

            for (String command : commands) {
                if (!executeCommand(command)) {
                    System.err.println("Failed to execute: " + command);
                }
            }

            // Pull files to local machine
            String[] pullCommands = {
                    String.format("adb shell run-as %s cat cache/temp/%s", packageName, databaseName),
                    String.format("adb shell run-as %s cat cache/temp/%s-wal", packageName, databaseName),
                    String.format("adb shell run-as %s cat cache/temp/%s-shm", packageName, databaseName)
            };

            String[] localFiles = {
                    "note_database.db",
                    "note_database.db-wal",
                    "note_database.db-shm"
            };

            for (int i = 0; i < pullCommands.length; i++) {
                if (!executeCommandWithOutput(pullCommands[i], tempDir + File.separator + localFiles[i])) {
                    System.err.println("Failed to pull: " + localFiles[i]);
                }
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error extracting database files: " + e.getMessage());
            return false;
        }
    }

    /**
     * Step 2: Execute delete query on local database
     */
    private boolean executeDeleteQuery(String deleteQuery) {
        try {
            System.out.println("🗑️ Executing delete query...");

            String[] command = {
                    "sqlite3",
                    tempDir + File.separator + "note_database.db",
                    deleteQuery
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Delete query executed successfully");
                return true;
            } else {
                System.err.println("Delete query failed with exit code: " + exitCode);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error executing delete query: " + e.getMessage());
            return false;
        }
    }

    /**
     * Step 3: Push modified database back to device
     */
    private boolean pushDatabaseBack() {
        try {
            System.out.println("📲 Pushing modified database back to device...");

            String[] commands = {
                    String.format("adb push %s/note_database.db /data/local/tmp/temp_db.db", tempDir),
                    String.format("adb shell run-as %s cp /data/local/tmp/temp_db.db cache/temp/note_database_modified", packageName),
                    String.format("adb shell run-as %s cp cache/temp/note_database_modified databases/%s", packageName, databaseName)
            };

            for (String command : commands) {
                if (!executeCommand(command)) {
                    System.err.println("Failed to execute: " + command);
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error pushing database back: " + e.getMessage());
            return false;
        }
    }

    /**
     * Step 4: Clean up and refresh app state
     */
    private boolean cleanupAndRefreshApp() {
        try {
            System.out.println("🧹 Cleaning up and refreshing app...");

            String[] commands = {
                    String.format("adb shell run-as %s rm databases/%s-wal", packageName, databaseName),
                    String.format("adb shell run-as %s rm databases/%s-shm", packageName, databaseName),
                    String.format("adb shell am force-stop %s", packageName),
                    "adb shell rm /data/local/tmp/temp_db.db",
                    String.format("adb shell run-as %s rm cache/temp/note_database_modified", packageName)
            };

            for (String command : commands) {
                executeCommand(command); // Continue even if some cleanup commands fail
            }

            // Optional: Restart the app (commented out as user might want to start manually)
            // executeCommand(String.format("adb shell am start %s/.MainActivity", packageName));

            return true;

        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
            return false;
        }
    }

    /**
     * Execute ADB command
     */
    private boolean executeCommand(String command) {
        try {
            System.out.println("Executing: " + command);

            ProcessBuilder pb = new ProcessBuilder();
            pb.command("sh", "-c", command); // Use 'sh' instead of 'bash' for better macOS compatibility
            pb.directory(new File(tempDir)); // Set working directory

            Process process = pb.start();

            // Read any error output
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorOutput.append(errorLine).append("\n");
                System.err.println("Error: " + errorLine);
            }

            // Read standard output too
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputLine;
            while ((outputLine = outputReader.readLine()) != null) {
                System.out.println("Output: " + outputLine);
            }

            int exitCode = process.waitFor();
            System.out.println("Exit code: " + exitCode);

            if (exitCode != 0 && errorOutput.length() > 0) {
                System.err.println("Full error output: " + errorOutput.toString());
            }

            return exitCode == 0;
        } catch (Exception e) {
            System.err.println("Error executing command: " + command + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Execute command and redirect output to file
     */
    private boolean executeCommandWithOutput(String command, String outputFile) {
        try {
            System.out.println("Executing with output: " + command + " > " + outputFile);

            ProcessBuilder pb = new ProcessBuilder();
            pb.command("sh", "-c", command); // Use 'sh' for better compatibility
            pb.directory(new File(tempDir)); // Set working directory
            pb.redirectOutput(new File(outputFile));

            Process process = pb.start();

            // Read any error output
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.err.println("Error: " + errorLine);
            }

            int exitCode = process.waitFor();
            System.out.println("Exit code: " + exitCode);

            // Check if file was created and has content
            File outputFileObj = new File(outputFile);
            if (outputFileObj.exists()) {
                System.out.println("File created: " + outputFile + " (size: " + outputFileObj.length() + " bytes)");
            } else {
                System.err.println("Output file was not created: " + outputFile);
            }

            return exitCode == 0;
        } catch (Exception e) {
            System.err.println("Error executing command with output: " + command + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clean up temporary files
     */
    public void cleanup() {
        try {
            String[] tempFiles = {
                    "note_database.db",
                    "note_database.db-wal",
                    "note_database.db-shm"
            };

            for (String fileName : tempFiles) {
                File file = new File(tempDir + File.separator + fileName);
                if (file.exists()) {
                    file.delete();
                }
            }

            System.out.println("🧹 Temporary files cleaned up");
        } catch (Exception e) {
            System.err.println("Error cleaning up temporary files: " + e.getMessage());
        }
    }
}

// Usage Example Class
class SQLiteNoteManagerExample {
    public static void main(String[] args) {
        // Initialize the manager
        SQLiteNoteManager noteManager = new SQLiteNoteManager("com.example.myapp", "note_database");

        try {
            // Example 1: Get all notes
            System.out.println("=== All Notes ===");
            List<String> notes = noteManager.getAllNotes();
            for (String note : notes) {
                System.out.println(note);
            }

            // Example 2: Delete note by ID
            System.out.println("\n=== Deleting Note by ID ===");
            boolean success = noteManager.deleteNoteById(3);
            if (success) {
                System.out.println("Note deleted successfully!");
            } else {
                System.out.println("Failed to delete note!");
            }

            // Example 3: Delete note by name
            System.out.println("\n=== Deleting Note by Name ===");
            success = noteManager.deleteNoteByName("Test Note");
            if (success) {
                System.out.println("Note deleted successfully!");
            } else {
                System.out.println("Failed to delete note!");
            }

            // Example 4: Delete multiple notes by IDs
            System.out.println("\n=== Deleting Multiple Notes ===");
            List<Integer> idsToDelete = new ArrayList<>();
            idsToDelete.add(1);
            idsToDelete.add(2);

            success = noteManager.deleteNotesByIds(idsToDelete);
            if (success) {
                System.out.println("Multiple notes deleted successfully!");
            } else {
                System.out.println("Failed to delete multiple notes!");
            }

        } finally {
            // Clean up temporary files
            noteManager.cleanup();
        }
    }
}