package com.vlasttenei.telegram.tests;

import java.time.Duration;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class CheckingCatalogTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(CheckingCatalogTest.class.getName());

    public CheckingCatalogTest() {
    }

    @Test(
            priority = 2,
            description = "Запуск игры"
    )
    public void startGame1() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20L)); // Увеличиваем время ожидания
        
        try {
            // Сначала проверяем, что мы на странице чатов
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'chat-list')]")));
            LOGGER.info("Страница чатов загружена.");
            
            // Ищем бота по названию
            WebElement chatItem = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'chat-item') and contains(., 'Зов Теней - Тест')]")));
            chatItem.click();
            LOGGER.info("Открыт чат с ботом.");
            
            // Теперь ищем кнопку игры
            WebElement gameButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//img[@alt='Зов Теней - Тест'] | //div[contains(text(), 'Зов Теней - Тест')]")));
            gameButton.click();
            LOGGER.info("Игра запущена.");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при запуске игры: " + e.getMessage(), e);
            throw e;
        }
    }
}
