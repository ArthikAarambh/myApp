package pages;

import com.media.net.ui.mobile.android.androidCommonMethod;
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
    private By list2 =By.xpath("//android.widget.TextView[@resource-id=\"com.example.myapp:id/noteNameTextView\" and @text=\"TestList_2\"]");
    private By list1 =By.xpath("//android.widget.TextView[@resource-id=\"com.example.myapp:id/noteNameTextView\" and @text=\"TestList_1\"]");
    private By item2 =By.xpath("//android.widget.TextView[@resource-id=\"com.example.myapp:id/todoTextView\" and @text=\"study_2\"]");
    private By itemcheck1 =By.xpath("(//android.widget.CheckBox[@resource-id=\"com.example.myapp:id/todoCheckBox\"])[1]");
    private By itemcheck2 =By.xpath("(//android.widget.CheckBox[@resource-id=\"com.example.myapp:id/todoCheckBox\"])[2]");

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

    public void editTitleAndaddNewItem(String title2,String editItem){
        WebElement Ele =driver.findElement(list2);
        Ele.click();
        enterTitle(title2);
        addNewItem(editItem);
        saveItem();
        saveList();
    }

    public boolean editExistcheckList(){
        WebElement Ele =driver.findElement(list2);
        Ele.click();
        WebElement ele = driver.findElement(item2);
        boolean flag =androidCommonMethod.isElementEditable(ele);
        return flag;
    }

    public void markedListItem(){
        WebElement list = driver.findElement(list1);
        list.click();
        WebElement checkbox1 = driver.findElement(itemcheck1);
        checkbox1.click();
        WebElement checkbox2= driver.findElement(itemcheck2);
        checkbox2.click();
    }

    public boolean checkedState(){
        WebElement checkbox1 = driver.findElement(itemcheck1);
        String isChecked = checkbox1.getAttribute("checked");
        androidCommonMethod.pressBack();
        if (!"true".equalsIgnoreCase(isChecked)) {
            System.out.println("Checkbox was not selected, now selected.");
            return false;
        } else {
            System.out.println("Checkbox is already selected.");
            return true;
        }
    }

    public void emptyCheck(){
        WebElement titleButton = driver.findElement(listTitle);
        addNewItem("");
        saveItem();
        enterTitle("");
        saveList();
        System.out.println("Field empty check done");
    }
}
