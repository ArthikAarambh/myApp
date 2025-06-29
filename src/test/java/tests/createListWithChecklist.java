package tests;

import com.media.net.ui.mobile.android.myDriver;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.homePage;
import pages.notePage;
import pages.toDOListPage;

public class createListWithChecklist {
    AndroidDriver driver;;
    pages.homePage homePage;
    pages.notePage notePage;
    toDOListPage toDoListPage;
    SoftAssert softAssert;
    private static int count;

    @BeforeClass
    public void setup() throws Exception {
        driver = myDriver.myAndroidDriver();;
        homePage = new homePage(driver);
        notePage = new notePage(driver);
        toDoListPage = new toDOListPage(driver);
        count=0;
    }

    @BeforeMethod
    public void initSoftAssert() {
        softAssert = new SoftAssert();
        count++;
    }

    @Test(description = "Create a new to do list",priority = 1,invocationCount = 2)
    public void createToDoList(){
        homePage.waitForHomeDisplayed();
        homePage.clickCreateNewNoteButton();
        homePage.clickNewTodoList();
        toDoListPage.enterTitle("TestList_"+count);
        for(int i=1;i<=2;i++){
            toDoListPage.addNewItem("study_"+i);
            toDoListPage.saveItem();
        }
        toDoListPage.saveList();
    }

    @Test(description = "edit to do List title and add a new checklist",priority = 3)
    public void edittoDoListTitle(){
        toDoListPage.editTitleAndaddNewItem("editTitle","editNewItem");
    }

    @Test(description = "edit checkList",priority = 2)
    public void editChekListItem(){
        boolean iseditable = toDoListPage.editExistcheckList();
        softAssert.assertTrue(iseditable, "You can not edit checklist item, you can add New one");
        softAssert.assertAll();
    }

    @Test(description = "Mark to do list items",priority = 4)
    public void markListItems(){
        toDoListPage.markedListItem();
    }

    @Test(description = "Validate items checked state",priority = 5)
    public void validateState(){
      boolean stated =  toDoListPage.checkedState();
        softAssert.assertTrue(stated, "item list state is not marked");
        softAssert.assertAll();
    }

    @Test(description = "Empty field check On toDoList",priority = 6)
    public void emptyField(){
        homePage.waitForHomeDisplayed();
        homePage.clickCreateNewNoteButton();
        homePage.clickNewTodoList();
        toDoListPage.emptyCheck();
        softAssert.assertFalse(homePage.isHomePageDisplayed(), "Title can note be empty content can be");
        softAssert.assertAll();
    }

//    @Test(description = "all notes & List at last",priority = 1)
//    public void showAll(){
////        deleteNoteAndList.gettingAllNode();
//        List<String> notes = deleteNoteAndList.gettingAllNode();
//        for (String note : notes) {
//            Allure.addAttachment("Note", note);
//        }
//    }
    @AfterClass
    public void tearDown() {
        myDriver.quitDriver();
    }
}
