package com.vlasttenei.telegram.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.*;
import java.util.logging.Logger;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import java.time.Duration;

import com.codeborne.selenide.Selenide;
import com.vlasttenei.telegram.api.ApiClient;
import com.vlasttenei.telegram.pages.ShopPageLocators;
import java.math.BigDecimal;

@Epic("Игровой магазин")
@Feature("Продажа ресурсов")
public class CheckingShopSellResourcesTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(CheckingShopSellResourcesTest.class.getName());
    // Стоимость Гибельсвета
    BigDecimal priceOfGibeltsvetDefault = new BigDecimal(46);

    @Test(priority = 4)
    @Story("Проверка продажи ресурсов")
    @Description("Тест проверяет возможность продажи ресурсов")
    @Severity(SeverityLevel.CRITICAL)
    public void startGame3() {
        LOGGER.info("Запуск игры");
        checkThatGameIsOpen();

        // Добавляем Гибельцвет в рюкзак
        ApiClient apiClient = new ApiClient();
        apiClient.addThingToBag("7517277660", "thing-deathbloom");

        // Находим и кликаем по кнопке магазина
        $x(ShopPageLocators.SHOP_BUTTON)
                .shouldBe(visible)
                .click();
        LOGGER.info("Кликнули по кнопке магазина");

        // Ждем появления iframe и переключаемся на него
        sleep(2000); // Даем время на загрузку iframe
        $x(ShopPageLocators.GAME_IFRAME)
                .shouldBe(visible)
                .shouldBe(interactable);
        switchTo().frame($x(ShopPageLocators.GAME_IFRAME));
        LOGGER.info("Переключились на iframe игры");

        BigDecimal crystals = new BigDecimal($x(ShopPageLocators.COINS_ITEM_1).getText().replace(",", ""));
        System.out.println("crystals: " + crystals);
        BigDecimal coins = new BigDecimal($x(ShopPageLocators.COINS_ITEM_2).getText().replace(",", ""));
        System.out.println("coins: " + coins);

                // Сначала прокрутим страницу до конца, а потом найдем элемент
        Selenide.executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        sleep(1000); 
        // Переходим на страницу продажи ресурсов
        $x(ShopPageLocators.SELL_RESOURCES_BUTTON)
                .shouldBe(visible)
                .click();
        LOGGER.info("Кликнули по кнопке продажи ресурсов");
        $x(ShopPageLocators.SELL_RESOURCES_SECTION_RESOURCES)
                .shouldBe(visible)
        .click();
        LOGGER.info("Кликнули по кнопке продажи ресурсов");

        // Кликаем по Гибельцвету
        $x(ShopPageLocators.RESOURCES_GIBELTSVET)
                .shouldBe(visible)
                .click();
        LOGGER.info("Кликнули по Гибельцвету");
        sleep(3000);
        LOGGER.info("Ждем 3 секунды");  
        // Проверяем стоимость Гибельсвета
        BigDecimal priceOfGibeltsvet = new BigDecimal($x(ShopPageLocators.SELL_RESOURCES_SECTION_RESOURCES_GIBELTSVET).getText().replace(",", ""));
        System.out.println("priceOfGibeltsvet: " + priceOfGibeltsvet);

        // Проверяем, что стоимость Гибельсвета равна заявленной
        Assert.assertEquals(priceOfGibeltsvet, priceOfGibeltsvetDefault, "Стоимость Гибельсвета не равна заявленной");

        // Кликаем по кнопке Продать
        $x(ShopPageLocators.SELL_BUTTON)
                .shouldBe(visible)
                .click();
        LOGGER.info("Кликнули по кнопке Продать");

        sleep(3000);
        LOGGER.info("Ждем 3 секунды");
        // Нажимаем на кнопку Назад
        $x(ShopPageLocators.BACK_BUTTON_RESOURCES_SECTION)
                .shouldBe(visible)
                .click();
        LOGGER.info("Нажали на кнопку Назад");
        sleep(3000);
        LOGGER.info("Ждем 3 секунды");
        BigDecimal crystalsAfterPay = new BigDecimal($x(ShopPageLocators.COINS_ITEM_1).getText().replace(",", ""));
        System.out.println("crystalsAfterPay: " + crystalsAfterPay);
        crystals = crystals.add(priceOfGibeltsvet);

        // Проверяем, что количество кристаллов после продажи равно количеству кристаллов до продажи + стоимость Гибельсвета
        Assert.assertEquals(crystalsAfterPay, crystals);
        LOGGER.info("Проверка количества кристаллов прошла успешно после продажи Гибельсвета: " + crystalsAfterPay
                + " = " + crystals);

        // Переключаемся обратно на основной контент
        switchTo().defaultContent();
        LOGGER.info("Переключились на основной контент");

        sleep(2000); // Даем время на переключение

        // Нажимаем на кнопку закрытия магазина
        $x(ShopPageLocators.CLOSE_BUTTON_SHOP_PAGE)
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(interactable, Duration.ofSeconds(10))
                .scrollIntoView(true)
                .click();
        LOGGER.info("Нажали на кнопку закрытия магазина");
    }
}
