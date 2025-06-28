package com.media.net.utils;

import com.media.net.ui.mobile.android.myDriver;
import org.openqa.selenium.WebElement;

public class androidCommonMethod {

    public static void typeIntoTextField(WebElement textFieldElement,String text) {
        textFieldElement.click();
        textFieldElement.clear();
        textFieldElement.sendKeys(text);
        myDriver.myAndroidDriver().hideKeyboard();
    }

    public static void pressBack(){
        myDriver.myAndroidDriver().navigate().back();
    }
    public static void closeApp(){
        try {
            String appPackage = myDriver.getPlatformCapabilities().getString("appPackage").toString();
            Runtime.getRuntime().exec("adb shell am force-stop "+appPackage);
            Thread.sleep(2000);
        }catch (Exception e){
            throw new RuntimeException();
        }
//        myDriver.myAndroidDriver().terminateApp(myDriver.getPlatformCapabilities().getString("appPackage"));
    }
}
