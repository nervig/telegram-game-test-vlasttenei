package com.vlasttenei.telegram.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WebDriverSingleton {
    private static WebDriver driver;
    private static String originalWindowHandle;
    private static final Logger LOGGER = Logger.getLogger(WebDriverSingleton.class.getName());
    private static final String USER_DATA_DIR = System.getProperty("user.dir") + File.separator + "chrome-profile";
    private static final String DEBUG_PORT = "9222";

    private WebDriverSingleton() {}

    public static WebDriver getDriver() {
        if (driver == null || isSessionClosed()) {
            LOGGER.info("Инициализация драйвера Chrome...");
            
            // Сначала пытаемся подключиться к существующей сессии
            try {
                ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("debuggerAddress", "localhost:" + DEBUG_PORT);
                driver = new ChromeDriver(options);
                originalWindowHandle = driver.getWindowHandle();
                LOGGER.info("Подключились к существующей сессии Chrome");
                return driver;
            } catch (Exception e) {
                LOGGER.info("Не удалось подключиться к существующей сессии, создаем новую");
                killChromeProcesses();
            }

            try {
                // Запускаем Chrome с отладочным портом
                ProcessBuilder processBuilder = new ProcessBuilder(
                    "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
                    "--remote-debugging-port=" + DEBUG_PORT,
                    "--user-data-dir=" + USER_DATA_DIR
                );
                processBuilder.start();
                Thread.sleep(2000); // Даем Chrome время на запуск

                ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("debuggerAddress", "localhost:" + DEBUG_PORT);
                driver = new ChromeDriver(options);
                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                originalWindowHandle = driver.getWindowHandle();
                LOGGER.info("Создана новая сессия Chrome с профилем: " + USER_DATA_DIR);
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Ошибка при создании сессии Chrome: " + e.getMessage(), e);
                try {
                    killChromeProcesses();
                    cleanProfileIfNeeded();
                    
                    // Последняя попытка с базовыми настройками
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--remote-debugging-port=" + DEBUG_PORT);
                    options.addArguments("--user-data-dir=" + USER_DATA_DIR);
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    
                    driver = new ChromeDriver(options);
                    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                    originalWindowHandle = driver.getWindowHandle();
                    LOGGER.info("Сессия Chrome создана после очистки");
                } catch (Exception retryEx) {
                    LOGGER.log(Level.SEVERE, "Не удалось создать сессию Chrome: " + retryEx.getMessage());
                    throw new RuntimeException("Не удалось инициализировать ChromeDriver", retryEx);
                }
            }
        } else {
            try {
                Set<String> handles = driver.getWindowHandles();
                if (handles.contains(originalWindowHandle)) {
                    driver.switchTo().window(originalWindowHandle);
                    LOGGER.info("Переключились на оригинальное окно");
                } else {
                    LOGGER.warning("Оригинальное окно не найдено, пересоздаем сессию");
                    resetDriver();
                    return getDriver();
                }
            } catch (Exception e) {
                LOGGER.warning("Ошибка при работе с окнами: " + e.getMessage());
                resetDriver();
                return getDriver();
            }
        }
        return driver;
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
            driver.getCurrentUrl();
            Set<String> handles = driver.getWindowHandles();
            return handles.isEmpty();
        } catch (Exception e) {
            LOGGER.warning("Сессия закрыта или недоступна: " + e.getMessage());
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
