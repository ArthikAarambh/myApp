package tests;

import com.media.net.ui.mobile.android.myDriver;
import com.media.net.utils.androidCommonMethod;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.deleteNoteAndList;
import pages.homePage;
import pages.notePage;
import pages.toDOListPage;

import java.util.List;

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

    @Test(description = "Create a new to do list",priority = 0,invocationCount = 2)
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
