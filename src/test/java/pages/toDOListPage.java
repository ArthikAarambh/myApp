package pages;

import com.media.net.utils.androidCommonMethod;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class toDOListPage {
    AndroidDriver driver;

    public toDOListPage(AndroidDriver driver) {
        this.driver = driver;
    }

    private By listTitle = By.id("com.example.myapp:id/todoNoteNameEditText");
    private By newItem = By.id("com.example.myapp:id/newTodoItemEditText");
    private By addButton = By.id("com.example.myapp:id/addTodoItemButton");
    private By saveList = By.id("com.example.myapp:id/saveTodoNoteButton");
    ;

    public void enterTitle(String title) {
        WebElement textFieldEle =driver.findElement(listTitle);
        androidCommonMethod.typeIntoTextField(textFieldEle,title);
    }

    public void addNewItem(String item) {
        WebElement textFieldEle =driver.findElement(newItem);
        androidCommonMethod.typeIntoTextField(textFieldEle,item);;
    }

    public void saveItem() {
        driver.findElement(addButton).click();
    }

    public void saveList() {
        driver.findElement(saveList).click();
    }

    public void editTitle(String title){
        WebElement textFieldEle =driver.findElement(listTitle);
        androidCommonMethod.typeIntoTextField(textFieldEle,title);
    }


//    public void editBody(String newText) {
//        WebElement body = driver.findElement(noteBody);
//        body.clear();
//        body.sendKeys(newText);
//    }
}
