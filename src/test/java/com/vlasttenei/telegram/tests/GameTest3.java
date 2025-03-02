package com.vlasttenei.telegram.tests;

import org.testng.annotations.Test;

public class GameTest3 extends BaseTest {

    @Test(priority = 4)
    public void startGame3() {
        driver.get("https://web.telegram.org/");

        System.out.println("Тест 3: проверка взаимодействия с ботом.");
        // Дополнительные шаги взаимодействия с ботом
    }
}
