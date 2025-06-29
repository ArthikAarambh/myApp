package pages;

import com.media.net.utils.AllureUtils;
import com.media.net.utils.SQLiteNoteManager;

import java.util.List;

public class deleteNoteAndList {
    SQLiteNoteManager noteManager = new SQLiteNoteManager("com.example.myapp", "note_database");
    public void deleteByTitle(String title){
            System.out.println("\n=== Deleting Note by Name ===");
            boolean success = noteManager.deleteNoteByName(title);
            if (success) {
                String str ="Note having title "+title;
                AllureUtils.log(str," Deleted successfully");
            } else {
                AllureUtils.log("failed","Failed to delete note!");
            }
    }

    public List<String>  gettingAllNode(){
        System.out.println("=== All Notes ===");
        List<String>  notes = noteManager.getAllNotes();
            for (String note : notes) {
//                AllureUtils.log("Notes is ",note);
                System.out.println(note);
            }
        return notes;
    }

    public List<String >getAllNodeAfterDelete(){
        List<String>  notes = noteManager.getAllNotesAfterDeletion();
        for (String note : notes) {
            System.out.println(note);
        }
        return notes;
    }

    public void deleteByNoteId(int id){
            System.out.println("\n=== Deleting Note by ID ===");
            boolean success = noteManager.deleteNoteById(id);
            if (success) {
                String str ="Note having id "+id;
                AllureUtils.log(str," Deleted successfully");
            } else {
                AllureUtils.log("failed","Failed to delete note!");
            }
    }

    public void cleanupDatabase() {
        // Clean up once at the very end
        noteManager.cleanup();
    }

}
