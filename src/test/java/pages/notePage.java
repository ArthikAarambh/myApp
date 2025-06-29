package pages;
import com.media.net.ui.mobile.android.androidCommonMethod;
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
    private By Title = By.xpath("//android.widget.TextView[@resource-id=\"com.example.myapp:id/noteNameTextView\" and @text=\"TestNote_2\"]");
    private By Content = By.xpath("//android.widget.TextView[@resource-id=\"com.example.myapp:id/noteContentPreviewTextView\" and @text=\"This is a test note content.\"]");

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
        System.out.println("Note created successfully");
    }

    public void editBody(String newText) {
        WebElement textFieldEle =driver.findElement(noteBody);
        androidCommonMethod.typeIntoTextField(textFieldEle,newText);
    }
    public void editTitle(String title){
        WebElement textFieldEle =driver.findElement(noteTitle);
        androidCommonMethod.typeIntoTextField(textFieldEle,title);
    }

    public boolean titleShowing(){
        WebElement title = driver.findElement(Title);
        return title.isDisplayed();
    }

    public boolean contentDisplayed(){
        WebElement content = driver.findElement(Content);
        System.out.println("Content check done");
        return content.isDisplayed();
    }

    public void editNote(String title,String noteBody){
        WebElement NoteTitle = driver.findElement(Title);
        NoteTitle.click();
        enterTitle(title);
        enterBody(noteBody);
        saveNote();
        System.out.println("Note edited successfully");
    }

    public void emptycheck(){
        WebElement NoteTitle = driver.findElement(noteTitle);
        NoteTitle.click();
        enterTitle("");
        enterBody("");
        saveNote();
    }

//    public boolean isNoteBodyUpdated(String expected) {
//        String actual = driver.findElement(noteBody).getText();
//        return actual.equals(expected);
//    }
//
//    public boolean isValidationShown(String text) {
//        return driver.getPageSource().contains(text);
//    }
}

