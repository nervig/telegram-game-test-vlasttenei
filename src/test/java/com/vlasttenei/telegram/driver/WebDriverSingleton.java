package com.vlasttenei.telegram.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.Set;

public class WebDriverSingleton {
    private static WebDriver driver;
    private static String originalWindowHandle;
    private static final Logger LOGGER = Logger.getLogger(WebDriverSingleton.class.getName());
    private static final String USER_DATA_DIR = System.getProperty("user.dir") + File.separator + "chrome-profile";
    private static final String TELEGRAM_WEB_URL = "https://web.telegram.org/";
    private static final int REMOTE_DEBUGGING_PORT = 9222;
    private static ChromeDriverService driverService;
    private static boolean isInitialized = false;

    private WebDriverSingleton() {}

    public static WebDriver getDriver() {
        if (driver == null || isSessionClosed()) {
            LOGGER.info("Инициализация драйвера Chrome...");
            
            try {
                // Проверяем, запущен ли уже Chrome с нужным портом отладки
                boolean chromeRunning = isChromeBrowserRunning();
                
                if (chromeRunning) {
                    LOGGER.info("Обнаружен запущенный Chrome с портом отладки, подключаемся к нему...");
                    
                    // Пытаемся подключиться к существующему процессу Chrome
                    try {
                        driver = connectToExistingChrome();
                        if (driver != null) {
                            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                            
                            // Сохраняем первое окно как основное
                            Set<String> handles = driver.getWindowHandles();
                            if (!handles.isEmpty()) {
                                originalWindowHandle = handles.iterator().next();
                                driver.switchTo().window(originalWindowHandle);
                                
                                // Закрываем лишние вкладки, если они есть
                                closeExtraWindows();
                                
                                String currentUrl = driver.getCurrentUrl();
                                if (currentUrl.equals("about:blank") || currentUrl.equals("chrome://newtab/") || 
                                    !currentUrl.contains("telegram.org")) {
                                    driver.navigate().to(TELEGRAM_WEB_URL);
                                }
                                
                                LOGGER.info("Успешное подключение к существующей сессии Chrome");
                                return driver;
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.warning("Не удалось подключиться к существующему Chrome: " + e.getMessage());
                        // Продолжаем с созданием нового процесса
                    }
                }
                
                // Если не удалось подключиться или Chrome не запущен, запускаем новый процесс
                LOGGER.info("Запуск нового процесса Chrome...");
                
                // Убиваем существующие процессы Chrome, если они есть
                killChromeProcesses();
                cleanProfileIfNeeded();
                
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--user-data-dir=" + USER_DATA_DIR);
                options.addArguments("--remote-debugging-port=" + REMOTE_DEBUGGING_PORT);
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--disable-session-crashed-bubble");
                options.addArguments("--disable-restore-session-state");
                
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                options.setExperimentalOption("useAutomationExtension", false);
                options.setExperimentalOption("detach", true);
                
                // Создаем и запускаем сервис WebDriver'а
                if (driverService == null || !driverService.isRunning()) {
                    driverService = ChromeDriverService.createDefaultService();
                    driverService.start();
                }
                
                driver = new ChromeDriver(driverService, options);
                isInitialized = true;
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                originalWindowHandle = driver.getWindowHandle();
                
                // Переходим на Telegram Web
                driver.navigate().to(TELEGRAM_WEB_URL);
                LOGGER.info("Создана новая сессия Chrome и открыт Telegram Web");
                
                return driver;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Ошибка при создании сессии Chrome: " + e.getMessage(), e);
                throw new RuntimeException("Не удалось инициализировать ChromeDriver", e);
            }
        } else {
            try {
                // Проверяем, что окно все еще доступно
                driver.getWindowHandle();
                
                // Закрываем все лишние окна, кроме основного
                closeExtraWindows();
                
                String currentUrl = driver.getCurrentUrl();
                // Если мы не на странице Telegram или на пустой странице, перенаправляем
                if (currentUrl.equals("about:blank") || currentUrl.equals("chrome://newtab/") || 
                    !currentUrl.contains("telegram.org")) {
                    LOGGER.info("Перенаправление на Telegram Web...");
                    driver.navigate().to(TELEGRAM_WEB_URL);
                    try {
                        Thread.sleep(1000); // Даем время на загрузку
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
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

    private static WebDriver connectToExistingChrome() {
        try {
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("debuggerAddress", "localhost:" + REMOTE_DEBUGGING_PORT);
            
            // Создаем новый сервис, если нужно
            if (driverService == null || !driverService.isRunning()) {
                driverService = ChromeDriverService.createDefaultService();
                driverService.start();
            }
            
            return new ChromeDriver(driverService, options);
        } catch (Exception e) {
            LOGGER.warning("Ошибка при подключении к существующему Chrome: " + e.getMessage());
            return null;
        }
    }
    
    private static boolean isChromeBrowserRunning() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Process process = Runtime.getRuntime().exec("netstat -ano | findstr " + REMOTE_DEBUGGING_PORT);
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
                String line;
                boolean found = false;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("LISTENING")) {
                        found = true;
                        break;
                    }
                }
                process.waitFor();
                reader.close();
                return found;
            } else {
                Process process = Runtime.getRuntime().exec("lsof -i :" + REMOTE_DEBUGGING_PORT);
                process.waitFor();
                return process.exitValue() == 0;
            }
        } catch (Exception e) {
            LOGGER.warning("Ошибка при проверке запущенных процессов Chrome: " + e.getMessage());
            return false;
        }
    }

    private static void closeExtraWindows() {
        try {
            Set<String> windowHandles = driver.getWindowHandles();
            
            // Если открыто больше одного окна
            if (windowHandles.size() > 1) {
                LOGGER.info("Обнаружено " + windowHandles.size() + " окон. Закрываем лишние...");
                
                // Если оригинальное окно все еще доступно, используем его
                if (windowHandles.contains(originalWindowHandle)) {
                    // Закрываем все окна кроме оригинального
                    for (String handle : windowHandles) {
                        if (!handle.equals(originalWindowHandle)) {
                            driver.switchTo().window(handle);
                            driver.close();
                            LOGGER.info("Закрыто дополнительное окно");
                        }
                    }
                    // Переключаемся на оригинальное окно
                    driver.switchTo().window(originalWindowHandle);
                } else {
                    // Оригинальное окно недоступно, сохраняем первое из текущих как новое оригинальное
                    String firstHandle = windowHandles.iterator().next();
                    driver.switchTo().window(firstHandle);
                    originalWindowHandle = firstHandle;
                    
                    // Закрываем все остальные
                    for (String handle : windowHandles) {
                        if (!handle.equals(originalWindowHandle)) {
                            driver.switchTo().window(handle);
                            driver.close();
                            LOGGER.info("Закрыто дополнительное окно");
                        }
                    }
                    // Переключаемся на оставшееся окно
                    driver.switchTo().window(originalWindowHandle);
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Ошибка при закрытии дополнительных окон: " + e.getMessage());
        }
    }

    private static void killChromeProcesses() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // Убиваем только процессы хромдрайвера, а не сам Chrome
                Process process = Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                process.waitFor(3, TimeUnit.SECONDS);
                
                // Chrome с отладочным портом закрываем только если не смогли подключиться
                if (!isInitialized) {
                    process = Runtime.getRuntime().exec(
                        "wmic process where name='chrome.exe' and commandline like '%remote-debugging-port=" 
                        + REMOTE_DEBUGGING_PORT + "%' delete");
                    process.waitFor(3, TimeUnit.SECONDS);
                }
                
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            LOGGER.warning("Ошибка при завершении процессов Chrome: " + e.getMessage());
        }
    }

    private static void cleanProfileIfNeeded() {
        try {
            File profileDir = new File(USER_DATA_DIR);
            if (profileDir.exists()) {
                // Удаляем только файлы блокировок, сохраняя остальные данные профиля
                File[] problemFiles = profileDir.listFiles((dir, name) -> 
                    name.contains("Lock") || 
                    name.contains(".tmp") || 
                    name.equals("Singleton") || 
                    name.equals("DevToolsActivePort"));
                
                if (problemFiles != null) {
                    for (File file : problemFiles) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            LOGGER.fine("Удален файл: " + file.getName());
                        }
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
            return driver.getWindowHandles().isEmpty();
        } catch (Exception e) {
            LOGGER.warning("Ошибка при проверке сессии: " + e.getMessage());
            return true;
        }
    }

    private static void resetDriver() {
        if (driver != null) {
            try {
                driver.close();
            } catch (Exception e) {
                LOGGER.warning("Ошибка при закрытии окна браузера: " + e.getMessage());
            }
        }
        driver = null;
        originalWindowHandle = null;
        isInitialized = false;
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
        
        if (driverService != null && driverService.isRunning()) {
            try {
                driverService.stop();
                LOGGER.info("Сервис драйвера остановлен");
            } catch (Exception e) {
                LOGGER.warning("Ошибка при остановке сервиса драйвера: " + e.getMessage());
            }
            driverService = null;
        }
    }
    
    public static void navigateToTelegramWeb() {
        if (driver != null) {
            try {
                driver.navigate().to(TELEGRAM_WEB_URL);
                LOGGER.info("Перенаправлено на Telegram Web");
            } catch (Exception e) {
                LOGGER.warning("Ошибка при переходе на Telegram Web: " + e.getMessage());
            }
        }
    }
}