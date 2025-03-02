package com.vlasttenei.telegram.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import java.io.*;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class TelegramWebTest extends BaseTest {

    private static final String COOKIE_FILE = "telegram_cookies.data";
    private static final Logger LOGGER = Logger.getLogger(TelegramWebTest.class.getName());

    @Test(priority = 1)
    public void loginToTelegramWeb() {
        driver.get("https://web.telegram.org/");
        LOGGER.info("Открыли Telegram Web.");

        // Загружаем cookies и обновляем страницу
        loadCookies();
        driver.navigate().refresh();
        LOGGER.info("Загрузили cookies и обновили страницу.");

        if (isLoggedIn()) {
            LOGGER.info("✅ Уже авторизованы.");
            return;
        }

        // Ожидание кнопки логина
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement logInByPhoneButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Log in by phone Number' or span[text()='Log in by phone Number']]")));
            logInByPhoneButton.click();
            LOGGER.info("Нажата кнопка 'Log in by phone Number'.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при нажатии на кнопку логина.", e);
            return;
        }

        // Ввод номера телефона
        try {
            WebElement phoneInput = driver.findElement(By.xpath("//input[@id='sign-in-phone-number']"));
            phoneInput.sendKeys("+9604771761");
            LOGGER.info("Введён номер телефона.");

            WebElement nextButton = driver.findElement(By.xpath("//button[contains(@class,'Button smaller primary')]"));
            nextButton.click();
            LOGGER.info("Нажата кнопка 'Далее'.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при вводе номера телефона.", e);
            return;
        }

        // Ожидание ввода кода вручную
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Поток ожидания был прерван.", e);
        }

        // Проверка авторизации и сохранение cookies
        if (isLoggedIn()) {
            LOGGER.info("✅ Авторизация успешна. Сохраняем cookies.");
            saveCookies();
        } else {
            LOGGER.severe("❌ Авторизация не удалась!");
        }
    }

    private boolean isLoggedIn() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'chat-list custom-scroll')]")));
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Элемент чата не найден, пользователь не авторизован.", e);
            return false;
        }
    }

    private void saveCookies() {
        File file = new File(COOKIE_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Cookie cookie : driver.manage().getCookies()) {
                writer.write(cookie.getName() + ";" + cookie.getValue() + ";" +
                        cookie.getDomain() + ";" + cookie.getPath() + ";" +
                        cookie.getExpiry() + ";" + cookie.isSecure());
                writer.newLine();
            }
            LOGGER.info("✅ Cookies сохранены.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ошибка при сохранении cookies.", e);
        }
    }

    private void loadCookies() {
        File file = new File(COOKIE_FILE);
        if (!file.exists()) {
            LOGGER.info("Файл cookies не найден. Вход будет выполнен вручную.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 6) continue;

                Cookie cookie = new Cookie(parts[0], parts[1], parts[2], parts[3],
                        "null".equals(parts[4]) ? null : new java.util.Date(parts[4]),
                        Boolean.parseBoolean(parts[5]));
                driver.manage().addCookie(cookie);
            }
            LOGGER.info("✅ Cookies загружены.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ошибка при загрузке cookies.", e);
        }
    }
}
