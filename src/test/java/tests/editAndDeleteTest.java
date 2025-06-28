package tests;
import com.media.net.ui.mobile.android.myDriver;
import com.media.net.utils.androidCommonMethod;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.Test;
import org.testng.annotations.*;
import pages.*;
import org.testng.asserts.SoftAssert;
public class editAndDeleteTest {
    AndroidDriver driver;;
    homePage homePage;
    notePage notePage;
    toDOListPage toDoListPage;
    SoftAssert softAssert;
    deleteNoteAndList deleteNoteAndList;

    @BeforeClass
    public void setup() throws Exception {
        driver = myDriver.myAndroidDriver();;
        homePage = new homePage(driver);
        notePage = new notePage(driver);
        toDoListPage = new toDOListPage(driver);
        deleteNoteAndList = new deleteNoteAndList();
    }

    @BeforeMethod
    public void initSoftAssert() {
        softAssert = new SoftAssert();
    }
    @Test(description = "Getting all notes before any deletion",priority = 0)
    public void gettingAllNotes(){
        deleteNoteAndList.gettingAllNode();
    }

    @Test(description = "Delete note by Title",priority = 1)
    public void testEditAndDeleteNote() {
        deleteNoteAndList.deleteByTitle("TestNote_1");
    }
    @Test(description = "Delete note by id",priority = 2)
    public void deleteNoteByid(){
        deleteNoteAndList.deleteByNoteId(3);
    }
    @Test(description = "Getting all notes after any deletion",priority = 3)
    public void gettingAllNotesAfter(){
        deleteNoteAndList.gettingAllNode();
    }

    @AfterClass
    public void tearDown() {
        androidCommonMethod.closeApp();
        myDriver.quitDriver();
    }
}
