package pages;
import com.media.net.utils.NoteDBUtils;
import com.media.net.utils.androidCommonMethod;
import org.openqa.selenium.WebElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class notePage {
    AndroidDriver driver;

    public notePage(AndroidDriver driver) {
        this.driver = driver;
    }

    private By noteTitle = By.id("com.example.myapp:id/noteNameEditText");
    private By noteBody = By.id("com.example.myapp:id/noteContentEditText");
    private By saveButton = By.id("com.example.myapp:id/saveNoteButton");
   ;

    public void enterTitle(String title) {
        WebElement textFieldEle =driver.findElement(noteTitle);
        androidCommonMethod.typeIntoTextField(textFieldEle,title);
    }

    public void enterBody(String body) {
        WebElement textFieldEle =driver.findElement(noteBody);
        androidCommonMethod.typeIntoTextField(textFieldEle,body);
    }

    public void saveNote() {
        driver.findElement(saveButton).click();
    }

    public void editBody(String newText) {
        WebElement textFieldEle =driver.findElement(noteBody);
        androidCommonMethod.typeIntoTextField(textFieldEle,newText);
    }
    public void editTitle(String title){
        WebElement textFieldEle =driver.findElement(noteTitle);
        androidCommonMethod.typeIntoTextField(textFieldEle,title);
    }

    public void deleteNote(){
        NoteDBUtils.pullDatabaseFromDevice("bf80077c"); // Replace with your device ID
        NoteDBUtils.deleteNoteByTitle("Test Note");
// OR
        NoteDBUtils.deleteNoteById(42);

    }

//    public void deleteNote() {
//        driver.findElement(deleteButton).click();
//        driver.findElement(confirmDelete).click();
//    }
//
//    public boolean isNoteBodyUpdated(String expected) {
//        String actual = driver.findElement(noteBody).getText();
//        return actual.equals(expected);
//    }
//
//    public boolean isValidationShown(String text) {
//        return driver.getPageSource().contains(text);
//    }
}

