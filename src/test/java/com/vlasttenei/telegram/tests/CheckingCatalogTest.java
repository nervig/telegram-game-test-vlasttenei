package com.vlasttenei.telegram.tests;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class CheckingCatalogTest extends BaseTest {
    public CheckingCatalogTest() {
    }

    @Test(
            priority = 2
    )
    public void startGame1() {
        driver.get("https://web.telegram.org/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10L));
        WebElement logInByPhoneButton = (WebElement)wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@alt='Зов Теней - Тест']")));
        logInByPhoneButton.click();
    }
}
