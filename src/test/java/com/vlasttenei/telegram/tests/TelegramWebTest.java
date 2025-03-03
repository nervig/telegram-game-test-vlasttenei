package com.vlasttenei.telegram.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class TelegramWebTest extends BaseTest {
    private static final String COOKIE_FILE = "telegram_cookies.data";
    private static final Logger LOGGER = Logger.getLogger(TelegramWebTest.class.getName());

    public TelegramWebTest() {
    }

    @Test(
            priority = 1,
            description = "Авторизация в Telegram Web"
    )
    public void loginToTelegramWeb() throws InterruptedException {
        driver.get("https://web.telegram.org/");
        LOGGER.info("Открыли Telegram Web.");
        this.loadCookies();
        driver.navigate().refresh();
        LOGGER.info("Загрузили cookies и обновили страницу.");
        if (this.isLoggedIn()) {
            LOGGER.info("✅ Уже авторизованы.");
        } else {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10L));

            WebElement phoneInput;
            try {
                phoneInput = (WebElement)wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Log in by phone Number' or span[text()='Log in by phone Number']]")));
                phoneInput.click();
                LOGGER.info("Нажата кнопка 'Log in by phone Number'.");
            } catch (Exception var6) {
                LOGGER.log(Level.SEVERE, "Ошибка при нажатии на кнопку логина.", var6);
                return;
            }

            phoneInput = (WebElement)wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='sign-in-phone-number'] | //span[@class='i18n' and text()='Phone Number']/ancestor::div[contains(@class, 'input-field-phone')]//div[contains(@class, 'input-field-input')]")));
            phoneInput.sendKeys(new CharSequence[]{"+9604771761"});
            LOGGER.info("Введён номер телефона.");
            WebElement nextButton = driver.findElement(By.xpath("//button[contains(@class,'Button smaller primary')]"));
            nextButton.click();
            LOGGER.info("Нажата кнопка 'Далее'.");

            try {
                Thread.sleep(15000L);
            } catch (InterruptedException var5) {
                LOGGER.log(Level.WARNING, "Поток ожидания был прерван.", var5);
            }

            if (this.isLoggedIn()) {
                LOGGER.info("✅ Авторизация успешна. Сохраняем cookies.");
                this.saveCookies();
                
                try {
                    CheckingCatalogTest catalogTest = new CheckingCatalogTest();
                    catalogTest.startGame1();
                    LOGGER.info("✅ Игра успешно запущена.");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Ошибка при запуске игры.", e);
                }
            } else {
                LOGGER.severe("❌ Авторизация не удалась!");
            }
        }
    }

    private boolean isLoggedIn() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10L));

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'chat-list custom-scroll')]")));
            return true;
        } catch (Exception var3) {
            LOGGER.log(Level.WARNING, "Элемент чата не найден, пользователь не авторизован.", var3);
            return false;
        }
    }

    private void saveCookies() {
        File file = new File(COOKIE_FILE);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            try {
                Iterator var3 = driver.manage().getCookies().iterator();

                while(true) {
                    if (!var3.hasNext()) {
                        LOGGER.info("✅ Cookies сохранены.");
                        break;
                    }

                    Cookie cookie = (Cookie)var3.next();
                    String var10001 = cookie.getName();
                    writer.write(var10001 + ";" + cookie.getValue() + ";" + cookie.getDomain() + ";" + cookie.getPath() + ";" + String.valueOf(cookie.getExpiry()) + ";" + cookie.isSecure());
                    writer.newLine();
                }
            } catch (Throwable var6) {
                try {
                    writer.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }

                throw var6;
            }

            writer.close();
        } catch (IOException var7) {
            LOGGER.log(Level.SEVERE, "Ошибка при сохранении cookies.", var7);
        }

    }

    private void loadCookies() {
        File file = new File(COOKIE_FILE);
        if (!file.exists()) {
            LOGGER.info("Файл cookies не найден. Вход будет выполнен вручную.");
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                try {
                    String line;
                    while((line = reader.readLine()) != null) {
                        String[] parts = line.split(";");
                        if (parts.length >= 6) {
                            Cookie cookie = new Cookie(parts[0], parts[1], parts[2], parts[3], "null".equals(parts[4]) ? null : new Date(parts[4]), Boolean.parseBoolean(parts[5]));
                            driver.manage().addCookie(cookie);
                        }
                    }

                    LOGGER.info("✅ Cookies загружены.");
                } catch (Throwable var7) {
                    try {
                        reader.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }

                    throw var7;
                }

                reader.close();
            } catch (IOException var8) {
                LOGGER.log(Level.SEVERE, "Ошибка при загрузке cookies.", var8);
            }

        }
    }
}
