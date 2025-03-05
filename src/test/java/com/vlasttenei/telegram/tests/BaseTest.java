package com.vlasttenei.telegram.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeSuite;
import com.vlasttenei.telegram.driver.WebDriverSingleton;
import org.testng.annotations.BeforeMethod;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

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

    public void checkThatGameIsOpen() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20L));
        try {         
            // Ищем бота по названию
            WebElement chatItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[text()='Зов Теней - Тест'] | //img[@alt='Зов Теней - Тест']/ancestor::a")));
            chatItem.click();
            LOGGER.info("Открыт чат с ботом.");
            
            // Теперь ищем кнопку игры
            WebElement gameButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Профиль']")));
            // gameButton.click();
            LOGGER.info("Игра запущена.");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при запуске игры: " + e.getMessage(), e);
            throw e;
        }
    }
}
