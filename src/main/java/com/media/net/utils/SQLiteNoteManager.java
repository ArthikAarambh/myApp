package com.media.net.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


//  Utility class for managing notes in SQLite database on Android device (non-rooted)
//  Supports deleting notes by ID or name through ADB commands

public class SQLiteNoteManager {

    private final String packageName;
    private final String databaseName;
    private final String tempDir;

    public SQLiteNoteManager(String packageName, String databaseName) {
        this.packageName = packageName;
        this.databaseName = databaseName;
        this.tempDir = System.getProperty("user.dir");
        System.out.println("Working directory: " + this.tempDir);
        System.out.println("Package: " + this.packageName);
        System.out.println("Database: " + this.databaseName);
    }

    public boolean deleteNoteById(int noteId) {
        String deleteQuery = String.format("DELETE FROM notes WHERE noteId = %d;", noteId);
        return executeDeleteOperation(deleteQuery, "noteId = " + noteId);
    }

    public boolean deleteNoteByName(String noteName) {
        String deleteQuery = String.format("DELETE FROM notes WHERE name = '%s';", noteName.replace("'", "''"));
        return executeDeleteOperation(deleteQuery.toString(), "name = '" + noteName + "'");
    }

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
                    "SELECT noteId, name,content FROM notes;"
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

    public List<String> getAllNotesAfterDeletion() {
        List<String> notes = new ArrayList<>();

        try {
            System.out.println("📋 Getting all notes after deletion...");

            // Clean up old local files first
            cleanup();

            // Force WAL checkpoint to ensure all changes are written to main database
            String checkpointCommand = String.format("adb shell run-as %s sqlite3 databases/%s \"PRAGMA wal_checkpoint(FULL);\"", packageName, databaseName);
            executeCommand(checkpointCommand);

            // Small delay to ensure database is fully updated
            Thread.sleep(500);

            // Extract fresh database files from device
            if (!extractDatabaseFilesAfterDeletion()) {
                System.err.println("Failed to extract database files for post-deletion query");
                return notes;
            }

            // Query all remaining notes from the fresh database
            String[] queryCommand = {
                    "sqlite3",
                    tempDir + File.separator + "note_database.db",
                    "SELECT noteId, name, content FROM notes;"
            };

            ProcessBuilder pb = new ProcessBuilder(queryCommand);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                notes.add(line);
            }

            int exitCode = process.waitFor();
            reader.close();

            if (exitCode == 0) {
                System.out.println("Successfully retrieved " + notes.size() + " notes after deletion");
            } else {
                System.err.println("Query failed with exit code: " + exitCode);
            }

        } catch (Exception e) {
            System.err.println("Error getting notes after deletion: " + e.getMessage());
            e.printStackTrace();
        }

        return notes;
    }

    private boolean extractDatabaseFilesAfterDeletion() {
        try {
            System.out.println("📱 Extracting fresh database files after deletion...");

            // Clean up any existing temp files in device
            String cleanupTempDir = String.format("adb shell run-as %s rm -rf cache/temp", packageName);
            executeCommand(cleanupTempDir);

            // Create fresh temp directory
            String createTempDir = String.format("adb shell run-as %s mkdir -p cache/temp", packageName);
            executeCommand(createTempDir);

            // Copy current database files to temp (force fresh copy)
            String[] commands = {
                    String.format("adb shell run-as %s cp databases/%s cache/temp/", packageName, databaseName),
                    String.format("adb shell run-as %s cp databases/%s-wal cache/temp/ 2>/dev/null || true", packageName, databaseName),
                    String.format("adb shell run-as %s cp databases/%s-shm cache/temp/ 2>/dev/null || true", packageName, databaseName)
            };

            for (String command : commands) {
                executeCommand(command);
            }

            // Pull fresh files to local machine with different names to avoid conflicts
            String timestamp = String.valueOf(System.currentTimeMillis());
            String[] pullCommands = {
                    String.format("adb shell run-as %s cat cache/temp/%s", packageName, databaseName),
                    String.format("adb shell run-as %s cat cache/temp/%s-wal 2>/dev/null || echo ''", packageName, databaseName),
                    String.format("adb shell run-as %s cat cache/temp/%s-shm 2>/dev/null || echo ''", packageName, databaseName)
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
            System.err.println("Error extracting database files after deletion: " + e.getMessage());
            return false;
        }
    }

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

            System.out.println("Successfully deleted note(s): " + logInfo);
            return true;

        } catch (Exception e) {
            System.err.println("Error during deletion process: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

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


    private boolean cleanupAndRefreshApp() {
        try {
            System.out.println("Cleaning up and refreshing app");

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

            return true;

        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
            return false;
        }
    }

    private boolean executeCommand(String command) {
        try {
            System.out.println("Executing: " + command);

            ProcessBuilder pb = new ProcessBuilder();
            pb.command("sh", "-c", command);
            pb.directory(new File(tempDir));

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

    private boolean executeCommandWithOutput(String command, String outputFile) {
        try {
            System.out.println("Executing with output: " + command + " > " + outputFile);

            ProcessBuilder pb = new ProcessBuilder();
            pb.command("sh", "-c", command);
            pb.directory(new File(tempDir));
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