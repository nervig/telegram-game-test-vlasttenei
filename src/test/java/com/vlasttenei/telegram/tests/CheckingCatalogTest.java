package com.vlasttenei.telegram.tests;

import java.util.logging.Logger;
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
        LOGGER.info("Запуск игры");
        checkThatGameIsOpen();
    }
}
