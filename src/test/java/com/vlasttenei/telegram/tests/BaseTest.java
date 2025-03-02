package com.vlasttenei.telegram.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
    protected static WebDriver driver; // Делаем driver статическим

    @BeforeSuite
    public void setUp() {
        if (driver == null) { // Запускаем браузер только если он ещё не запущен
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        }
    }

    @AfterSuite
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null; // Обнуляем driver после завершения всех тестов
        }
    }
}
