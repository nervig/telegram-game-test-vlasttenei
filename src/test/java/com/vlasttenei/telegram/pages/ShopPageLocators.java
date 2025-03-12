package com.vlasttenei.telegram.pages;

public class ShopPageLocators {
    // Shop button
    public static final String SHOP_BUTTON = "//span[contains(text(), 'Лавка ❘←')]/ancestor::button";
    
    // Game iframe
    public static final String GAME_IFRAME = "//iframe[contains(@title, 'Зов Теней - Тест Web App')]";
    // Sell resources button
    public static final String SELL_RESOURCES_BUTTON = "//button[contains(@style, 'background-image') and contains(@style, 'c6230e0523.png')]";
    // Sell button
    public static final String SELL_BUTTON = "//button[text()='Продать']";
    // Back button in resources section
    public static final String BACK_BUTTON_RESOURCES_SECTION = "//h2[contains(text(), 'На продажу')]/preceding::button[1]";

    // Sell resources section
    public static final String SELL_RESOURCES_SECTION_ELIXIR = "//button[text()='Эликсиры']";
    public static final String SELL_RESOURCES_SECTION_RESOURCES = "//button[text()='Ресурсы']";
    // Гибельцвет
    public static final String RESOURCES_GIBELTSVET = "//span[img[contains(@src, 'Resurs_Nazvanie_Czvetok_Gibelczveta')]]";
    public static final String SELL_RESOURCES_SECTION_RESOURCES_GIBELTSVET = "//div[contains(text(), 'Гибельцвет')]/following::div[contains(text(), 'Стоимость продажи')]/following::div[1]";
    // Eternity Shards section
    public static final String ETERNITY_SHARDS_ITEM = "//img[@alt='Осколки Вечности']/ancestor::a";
    // coins
    public static final String COINS_ITEM_1 = "//div[img[@alt='coin']]/span";
    public static final String COINS_ITEM_2 = "//div[img[@alt='coin']][2]/span";
    
    // Shard packs
    public static final String SMALL_PACK_NAME = "//div[contains(@class, '_name_') and contains(text(), '500 Осколков Вечности')]";
    public static final String SMALL_PACK_DESC = "//div[contains(@class, '_desc_') and contains(text(), 'Малый набор осколков')]";
    
    public static final String MEDIUM_PACK_NAME = "//div[contains(@class, '_name_') and contains(text(), '1500 Осколков Вечности')]";
    public static final String MEDIUM_PACK_DESC = "//div[contains(@class, '_desc_') and contains(text(), 'Средний набор осколков')]";
    
    public static final String LARGE_PACK_NAME = "//div[contains(@class, '_name_') and contains(text(), '3500 Осколков Вечности')]";
    public static final String LARGE_PACK_DESC = "//div[contains(@class, '_desc_') and contains(text(), 'Большой набор осколков')]";
    
    public static final String HUGE_PACK_NAME = "//div[contains(@class, '_name_') and contains(text(), '5500 Осколков Вечности')]";
    public static final String HUGE_PACK_DESC = "//div[contains(@class, '_desc_') and contains(text(), 'Огромный набор осколков')]";
} 