package tests;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

public class TestSuiteSetup {

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("Initializing before the entire test suite...");
        // e.g., Initialize Appium server, reporting, logs, etc.
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("Cleaning up after the test suite...");
        // Stop server, flush reports, etc.
    }
}
