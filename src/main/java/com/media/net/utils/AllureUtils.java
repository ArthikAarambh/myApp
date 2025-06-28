package com.media.net.utils;

import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;

public class AllureUtils {

    public static void log(String title, String message) {
        Allure.addAttachment(title, new ByteArrayInputStream(message.getBytes()));
    }

//    public static void attachScreenshot(AndroidDriver driver) {
//        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//        Allure.addAttachment("Screenshot", "image/png", new ByteArrayInputStream(screenshot), ".png");
//    }
}
