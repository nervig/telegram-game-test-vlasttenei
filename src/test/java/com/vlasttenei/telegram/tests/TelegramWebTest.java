package com.vlasttenei.telegram.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.*;
import java.time.Duration;
import java.util.Date;

public class TelegramWebTest extends BaseTest {

    private static final String COOKIE_FILE = "telegram_cookies.data";

    @Test
    public void loginToTelegramWeb() {
        driver.get("https://web.telegram.org/");

        // Загружаем cookies, если есть
        loadCookies();
        driver.navigate().refresh();

        if (isLoggedIn()) {
            System.out.println("✅ Уже авторизованы.");
            return;
        }

        // Ждём кнопку входа
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement logInByPhoneButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Log in by phone Number'] | //button[span[text()='Log in by phone Number']]")
        ));
        logInByPhoneButton.click();

        // Вводим номер телефона
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in-phone-number")));
        phoneInput.sendKeys("+9604771761");

        // Нажимаем "Далее"
        WebElement nextButton = driver.findElement(By.xpath("//button[contains(@class,'Button smaller primary has-ripple')]"));
        nextButton.click();

        // Ждём ввод кода
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isLoggedIn()) {
            System.out.println("✅ Авторизация успешна, сохраняем cookies.");
            saveCookies();
        } else {
            System.err.println("❌ Авторизация не удалась!");
        }
    }

    private boolean isLoggedIn() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'chat-list custom-scroll')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void saveCookies() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COOKIE_FILE))) {
            for (Cookie cookie : driver.manage().getCookies()) {
                writer.write(cookie.getName() + ";" + cookie.getValue() + ";" +
                        cookie.getDomain() + ";" + cookie.getPath() + ";" +
                        (cookie.getExpiry() != null ? cookie.getExpiry().getTime() : "null") + ";" +
                        cookie.isSecure());
                writer.newLine();
            }
            System.out.println("✅ Cookies сохранены.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCookies() {
        File file = new File(COOKIE_FILE);
        if (!file.exists()) {
            System.out.println("⚠ Файл с cookies не найден.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 6) continue;

                Cookie cookie = new Cookie(parts[0], parts[1], parts[2], parts[3],
                        "null".equals(parts[4]) ? null : new Date(Long.parseLong(parts[4])),
                        Boolean.parseBoolean(parts[5]));
                driver.manage().addCookie(cookie);
            }
            System.out.println("✅ Cookies загружены.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
