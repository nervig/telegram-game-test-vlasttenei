package com.vlasttenei.telegram.tests;

import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import java.util.logging.Logger;
import com.codeborne.selenide.Selenide;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import java.time.Duration;

public class CheckingShopTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(CheckingShopTest.class.getName());
    WebElement shopButton;

    @Test(priority = 3)
    public void startGame2() {
        LOGGER.info("Запуск игры");
        checkThatGameIsOpen();
        
        // Находим и кликаем по кнопке магазина
        $x("//span[contains(text(), 'Лавка ❘←')]/ancestor::button")
            .shouldBe(visible)
            .click();
        LOGGER.info("Кликнули по кнопке магазина");
        
        // Ждем появления iframe и переключаемся на него
        sleep(2000); // Даем время на загрузку iframe
        $x("//iframe[contains(@title, 'Зов Теней - Тест Web App')]")
            .shouldBe(visible)
            .shouldBe(interactable);
        switchTo().frame($x("//iframe[contains(@title, 'Зов Теней - Тест Web App')]"));
        LOGGER.info("Переключились на iframe игры");

// Сначала прокрутим страницу до конца, а потом найдем элемент
Selenide.executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
sleep(1000); // Дадим время на завершение прокрутки
$x("//img[@alt='Осколки Вечности']/ancestor::a")
    .shouldBe(visible, Duration.ofSeconds(10))
    .click();
        LOGGER.info("Кликнули по элементу 'Осколки Вечности'");
      

        
        // Находим элемент '500 Осколков Вечности'
        $x("//div[contains(@class, '_name_') and contains(text(), '500 Осколков Вечности')]")
            .shouldBe(visible);
        LOGGER.info("Элемент '500 Осколков Вечности' виден");
        $x("//div[contains(@class, '_desc_') and contains(text(), 'Малый набор осколков')]")
            .shouldBe(visible)
            .click();
        LOGGER.info("Элемент 'Малый набор осколков' виден");
        // Находим элемент 'Купить'
    }
}
