package com.vlasttenei.telegram.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeSuite;
import com.vlasttenei.telegram.driver.WebDriverSingleton;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import java.util.logging.Logger;
import java.util.logging.Level;

public class BaseTest {
    protected static WebDriver driver;
    private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

    @BeforeSuite
    public void setUpSuite() {
        LOGGER.info("Настройка ChromeDriver...");
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setUp() {
        LOGGER.info("Инициализация WebDriver...");
        driver = WebDriverSingleton.getDriver();
    }
}
