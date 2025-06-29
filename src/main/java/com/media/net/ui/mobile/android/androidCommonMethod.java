package com.media.net.ui.mobile.android;

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
            Thread.sleep(4000);
        }catch (Exception e){
            throw new RuntimeException();
        }
//        myDriver.myAndroidDriver().terminateApp(myDriver.getPlatformCapabilities().getString("appPackage"));
    }

    public static void adbUninstall(){

    }
    public static void adbInstall(){

    }
    public static boolean isElementEditable(WebElement element) {
        String enabled = element.getAttribute("enabled");
        String focusable = element.getAttribute("focusable");
        pressBack();
        return "true".equals(enabled) && "true".equals(focusable);
    }
}
