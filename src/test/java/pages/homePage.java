package pages;

import com.media.net.ui.mobile.android.androidCommonMethod;
import org.openqa.selenium.WebElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class homePage {
    AndroidDriver driver;

    public homePage(AndroidDriver driver) {
        this.driver = driver;
    }

    private By createNewNoteButton = By.id("com.example.myapp:id/addNoteFab");
    private By homePageElement = By.id("android:id/content");
    private By newTextNote = By.xpath("//android.widget.TextView[@resource-id=\"android:id/text1\" and @text=\"New Text Note\"]");
    private By newTodoList = By.xpath("//android.widget.TextView[@resource-id=\"android:id/text1\" and @text=\"New Todo List\"]");

    public void clickCreateNewNoteButton() {
        driver.findElement(createNewNoteButton).click();
    }

    public void waitForHomeDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(homePageElement));
        System.out.println("Home page loaded");
        try {
            Thread.sleep(5000); // 5000 milliseconds = 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clickNewTextNote(){
        driver.findElement(newTextNote).click();
    }
    public void clickNewTodoList(){
            driver.findElement(newTodoList).click();
    }

    public boolean isHomePageDisplayed(){
        WebElement element = driver.findElement(homePageElement);
        boolean isDisplay = element.isDisplayed();
        androidCommonMethod.pressBack();
        return isDisplay;
    }

}
