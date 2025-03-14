package com.vlasttenei.telegram.tests;

import java.util.logging.Logger;
import java.util.logging.Level;
import org.testng.annotations.Test;

public class CheckingCatalogTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(CheckingCatalogTest.class.getName());

    public CheckingCatalogTest() {
    }

    @Test(priority = 2, description = "Проверка каталога", dependsOnGroups = "auth", groups = "TC-2")
    public void startGame1() {
        try {
            checkThatGameIsOpen();
            LOGGER.info("✅ Игра успешно запущена.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при запуске игры.", e);
        }
    }
}
