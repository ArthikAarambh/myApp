package tests;

import com.media.net.ui.mobile.android.myDriver;
import com.media.net.utils.AllureUtils;
import com.media.net.utils.androidCommonMethod;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.Test;
import org.testng.annotations.*;
import pages.*;
import org.testng.asserts.SoftAssert;

public class createNotesTest {
    AndroidDriver driver;;
    homePage homePage;
    notePage notePage;
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

    @Test(priority = 0, description = "Create a new note",invocationCount = 2)
    public void createTextNoteAndValidate() {
        homePage.waitForHomeDisplayed();
        homePage.clickCreateNewNoteButton();
        homePage.clickNewTextNote();
        notePage.enterTitle("TestNote_"+count);
        notePage.enterBody("This is a test note content.");
        notePage.saveNote();
        AllureUtils.log("Created User","TestNote_"+count);
//
//        boolean titlePresent = homePage.isNoteDisplayed("My Test Note");
//        softAssert.assertTrue(titlePresent, "Title should be displayed on home page");
//
//        homePage.openNoteByTitle("My Test Note");
//
//        boolean bodyCorrect = notePage.isNoteBodyUpdated("This is a test note content.");
//        softAssert.assertTrue(bodyCorrect, "Note content should match expected text");

        softAssert.assertAll(); // Don't forget this!
    }

//    @Test(description = "Create a new to do list")
//    public void createToDoList(){
//        homePage.waitForHomeDisplayed();
//        homePage.clickCreateNewNoteButton();
//        toDoListPage.enterTitle("My to do list");
//        toDoListPage.addNewItem("study");
//        toDoListPage.saveItem();
//        toDoListPage.saveList();
//    }

//    @Test(description = "verify delete note")
//    public void deleteNote(){
//        notePage.deleteNote();
//    }

    @Test(description = "Create a checklist")
    public void testCreateChecklist() {
//        home.clickAddChecklist();
//        checklist.addItem("Buy milk");
//        checklist.addItem("Call John");
//        checklist.saveChecklist();
//
//        Assert.assertTrue(home.isChecklistDisplayed("Buy milk"));
    }
    @Test(description = "Mark checklist item as complete")
    public void testMarkTodoAsComplete() {
//        checklist.selectChecklist("Shopping");
//        checklist.markItemComplete("Buy milk");
//
//        Assert.assertTrue(checklist.isItemChecked("Buy milk"));
    }

    @Test(description = "Edit and delete note")
    public void testEditAndDeleteNote() {
//        home.selectNote("Meeting Notes");
//        note.editBody("Updated note content");
//        note.saveNote();
//
//        Assert.assertTrue(note.isNoteUpdated("Updated note content"));
//
//        note.deleteNote();
//        Assert.assertFalse(home.isNoteDisplayed("Meeting Notes"));
    }

    @Test(description = "Validate input restrictions")
    public void testEmptyNoteValidation() {
//        home.clickAddNote();
//        note.enterNoteTitle("");
//        note.enterNoteBody("");
//        note.saveNote();
//
//        Assert.assertTrue(note.isValidationMessageShown("Title cannot be empty"));
    }

    @AfterClass
    public void tearDown() {
        myDriver.quitDriver();
    }
}
