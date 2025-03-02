package com.vlasttenei.telegram.tests;

import org.testng.annotations.Test;

public class GameTest2 extends BaseTest {

    @Test(priority = 3)
    public void startGame2() {
        driver.get("https://web.telegram.org/");

        System.out.println("Тест 2: проверка игровых механик.");
        // Дополнительные шаги взаимодействия с ботом
    }
}
