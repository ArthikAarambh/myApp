package tests;

import com.media.net.ui.mobile.android.myDriver;
import com.media.net.utils.AllureUtils;
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

    @Test(priority = 1, description = "Create a new note",invocationCount = 2)
    public void createTextNoteAndValidate() {
        homePage.waitForHomeDisplayed();
        homePage.clickCreateNewNoteButton();
        homePage.clickNewTextNote();
        notePage.enterTitle("TestNote_"+count);
        notePage.enterBody("This is a test note content.");
        notePage.saveNote();
        AllureUtils.log("Created User","TestNote_"+count);
        softAssert.assertAll();
    }
    @Test(description = "Verify On Ui the Note is Correctly displayed or not",priority = 2)
    public void verificationOnUI(){
            boolean titlePresent = notePage.titleShowing();
            softAssert.assertTrue(titlePresent, "Title should be displayed on home page");
            boolean bodyCorrect = notePage.contentDisplayed();
            softAssert.assertTrue(bodyCorrect, "Note content should match expected text");
            softAssert.assertAll();
    }

    @Test(description = "Edit a existing note",priority = 3)
    public void editNote(){
        notePage.editNote("edit_Test","edit note test content");
    }

    @Test(description = "Empty field check On Note",priority = 4)
    public void emptyField(){
        homePage.waitForHomeDisplayed();
        homePage.clickCreateNewNoteButton();
        homePage.clickNewTextNote();
        notePage.emptycheck();
        softAssert.assertFalse(homePage.isHomePageDisplayed(), "Title can note be empty content can be");
        softAssert.assertAll();
    }

    @AfterClass
    public void tearDown() {
        myDriver.quitDriver();
    }
}
