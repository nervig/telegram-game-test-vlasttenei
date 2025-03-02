package com.vlasttenei.telegram.tests;

import org.testng.annotations.Test;

public class CheckingCatalogTest extends BaseTest {

    @Test(priority = 2)
    public void startGame1() {
        driver.get("https://web.telegram.org/");

        System.out.println("Тест 1: проверка работы игры.");
        // Дополнительные шаги взаимодействия с ботом
    }
}
