package pages;

import org.openqa.selenium.WebElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class homePage {
    AndroidDriver driver;

    public homePage(AndroidDriver driver) {
        this.driver = driver;
    }

    private By addNoteButton = By.id("add_note_button");
    private By addChecklistButton = By.id("add_checklist_button");

    public void clickAddNote() {
        driver.findElement(addNoteButton).click();
    }

    public void clickAddChecklist() {
        driver.findElement(addChecklistButton).click();
    }

    public boolean isNoteDisplayed(String noteTitle) {
        return driver.findElements(By.xpath("//android.widget.TextView[@text='" + noteTitle + "']")).size() > 0;
    }

    public void openNoteByTitle(String noteTitle) {
        driver.findElement(By.xpath("//android.widget.TextView[@text='" + noteTitle + "']")).click();
    }
}
