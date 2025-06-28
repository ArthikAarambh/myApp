package com.media.net.ui.mobile.android;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import com.media.net.utils.JsonReader;

public class myDriver {
    private static final String HOST = "127.0.0.1";
    private static final String PORT = "4723";//getPort();
    protected static AndroidDriver androidDriver;

    public static JSONObject getPlatformCapabilities() {
        //we can make app and device passing dynamic by using system.getProperty() from the command line and the systemproperties file
        JSONObject appCaps = JsonReader.getJsonData("config/app/myApp.json");
        JSONObject deviceCaps = JsonReader.getJsonData("config/devices/realmeGT.json");

        for (String key : appCaps.keySet()) {
            deviceCaps.put(key, appCaps.opt(key));
        }

        return deviceCaps;
    }
    private static AndroidDriver initAndroidDriver() throws MalformedURLException {
        if(androidDriver!=null){
            return androidDriver;
        }

        JSONObject requiredDeviceCaps = getPlatformCapabilities();
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(requiredDeviceCaps.getString("deviceName"))
                .setUdid(requiredDeviceCaps.getString("udid"))
                .setPlatformName(requiredDeviceCaps.getString("platformName"))
                .setPlatformVersion(requiredDeviceCaps.getString("platformVersion"))
                .setAppPackage(requiredDeviceCaps.getString("appPackage"))
                .setAppActivity(requiredDeviceCaps.getString("appActivity"))
                .setAutomationName(requiredDeviceCaps.getString("automationName"))
                .setIgnoreHiddenApiPolicyError(requiredDeviceCaps.getBoolean("ignoreHiddenApiPolicyError"))
                .setNoReset(requiredDeviceCaps.getBoolean("noReset"))
                .setFullReset(requiredDeviceCaps.getBoolean("fullReset"))
                .setApp(requiredDeviceCaps.getString("app"))
                .autoGrantPermissions()
                .setNewCommandTimeout(Duration.ofMinutes(10));

        String serverAddress = "http://" + HOST + ":" + PORT + "/";
        androidDriver = new AndroidDriver(new URL(serverAddress), options);
        androidDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return androidDriver;
    }

    public static AndroidDriver myAndroidDriver(){
        try {
            return initAndroidDriver();
        }catch (MalformedURLException e){
            throw new RuntimeException("Driver initialization failed", e);
        }
    }

    public static void quitDriver(){
        androidDriver.quit();
        androidDriver=null;
    }
}
