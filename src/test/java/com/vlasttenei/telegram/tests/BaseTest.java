package com.vlasttenei.telegram.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeMethod;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.vlasttenei.telegram.driver.WebDriverSingleton;
import io.qameta.allure.Step;
import io.qameta.allure.Attachment;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import java.time.Duration;

public class BaseTest {
    protected static WebDriver driver;
    private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

    @BeforeSuite
    public void setUpSuite() {
        LOGGER.info("Настройка ChromeDriver...");
        Configuration.browser = "chrome";
        Configuration.timeout = 20000;
        Configuration.browserSize = "1920x1080";
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    @Step("Инициализация драйвера и подготовка к тесту")
    public void setUp() {
        LOGGER.info("Инициализация WebDriver...");
        driver = WebDriverSingleton.getDriver();
        WebDriverRunner.setWebDriver(driver);
        
        // Проверяем и восстанавливаем связь Selenide с DOM
        try {
            refresh();
            sleep(2000);
        } catch (Exception e) {
            LOGGER.warning("Ошибка при восстановлении связи с DOM: " + e.getMessage());
            makeScreenshot();
        }
    }

    public void checkThatGameIsOpen() {
        try {
            sleep(2000);
            
            // Проверяем загрузку страницы чатов
            $x("//div[contains(@class, 'chat-list')] | //div[contains(@class, 'chatlist-top has-contacts')]//ul")
                .shouldBe(visible, Duration.ofSeconds(10));
            
            // Ищем бота по названию
            $x("//span[text()='Зов Теней - Тест']/ancestor::a | //img[@alt='Зов Теней - Тест']/ancestor::a")
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(interactable, Duration.ofSeconds(10))
                .click();
            LOGGER.info("Открыт чат с ботом.");
            
            sleep(1000);
            
            // Теперь ищем кнопку игры
            $x("//span[text()='Профиль'] | //div[text()='Профиль']")
                .shouldBe(visible, Duration.ofSeconds(10));
            LOGGER.info("Игра запущена.");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при запуске игры: " + e.getMessage(), e);
            makeScreenshot();
            throw e;
        }
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] makeScreenshot() {
        return ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }
}
