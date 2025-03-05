package com.vlasttenei.telegram.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import java.util.logging.Logger;

public class CheckingShopTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(CheckingShopTest.class.getName());
    WebElement shopButton;

    @Test(priority = 3)
    public void startGame2() {
        LOGGER.info("Запуск игры");
        checkThatGameIsOpen();
        shopButton = driver.findElement(By.xpath("//span[contains(text(), 'Лавка ❘←')]"));
        shopButton.click();
    }
}
