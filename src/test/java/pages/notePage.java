package pages;
import org.openqa.selenium.WebElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class notePage {
    AndroidDriver driver;

    public notePage(AndroidDriver driver) {
        this.driver = driver;
    }

    private By noteTitle = By.id("note_title");
    private By noteBody = By.id("note_body");
    private By saveButton = By.id("save_button");
    private By deleteButton = By.id("delete_button");
    private By confirmDelete = By.id("confirm_delete");

    public void enterTitle(String title) {
        driver.findElement(noteTitle).sendKeys(title);
    }

    public void enterBody(String body) {
        driver.findElement(noteBody).sendKeys(body);
    }

    public void saveNote() {
        driver.findElement(saveButton).click();
    }

    public void editBody(String newText) {
        WebElement body = driver.findElement(noteBody);
        body.clear();
        body.sendKeys(newText);
    }

    public void deleteNote() {
        driver.findElement(deleteButton).click();
        driver.findElement(confirmDelete).click();
    }

    public boolean isNoteBodyUpdated(String expected) {
        String actual = driver.findElement(noteBody).getText();
        return actual.equals(expected);
    }

    public boolean isValidationShown(String text) {
        return driver.getPageSource().contains(text);
    }
}

