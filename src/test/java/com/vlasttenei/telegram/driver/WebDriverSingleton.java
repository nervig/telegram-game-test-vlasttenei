package com.vlasttenei.telegram.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class WebDriverSingleton {
    private static WebDriver driver;
    private static String originalWindowHandle;
    private static final Logger LOGGER = Logger.getLogger(WebDriverSingleton.class.getName());
    private static final String USER_DATA_DIR = System.getProperty("user.dir") + File.separator + "chrome-profile";

    private WebDriverSingleton() {}

    public static WebDriver getDriver() {
        if (driver == null || isSessionClosed()) {
            LOGGER.info("Инициализация драйвера Chrome...");
            
            try {
                killChromeProcesses();
                cleanProfileIfNeeded();
                
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--user-data-dir=" + USER_DATA_DIR);
                options.addArguments("--remote-debugging-port=9222");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-gpu");
                options.addArguments("--auto-open-devtools-for-tabs");
                options.addArguments("--window-size=1920,1080");
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                options.setExperimentalOption("detach", true);
                
                driver = new ChromeDriver(options);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                originalWindowHandle = driver.getWindowHandle();
                LOGGER.info("Создана новая сессия Chrome");
                return driver;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Ошибка при создании сессии Chrome: " + e.getMessage(), e);
                throw new RuntimeException("Не удалось инициализировать ChromeDriver", e);
            }
        } else {
            try {
                // Проверяем, что окно все еще доступно
                driver.getWindowHandle();
                
                // Проверяем и восстанавливаем связь с DOM
                try {
                    driver.getCurrentUrl();
                } catch (Exception e) {
                    LOGGER.warning("Связь с DOM потеряна, перезагружаем страницу");
                    driver.navigate().refresh();
                    Thread.sleep(2000); // Даем время на загрузку
                }
                
                LOGGER.info("Использование существующей сессии Chrome");
                return driver;
            } catch (Exception e) {
                LOGGER.warning("Существующая сессия недоступна, создаем новую: " + e.getMessage());
                resetDriver();
                return getDriver();
            }
        }
    }

    private static void killChromeProcesses() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Process process = Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
                process.waitFor(5, TimeUnit.SECONDS);
                process = Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                process.waitFor(5, TimeUnit.SECONDS);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            LOGGER.warning("Ошибка при завершении процессов Chrome: " + e.getMessage());
        }
    }

    private static void cleanProfileIfNeeded() {
        try {
            File profileDir = new File(USER_DATA_DIR);
            if (profileDir.exists()) {
                File[] problemFiles = profileDir.listFiles((dir, name) -> 
                    name.contains("Lock") || 
                    name.contains(".tmp") || 
                    name.equals("Singleton") || 
                    name.equals("DevToolsActivePort"));
                
                if (problemFiles != null) {
                    for (File file : problemFiles) {
                        file.delete();
                    }
                }
                LOGGER.info("Очищены проблемные файлы профиля");
            } else {
                profileDir.mkdirs();
                LOGGER.info("Создана директория профиля");
            }
        } catch (Exception e) {
            LOGGER.warning("Ошибка при очистке профиля: " + e.getMessage());
        }
    }

    private static boolean isSessionClosed() {
        if (driver == null) return true;
        try {
            // Проверяем только наличие окон, без дополнительных действий
            return driver.getWindowHandles().isEmpty();
        } catch (Exception e) {
            LOGGER.warning("Ошибка при проверке сессии: " + e.getMessage());
            return true;
        }
    }

    private static void resetDriver() {
        driver = null;
        originalWindowHandle = null;
    }

    public static void forceQuit() {
        if (driver != null) {
            try {
                driver.quit();
                LOGGER.info("Браузер принудительно закрыт");
            } catch (Exception e) {
                LOGGER.warning("Ошибка при закрытии браузера: " + e.getMessage());
            } finally {
                resetDriver();
            }
        }
    }
}
