package pages;

import com.media.net.utils.AllureUtils;
import com.media.net.utils.SQLiteNoteManager;

import java.util.List;

public class deleteNoteAndList {
    SQLiteNoteManager noteManager = new SQLiteNoteManager("com.example.myapp", "note_database");
    public void deleteByTitle(String title){
        try{
            // Example 3: Delete note by name
            System.out.println("\n=== Deleting Note by Name ===");
            boolean success = noteManager.deleteNoteByName(title);
            if (success) {
                String str ="Note having title "+title;
                AllureUtils.log(str," Deleted successfully");
            } else {
                AllureUtils.log("failed","Failed to delete note!");
            }

        }finally {
            // Clean up temporary files
            noteManager.cleanup();
        }

    }

    public void gettingAllNode(){
        try{
            // Example 1: Get all notes
            System.out.println("=== All Notes ===");
            List<String> notes = noteManager.getAllNotes();
            for (String note : notes) {
                AllureUtils.log("Notes is ",note);
            }
        }finally {
            // Clean up temporary files
            noteManager.cleanup();
        }
    }

    public void deleteByNoteId(int id){
        try{
            System.out.println("\n=== Deleting Note by ID ===");
            boolean success = noteManager.deleteNoteById(id);
            if (success) {
                String str ="Note having id "+id;
                AllureUtils.log(str," Deleted successfully");
            } else {
                AllureUtils.log("failed","Failed to delete note!");
            }
        }finally {
            // Clean up temporary files
            noteManager.cleanup();
        }
    }

}
