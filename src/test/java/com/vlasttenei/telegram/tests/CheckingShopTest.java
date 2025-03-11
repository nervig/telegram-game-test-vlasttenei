package com.vlasttenei.telegram.tests;

import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.logging.Logger;
import com.codeborne.selenide.Selenide;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import io.qameta.allure.*;

import java.math.BigDecimal;
import java.time.Duration;
import com.vlasttenei.telegram.pages.ShopPageLocators;

@Epic("Игровой магазин")
@Feature("Покупка осколков вечности")
public class CheckingShopTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(CheckingShopTest.class.getName());
    WebElement shopButton;

    @Test(priority = 3)
    @Story("Проверка покупки всех наборов осколков")
    @Description("Тест проверяет возможность покупки всех доступных наборов осколков вечности")
    @Severity(SeverityLevel.CRITICAL)
    public void startGame2() {
        BigDecimal costOfSmallPack = new BigDecimal(27);
        BigDecimal costOfMediumPack = new BigDecimal(81);
        BigDecimal costOfLargePack = new BigDecimal(161);
        BigDecimal costOfHugePack = new BigDecimal(253);
        BigDecimal packageOfCrystalsSmall = new BigDecimal(500);
        BigDecimal packageOfCrystalsMedium = new BigDecimal(1500);
        BigDecimal packageOfCrystalsLarge = new BigDecimal(3500);
        BigDecimal packageOfCrystalsHuge = new BigDecimal(5500);

        LOGGER.info("Запуск игры");
        checkThatGameIsOpen();

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

        // Сначала прокрутим страницу до конца, а потом найдем элемент
        Selenide.executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        sleep(1000); // Дадим время на завершение прокрутки
        $x(ShopPageLocators.ETERNITY_SHARDS_ITEM)
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        LOGGER.info("Кликнули по элементу 'Осколки Вечности'");
        // Сохраняем количество монеток
        BigDecimal crystals = new BigDecimal($x(ShopPageLocators.COINS_ITEM_1).getText().replace(",", ""));
        System.out.println("crystals: " + crystals);
        BigDecimal coins = new BigDecimal($x(ShopPageLocators.COINS_ITEM_2).getText().replace(",", ""));
        System.out.println("coins: " + coins);

        // Проверяем все наборы осколков
        $x(ShopPageLocators.SMALL_PACK_NAME).shouldBe(visible);
        LOGGER.info("Элемент '500 Осколков Вечности' виден");
        $x(ShopPageLocators.SMALL_PACK_DESC)
                .shouldBe(visible)
                .click();
        sleep(3000);
        // Проверяем количество инвентаря после покупки малого набора
        BigDecimal crystalsAfterPay = new BigDecimal($x(ShopPageLocators.COINS_ITEM_1).getText().replace(",", ""));
        System.out.println("crystalsAfterPay: " + crystalsAfterPay);
        BigDecimal coinsAfterPay = new BigDecimal($x(ShopPageLocators.COINS_ITEM_2).getText().replace(",", ""));
        System.out.println("coinsAfterPay: " + coinsAfterPay);
        crystals = crystals.add(packageOfCrystalsSmall);
        coins = coins.subtract(costOfSmallPack);
        System.out.println("crystals: " + crystals);
        System.out.println("coins: " + coins);

        Assert.assertEquals(crystalsAfterPay, crystals);
        LOGGER.info("Проверка количества кристаллов прошла успешно после покупки малого набора: " + crystalsAfterPay
                + " = " + crystals);
        Assert.assertEquals(coinsAfterPay, coins);
        LOGGER.info("Проверка количества монет прошла успешно после покупки малого набора: " + coinsAfterPay + " = "
                + coins);

        LOGGER.info("Количество кристаллов после покупки малого набора: " + crystalsAfterPay);
        LOGGER.info("Количество монеток после покупки малого набора: " + coinsAfterPay);
        LOGGER.info("Элемент 'Малый набор осколков' виден");

        $x(ShopPageLocators.MEDIUM_PACK_NAME).shouldBe(visible);
        LOGGER.info("Элемент '1500 Осколков Вечности' виден");
        $x(ShopPageLocators.MEDIUM_PACK_DESC)
                .shouldBe(visible)
                .click();
        sleep(3000);
        LOGGER.info("Элемент 'Средний набор осколков' виден");
        // Проверяем количество инвентаря после покупки среднего набора
        crystalsAfterPay = new BigDecimal($x(ShopPageLocators.COINS_ITEM_1).getText().replace(",", ""));
        System.out.println("crystalsAfterPay: " + crystalsAfterPay);
        coinsAfterPay = new BigDecimal($x(ShopPageLocators.COINS_ITEM_2).getText().replace(",", ""));
        System.out.println("coinsAfterPay: " + coinsAfterPay);
        crystals = crystals.add(packageOfCrystalsMedium);
        coins = coins.subtract(costOfMediumPack);
        System.out.println("crystals: " + crystals);
        System.out.println("coins: " + coins);

        Assert.assertEquals(crystalsAfterPay, crystals);
        LOGGER.info("Проверка количества кристаллов прошла успешно после покупки среднего набора: " + crystalsAfterPay
                + " = " + crystals);
        Assert.assertEquals(coinsAfterPay, coins);
        LOGGER.info("Проверка количества монет прошла успешно после покупки среднего набора: " + coinsAfterPay + " = "
                + coins);

        $x(ShopPageLocators.LARGE_PACK_NAME).shouldBe(visible);
        LOGGER.info("Элемент '3500 Осколков Вечности' виден");
        $x(ShopPageLocators.LARGE_PACK_DESC)
                .shouldBe(visible)
                .click();
        sleep(3000);
        LOGGER.info("Элемент 'Большой набор осколков' виден");
        // Проверяем количество инвентаря после покупки большого набора
        crystalsAfterPay = new BigDecimal($x(ShopPageLocators.COINS_ITEM_1).getText().replace(",", ""));
        System.out.println("crystalsAfterPay: " + crystalsAfterPay);
        coinsAfterPay = new BigDecimal($x(ShopPageLocators.COINS_ITEM_2).getText().replace(",", ""));
        System.out.println("coinsAfterPay: " + coinsAfterPay);
        crystals = crystals.add(packageOfCrystalsLarge);
        coins = coins.subtract(costOfLargePack);
        System.out.println("crystals: " + crystals);
        System.out.println("coins: " + coins);

        Assert.assertEquals(crystalsAfterPay, crystals);
        LOGGER.info("Проверка количества кристаллов прошла успешно после покупки большого набора: " + crystalsAfterPay
                + " = " + crystals);
        Assert.assertEquals(coinsAfterPay, coins);
        LOGGER.info("Проверка количества монет прошла успешно после покупки большого набора: " + coinsAfterPay + " = "
                + coins);

        $x(ShopPageLocators.HUGE_PACK_NAME).shouldBe(visible);
        LOGGER.info("Элемент '5500 Осколков Вечности' виден");
        $x(ShopPageLocators.HUGE_PACK_DESC)
                .shouldBe(visible)
                .click();
        sleep(3000);
        LOGGER.info("Элемент 'Огромный набор осколков' виден");
        // Проверяем количество инвентаря после покупки огромного набора
        crystalsAfterPay = new BigDecimal($x(ShopPageLocators.COINS_ITEM_1).getText().replace(",", ""));
        System.out.println("crystalsAfterPay: " + crystalsAfterPay);
        coinsAfterPay = new BigDecimal($x(ShopPageLocators.COINS_ITEM_2).getText().replace(",", ""));
        System.out.println("coinsAfterPay: " + coinsAfterPay);
        crystals = crystals.add(packageOfCrystalsHuge);
        coins = coins.subtract(costOfHugePack);
        System.out.println("crystals: " + crystals);
        System.out.println("coins: " + coins);

        Assert.assertEquals(crystalsAfterPay, crystals);
        LOGGER.info("Проверка количества кристаллов прошла успешно после покупки огромного набора: " + crystalsAfterPay
                + " = " + crystals);
        Assert.assertEquals(coinsAfterPay, coins);
        LOGGER.info("Проверка количества монет прошла успешно после покупки огромного набора: " + coinsAfterPay + " = "
                + coins);

        LOGGER.info("Количество кристаллов после покупки огромного набора: " + crystalsAfterPay);
        LOGGER.info("Количество монеток после покупки огромного набора: " + coinsAfterPay);
    }
}
